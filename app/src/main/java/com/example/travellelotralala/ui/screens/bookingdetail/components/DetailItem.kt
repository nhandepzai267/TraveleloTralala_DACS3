package com.example.travellelotralala.ui.screens.bookingdetail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DetailItem(
    label: String,
    value: String
) {
    Column {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(2.dp))
        
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.White
        )
    }
}