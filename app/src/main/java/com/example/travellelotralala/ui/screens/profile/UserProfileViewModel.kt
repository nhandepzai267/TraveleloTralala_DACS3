package com.example.travellelotralala.ui.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor() : ViewModel() {
    
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    fun loadUserProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val auth = FirebaseAuth.getInstance()
                val currentUserId = auth.currentUser?.uid
                
                if (currentUserId != null) {
                    val userDoc = FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(currentUserId)
                        .get()
                        .await()
                    
                    if (userDoc.exists()) {
                        val user = userDoc.toObject(User::class.java)
                        _user.value = user
                    } else {
                        _error.value = "User profile not found"
                    }
                } else {
                    _error.value = "User not authenticated"
                }
            } catch (e: Exception) {
                Log.e("UserProfileViewModel", "Error loading user profile: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun logout() {
        FirebaseAuth.getInstance().signOut()
    }
}