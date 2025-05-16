package com.example.travellelotralala.ui.screens.profile

import android.graphics.drawable.ColorDrawable
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travellelotralala.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    onBackClick: () -> Unit,
    onLogout: () -> Unit,
    viewModel: UserProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    LaunchedEffect(Unit) {
        viewModel.loadUserProfile()
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
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
                // Loại bỏ padding mặc định để sửa khoảng trống thừa
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        },
        // Loại bỏ padding mặc định của Scaffold
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
                user != null -> {
                    UserProfileContent(
                        user = user!!,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}

@Composable
fun UserProfileContent(
    user: User,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        
        // Đặt avatar và nút edit trong một Box lớn hơn
        Box(
            modifier = Modifier
                .size(140.dp) // Kích thước lớn hơn để chứa cả avatar và nút edit
                .padding(10.dp) // Padding để avatar nằm ở giữa
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(user.avatarUrl)
                        .crossfade(true)
                        .placeholder(ColorDrawable(Color.LightGray.toArgb()))
                        .error(ColorDrawable(Color.Gray.toArgb()))
                        .build(),
                    contentDescription = "User Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            
            // Nút edit nổi ra ngoài góc dưới phải
            FloatingActionButton(
                onClick = { /* Xử lý khi click vào nút edit */ },
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.BottomEnd),
                containerColor = Color(0xFFFFAA33),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 6.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Avatar",
                    modifier = Modifier.size(15.dp),
                    tint = Color.White
                )
            }
        }
        
        // Phần còn lại giữ nguyên
        Spacer(modifier = Modifier.height(16.dp))
        
        // User Name
        Text(
            text = user.name,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        
        // User Email
        Text(
            text = user.email,
            fontSize = 16.sp,
            color = Color.LightGray,
            modifier = Modifier.padding(top = 4.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Profile Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2A2A2A)
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                ProfileInfoItem(title = "User ID", value = user.uid)
                Divider(color = Color(0xFF3A3A3A), modifier = Modifier.padding(vertical = 12.dp))
                ProfileInfoItem(title = "Account Created", value = formatDate(user.createdAt))
                // Add more profile info items as needed
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Logout Button
        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF5722)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.White
                )
                Text(
                    text = "Logout",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun ProfileInfoItem(title: String, value: String) {
    Column {
        Text(
            text = title,
            fontSize = 14.sp,
            color = Color.Gray
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.White,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

// Helper function to format timestamp
fun formatDate(timestamp: Long): String {
    if (timestamp == 0L) return "N/A"
    val date = java.util.Date(timestamp)
    val format = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
    return format.format(date)
}





