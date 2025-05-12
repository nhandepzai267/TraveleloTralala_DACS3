package com.example.travellelotralala.ui.screens.hotels.roomdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.RoomType
import com.example.travellelotralala.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RoomDetailViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {
    
    private val _roomType = MutableStateFlow<RoomType?>(null)
    val roomType: StateFlow<RoomType?> = _roomType
    
    private val _availableRooms = MutableStateFlow<List<String>>(emptyList())
    val availableRooms: StateFlow<List<String>> = _availableRooms
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    fun loadRoomDetails(hotelId: String, roomTypeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                Log.d("RoomDetailViewModel", "Loading room details for hotel: $hotelId, roomType: $roomTypeId")
                
                // Lấy thông tin loại phòng từ repository
                val roomTypesResult = hotelRepository.getRoomTypes(hotelId)
                
                roomTypesResult.onSuccess { roomTypesData ->
                    // Tìm loại phòng theo ID
                    val roomTypeData = roomTypesData.find { it["id"] == roomTypeId }
                    
                    if (roomTypeData != null) {
                        _roomType.value = RoomType(
                            id = roomTypeData["id"] as String,
                            name = roomTypeData["name"] as String,
                            description = roomTypeData["description"] as String,
                            basePrice = roomTypeData["basePrice"] as String,
                            imageUrl = roomTypeData["imageUrl"] as String
                        )
                        
                        Log.d("RoomDetailViewModel", "Room type loaded: ${_roomType.value?.name}")
                        
                        // Lấy danh sách phòng trống
                        val availableRoomsResult = hotelRepository.getAvailableRooms(hotelId, roomTypeId)
                        
                        availableRoomsResult.onSuccess { rooms ->
                            _availableRooms.value = rooms
                            Log.d("RoomDetailViewModel", "Available rooms: ${rooms.size}")
                            
                            // Nếu không có phòng trống, tạo dữ liệu mẫu
                            if (rooms.isEmpty()) {
                                _availableRooms.value = getSampleRooms()
                                Log.d("RoomDetailViewModel", "Using sample rooms")
                            }
                        }.onFailure { exception ->
                            Log.e("RoomDetailViewModel", "Error loading available rooms: ${exception.message}")
                            _availableRooms.value = getSampleRooms()
                        }
                    } else {
                        // Nếu không tìm thấy loại phòng, sử dụng dữ liệu mẫu
                        Log.d("RoomDetailViewModel", "Room type not found, using sample data")
                        _roomType.value = getSampleRoomType(roomTypeId)
                        _availableRooms.value = getSampleRooms()
                    }
                }.onFailure { exception ->
                    Log.e("RoomDetailViewModel", "Error loading room types: ${exception.message}")
                    _error.value = "Failed to load room details: ${exception.message}"
                    
                    // Sử dụng dữ liệu mẫu nếu có lỗi
                    _roomType.value = getSampleRoomType(roomTypeId)
                    _availableRooms.value = getSampleRooms()
                }
                
            } catch (e: Exception) {
                Log.e("RoomDetailViewModel", "Exception: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private fun getSampleRoomType(roomTypeId: String): RoomType {
        return when (roomTypeId) {
            "standard" -> RoomType(
                id = "standard",
                name = "Standard Room",
                description = "Comfortable room with basic amenities. Includes free WiFi, TV, and private bathroom.",
                basePrice = "50",
                imageUrl = "https://res.cloudinary.com/dyqpkwzib/image/upload/v1747015187/hoi_an_hotel_1_1oc8ay.jpg"
            )
            "delux" -> RoomType(
                id = "delux",
                name = "Deluxe Room",
                description = "Spacious room with premium amenities and city view. Includes free WiFi, mini bar, and premium toiletries.",
                basePrice = "80",
                imageUrl = "https://res.cloudinary.com/dyqpkwzib/image/upload/v1747015187/hoi_an_hotel_1_1oc8ay.jpg"
            )
            "business" -> RoomType(
                id = "business",
                name = "Business Suite",
                description = "Luxury suite with separate living area and work space. Includes free WiFi, complimentary breakfast, and access to business lounge.",
                basePrice = "120",
                imageUrl = "https://res.cloudinary.com/dyqpkwzib/image/upload/v1747015187/hoi_an_hotel_1_1oc8ay.jpg"
            )
            else -> RoomType(
                id = roomTypeId,
                name = "Room Type",
                description = "Room description not available.",
                basePrice = "0",
                imageUrl = "https://res.cloudinary.com/dyqpkwzib/image/upload/v1747015187/hoi_an_hotel_1_1oc8ay.jpg"
            )
        }
    }
    
    private fun getSampleRooms(): List<String> {
        return listOf("101", "102", "103", "104", "105")
    }
}