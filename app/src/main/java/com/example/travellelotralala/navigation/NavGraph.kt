package com.example.travellelotralala.navigation

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.travellelotralala.ui.components.TabItem
import com.example.travellelotralala.ui.screens.WelcomeScreen
import com.example.travellelotralala.ui.screens.authscreens.AuthScreen
import com.example.travellelotralala.ui.screens.authscreens.login.LoginScreen
import com.example.travellelotralala.ui.screens.authscreens.signup.SignupScreen
import com.example.travellelotralala.ui.screens.mainscreens.bookedtripsscreen.BookedTripsScreen
import com.example.travellelotralala.ui.screens.mainscreens.homescreen.HomeScreen
import com.example.travellelotralala.ui.screens.mainscreens.notificationsscreen.NotificationsScreen
import com.example.travellelotralala.ui.screens.mainscreens.savedscreen.SavedScreen
import com.example.travellelotralala.ui.screens.tripdetail.TripDetailScreen
import com.example.travellelotralala.ui.screens.alltrips.AllTripsScreen
import com.example.travellelotralala.ui.screens.booking.BookingScreen
import androidx.compose.material3.CircularProgressIndicator
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.travellelotralala.ui.screens.booking.BookingConfirmationScreen
import com.example.travellelotralala.ui.screens.booking.BookingConfirmationViewModel
import com.example.travellelotralala.ui.screens.bookingdetail.BookingDetailScreen
import com.example.travellelotralala.ui.screens.hotels.HotelsScreen
import com.example.travellelotralala.ui.screens.hotels.hoteldetail.HotelDetailScreen
import com.example.travellelotralala.ui.screens.notificationdetail.NotificationDetailScreen
import com.example.travellelotralala.ui.screens.hotels.roomdetail.RoomDetailScreen
import com.example.travellelotralala.ui.screens.profile.UserProfileScreen

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Auth : Screen("auth")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Home : Screen("home")
    object ForgotPassword : Screen("forgot_password")
    object Saved : Screen("saved")
    object Bookings : Screen("bookings")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")
    object TripDetail : Screen("trip_detail/{tripId}") {
        fun createRoute(tripId: String) = "trip_detail/$tripId"
    }
    object AllTrips : Screen("all_trips")
    object Booking : Screen("booking/{tripId}") {
        fun createRoute(tripId: String) = "booking/$tripId"
    }
    object BookingWithRoom : Screen("booking_with_room/{tripId}/{hotelId}/{hotelName}/{roomTypeId}/{roomTypeName}/{roomNumber}") {
        fun createRoute(tripId: String, hotelId: String, hotelName: String, roomTypeId: String, roomTypeName: String, roomNumber: String): String {
            val encodedHotelName = Uri.encode(hotelName)
            val encodedRoomTypeName = Uri.encode(roomTypeName)
            return "booking_with_room/$tripId/$hotelId/$encodedHotelName/$roomTypeId/$encodedRoomTypeName/$roomNumber"
        }
    }
    object BookingConfirmation : Screen("booking_confirmation/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_confirmation/$bookingId"
    }
    object BookingDetail : Screen("booking_detail/{bookingId}") {
        fun createRoute(bookingId: String): String = "booking_detail/$bookingId"
    }
    object NotificationDetail : Screen("notification_detail/{notificationId}") {
        fun createRoute(notificationId: String) = "notification_detail/$notificationId"
    }
    object Hotels : Screen("hotels/{location}") {
        fun createRoute(location: String) = "hotels/${Uri.encode(location)}"
    }
    object HotelDetail : Screen("hotel_detail/{hotelId}") {
        fun createRoute(hotelId: String) = "hotel_detail/$hotelId"
    }
    object RoomDetail : Screen("room_detail/{hotelId}/{roomTypeId}") {
        fun createRoute(hotelId: String, roomTypeId: String): String {
            return "room_detail/$hotelId/$roomTypeId"
        }
    }
}

