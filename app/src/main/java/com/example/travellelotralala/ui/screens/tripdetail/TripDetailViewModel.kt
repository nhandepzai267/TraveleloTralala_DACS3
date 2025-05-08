package com.example.travellelotralala.ui.screens.tripdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.repository.SavedTripsRepository
import com.example.travellelotralala.repository.TripRepository
import com.example.travellelotralala.model.Trip
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    private val tripsRepository: TripRepository,
    private val savedTripsRepository: SavedTripsRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    
    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved
    
    // Thêm state mới cho thông báo
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage
    
    // Hàm để xóa thông báo sau khi hiển thị
    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }
    
    fun loadTrip(tripId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val tripResult = tripsRepository.getTripById(tripId)
                tripResult.fold(
                    onSuccess = { trip ->
                        _trip.value = trip
                        // Kiểm tra xem trip đã được lưu hay chưa
                        _isSaved.value = savedTripsRepository.isTripSaved(tripId)
                    },
                    onFailure = { e ->
                        _error.value = e.message
                    }
                )
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun toggleSaveTrip(onNotLoggedIn: () -> Unit) {
        viewModelScope.launch {
            try {
                // Kiểm tra xem người dùng đã đăng nhập chưa
                if (auth.currentUser == null) {
                    Log.d("TripDetailViewModel", "User not logged in, redirecting to login")
                    onNotLoggedIn()
                    return@launch
                }
                
                val trip = _trip.value ?: return@launch
                
                // Tiếp tục với logic lưu/xóa trip
                Log.d("TripDetailViewModel", "Toggling save for trip: ${trip.id}, current state: ${_isSaved.value}")
                
                if (_isSaved.value) {
                    val result = savedTripsRepository.removeTrip(trip.id)
                    result.onSuccess {
                        // Thêm thông báo khi xóa thành công
                        _snackbarMessage.value = "Removed from saved trips"
                    }.onFailure { e ->
                        Log.e("TripDetailViewModel", "Failed to remove trip: ${e.message}")
                        _error.value = "Failed to remove trip: ${e.message}"
                    }
                } else {
                    val result = savedTripsRepository.saveTrip(trip)
                    result.onSuccess {
                        // Thêm thông báo khi lưu thành công
                        _snackbarMessage.value = "Trip saved successfully"
                    }.onFailure { e ->
                        Log.e("TripDetailViewModel", "Failed to save trip: ${e.message}")
                        _error.value = "Failed to save trip: ${e.message}"
                    }
                }
                
                // Cập nhật trạng thái sau khi thao tác thành công
                _isSaved.value = !_isSaved.value
                Log.d("TripDetailViewModel", "New saved state: ${_isSaved.value}")
            } catch (e: Exception) {
                Log.e("TripDetailViewModel", "Error toggling save: ${e.message}")
                _error.value = "Failed to ${if (_isSaved.value) "remove" else "save"} trip: ${e.message}"
            }
        }
    }
}








