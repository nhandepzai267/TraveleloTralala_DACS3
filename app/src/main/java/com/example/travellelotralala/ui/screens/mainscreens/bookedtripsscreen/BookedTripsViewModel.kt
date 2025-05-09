package com.example.travellelotralala.ui.screens.mainscreens.bookedtripsscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Booking
import com.example.travellelotralala.repository.BookingRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookedTripsViewModel @Inject constructor(
    private val bookingRepository: BookingRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadBookings()
    }

    private fun loadBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            val userId = auth.currentUser?.uid
            if (userId == null) {
                _error.value = "Please log in to view your bookings"
                _isLoading.value = false
                return@launch
            }

            try {
                bookingRepository.getUserBookings()
                    .onSuccess { bookingsList ->
                        _bookings.value = bookingsList
                    }
                    .onFailure { exception ->
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
