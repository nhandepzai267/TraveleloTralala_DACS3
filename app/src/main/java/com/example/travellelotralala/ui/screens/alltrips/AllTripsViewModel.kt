package com.example.travellelotralala.ui.screens.alltrips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Trip
import com.example.travellelotralala.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTripsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {
    
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    init {
        loadAllTrips()
    }
    
    private fun loadAllTrips() {
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
}