package com.example.travellelotralala.ui.screens.booking

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    tripId: String,
    onBackClick: () -> Unit,
    onBookingComplete: (String) -> Unit, // Thay đổi thành (String) -> Unit
    viewModel: BookingViewModel = hiltViewModel()
) {
    val trip by viewModel.trip.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var guestCount by remember { mutableStateOf(1) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    // Xử lý hiển thị DatePicker
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(
                state = rememberDatePickerState(),
                title = { Text("Select Date") },
                headline = { Text("Select travel date") },
                showModeToggle = true,
                onDateSelected = { millis ->
                    millis?.let {
                        selectedDate = LocalDate.ofEpochDay(it / (24 * 60 * 60 * 1000))
                        showDatePicker = false
                    }
                }
            )
        }
    }
    
    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
    }

    // Sử dụng Scaffold để xử lý insets một cách chính xác
    Scaffold(
        topBar = {
            // TopAppBar với contentPadding = PaddingValues(0.dp) để loại bỏ padding mặc định
            TopAppBar(
                title = { 
                    Text(
                        "Checkout", 
                        color = Color.White
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E)
                ),
                // Loại bỏ padding mặc định
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        // Đặt windowInsets = WindowInsets(0, 0, 0, 0) để loại bỏ tất cả insets mặc định
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color(0xFF1E1E1E)
    ) { innerPadding ->
        // Nội dung chính
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFFAA33))
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (trip != null) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Thêm khả năng cuộn cho nội dung
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 80.dp) // Để tránh che nút Buy Ticket
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp)
                    ) {
                        // Nội dung không thay đổi
                        // ...                        
                        // Trip Info Card
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Trip Image
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(trip!!.imageUrl)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = trip!!.name,
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            // Trip Details
                            Column(verticalArrangement = Arrangement.Top) {
                                Text(
                                    text = trip!!.name,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(bottom = 4.dp) // Thêm padding cố định ở dưới tên
                                )
                                
                                Text(
                                    text = trip!!.location,
                                    fontSize = 14.sp,
                                    color = Color.LightGray
                                )
                            }
                        }
                        
                        // Non-refundable Notice
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(20.dp)
                                )
                                
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Column {
                                    Text(
                                        text = "Non-refundable",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                    
                                    Text(
                                        text = "You can not refund your payment.",
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Trip Details Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "Trip Details",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Duration
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CalendarMonth,
                                        contentDescription = "Duration",
                                        tint = Color(0xFFFFAA33),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = "Duration: ${trip!!.durationUnit}",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Rating
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Star,
                                        contentDescription = "Rating",
                                        tint = Color(0xFFFFAA33),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = "Rating: ${trip!!.rating}",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Category
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Category,
                                        contentDescription = "Category",
                                        tint = Color(0xFFFFAA33),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    
                                    Spacer(modifier = Modifier.width(8.dp))
                                    
                                    Text(
                                        text = "Category: ${trip!!.category}",
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                }
                                
                                // Hiển thị mô tả ngắn về chuyến đi
                                if (trip!!.description.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    Text(
                                        text = trip!!.description,
                                        fontSize = 14.sp,
                                        color = Color.LightGray,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))

                        // What's Included Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color(0xFF2A2A2A)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = "What's Included",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Hiển thị danh sách các dịch vụ được bao gồm dựa vào loại trip
                                val includedServices = when {
                                    trip!!.price > 300 -> listOf(
                                        "Flights", 
                                        "Hotel (5-star)", 
                                        "All Meals", 
                                        "Airport Transfer", 
                                        "Tour Guide",
                                        "All Activities"
                                    )
                                    trip!!.price > 200 -> listOf(
                                        "Hotel (4-star)", 
                                        "Breakfast & Dinner", 
                                        "Airport Transfer", 
                                        "Tour Guide",
                                        "Selected Activities"
                                    )
                                    else -> listOf(
                                        "Hotel (3-star)", 
                                        "Breakfast", 
                                        "Tour Guide",
                                        "Basic Activities"
                                    )
                                }
                                
                                includedServices.forEach { service ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color(0xFFFFAA33),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        
                                        Spacer(modifier = Modifier.width(8.dp))
                                        
                                        Text(
                                            text = service,
                                            fontSize = 14.sp,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Date Selection
                        Text(
                            text = "Select Travel Date",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color(0xFF2A2A2A)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFFFAA33))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = selectedDate?.format(
                                        DateTimeFormatter.ofPattern("EEE, d MMM yyyy")
                                    ) ?: "Select Date",
                                    color = if (selectedDate != null) Color.White else Color.Gray
                                )
                                
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = "Select Date",
                                    tint = Color(0xFFFFAA33)
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Guest Count
                        Text(
                            text = "Number of Travelers",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    width = 1.dp,
                                    color = Color.DarkGray,
                                    shape = RoundedCornerShape(28.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Decrease Button
                            IconButton(
                                onClick = { if (guestCount > 1) guestCount-- },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2A2A2A))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = Color.White
                                )
                            }
                            
                            // Guest Count
                            Text(
                                text = guestCount.toString(),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                            
                            // Increase Button
                            IconButton(
                                onClick = { guestCount++ },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2A2A2A))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color.White
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Price Summary
                        Text(
                            text = "Price Summary",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Price per person",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Text(
                                text = "$${trip!!.price}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Number of travelers",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                            
                            Text(
                                text = guestCount.toString(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Divider(color = Color.DarkGray)
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Tính và hiển thị tổng giá tiền
                        val totalPrice = trip!!.price * guestCount
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            
                            Text(
                                text = "$${totalPrice}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Thêm thông báo về những gì được bao gồm trong giá với icon
                        val includedServices = when {
                            trip!!.price > 300 -> "flights, 5-star hotel, all meals, airport transfer, tour guide, and all activities"
                            trip!!.price > 200 -> "4-star hotel, breakfast & dinner, airport transfer, tour guide, and selected activities"
                            else -> "3-star hotel, breakfast, tour guide, and basic activities"
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = Color(0xFFFFAA33),
                                modifier = Modifier
                                    .size(18.dp)
                                    .padding(top = 2.dp)
                            )
                            
                            Spacer(modifier = Modifier.width(8.dp))
                            
                            Text(
                                text = "The total price includes $includedServices.",
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Thêm khoảng trống ở cuối để tránh che nút
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                    
                    // Buy Ticket Button - Di chuyển ra ngoài và đặt ở dưới cùng
                    Button(
                        onClick = {
                            if (selectedDate != null) {
                                viewModel.createBooking(
                                    tripId = trip!!.id,
                                    numberOfTravelers = guestCount,
                                    travelDate = selectedDate!!,
                                    onSuccess = { bookingId -> // Nhận bookingId từ ViewModel
                                        onBookingComplete(bookingId) // Truyền bookingId
                                    }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp) // Tăng chiều cao của button
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFAA33),
                            disabledContainerColor = Color(0xFFFFAA33).copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(28.dp),
                        enabled = selectedDate != null
                    ) {
                        // Định dạng giá tiền với 2 chữ số thập phân và đảm bảo hiển thị đầy đủ
                        val formattedPrice = String.format("%.2f", trip!!.price * guestCount)
                        
                        Text(
                            text = "Buy Ticket - $$formattedPrice",
                            fontSize = 16.sp, // Giảm font size một chút
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePicker(
    state: DatePickerState,
    title: @Composable () -> Unit,
    headline: @Composable () -> Unit,
    showModeToggle: Boolean,
    onDateSelected: (Long?) -> Unit
) {
    val selectedDate = state.selectedDateMillis
    
    LaunchedEffect(selectedDate) {
        onDateSelected(selectedDate)
    }
    
    androidx.compose.material3.DatePicker(
        state = state,
        title = title,
        headline = headline,
        showModeToggle = showModeToggle
    )
}


















