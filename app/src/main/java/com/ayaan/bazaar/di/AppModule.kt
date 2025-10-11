package com.ayaan.bazaar.di

import com.ayaan.bazaar.data.firebase.FirebaseAuthRepositoryImpl
import com.ayaan.bazaar.data.firebase.FirestoreProductRepositoryImpl
import com.ayaan.bazaar.domain.repository.AuthRepository
import com.ayaan.bazaar.domain.repository.ProductRepository
import com.ayaan.bazaar.ui.auth.AuthViewModel
import com.ayaan.bazaar.ui.product.createproduct.CreateProductViewModel
import com.ayaan.bazaar.ui.product.productdetails.ProductDetailViewModel
import com.ayaan.bazaar.ui.product.productlist.ProductListViewModel
import com.ayaan.bazaar.ui.product.myuploads.MyUploadsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Firebase instances
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    // Repositories
    single<AuthRepository> {
        FirebaseAuthRepositoryImpl(get(), get())
    }
    single<ProductRepository> {
        FirestoreProductRepositoryImpl(get(), get(), get())
    }

    // ViewModels
    viewModel { AuthViewModel(get()) }
    viewModel { ProductListViewModel(get(), get()) }
    viewModel { CreateProductViewModel(get()) }
    viewModel { ProductDetailViewModel(get()) }
    viewModel { MyUploadsViewModel(get()) }
}
