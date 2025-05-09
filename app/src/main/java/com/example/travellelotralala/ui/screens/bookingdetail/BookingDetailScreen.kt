package com.example.travellelotralala.ui.screens.bookingdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travellelotralala.ui.screens.bookingdetail.components.DetailItem
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailScreen(
    bookingId: String,
    onBackClick: () -> Unit,
    viewModel: BookingDetailViewModel = hiltViewModel()
) {
    val booking by viewModel.booking.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Load booking details
    LaunchedEffect(bookingId) {
        viewModel.loadBookingDetails(bookingId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFFFFAA33)
            )
        } else if (error != null) {
            Text(
                text = "Error: $error",
                color = Color.Red,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else if (booking != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E1E1E))
            ) {
                // Header with image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                ) {
                    // Trip Image
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(booking!!.tripImageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = booking!!.tripName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Back Button
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.7f))
                            .align(Alignment.TopStart)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.Black
                        )
                    }

                    // Booking status
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF1E5631).copy(alpha = 0.8f))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Text(
                            text = booking!!.status,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Trip name
                    Text(
                        text = booking!!.tripName,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Booking details section
                    Text(
                        text = "Booking Details",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Booking ID
                    DetailItem(
                        label = "Booking ID",
                        value = booking!!.id
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Travel date
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = "Travel date",
                            tint = Color(0xFFFFAA33),
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = "Travel Date",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Text(
                                text = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(booking!!.travelDate),
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Number of travelers
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Travelers",
                            tint = Color(0xFFFFAA33),
                            modifier = Modifier.size(20.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Column {
                            Text(
                                text = "Travelers",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Text(
                                text = "${booking!!.numberOfTravelers} person(s)",
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Contact information section
                    Text(
                        text = "Contact Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Contact name
                    DetailItem(
                        label = "Name",
                        value = booking!!.contactInfo["name"] ?: "N/A"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Contact email
                    DetailItem(
                        label = "Email",
                        value = booking!!.contactInfo["email"] ?: "N/A"
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Payment information section
                    Text(
                        text = "Payment Information",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Total price
                    DetailItem(
                        label = "Total Price",
                        value = "$${String.format("%.2f", booking!!.totalPrice)}"
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Payment status
                    DetailItem(
                        label = "Payment Status",
                        value = booking!!.paymentStatus
                    )
                }
            }
        }
    }
}

