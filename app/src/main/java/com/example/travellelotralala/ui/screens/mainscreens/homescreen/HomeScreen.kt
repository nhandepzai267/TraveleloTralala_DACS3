package com.example.travellelotralala.ui.screens.mainscreens.homescreen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travellelotralala.model.Trip
import com.example.travellelotralala.ui.components.TabItem
import com.example.travellelotralala.ui.components.TabSwitcher
import com.example.travellelotralala.ui.screens.mainscreens.homescreen.components.CategoryItem

@Composable
fun HomeScreen(
    onDestinationClick: (String) -> Unit = {},
    onNavigateToTab: (TabItem) -> Unit = {},
    onSeeAllClick: () -> Unit = {}, // Thêm callback mới
    viewModel: HomeViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val trips by viewModel.trips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val categories by viewModel.categories.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // Top Bar with Menu, Greeting and Profile
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
            
            Text(
                text = "Hello Aldito",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
                    .clickable { }
            )
        }
        
        // Search Bar - Sửa lại để text hiển thị đúng
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp),
            placeholder = {
                Text(
                    text = "Try \"Hawaii\"",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    style = TextStyle(
                        platformStyle = PlatformTextStyle(includeFontPadding = false)
                    )
                )
            },

            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray,
                    modifier = Modifier.padding(end = 8.dp)
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                cursorColor = Color(0xFFFF8C00)
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.Black,
                lineHeight = 20.sp,
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            )
        )



        Spacer(modifier = Modifier.height(12.dp))

        // Categories
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            contentPadding = PaddingValues(8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryItem(
                    category = category,
                    onClick = { /* Handle category click */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(7.dp))

        // Featured Destinations với nút See All - Điều chỉnh vị trí
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(
                text = "Popular",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterStart)
            )
            
            Text(
                text = "See All",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFAA33),
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onSeeAllClick() } // Sử dụng callback mới
            )
        }

        // Phần hiển thị trips - Giảm chiều cao để không che TabSwitcher
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp), // Giảm từ 400dp xuống 350dp
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFFAA33))
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp), // Giảm từ 400dp xuống 350dp
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading trips: $error",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (trips.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp), // Giảm từ 400dp xuống 350dp
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No trips found",
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp) // Giảm từ 400dp xuống 350dp
                    .padding(horizontal = 8.dp),
                contentPadding = PaddingValues(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(trips) { trip ->
                    TripCard(
                        trip = trip,
                        onClick = { onDestinationClick(trip.id) }
                    )
                }
            }
        }
        
        // Thêm Spacer với weight để đẩy TabSwitcher xuống dưới cùng
        Spacer(modifier = Modifier.weight(1f))
        
        // Bottom Navigation using TabSwitcher
        TabSwitcher(
            currentTab = TabItem.HOME,
            onTabSelected = onNavigateToTab
        )
    }
}

@Composable
fun TripCard(
    trip: Trip,
    onClick: () -> Unit
) {
    // Thêm log để kiểm tra ID
    Log.d("TripCard", "Trip ID: ${trip.id}")
    
    Box(
        modifier = Modifier
            .width(300.dp)
            .height(350.dp) // Giảm từ 400dp xuống 350dp
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
    ) {
        // Background Image
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(trip.imageUrl)
                .crossfade(true)
                .build(),
            contentDescription = trip.name,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    androidx.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.8f)
                        ),
                        startY = 150f // Điều chỉnh gradient cho phù hợp với chiều cao mới
                    )
                )
        )
        
        // Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = trip.name,
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = trip.description,
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFAA33)
                ),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                modifier = Modifier
                    .height(48.dp)
                    .width(120.dp) // Đặt chiều rộng cố định
            ) {
                Text(
                    text = "Explore",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White
                )
            }
        }
    }
}

// TabSwitcher component is now in com.example.travellelotralala.ui.components.TabSwitcher

@Composable
fun FeaturedTripsSection(
    trips: List<Trip>,
    onTripClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Featured Trips",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        LazyRow(
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(trips.filter { it.featured }) { trip ->
                TripCard(
                    trip = trip,
                    onClick = { 
                        Log.d("HomeScreen", "Navigating to trip detail with ID: ${trip.id}")
                        onTripClick(trip.id) 
                    }
                )
            }
        }
    }
}

