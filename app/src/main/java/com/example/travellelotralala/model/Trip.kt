package com.example.travellelotralala.model

data class Trip(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val details: String = "", // Chi tiết kết hợp với danh sách dịch vụ
    val imageUrl: String = "",
    val location: String = "",
    val rating: Float = 0f,
    val price: Double = 0.0,
    val featured: Boolean = false,
    val category: String = "",
    val durationUnit: String = "", // Ví dụ: "2 Nights 3 Days"
    val createdAt: Long = System.currentTimeMillis()
)
