package com.example.travellelotralala.ui.screens.notificationdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class NotificationDetailViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    
    private val _notification = MutableStateFlow<Notification?>(null)
    val notification: StateFlow<Notification?> = _notification
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    fun loadNotification(notificationId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val documentSnapshot = firestore.collection("notifications")
                    .document(notificationId)
                    .get()
                    .await()
                
                if (documentSnapshot.exists()) {
                    val notiId = documentSnapshot.id
                    val notiTitle = documentSnapshot.getString("notiTitle") ?: ""
                    val notiDescription = documentSnapshot.getString("notiDescription") ?: ""
                    val notiContent = documentSnapshot.getString("notiContent") ?: ""
                    val notiImage = documentSnapshot.getString("notiImage") ?: ""
                    val notiType = documentSnapshot.getString("notiType") ?: ""
                    val contentImage = documentSnapshot.get("contentImage") as? List<String> ?: emptyList()
                    val createdAt = documentSnapshot.getTimestamp("createdAt")?.toDate()
                    val relatedTripId = documentSnapshot.getString("relatedTripId") ?: ""
                    
                    Log.d("NotificationDetailViewModel", "Loaded relatedTripId: $relatedTripId")
                    
                    _notification.value = Notification(
                        notiId = notiId,
                        notiTitle = notiTitle,
                        notiDescription = notiDescription,
                        notiContent = notiContent,
                        notiImage = notiImage,
                        contentImage = contentImage,
                        notiType = notiType,
                        createdAt = createdAt,
                        relatedTripId = relatedTripId
                    )
                } else {
                    _error.value = "Notification not found"
                }
            } catch (e: Exception) {
                Log.e("NotificationDetailViewModel", "Error loading notification: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}



