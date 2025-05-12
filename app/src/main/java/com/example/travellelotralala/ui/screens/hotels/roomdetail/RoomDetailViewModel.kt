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
    
    // Thêm hotelName để hiển thị tên khách sạn
    private val _hotelName = MutableStateFlow<String?>(null)
    val hotelName: StateFlow<String?> = _hotelName
    
    fun loadRoomDetails(hotelId: String, roomTypeId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                // Lấy thông tin khách sạn
                hotelRepository.getHotelById(hotelId).onSuccess { hotelData ->
                    // Truy cập thuộc tính name từ Map
                    _hotelName.value = hotelData["name"] as? String
                }
                
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
                            if (rooms.isEmpty()) {
                                // Nếu không có phòng trống thực tế, tạo dữ liệu mẫu
                                _availableRooms.value = getSampleRooms(roomTypeId)
                                Log.d("RoomDetailViewModel", "No rooms found, using sample rooms")
                            } else {
                                _availableRooms.value = rooms
                                Log.d("RoomDetailViewModel", "Available rooms: ${rooms.size}")
                            }
                        }.onFailure { exception ->
                            Log.e("RoomDetailViewModel", "Error loading available rooms: ${exception.message}")
                            _availableRooms.value = getSampleRooms(roomTypeId)
                            Log.d("RoomDetailViewModel", "Using sample rooms due to error")
                        }
                    } else {
                        // Nếu không tìm thấy loại phòng, sử dụng dữ liệu mẫu
                        Log.d("RoomDetailViewModel", "Room type not found, using sample data")
                        _roomType.value = getSampleRoomType(roomTypeId)
                        _availableRooms.value = getSampleRooms(roomTypeId)
                    }
                }.onFailure { exception ->
                    Log.e("RoomDetailViewModel", "Error loading room types: ${exception.message}")
                    _error.value = "Failed to load room details: ${exception.message}"
                    
                    // Sử dụng dữ liệu mẫu nếu có lỗi
                    _roomType.value = getSampleRoomType(roomTypeId)
                    _availableRooms.value = getSampleRooms(roomTypeId)
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
    
    private fun getSampleRooms(roomTypeId: String): List<String> {
        // Tạo số phòng dựa trên loại phòng
        val prefix = when (roomTypeId) {
            "standard" -> "1"
            "delux" -> "2"
            "business" -> "3"
            else -> "4"
        }
        
        // Tạo 5 phòng mẫu
        return (1..5).map { "$prefix${it.toString().padStart(2, '0')}" }
    }
}



