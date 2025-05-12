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
    val updatedAt: Date = Date(),
    // Thêm các trường mới cho khách sạn và phòng
    val hotelId: String = "",
    val hotelName: String = "",
    val roomTypeId: String = "",
    val roomTypeName: String = "",
    val roomNumber: String = "",
    val hotelBooked: Boolean = false // Để biết người dùng đã chọn khách sạn/phòng chưa
)
