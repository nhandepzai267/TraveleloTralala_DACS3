package com.example.travellelotralala.ui.screens.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Booking
import com.example.travellelotralala.repository.BookingRepository
import com.example.travellelotralala.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingConfirmationViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val tripRepository: TripRepository
) : ViewModel() {
    
    private val _bookingDetails = MutableStateFlow<Booking?>(null)
    val bookingDetails: StateFlow<Booking?> = _bookingDetails
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _tripLocation = MutableStateFlow<String?>(null)
    val tripLocation: StateFlow<String?> = _tripLocation
    
    fun loadBookingDetails(bookingId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            bookingRepository.getBookingById(bookingId)
                .onSuccess { booking ->
                    _bookingDetails.value = booking
                    
                    // Lấy thêm thông tin location từ trip
                    tripRepository.getTripById(booking.tripId)
                        .onSuccess { trip ->
                            _tripLocation.value = trip.location
                        }
                        .onFailure { exception ->
                            // Không cần hiển thị lỗi này, chỉ log
                            _tripLocation.value = ""
                        }
                }
                .onFailure { exception ->
                    _error.value = exception.message
                }
            
            _isLoading.value = false
        }
    }
}