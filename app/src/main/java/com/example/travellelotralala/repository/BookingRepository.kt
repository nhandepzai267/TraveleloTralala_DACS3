package com.example.travellelotralala.repository

import com.example.travellelotralala.model.Booking
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    
    private val bookingsCollection = firestore.collection("bookings")
    
    suspend fun createBooking(
        tripId: String,
        tripName: String,
        tripImageUrl: String,
        numberOfTravelers: Int,
        totalPrice: Double,
        travelDate: LocalDate
    ): Result<String> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(Exception("User not authenticated"))
            
            val bookingId = "${currentUser.uid}-$tripId-${System.currentTimeMillis()}"
            
            val booking = Booking(
                id = bookingId,
                userId = currentUser.uid,
                tripId = tripId,
                tripName = tripName,
                tripImageUrl = tripImageUrl,
                numberOfTravelers = numberOfTravelers,
                totalPrice = totalPrice,
                bookingDate = Date(),
                travelDate = Date.from(
                    travelDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
                ),
                status = "CONFIRMED",
                paymentStatus = "PAID"
            )
            
            bookingsCollection.document(bookingId).set(booking).await()
            
            Result.success(bookingId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserBookings(): Result<List<Booking>> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(Exception("User not authenticated"))
            
            val snapshot = bookingsCollection
                .whereEqualTo("userId", currentUser.uid)
                .orderBy("bookingDate", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
            
            val bookings = snapshot.documents.mapNotNull { document ->
                document.toObject(Booking::class.java)
            }
            
            Result.success(bookings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getBookingById(bookingId: String): Result<Booking> {
        return try {
            val document = bookingsCollection.document(bookingId).get().await()
            val booking = document.toObject(Booking::class.java)
                ?: return Result.failure(Exception("Booking not found"))
            
            Result.success(booking)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun cancelBooking(bookingId: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser
                ?: return Result.failure(Exception("User not authenticated"))
            
            val document = bookingsCollection.document(bookingId).get().await()
            val booking = document.toObject(Booking::class.java)
                ?: return Result.failure(Exception("Booking not found"))
            
            if (booking.userId != currentUser.uid) {
                return Result.failure(Exception("You don't have permission to cancel this booking"))
            }
            
            bookingsCollection.document(bookingId)
                .update("status", "CANCELLED")
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}