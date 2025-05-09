package com.example.travellelotralala.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
    object BookingConfirmation : Screen("booking_confirmation/{bookingId}") {
        fun createRoute(bookingId: String) = "booking_confirmation/$bookingId"
    }
    object BookingDetail : Screen("booking_detail/{bookingId}") {
        fun createRoute(bookingId: String): String = "booking_detail/$bookingId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
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
                    navController.navigate(Screen.TripDetail.createRoute(tripId))
                },
                onNavigateToTab = { tab ->
                    navigateToTab(navController, tab, Screen.Home.route)
                },
                onSeeAllClick = {
                    navController.navigate(Screen.AllTrips.route)
                }
            )
        }
        
        composable(Screen.Saved.route) {
            SavedScreen(
                onNavigateToTab = { tab ->
                    navigateToTab(navController, tab, Screen.Saved.route)
                },
                onTripClick = { tripId ->
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
                }
            )
        }
        
        composable(Screen.Profile.route) {
            // Tạm thời hiển thị một màn hình đơn giản
            Box(modifier = Modifier.fillMaxSize()) {
                Text("Profile Screen", modifier = Modifier.align(Alignment.Center))
            }
        }
        
        composable(
            route = Screen.TripDetail.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            Log.d("NavGraph", "TripDetail screen with ID: $tripId")
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
                    navController.navigate(Screen.TripDetail.createRoute(tripId))
                }
            )
        }
        composable(
            route = Screen.Booking.route,
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId") ?: ""
            BookingScreen(
                tripId = tripId,
                onBackClick = { navController.popBackStack() },
                onBookingComplete = { bookingId ->
                    navController.navigate(Screen.BookingConfirmation.createRoute(bookingId)) {
                        popUpTo(Screen.Booking.route) { inclusive = true }
                    }
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
            val tripLocation by viewModel.tripLocation.collectAsState()
            
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
                val booking = bookingDetails!!
                BookingConfirmationScreen(
                    bookingId = booking.id,
                    tripName = booking.tripName,
                    tripImageUrl = booking.tripImageUrl,
                    location = tripLocation ?: "",
                    travelDate = booking.travelDate,
                    numberOfTravelers = booking.numberOfTravelers,
                    totalPrice = booking.totalPrice,
                    contactName = booking.contactInfo["name"] ?: "",
                    contactEmail = booking.contactInfo["email"] ?: "",
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





















