package com.ayaan.bazaar.domain.repository

import android.net.Uri
import com.ayaan.bazaar.domain.model.Product
import com.ayaan.bazaar.util.Result
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    suspend fun createProduct(product: Product, imageUris: List<Uri>): Result<String>
    fun getProducts(): Flow<List<Product>>
    suspend fun getProduct(productId: String): Result<Product>
    suspend fun getUserProducts(userId: String): Flow<List<Product>>
    suspend fun uploadImages(imageUris: List<Uri>, productId: String): Result<List<String>>
    suspend fun deleteProduct(productId: String): Result<Unit>
}
