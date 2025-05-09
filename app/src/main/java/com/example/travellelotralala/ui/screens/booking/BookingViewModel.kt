package com.example.travellelotralala.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Trip
import com.example.travellelotralala.repository.BookingRepository
import com.example.travellelotralala.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val bookingRepository: BookingRepository
) : ViewModel() {
    
    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    fun loadTrip(tripId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            tripRepository.getTripById(tripId)
                .onSuccess { trip ->
                    _trip.value = trip
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }
    
    fun createBooking(
        tripId: String,
        numberOfTravelers: Int,
        travelDate: LocalDate,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val trip = _trip.value
            if (trip != null) {
                bookingRepository.createBooking(
                    tripId = tripId,
                    tripName = trip.name,
                    tripImageUrl = trip.imageUrl,
                    numberOfTravelers = numberOfTravelers,
                    totalPrice = trip.price * numberOfTravelers,
                    travelDate = travelDate
                ).onSuccess {
                    onSuccess()
                }.onFailure { exception ->
                    _error.value = exception.message
                }
            } else {
                _error.value = "Trip information not available"
            }
            
            _isLoading.value = false
        }
    }
}