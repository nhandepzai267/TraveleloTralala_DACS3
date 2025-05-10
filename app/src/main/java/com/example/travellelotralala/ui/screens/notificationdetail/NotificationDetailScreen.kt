package com.example.travellelotralala.ui.screens.notificationdetail

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.travellelotralala.model.Notification

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationDetailScreen(
    notificationId: String,
    onBackClick: () -> Unit,
    onNavigateToTrip: (String) -> Unit = {}, // Thêm callback để điều hướng đến trang chi tiết chuyến đi
    viewModel: NotificationDetailViewModel = hiltViewModel()
) {
    viewModel.loadNotification(notificationId)
    
    val notification by viewModel.notification.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Notification Detail") },
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
                // Loại bỏ padding mặc định
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        // Loại bỏ tất cả insets mặc định
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = Color(0xFF1E1E1E)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFFFFAA33)
                    )
                }
                error != null -> {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                notification != null -> {
                    NotificationDetailContent(
                        notification = notification!!,
                        onExploreClick = { 
                            if (notification!!.relatedTripId.isNotEmpty()) {
                                onNavigateToTrip(notification!!.relatedTripId)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun NotificationDetailContent(
    notification: Notification,
    onExploreClick: () -> Unit = {}
) {
    // Log để kiểm tra relatedTripId
    Log.d("NotificationDetail", "RelatedTripId: ${notification.relatedTripId}")
    
    // Định nghĩa contentParagraphs ở đây để tránh lỗi
    val contentParagraphs = notification.notiContent.split("\n\n")
        .filter { it.isNotEmpty() }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Phần 1: Nội dung thông báo
        // Tiêu đề
        Text(
            text = notification.notiTitle,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        // Mô tả ngắn
        Text(
            text = notification.notiDescription,
            fontSize = 16.sp,
            color = Color.Gray
        )
        
        // Hình ảnh chính
        if (notification.notiImage.isNotEmpty()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(notification.notiImage)
                    .crossfade(true)
                    .build(),
                contentDescription = notification.notiTitle,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
        
        // Nội dung chi tiết
        if (notification.contentImage.isNotEmpty()) {
            // Nếu có hình ảnh bổ sung, hiển thị nội dung theo đoạn
            contentParagraphs.forEachIndexed { index, paragraph ->
                Text(
                    text = paragraph,
                    fontSize = 16.sp,
                    color = Color.White,
                    lineHeight = 24.sp
                )
                
                // Chèn hình ảnh sau mỗi đoạn nếu có
                if (index < notification.contentImage.size) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(notification.contentImage[index])
                            .crossfade(true)
                            .build(),
                        contentDescription = "Content image ${index + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        } else {
            // Nếu không có hình ảnh bổ sung, chỉ hiển thị nội dung
            Text(
                text = notification.notiContent,
                fontSize = 16.sp,
                color = Color.White,
                lineHeight = 24.sp
            )
        }

        // Thêm Spacer để đẩy nút xuống cuối
        Spacer(modifier = Modifier.weight(1f, fill = false))

        // Phần 2: Nút ở cuối trang (không cố định)
        if (notification.relatedTripId.isNotEmpty()) {
            Button(
                onClick = onExploreClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFFAA33)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Take a trip?!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Explore",
                        tint = Color.White
                    )
                }
            }
        }
    }
}