// Lưu trữ tripId hiện tại
class NavigationState {
    var currentTripId: String = ""
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    // Tạo và nhớ NavigationState để lưu trữ tripId hiện tại
    val navigationState = remember { NavigationState() }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onGetStartedClick = {
                    navController.navigate(Screen.Auth.route)
                }
            )
        }
        
        composable(Screen.Auth.route) {
            AuthScreen(
                onLoginClick = {
                    navController.navigate(Screen.Login.route)
                },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(Screen.ForgotPassword.route)
                }
            )
        }

        composable(Screen.SignUp.route) {
            SignupScreen(
                onSignUpSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.SignUp.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.ForgotPassword.route) {
            Box(modifier = Modifier.fillMaxSize()) {
                Text("Forgot Password Screen", modifier = Modifier.align(Alignment.Center))
            }
        }
        
        composable(Screen.Home.route) {
            HomeScreen(
                onDestinationClick = { tripId ->
                    navigationState.currentTripId = tripId
                    navController.navigate(Screen.TripDetail.createRoute(tripId))
                },
                onNavigateToTab = { tab ->
                    navigateToTab(navController, tab, Screen.Home.route)
                },
                onSeeAllClick = {
                    navController.navigate(Screen.AllTrips.route)
                },
                onNavigateToProfile = {
                    navController.navigate(Screen.Profile.route)
                }
            )
        }
        
        composable(Screen.Saved.route) {
            SavedScreen(
                onNavigateToTab = { tab ->
                    navigateToTab(navController, tab, Screen.Saved.route)
                },
                onTripClick = { tripId ->
                    navigationState.currentTripId = tripId
                    navController.navigate(Screen.TripDetail.createRoute(tripId))
                }
            )
        }
        
        composable(Screen.Bookings.route) {
            BookedTripsScreen(
                onNavigateToTab = { tabItem ->
                    when (tabItem) {
                        TabItem.HOME -> navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        TabItem.SAVED -> navController.navigate(Screen.Saved.route) {
                            popUpTo(Screen.Saved.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                        TabItem.BOOKINGS -> { /* Already on Bookings screen */ }
                        TabItem.NOTIFICATIONS -> navController.navigate(Screen.Notifications.route) {
                            popUpTo(Screen.Notifications.route) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                onBookingClick = { bookingId ->
                    navController.navigate(Screen.BookingDetail.createRoute(bookingId))
                }
            )
        }
        
        composable(Screen.Notifications.route) {
            NotificationsScreen(
                onNavigateToTab = { tab ->
                    navigateToTab(navController, tab, Screen.Notifications.route)
                },
                onNotificationClick = { notificationId ->
                    navController.navigate(Screen.NotificationDetail.createRoute(notificationId))
                }
            )
        }
        
        composable(Screen.Profile.route) {
            UserProfileScreen(
                onBackClick = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(
            route = Screen.TripDetail.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            Log.d("NavGraph", "TripDetail screen with ID: $tripId")
            
            navigationState.currentTripId = tripId
            
            TripDetailScreen(
                tripId = tripId,
                onBackClick = { navController.popBackStack() },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route)
                },
                onNavigateToBooking = { tripId ->
                    navController.navigate(Screen.Booking.createRoute(tripId))
                }
            )
        }
        
        composable(Screen.AllTrips.route) {
            AllTripsScreen(
                onBackClick = { navController.popBackStack() },
                onTripClick = { tripId ->
                    navigationState.currentTripId = tripId
                    navController.navigate(Screen.TripDetail.createRoute(tripId))
                }
            )
        }
        
        composable(
            route = Screen.Booking.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            
            navigationState.currentTripId = tripId
            
            BookingScreen(
                tripId = tripId,
                onBackClick = { navController.popBackStack() },
                onBookingComplete = { bookingId ->
                    navController.navigate(Screen.BookingConfirmation.createRoute(bookingId)) {
                        popUpTo(Screen.Booking.route) { inclusive = true }
                    }
                },
                onViewHotels = { location ->
                    navController.navigate(Screen.Hotels.createRoute(location))
                }
            )
        }
        
        // Màn hình Booking với thông tin phòng
        composable(
            route = Screen.BookingWithRoom.route,
            arguments = listOf(
                navArgument("tripId") { type = NavType.StringType },
                navArgument("hotelId") { type = NavType.StringType },
                navArgument("hotelName") { 
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("roomTypeId") { type = NavType.StringType },
                navArgument("roomTypeName") { 
                    type = NavType.StringType
                    nullable = false
                },
                navArgument("roomNumber") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            val hotelName = Uri.decode(backStackEntry.arguments?.getString("hotelName") ?: "")
            val roomTypeId = backStackEntry.arguments?.getString("roomTypeId") ?: ""
            val roomTypeName = Uri.decode(backStackEntry.arguments?.getString("roomTypeName") ?: "")
            val roomNumber = backStackEntry.arguments?.getString("roomNumber") ?: ""
            
            navigationState.currentTripId = tripId
            
            BookingScreen(
                tripId = tripId,
                hotelId = hotelId,
                hotelName = hotelName,
                roomTypeId = roomTypeId,
                roomTypeName = roomTypeName,
                roomNumber = roomNumber,
                onBackClick = { navController.popBackStack() },
                onBookingComplete = { bookingId ->
                    navController.navigate(Screen.BookingConfirmation.createRoute(bookingId)) {
                        popUpTo(Screen.Home.route)
                    }
                },
                onViewHotels = { location ->
                    navController.navigate(Screen.Hotels.createRoute(location))
                }
            )
        }
        
        composable(
            route = Screen.BookingConfirmation.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            val viewModel: BookingConfirmationViewModel = hiltViewModel()
            
            LaunchedEffect(bookingId) {
                viewModel.loadBookingDetails(bookingId)
            }
            
            val bookingDetails by viewModel.bookingDetails.collectAsState()
            val isLoading by viewModel.isLoading.collectAsState()
            val error by viewModel.error.collectAsState()
            
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFFFFA07A))
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: $error",
                        color = Color.Red,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else if (bookingDetails != null) {
                BookingConfirmationScreen(
                    bookingId = bookingId,
                    onDoneClick = {
                        navController.navigate(Screen.Bookings.route) {
                            popUpTo(Screen.BookingConfirmation.route) { inclusive = true }
                        }
                    }
                )
            }
        }
        
        composable(
            route = Screen.BookingDetail.route,
            arguments = listOf(
                navArgument("bookingId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingDetailScreen(
                bookingId = bookingId,
                onBackClick = { navController.popBackStack() }
            )
        }
        
        composable(
            route = Screen.NotificationDetail.route,
            arguments = listOf(
                navArgument("notificationId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val notificationId = backStackEntry.arguments?.getString("notificationId") ?: ""
            NotificationDetailScreen(
                notificationId = notificationId,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToTrip = { tripId ->
                    navController.navigate(Screen.TripDetail.createRoute(tripId))
                }
            )
        }
        
        composable(
            route = Screen.Hotels.route,
            arguments = listOf(
                navArgument("location") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val location = Uri.decode(backStackEntry.arguments?.getString("location") ?: "")
            HotelsScreen(
                location = location,
                onBackClick = { navController.popBackStack() },
                onHotelClick = { hotelId ->
                    navController.navigate(Screen.HotelDetail.createRoute(hotelId))
                }
            )
        }
        
        composable(
            route = Screen.HotelDetail.route,
            arguments = listOf(
                navArgument("hotelId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            HotelDetailScreen(
                hotelId = hotelId,
                onBackClick = { navController.popBackStack() },
                onBookRoom = { hotelId, roomTypeId ->
                    navController.navigate(Screen.RoomDetail.createRoute(hotelId, roomTypeId))
                }
            )
        }
        
        composable(
            route = Screen.RoomDetail.route,
            arguments = listOf(
                navArgument("hotelId") { type = NavType.StringType },
                navArgument("roomTypeId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val hotelId = backStackEntry.arguments?.getString("hotelId") ?: ""
            val roomTypeId = backStackEntry.arguments?.getString("roomTypeId") ?: ""
            RoomDetailScreen(
                hotelId = hotelId,
                roomTypeId = roomTypeId,
                onBackClick = { navController.popBackStack() },
                onBookRoom = { hotelId, roomTypeId, roomNumber, hotelName, roomTypeName ->
                    val tripId = navigationState.currentTripId
                    
                    if (tripId.isNotEmpty()) {
                        navController.navigate(
                            Screen.BookingWithRoom.createRoute(
                                tripId,
                                hotelId,
                                hotelName,
                                roomTypeId,
                                roomTypeName,
                                roomNumber
                            )
                        ) {
                            popUpTo(Screen.Home.route) {
                                inclusive = false
                            }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }
    }
}

// Hàm helper để tránh lặp lại code khi điều hướng giữa các tab
private fun navigateToTab(navController: NavHostController, tab: TabItem, currentRoute: String) {
    val targetRoute = when (tab) {
        TabItem.HOME -> Screen.Home.route
        TabItem.SAVED -> Screen.Saved.route
        TabItem.BOOKINGS -> Screen.Bookings.route
        TabItem.NOTIFICATIONS -> Screen.Notifications.route
    }
    
    if (targetRoute != currentRoute) {
        navController.navigate(targetRoute) {
            popUpTo(targetRoute) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}




