package com.example.travellelotralala.ui.screens.tripdetail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.LocationOn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travellelotralala.model.Trip

@Composable
fun TripDetailScreen(
    tripId: String,
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit = {},
    viewModel: TripDetailViewModel = hiltViewModel()
) {
    val trip by viewModel.trip.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    
    LaunchedEffect(tripId) {
        viewModel.loadTrip(tripId)
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
        } else if (trip != null) {
            TripDetailContent(
                trip = trip!!,
                isFavorite = isSaved,
                onFavoriteClick = { viewModel.toggleSaveTrip(onNavigateToLogin) },
                onBackClick = onBackClick
            )
        }
    }
}

@Composable
fun TripDetailContent(
    trip: Trip,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Header Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(trip.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = trip.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp), // Tăng từ 300.dp lên 350.dp
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
        
        // Content Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.6f) // Thay đổi từ 0.75f xuống 0.6f để hiển thị thấp hơn
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            // Trip Name and Location
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = trip.name,
                        fontSize = 24.sp,
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
                        Text(
                            text = trip.location,
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
                
                // Favorite Button
                IconButton(
                    onClick = { 
                        Log.d("TripDetailScreen", "Bookmark button clicked, current state: $isFavorite")
                        onFavoriteClick() 
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFF4E6))
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = if (isFavorite) "Remove from saved" else "Save trip",
                        tint = Color(0xFFFFAA33),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Description Section
            Text(
                text = "Description",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = trip.details,
                fontSize = 14.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Price, Rating, Duration Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp), // Thêm padding ngang cho toàn bộ Row
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Price - Đặt ở bên trái
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f) // Sử dụng weight thay vì padding
                ) {
                    Text(
                        text = "PRICE",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$${trip.price.toInt()}/person",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFAA33)
                    )
                }
                
                // Rating - Đặt ở giữa
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f) // Sử dụng weight
                ) {
                    Text(
                        text = "RATING",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = if (trip.rating > 0) "${trip.rating}/10" else "N/A",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                
                // Duration - Đặt ở bên phải
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f) // Sử dụng weight
                ) {
                    Text(
                        text = "DURATION", // Giữ nguyên text
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1 // Đảm bảo không xuống dòng
                    )
                    Text(
                        text = trip.durationUnit,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        maxLines = 1 // Đảm bảo không xuống dòng
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Book Now Button
            Button(
                onClick = { /* Handle booking */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFAA33)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "BOOK NOW",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}










