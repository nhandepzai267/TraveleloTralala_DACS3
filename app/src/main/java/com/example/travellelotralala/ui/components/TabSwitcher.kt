package com.example.travellelotralala.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

enum class TabItem(val icon: ImageVector, val contentDescription: String) {
    HOME(Icons.Default.Home, "Home"),
    SAVED(Icons.Default.Bookmark, "Saved"),
    BOOKINGS(Icons.Default.ConfirmationNumber, "Bookings"),
    NOTIFICATIONS(Icons.Default.Notifications, "Notifications")
}

@Composable
fun TabSwitcher(
    currentTab: TabItem,
    onTabSelected: (TabItem) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabItem.values().forEach { tab ->
                TabIcon(
                    tab = tab,
                    isSelected = tab == currentTab,
                    onClick = { onTabSelected(tab) }
                )
            }
        }
    }
}

@Composable
private fun TabIcon(
    tab: TabItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) Color.LightGray.copy(alpha = 0.3f) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = tab.icon,
            contentDescription = tab.contentDescription,
            tint = if (isSelected) Color.White else Color.Gray,
            modifier = Modifier.size(24.dp)
        )
    }
}

