package com.example.travellelotralala.repository

import com.example.travellelotralala.model.Trip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class SavedTripsRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val tripsRepository: TripRepository
) {
    private val savedTripsCollection = firestore.collection("saved_trips")
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    // Lấy userId hiện tại (uid)
    private fun getCurrentUserId(): String {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("SavedTripsRepository", "User not logged in")
            throw IllegalStateException("User not logged in")
        }
        return currentUser.uid
    }
    
    // Lưu một trip
    suspend fun saveTrip(trip: Trip): Result<Unit> {
        return try {
            if (auth.currentUser == null) {
                Log.e("SavedTripsRepository", "User not logged in")
                return Result.failure(IllegalStateException("User not logged in"))
            }
            
            val userId = auth.currentUser!!.uid
            val savedTripId = "$userId-${trip.id}"
            
            Log.d("SavedTripsRepository", "Saving trip with ID: ${trip.id} for user: $userId")
            
            val savedTrip = hashMapOf(
                "userId" to userId,
                "tripId" to trip.id,
                "savedAt" to System.currentTimeMillis()
            )
            
            try {
                savedTripsCollection.document(savedTripId).set(savedTrip).await()
                Log.d("SavedTripsRepository", "Trip saved successfully")
                Result.success(Unit)
            } catch (e: Exception) {
                Log.e("SavedTripsRepository", "Error saving trip: ${e.message}")
                Result.failure(e)
            }
        } catch (e: Exception) {
            Log.e("SavedTripsRepository", "Error in saveTrip: ${e.message}")
            Result.failure(e)
        }
    }
    
    // Xóa một trip đã lưu
    suspend fun removeTrip(tripId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId()
            val savedTripId = "$userId-$tripId"
            
            Log.d("SavedTripsRepository", "Removing trip with ID: $tripId for user: $userId")
            
            savedTripsCollection.document(savedTripId).delete().await()
            Log.d("SavedTripsRepository", "Trip removed successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SavedTripsRepository", "Error removing trip: ${e.message}")
            Result.failure(e)
        }
    }
    
    // Kiểm tra xem một trip đã được lưu hay chưa
    suspend fun isTripSaved(tripId: String): Boolean {
        return try {
            val userId = getCurrentUserId()
            val savedTripId = "$userId-$tripId"
            
            val document = savedTripsCollection.document(savedTripId).get().await()
            document.exists()
        } catch (e: Exception) {
            false
        }
    }
    
    // Lấy danh sách tất cả các trip đã lưu
    fun getSavedTrips(): Flow<List<Trip>> = callbackFlow {
        try {
            val userId = getCurrentUserId()
            
            val listener = savedTripsCollection
                .whereEqualTo("userId", userId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(emptyList())
                        return@addSnapshotListener
                    }
                    
                    if (snapshot != null) {
                        // Lấy danh sách tripId từ các document
                        val tripIds = snapshot.documents.mapNotNull { it.getString("tripId") }
                        
                        // Nếu không có trip nào được lưu
                        if (tripIds.isEmpty()) {
                            trySend(emptyList())
                            return@addSnapshotListener
                        }
                        
                        // Sử dụng scope đã định nghĩa để gọi hàm suspend
                        scope.launch {
                            try {
                                // Lấy thông tin chi tiết của từng trip
                                val result = tripsRepository.getAllTrips()
                                result.fold(
                                    onSuccess = { allTrips ->
                                        val savedTrips = allTrips.filter { trip -> tripIds.contains(trip.id) }
                                        trySend(savedTrips)
                                    },
                                    onFailure = {
                                        trySend(emptyList())
                                    }
                                )
                            } catch (e: Exception) {
                                trySend(emptyList())
                            }
                        }
                    }
                }
            
            awaitClose { 
                listener.remove() 
            }
        } catch (e: Exception) {
            trySend(emptyList())
            awaitClose { }
        }
    }
}







