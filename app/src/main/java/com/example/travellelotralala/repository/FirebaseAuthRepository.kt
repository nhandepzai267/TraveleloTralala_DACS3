package com.example.travellelotralala.repository

import com.example.travellelotralala.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository : AuthRepository {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    
    override suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            // 1. Tạo tài khoản với Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("User ID is null")
            
            // 2. Tạo đối tượng User
            val user = User(
                uid = uid,
                name = name,
                email = email,
                createdAt = System.currentTimeMillis()
            )
            
            // 3. Lưu thông tin user vào Firestore
            firestore.collection("users").document(uid).set(user).await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            // 1. Đăng nhập với Firebase Authentication
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val uid = authResult.user?.uid ?: throw IllegalStateException("User ID is null")
            
            // 2. Lấy thông tin user từ Firestore
            val userDoc = firestore.collection("users").document(uid).get().await()
            val user = userDoc.toObject(User::class.java) 
                ?: throw IllegalStateException("User data not found")
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        
        return try {
            val userDoc = firestore.collection("users")
                .document(firebaseUser.uid).get().await()
            userDoc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    override fun isUserAuthenticated(): Boolean {
        return auth.currentUser != null
    }
    
    override suspend fun signOut() {
        auth.signOut()
    }
}