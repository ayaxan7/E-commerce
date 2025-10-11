package com.ayaan.bazaar

import android.app.Application
import android.util.Log
import com.ayaan.bazaar.di.appModule
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class BazaarApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Firebase
        initializeFirebase()

        startKoin {
            androidContext(this@BazaarApplication)
            modules(appModule)
        }
    }

    private fun initializeFirebase() {
        try {
            // Initialize Firebase if not already initialized
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
            }

            // Configure Firestore settings for better performance
            val firestore = FirebaseFirestore.getInstance()
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()
            firestore.firestoreSettings = settings

            // Configure Storage with proper settings
            val storage = FirebaseStorage.getInstance()
            storage.maxUploadRetryTimeMillis = 60000 // 60 seconds
            storage.maxDownloadRetryTimeMillis = 60000 // 60 seconds

            Log.d("BazaarApp", "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e("BazaarApp", "Failed to initialize Firebase", e)
        }
    }
}
