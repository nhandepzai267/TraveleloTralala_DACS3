package com.example.travellelotralala.ui.screens.booking

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Trip
import com.example.travellelotralala.repository.BookingRepository
import com.example.travellelotralala.repository.TripRepository
import com.example.travellelotralala.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val tripRepository: TripRepository,
    private val bookingRepository: BookingRepository,
    private val hotelRepository: HotelRepository
) : ViewModel() {
    
    private val _trip = MutableStateFlow<Trip?>(null)
    val trip: StateFlow<Trip?> = _trip
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Thêm các trường cho thông tin khách sạn
    private val _hotelId = MutableStateFlow<String?>(null)
    val hotelId: StateFlow<String?> = _hotelId
    
    private val _hotelName = MutableStateFlow<String?>(null)
    val hotelName: StateFlow<String?> = _hotelName
    
    private val _roomTypeId = MutableStateFlow<String?>(null)
    val roomTypeId: StateFlow<String?> = _roomTypeId
    
    private val _roomTypeName = MutableStateFlow<String?>(null)
    val roomTypeName: StateFlow<String?> = _roomTypeName
    
    private val _roomNumber = MutableStateFlow<String?>(null)
    val roomNumber: StateFlow<String?> = _roomNumber
    
    private val _hotelBooked = MutableStateFlow(false)
    val hotelBooked: StateFlow<Boolean> = _hotelBooked
    
    fun loadTrip(tripId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            tripRepository.getTripById(tripId)
                .onSuccess { trip ->
                    _trip.value = trip
                    Log.d("BookingViewModel", "Trip loaded: ${trip.name}, imageUrl: ${trip.imageUrl}")
                }
                .onFailure { exception ->
                    _error.value = exception.message
                    Log.e("BookingViewModel", "Error loading trip: ${exception.message}")
                }
            
            _isLoading.value = false
        }
    }
    
    fun setHotelInfo(hotelId: String, hotelName: String, roomTypeId: String, roomTypeName: String, roomNumber: String) {
        Log.d("BookingViewModel", "Setting hotel info: hotelId=$hotelId, hotelName=$hotelName, roomTypeId=$roomTypeId, roomTypeName=$roomTypeName, roomNumber=$roomNumber")
        _hotelId.value = hotelId
        _hotelName.value = hotelName
        _roomTypeId.value = roomTypeId
        _roomTypeName.value = roomTypeName
        _roomNumber.value = roomNumber
        _hotelBooked.value = true
        Log.d("BookingViewModel", "Hotel booked status after setting: ${_hotelBooked.value}")
    }
    
    fun createBooking(
        tripId: String,
        numberOfTravelers: Int,
        travelDate: LocalDate,
        onSuccess: (String) -> Unit
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val trip = _trip.value
            if (trip != null) {
                Log.d("BookingViewModel", "Creating booking for trip: ${trip.name}")
                
                // Tạo booking
                bookingRepository.createBooking(
                    tripId = tripId,
                    tripName = trip.name,
                    tripImageUrl = trip.imageUrl,
                    numberOfTravelers = numberOfTravelers,
                    totalPrice = trip.price * numberOfTravelers,
                    travelDate = travelDate
                ).onSuccess { bookingId ->
                    Log.d("BookingViewModel", "Booking created successfully with ID: $bookingId")
                    
                    // Nếu đã chọn khách sạn, cập nhật thông tin khách sạn cho booking
                    if (_hotelBooked.value) {
                        Log.d("BookingViewModel", "Hotel is booked, updating booking with hotel info")
                        val hotelId = _hotelId.value ?: ""
                        val hotelName = _hotelName.value ?: ""
                        val roomTypeId = _roomTypeId.value ?: ""
                        val roomTypeName = _roomTypeName.value ?: ""
                        val roomNumber = _roomNumber.value ?: ""
                        
                        if (hotelId.isNotEmpty() && roomTypeId.isNotEmpty() && roomNumber.isNotEmpty()) {
                            // Cập nhật trạng thái phòng - bỏ qua lỗi nếu có
                            hotelRepository.bookRoom(hotelId, roomTypeId, roomNumber)
                                .onSuccess {
                                    Log.d("BookingViewModel", "Room booked successfully")
                                }
                                .onFailure { exception ->
                                    Log.e("BookingViewModel", "Error booking room: ${exception.message}")
                                }
                            
                            // Luôn cập nhật thông tin khách sạn cho booking, bất kể có lỗi khi đặt phòng hay không
                            bookingRepository.updateBookingWithHotel(
                                bookingId = bookingId,
                                hotelId = hotelId,
                                hotelName = hotelName,
                                roomTypeId = roomTypeId,
                                roomTypeName = roomTypeName,
                                roomNumber = roomNumber
                            ).onSuccess {
                                Log.d("BookingViewModel", "Booking updated with hotel info")
                                onSuccess(bookingId)
                            }.onFailure { exception ->
                                _error.value = "Failed to update booking with hotel info: ${exception.message}"
                                Log.e("BookingViewModel", "Error updating booking: ${exception.message}")
                                // Vẫn gọi onSuccess để tiếp tục flow, mặc dù có lỗi khi cập nhật thông tin khách sạn
                                onSuccess(bookingId)
                            }
                        } else {
                            Log.e("BookingViewModel", "Hotel info incomplete: hotelId=$hotelId, roomTypeId=$roomTypeId, roomNumber=$roomNumber")
                            onSuccess(bookingId)
                        }
                    } else {
                        Log.d("BookingViewModel", "No hotel booked, continuing without hotel info")
                        onSuccess(bookingId)
                    }
                }.onFailure { exception ->
                    _error.value = "Failed to create booking: ${exception.message}"
                    Log.e("BookingViewModel", "Error creating booking: ${exception.message}")
                }
            } else {
                _error.value = "Trip information not available"
                Log.e("BookingViewModel", "Trip information not available")
            }
            
            _isLoading.value = false
        }
    }
}






