package com.example.travellelotralala.ui.screens.mainscreens.savedscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travellelotralala.ui.components.TabItem
import com.example.travellelotralala.ui.components.TabSwitcher
import com.example.travellelotralala.ui.screens.mainscreens.savedscreen.components.SavedTripCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(
    onNavigateToTab: (TabItem) -> Unit = {},
    onTripClick: (String) -> Unit = {}, // Tham số này sẽ được sử dụng để điều hướng
    viewModel: SavedViewModel = hiltViewModel()
) {
    val savedTrips by viewModel.savedTrips.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E)) // Màu đen giống với HomeScreen
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Saved Places",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White, // Màu chữ trắng để hiển thị trên nền đen
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        
        // Search Bar
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
                color = Color.Black
            )
        )
        
        // Content
        if (isLoading) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFFFAA33))
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Error loading saved trips: $error",
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else if (savedTrips.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You haven't saved any trips yet",
                    color = Color.White,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(
                    items = savedTrips.filter { 
                        searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true) 
                    },
                    key = { it.id }
                ) { trip ->
                    SavedTripCard(
                        trip = trip,
                        onRemoveClick = { viewModel.removeFromSaved(trip.id) },
                        onClick = { onTripClick(trip.id) } // Gọi callback với ID của trip
                    )
                }
            }
        }
        
        // Bottom Navigation
        TabSwitcher(
            currentTab = TabItem.SAVED,
            onTabSelected = onNavigateToTab
        )
    }
}







