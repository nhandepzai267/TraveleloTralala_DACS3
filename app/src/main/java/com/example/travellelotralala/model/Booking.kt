package com.example.travellelotralala.model

import java.util.*

data class Booking(
    val id: String = "",
    val userId: String = "",
    val tripId: String = "",
    val tripName: String = "",
    val tripImageUrl: String = "",
    val numberOfTravelers: Int = 0,
    val totalPrice: Double = 0.0,
    val bookingDate: Date = Date(),
    val travelDate: Date = Date(),
    val status: String = "PENDING", // PENDING, CONFIRMED, CANCELLED, COMPLETED
    val paymentStatus: String = "UNPAID", // UNPAID, PAID
    val contactInfo: Map<String, String> = emptyMap(),
    val specialRequests: String = "",
    val updatedAt: Date = Date()
)