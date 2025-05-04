
package com.example.travellelotralala.repository

import com.example.travellelotralala.model.Trip
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

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
                    imageUrl = "https://images.unsplash.com/photo-1537996194471-e657df975ab4?q=80&w=1000",
                    location = "Bali, Indonesia",
                    rating = 4.8f,
                    price = 120.0,
                    featured = true
                ),
                Trip(
                    id = "maldives",
                    name = "Maldives",
                    description = "Experience the crystal clear waters and white sandy beaches of Maldives.",
                    imageUrl = "https://images.unsplash.com/photo-1573843981267-be1999ff37cd?q=80&w=1000",
                    location = "Maldives",
                    rating = 4.9f,
                    price = 350.0
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
}

