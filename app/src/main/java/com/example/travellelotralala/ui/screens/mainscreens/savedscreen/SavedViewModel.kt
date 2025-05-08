package com.example.travellelotralala.ui.screens.mainscreens.savedscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.repository.SavedTripsRepository
import com.example.travellelotralala.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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
    
    init {
        loadSavedTrips()
    }
    
    private fun loadSavedTrips() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            savedTripsRepository.getSavedTrips()
                .catch { e ->
                    _error.value = e.message
                    _isLoading.value = false
                }
                .collect { trips ->
                    _savedTrips.value = trips
                    _isLoading.value = false
                }
        }
    }
    
    fun removeFromSaved(tripId: String) {
        viewModelScope.launch {
            try {
                savedTripsRepository.removeTrip(tripId)
                // Không cần cập nhật UI vì Flow sẽ tự động cập nhật khi dữ liệu thay đổi
            } catch (e: Exception) {
                _error.value = "Failed to remove trip: ${e.message}"
            }
        }
    }
}

