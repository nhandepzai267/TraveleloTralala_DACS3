package com.example.travellelotralala.ui.screens.booking

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Hotel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun BookingConfirmationScreen(
    bookingId: String,
    onDoneClick: () -> Unit,
    viewModel: BookingConfirmationViewModel = hiltViewModel()
) {
    // Sửa cách sử dụng collectAsState để tránh lỗi delegate
    val bookingDetails = viewModel.bookingDetails.collectAsState().value
    val tripLocation = viewModel.tripLocation.collectAsState().value ?: ""  // Xử lý null
    val isLoading = viewModel.isLoading.collectAsState().value
    val error = viewModel.error.collectAsState().value
    
    // Load booking details
    LaunchedEffect(bookingId) {
        if (bookingDetails == null) {
            viewModel.loadBookingDetails(bookingId)
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color(0xFFFFAA33)
            )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else if (error != null) {
            Text(
                text = "Error: $error",
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            )
        } else if (bookingDetails != null) {
            // Lấy thông tin từ bookingDetails
            val booking = bookingDetails
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                // Success Icon
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = Color.White,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Success Message
                Text(
                    text = "HAPPY JOURNEY",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                
                Text(
                    text = "Your Ticket Booked Successfully",
                    fontSize = 18.sp,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Ticket Details Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        // Ticket Header
                        Text(
                            text = "YOUR TICKET DETAILS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFAA33),
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        
                        // Trip Image and Basic Info
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            // Trip Image
                            AsyncImage(
                                model = booking.tripImageUrl,
                                contentDescription = booking.tripName,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Trip Info
                            Column {
                                Text(
                                    text = booking.tripName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = "Location",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(4.dp))
                                    
                                    Text(
                                        text = tripLocation,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                                
                                // Booking ID
                                Text(
                                    text = "Booking ID: ${bookingId.takeLast(8).uppercase()}",
                                    fontSize = 12.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                        
                        Divider()
                        
                        // Travel Details
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Travel Date
                            Column {
                                Text(
                                    text = "TRAVEL DATE",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                
                                Text(
                                    text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(booking.travelDate),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                            
                            // Number of Travelers
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "TRAVELERS",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                
                                Text(
                                    text = "${booking.numberOfTravelers} ${if (booking.numberOfTravelers > 1) "persons" else "person"}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Black
                                )
                            }
                        }
                        
                        Divider()
                        
                        // Contact Information
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                        ) {
                            Text(
                                text = "CONTACT INFORMATION",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                // Contact Icon
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFA07A).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Contact",
                                        tint = Color(0xFFFFA07A),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                // Contact Details
                                Column {
                                    val contactName = booking.contactInfo["name"] as? String ?: ""
                                    val contactEmail = booking.contactInfo["email"] as? String ?: ""
                                    
                                    Text(
                                        text = contactName.ifEmpty { "Not provided" },
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Black
                                    )
                                    
                                    Text(
                                        text = contactEmail,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        
                        Divider()
                        
                        // Thêm phần hiển thị thông tin khách sạn nếu có
                        if (booking.hotelBooked) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp)
                            ) {
                                Text(
                                    text = "HOTEL INFORMATION",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 8.dp)
                                ) {
                                    // Hotel Icon
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF4CAF50).copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Hotel,
                                            contentDescription = "Hotel",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    // Hotel Details
                                    Column {
                                        Text(
                                            text = booking.hotelName,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Medium,
                                            color = Color.Black
                                        )
                                        
                                        Text(
                                            text = "${booking.roomTypeName} - Room ${booking.roomNumber}",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                            
                            Divider()
                        }
                        
                        // Total Price
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "TOTAL PRICE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            
                            Text(
                                text = "$${String.format("%.2f", booking.totalPrice)}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFAA33)
                            )
                        }
                        
                        // Done Button
                        Button(
                            onClick = onDoneClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFFFAA33)
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(
                                text = "Done",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}







