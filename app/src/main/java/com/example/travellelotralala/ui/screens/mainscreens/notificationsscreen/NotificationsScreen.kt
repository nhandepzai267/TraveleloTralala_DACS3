package com.example.travellelotralala.ui.screens.mainscreens.notificationsscreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.travellelotralala.ui.components.TabItem
import com.example.travellelotralala.ui.components.TabSwitcher

@Composable
fun NotificationsScreen(
    onNavigateToTab: (TabItem) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E1E))
    ) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Notifications",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.align(Alignment.CenterStart)
            )
        }
        
        // Content
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Your notifications will appear here",
                color = Color.White,
                fontSize = 16.sp
            )
        }
        
        // Bottom Navigation
        TabSwitcher(
            currentTab = TabItem.NOTIFICATIONS,
            onTabSelected = onNavigateToTab
        )
    }
}