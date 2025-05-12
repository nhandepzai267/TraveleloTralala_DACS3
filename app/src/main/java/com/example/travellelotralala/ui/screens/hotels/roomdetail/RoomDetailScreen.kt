package com.example.travellelotralala.ui.screens.hotels.roomdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.travellelotralala.model.RoomType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    hotelId: String,
    roomTypeId: String,
    onBackClick: () -> Unit,
    onBookRoom: (String, String, String) -> Unit, // hotelId, roomTypeId, roomNumber
    viewModel: RoomDetailViewModel = hiltViewModel()
) {
    val roomType by viewModel.roomType.collectAsState()
    val availableRooms by viewModel.availableRooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedRoom by remember { mutableStateOf<String?>(null) }
    
    // Load room details when screen is first composed
    LaunchedEffect(hotelId, roomTypeId) {
        viewModel.loadRoomDetails(hotelId, roomTypeId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Room Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E1E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFF1E1E1E))
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.White
                    )
                }
                error != null -> {
                    Text(
                        text = "Error: ${error ?: "Unknown error"}",
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                roomType != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(16.dp)
                    ) {
                        // Room Image
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(roomType!!.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = roomType!!.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Room Name
                        Text(
                            text = roomType!!.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Price
                        Text(
                            text = "$${roomType!!.basePrice} / night",
                            fontSize = 18.sp,
                            color = Color(0xFF4FC3F7)
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Description
                        Text(
                            text = "Description",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = roomType!!.description,
                            fontSize = 16.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Available Rooms
                        Text(
                            text = "Available Rooms",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        if (availableRooms.isEmpty()) {
                            Text(
                                text = "No rooms available for this type",
                                color = Color.Red.copy(alpha = 0.8f)
                            )
                        } else {
                            // Room selection
                            Column {
                                availableRooms.forEach { roomNumber ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = selectedRoom == roomNumber,
                                            onClick = { selectedRoom = roomNumber },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = Color(0xFF4FC3F7),
                                                unselectedColor = Color.White
                                            )
                                        )
                                        
                                        Text(
                                            text = "Room $roomNumber",
                                            color = Color.White,
                                            modifier = Modifier.padding(start = 8.dp)
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(24.dp))
                        
                        // Book Button
                        Button(
                            onClick = {
                                selectedRoom?.let { roomNumber ->
                                    onBookRoom(hotelId, roomTypeId, roomNumber)
                                }
                            },
                            enabled = selectedRoom != null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50),
                                disabledContainerColor = Color.Gray
                            )
                        ) {
                            Text(
                                text = "Book Now",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }
                    }
                }
                else -> {
                    Text(
                        text = "Room details not found",
                        color = Color.White,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
            }
        }
    }
}