package com.example.travellelotralala.repository

import com.example.travellelotralala.model.User

interface AuthRepository {
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun getCurrentUser(): User?
    fun isUserAuthenticated(): Boolean
    suspend fun signOut()
}