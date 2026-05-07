package com.nirengi.kapnews.android.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nirengi.kapnews.android.KapApplication
import com.nirengi.kapnews.android.ui.screens.HomeScreen
import com.nirengi.kapnews.android.ui.screens.LoginScreen
import com.nirengi.kapnews.android.ui.screens.RegisterScreen

@Composable
fun KapNavHost() {
    val app = LocalContext.current.applicationContext as KapApplication
    val navController = rememberNavController()
    var initialRoute by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        initialRoute = if (app.readJwt().isNullOrBlank()) "login" else "home"
    }

    if (initialRoute == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        NavHost(navController = navController, startDestination = initialRoute!!) {
            composable("login") {
                LoginScreen(
                    onLoggedIn = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onRegister = { navController.navigate("register") },
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegistered = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() },
                )
            }
            composable("home") {
                HomeScreen(
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
