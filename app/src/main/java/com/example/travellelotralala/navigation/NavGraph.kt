package com.example.travellelotralala.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.travellelotralala.ui.screens.mainscreens.bookingsscreen.BookingsScreen
import com.example.travellelotralala.ui.screens.mainscreens.homescreen.HomeScreen
import com.example.travellelotralala.ui.screens.mainscreens.notificationsscreen.NotificationsScreen
import com.example.travellelotralala.ui.screens.mainscreens.savedscreen.SavedScreen
import com.example.travellelotralala.ui.screens.tripdetail.TripDetailScreen
import com.example.travellelotralala.ui.screens.alltrips.AllTripsScreen

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
            BookingsScreen(
                onNavigateToTab = { tab ->
                    navigateToTab(navController, tab, Screen.Bookings.route)
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
                onBackClick = { navController.popBackStack() }
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
            popUpTo(currentRoute) { inclusive = true }
        }
    }
}








