package com.example.travellelotralala.ui.screens.hotels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.travellelotralala.model.Hotel
import com.example.travellelotralala.repository.HotelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import android.util.Log

@HiltViewModel
class HotelsViewModel @Inject constructor(
    private val hotelRepository: HotelRepository
) : ViewModel() {
    
    private val _hotels = MutableStateFlow<List<Hotel>>(emptyList())
    val hotels: StateFlow<List<Hotel>> = _hotels
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    fun loadHotels(location: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = hotelRepository.getHotelsByLocation(location)
                
                result.onSuccess { hotelsData ->
                    _hotels.value = hotelsData.map { hotelMap ->
                        val hotel = Hotel(
                            id = hotelMap["id"] as String,
                            name = hotelMap["name"] as String,
                            description = hotelMap["description"] as String,
                            imageUrl = hotelMap["imageUrl"] as String,
                            location = hotelMap["location"] as String,
                            rating = hotelMap["rating"] as Double
                        )
                        Log.d("HotelsViewModel", "Loaded hotel: ${hotel.name}, imageUrl: ${hotel.imageUrl}")
                        hotel
                    }
                }.onFailure { exception ->
                    _error.value = exception.message
                    Log.e("HotelsViewModel", "Error loading hotels: ${exception.message}")
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("HotelsViewModel", "Exception loading hotels: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
