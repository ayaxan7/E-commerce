package com.ayaan.bazaar.data.firebase

import android.net.Uri
import com.ayaan.bazaar.domain.model.Product
import com.ayaan.bazaar.domain.repository.ProductRepository
import com.ayaan.bazaar.util.PRODUCTS_DB
import com.ayaan.bazaar.util.Result
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirestoreProductRepositoryImpl(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
    private val auth: FirebaseAuth
) : ProductRepository {

    override suspend fun createProduct(product: Product, imageUris: List<Uri>): Result<String> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")
            val productId = UUID.randomUUID().toString()

            // Upload images first
            val imageUrlsResult = uploadImages(imageUris, productId)
            if (imageUrlsResult is Result.Error) {
                return imageUrlsResult as Result<String>
            }

            val imageUrls = (imageUrlsResult as Result.Success).data
            val productWithImages = product.copy(
                id = productId,
                imageUrls = imageUrls,
                coverImageUrl = imageUrls.firstOrNull() ?: "",
                ownerId = currentUser.uid,
                ownerEmail = currentUser.email ?: ""
            )

            // Save product to Firestore
            firestore.collection(PRODUCTS_DB)
                .document(productId)
                .set(productWithImages)
                .await()

            Result.Success(productId)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override fun getProducts(): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection(PRODUCTS_DB)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                } ?: emptyList()

                trySend(products)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun getProduct(productId: String): Result<Product> {
        return try {
            val doc = firestore.collection(PRODUCTS_DB)
                .document(productId)
                .get()
                .await()

            val product = doc.toObject(Product::class.java)
                ?: throw Exception("Product not found")

            Result.Success(product)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun getUserProducts(userId: String): Flow<List<Product>> = callbackFlow {
        val listener = firestore.collection(PRODUCTS_DB)
            .whereEqualTo("ownerId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("FirestoreRepo", "Error fetching user products", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)
                }?.sortedByDescending { it.createdAt } ?: emptyList()

                android.util.Log.d("FirestoreRepo", "Fetched ${products.size} products for user $userId")
                trySend(products)
            }

        awaitClose {
            listener.remove()
        }
    }

    override suspend fun uploadImages(imageUris: List<Uri>, productId: String): Result<List<String>> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")
            val uploadTasks = mutableListOf<String>()
            
            imageUris.forEachIndexed { index, uri ->
                try {
                    val imageRef = storage.reference
                        .child(PRODUCTS_DB)
                        .child(currentUser.uid)
                        .child("${productId}_${index}_${System.currentTimeMillis()}.jpg")
                    
                    // Upload the file first
                    val uploadTask = imageRef.putFile(uri).await()
                    
                    // Only get download URL if upload was successful
                    if (uploadTask.task.isSuccessful) {
                        val downloadUrl = imageRef.downloadUrl.await().toString()
                        uploadTasks.add(downloadUrl)
                    } else {
                        throw Exception("Upload failed for image $index")
                    }
                } catch (e: Exception) {
                    throw Exception("Failed to upload image $index: ${e.message}")
                }
            }
            
            if (uploadTasks.size != imageUris.size) {
                throw Exception("Not all images were uploaded successfully")
            }
            
            Result.Success(uploadTasks)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    override suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("User not authenticated")

            // First, get the product to access its image URLs and verify ownership
            val productDoc = firestore.collection(PRODUCTS_DB).document(productId).get().await()
            val product = productDoc.toObject(Product::class.java)
                ?: throw Exception("Product not found")

            // Verify that the current user owns this product
            if (product.ownerId != currentUser.uid) {
                throw Exception("You can only delete your own products")
            }

            // Delete images from Storage
            product.imageUrls.forEach { imageUrl ->
                try {
                    val imageRef = storage.getReferenceFromUrl(imageUrl)
                    imageRef.delete().await()
                } catch (e: Exception) {
                    android.util.Log.w("FirestoreRepo", "Failed to delete image: $imageUrl", e)
                    // Continue deleting other images even if one fails
                }
            }

            // Delete the product document from Firestore
            firestore.collection(PRODUCTS_DB).document(productId).delete().await()

            android.util.Log.d("FirestoreRepo", "Successfully deleted product $productId")
            Result.Success(Unit)
        } catch (e: Exception) {
            android.util.Log.e("FirestoreRepo", "Failed to delete product $productId", e)
            Result.Error(e)
        }
    }
}