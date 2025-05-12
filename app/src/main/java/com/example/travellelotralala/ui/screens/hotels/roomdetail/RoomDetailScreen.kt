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
import com.example.travellelotralala.ui.theme.PrimaryOrange
import com.example.travellelotralala.ui.theme.DarkBackground
import com.example.travellelotralala.ui.theme.CardBackground
import com.example.travellelotralala.ui.theme.InfoBlue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomDetailScreen(
    hotelId: String,
    roomTypeId: String,
    onBackClick: () -> Unit,
    onBookRoom: (String, String, String, String, String) -> Unit, // hotelId, roomTypeId, roomNumber, hotelName, roomTypeName
    viewModel: RoomDetailViewModel = hiltViewModel()
) {
    val roomType by viewModel.roomType.collectAsState()
    val availableRooms by viewModel.availableRooms.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var selectedRoom by remember { mutableStateOf<String?>(null) }
    val hotelName by viewModel.hotelName.collectAsState()
    
    // Load room details when screen is first composed
    LaunchedEffect(hotelId, roomTypeId) {
        viewModel.loadRoomDetails(hotelId, roomTypeId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "Room Details",
                    ) 
                },
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
                ),
                // Thêm cài đặt để giảm padding
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        // Loại bỏ padding mặc định của Scaffold
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(DarkBackground)
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
                            color = InfoBlue
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
                            // Hiển thị thông báo nếu đang sử dụng dữ liệu mẫu
                            if (availableRooms.size == 5 && availableRooms[0].length == 3 && 
                                availableRooms[0][0].toString() == when(roomType?.id) {
                                    "standard" -> "1"
                                    "delux" -> "2"
                                    "business" -> "3"
                                    else -> "4"
                                }) {
                                Text(
                                    text = "Sample data: These rooms are for demonstration only",
                                    color = Color.Yellow,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            
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
                                    roomType?.let { roomType ->
                                        onBookRoom(
                                            hotelId, 
                                            roomTypeId, 
                                            roomNumber,
                                            hotelName ?: "Unknown Hotel",
                                            roomType.name
                                        )
                                    }
                                }
                            },
                            enabled = selectedRoom != null,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PrimaryOrange,
                                disabledContainerColor = PrimaryOrange.copy(alpha = 0.5f)
                            )
                        ) {
                            Text(
                                text = "Book Now",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
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







