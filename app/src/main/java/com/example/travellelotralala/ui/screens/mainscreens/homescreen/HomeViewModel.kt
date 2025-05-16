package com.example.travellelotralala.ui.screens.mainscreens.homescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Trip
import com.example.travellelotralala.model.TravelCategory
import com.example.travellelotralala.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Museum
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.LocalActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import android.util.Log
import com.example.travellelotralala.model.User

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {
    
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _categories = MutableStateFlow<List<TravelCategory>>(emptyList())
    val categories: StateFlow<List<TravelCategory>> = _categories

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    init {
        loadTrips()
        loadCategories()
        getCurrentUser()
    }
    
    private fun loadTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = tripRepository.getAllTrips()
                result.onSuccess { tripList ->
                    _trips.value = tripList
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun loadCategories() {
        val categoryList = listOf(
            TravelCategory("Mountains", Icons.Default.Landscape),
            TravelCategory("Beaches", Icons.Default.BeachAccess),
            TravelCategory("Historical", Icons.Default.Museum),
            TravelCategory("Parks", Icons.Default.Park),
            TravelCategory("Food", Icons.Default.Restaurant),
            TravelCategory("Activities", Icons.Default.LocalActivity)
        )
        _categories.value = categoryList
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
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
                        _currentUser.value = user
                    }
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error getting current user: ${e.message}")
            }
        }
    }
}


