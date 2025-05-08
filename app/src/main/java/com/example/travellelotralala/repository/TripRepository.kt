
package com.example.travellelotralala.repository

import com.example.travellelotralala.model.Trip
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import android.util.Log

@Singleton
class TripRepository @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()
    private val tripsCollection = firestore.collection("trips")
    
    suspend fun getAllTrips(): Result<List<Trip>> {
        return try {
            val querySnapshot = tripsCollection.get().await()
            val trips = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Trip::class.java)
            }
            Result.success(trips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun addSampleTrips(): Result<Unit> {
        return try {
            val trips = listOf(
                Trip(
                    id = "pantai_dreamland",
                    name = "Pantai Dreamland",
                    description = "The beauty of Dreamland beach is almost similar to Bali's Balangan beach and Jimbaran's Tegal Wangi beach. Check it out!",
                    details = "Dreamland beach there are white coral rocks that surround the beach, this creates a beautiful view of its own. Include Flights Transfer Accommodation",
                    imageUrl = "https://images.unsplash.com/photo-1537996194471-e657df975ab4?q=80&w=1000",
                    location = "Bali, Indonesia",
                    rating = 4.8f,
                    price = 120.0,
                    featured = true,
                    category = "Beaches",
                    durationUnit = "2 Days 1 Night"
                ),
                Trip(
                    id = "maldives",
                    name = "Maldives",
                    description = "Experience the crystal clear waters and white sandy beaches of Maldives.",
                    details = "Maldives offers pristine white beaches, crystal clear waters and luxurious overwater bungalows. Include Flights Transfer Accommodation Meals",
                    imageUrl = "https://images.unsplash.com/photo-1573843981267-be1999ff37cd?q=80&w=1000",
                    location = "Maldives",
                    rating = 4.9f,
                    price = 350.0,
                    category = "Beaches",
                    durationUnit = "5 Days 4 Nights"
                ),
                Trip(
                    id = "bali",
                    name = "Bali",
                    description = "Explore the beautiful island of Bali with its rich culture and stunning landscapes.",
                    imageUrl = "https://images.unsplash.com/photo-1558005530-a7958896ec60?q=80&w=1000",
                    location = "Bali, Indonesia",
                    rating = 4.7f,
                    price = 180.0
                ),
                Trip(
                    id = "hawaii",
                    name = "Hawaii",
                    description = "Discover the paradise islands of Hawaii with volcanic landscapes and beautiful beaches.",
                    imageUrl = "https://images.unsplash.com/photo-1542259009477-d625272157b7?q=80&w=1000",
                    location = "Hawaii, USA",
                    rating = 4.6f,
                    price = 280.0
                )
            )
            
            trips.forEach { trip ->
                tripsCollection.document(trip.id).set(trip).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTripById(tripId: String): Result<Trip> {
        return try {
            Log.d("TripRepository", "Getting trip with field id: $tripId")
            
            // Tìm theo trường id trong dữ liệu
            val querySnapshot = tripsCollection.whereEqualTo("id", tripId).get().await()
            
            if (!querySnapshot.isEmpty) {
                val trip = querySnapshot.documents[0].toObject(Trip::class.java)
                if (trip != null) {
                    Log.d("TripRepository", "Trip found by field 'id': ${trip.name}")
                    Result.success(trip)
                } else {
                    Log.e("TripRepository", "Trip is null after conversion")
                    Result.failure(Exception("Trip data is invalid"))
                }
            } else {
                Log.e("TripRepository", "Trip not found with field id: $tripId")
                Result.failure(Exception("Trip not found"))
            }
        } catch (e: Exception) {
            Log.e("TripRepository", "Exception: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun addTrip(trip: Trip): Result<Trip> {
        return try {
            // Sử dụng trường id làm Document ID
            tripsCollection.document(trip.id).set(trip).await()
            Result.success(trip)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}





