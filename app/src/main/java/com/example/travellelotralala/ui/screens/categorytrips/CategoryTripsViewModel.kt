package com.example.travellelotralala.ui.screens.categorytrips

import android.util.Log
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
class CategoryTripsViewModel @Inject constructor(
    private val tripRepository: TripRepository
) : ViewModel() {
    
    private val _trips = MutableStateFlow<List<Trip>>(emptyList())
    val trips: StateFlow<List<Trip>> = _trips
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    fun loadTripsByCategory(categoryName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val result = tripRepository.getAllTrips()
                result.onSuccess { allTrips ->
                    // Lọc các chuyến đi theo danh mục sử dụng trường category
                    val filteredTrips = allTrips.filter { trip ->
                        // Ưu tiên sử dụng trường category nếu có
                        if (trip.category.isNotEmpty()) {
                            // So sánh trực tiếp với categoryName
                            trip.category.equals(categoryName, ignoreCase = true)
                        } else {
                            // Fallback: Sử dụng phương pháp lọc theo từ khóa nếu không có trường category
                            when (categoryName) {
                                "Mountains" -> trip.description.contains("mountain", ignoreCase = true)
                                "Beaches" -> trip.description.contains("beach", ignoreCase = true)
                                "Historical" -> trip.description.contains("historical", ignoreCase = true) || 
                                               trip.description.contains("history", ignoreCase = true)
                                "Parks" -> trip.description.contains("park", ignoreCase = true) || 
                                          trip.description.contains("nature", ignoreCase = true)
                                "Food" -> trip.description.contains("food", ignoreCase = true) || 
                                         trip.description.contains("cuisine", ignoreCase = true)
                                "Activities" -> trip.description.contains("activity", ignoreCase = true) || 
                                              trip.description.contains("adventure", ignoreCase = true)
                                else -> false
                            }
                        }
                    }
                    
                    _trips.value = filteredTrips
                    Log.d("CategoryTripsViewModel", "Loaded ${filteredTrips.size} trips for category: $categoryName")
                }.onFailure { exception ->
                    _error.value = exception.message
                    Log.e("CategoryTripsViewModel", "Error loading trips: ${exception.message}")
                }
            } catch (e: Exception) {
                _error.value = e.message
                Log.e("CategoryTripsViewModel", "Exception loading trips: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
