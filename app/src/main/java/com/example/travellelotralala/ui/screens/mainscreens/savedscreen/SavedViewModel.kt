package com.example.travellelotralala.ui.screens.mainscreens.savedscreen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.repository.SavedTripsRepository
import com.example.travellelotralala.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SavedViewModel @Inject constructor(
    private val savedTripsRepository: SavedTripsRepository
) : ViewModel() {
    
    private val _savedTrips = MutableStateFlow<List<Trip>>(emptyList())
    val savedTrips: StateFlow<List<Trip>> = _savedTrips
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage
    
    init {
        loadSavedTrips()
    }
    
    private fun loadSavedTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                savedTripsRepository.getSavedTrips().collect { trips ->
                    _savedTrips.value = trips
                    _isLoading.value = false
                }
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error loading saved trips: ${e.message}")
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun removeFromSaved(tripId: String) {
        viewModelScope.launch {
            try {
                savedTripsRepository.removeTrip(tripId)
                _snackbarMessage.value = "Trip removed from saved"
            } catch (e: Exception) {
                Log.e("SavedViewModel", "Error removing trip: ${e.message}")
                _snackbarMessage.value = "Error removing trip: ${e.message}"
            }
        }
    }
    
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
}








