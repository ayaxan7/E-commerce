package com.ayaan.bazaar.domain.repository

import com.ayaan.bazaar.domain.model.User
import com.ayaan.bazaar.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signOut(): Result<Unit>
    suspend fun getCurrentUser(): User?
    fun getAuthState(): Flow<User?>
}
