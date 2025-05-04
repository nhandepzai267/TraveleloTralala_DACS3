package com.example.travellelotralala.model

data class Trip(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val imageUrl: String = "", // URL hình ảnh từ internet
    val location: String = "",
    val rating: Float = 0f,
    val price: Double = 0.0,
    val featured: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)