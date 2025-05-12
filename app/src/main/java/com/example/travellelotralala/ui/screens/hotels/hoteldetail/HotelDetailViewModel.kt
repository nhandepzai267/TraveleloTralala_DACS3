package com.example.travellelotralala.ui.screens.hotels.hoteldetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Hotel
import com.example.travellelotralala.model.RoomType
import com.example.travellelotralala.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HotelDetailViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {
    
    private val _hotel = MutableStateFlow<Hotel?>(null)
    val hotel: StateFlow<Hotel?> = _hotel
    
    private val _roomTypes = MutableStateFlow<List<RoomType>>(emptyList())
    val roomTypes: StateFlow<List<RoomType>> = _roomTypes
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Biến để kiểm soát việc gọi API nhiều lần
    private var isLoadingData = false
    
    fun loadHotelDetails(hotelId: String) {
        // Tránh gọi lại API nếu đang loading
        if (isLoadingData) return
        
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            isLoadingData = true
            
            try {
                Log.d("HotelDetailViewModel", "Loading hotel details for ID: $hotelId")
                
                // Lấy thông tin khách sạn
                val hotelResult = hotelRepository.getHotelById(hotelId)
                
                hotelResult.onSuccess { hotelData ->
                    Log.d("HotelDetailViewModel", "Successfully loaded hotel: ${hotelData["name"]}")
                    
                    _hotel.value = Hotel(
                        id = hotelData["id"] as String,
                        name = hotelData["name"] as String,
                        description = hotelData["description"] as String,
                        imageUrl = hotelData["imageUrl"] as String,
                        location = hotelData["location"] as String,
                        rating = hotelData["rating"] as Double
                    )
                    
                    // Lấy danh sách loại phòng từ repository
                    Log.d("HotelDetailViewModel", "Loading room types for hotel: $hotelId")
                    val roomTypesResult = hotelRepository.getRoomTypes(hotelId)
                    
                    roomTypesResult.onSuccess { roomTypesData ->
                        Log.d("HotelDetailViewModel", "Room types data received: ${roomTypesData.size} items")
                        
                        if (roomTypesData.isNotEmpty()) {
                            val mappedRoomTypes = roomTypesData.map { roomTypeData ->
                                val id = roomTypeData["id"] as String
                                val name = roomTypeData["name"] as String
                                val description = roomTypeData["description"] as String
                                val basePrice = roomTypeData["basePrice"] as String
                                val imageUrl = roomTypeData["imageUrl"] as String
                                
                                Log.d("HotelDetailViewModel", "Room type: $id, name: $name, price: $basePrice, imageUrl: $imageUrl")
                                
                                RoomType(
                                    id = id,
                                    name = name,
                                    description = description,
                                    basePrice = basePrice,
                                    imageUrl = imageUrl
                                )
                            }
                            
                            _roomTypes.value = mappedRoomTypes
                            Log.d("HotelDetailViewModel", "Set room types: ${mappedRoomTypes.size}")
                        } else {
                            Log.d("HotelDetailViewModel", "No room types found, using sample data")
                            // Sử dụng dữ liệu mẫu nếu không tìm thấy dữ liệu
                            _roomTypes.value = getSampleRoomTypes()
                        }
                    }.onFailure { exception ->
                        Log.e("HotelDetailViewModel", "Error loading room types: ${exception.message}")
                        // Sử dụng dữ liệu mẫu nếu có lỗi
                        _roomTypes.value = getSampleRoomTypes()
                    }
                    
                }.onFailure { exception ->
                    Log.e("HotelDetailViewModel", "Error loading hotel: ${exception.message}")
                    _error.value = "Failed to load hotel details: ${exception.message}"
                }
                
            } catch (e: Exception) {
                Log.e("HotelDetailViewModel", "Exception: ${e.message}")
                _error.value = e.message
            } finally {
                _isLoading.value = false
                isLoadingData = false
            }
        }
    }
    
    // Tách dữ liệu mẫu thành một hàm riêng để dễ quản lý
    private fun getSampleRoomTypes(): List<RoomType> {
        return listOf(
            RoomType(
                id = "standard",
                name = "Standard Room",
                description = "Comfortable room with basic amenities. Includes free WiFi, TV, and private bathroom.",
                basePrice = "50",
                imageUrl = "https://res.cloudinary.com/dyqpkwzib/image/upload/v1747015187/hoi_an_hotel_1_1oc8ay.jpg"
            ),
            RoomType(
                id = "delux",
                name = "Deluxe Room",
                description = "Spacious room with premium amenities and city view. Includes free WiFi, mini bar, and premium toiletries.",
                basePrice = "80",
                imageUrl = "https://res.cloudinary.com/dyqpkwzib/image/upload/v1747015187/hoi_an_hotel_1_1oc8ay.jpg"
            ),
            RoomType(
                id = "business",
                name = "Business Suite",
                description = "Luxury suite with separate living area and work space. Includes free WiFi, complimentary breakfast, and access to business lounge.",
                basePrice = "120",
                imageUrl = "https://res.cloudinary.com/dyqpkwzib/image/upload/v1747015187/hoi_an_hotel_1_1oc8ay.jpg"
            )
        )
    }
}








