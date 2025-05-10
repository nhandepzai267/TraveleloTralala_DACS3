package com.example.travellelotralala.ui.screens.mainscreens.notificationsscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Notification
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {
    
    private val _notifications = MutableStateFlow<List<Notification>>(emptyList())
    val notifications: StateFlow<List<Notification>> = _notifications
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    init {
        loadNotifications()
    }
    
    private fun loadNotifications() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val notificationsCollection = firestore.collection("notifications")
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .get()
                    .await()
                
                val notificationsList = notificationsCollection.documents.mapNotNull { document ->
                    try {
                        val notiId = document.id
                        val notiTitle = document.getString("notiTitle") ?: ""
                        val notiDescription = document.getString("notiDescription") ?: ""
                        val notiContent = document.getString("notiContent") ?: ""
                        val notiImage = document.getString("notiImage") ?: ""
                        val notiType = document.getString("notiType") ?: ""
                        val contentImage = document.get("contentImage") as? List<String> ?: emptyList()
                        val createdAtTimestamp = document.getTimestamp("createdAt")
                        val createdAt = createdAtTimestamp?.toDate()
                        val relatedTripId = document.getString("relatedTripId") ?: ""
                        
                        Notification(
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
                    } catch (e: Exception) {
                        Log.e("NotificationsViewModel", "Error parsing notification: ${e.message}")
                        null
                    }
                }
                
                _notifications.value = notificationsList
            } catch (e: Exception) {
                Log.e("NotificationsViewModel", "Error loading notifications: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun refreshNotifications() {
        loadNotifications()
    }
}


