package com.example.travellelotralala.model

import java.util.*

data class Notification(
    val notiId: String = "",
    val notiTitle: String = "",
    val notiDescription: String = "",
    val notiContent: String = "",
    val notiImage: String = "",
    val contentImage: List<String> = emptyList(),
    val notiType: String = "",
    val createdAt: Date? = null,
    val relatedTripId: String = "" // Thêm trường mới để liên kết với Trip
)