package com.d4m0n1.managerone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.d4m0n1.managerone.ui.screens.PasswordListScreen
import com.d4m0n1.managerone.ui.screens.AddPasswordScreen

const val ROUTE_LIST = "password_list"
const val ROUTE_ADD = "add_password"

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ROUTE_LIST
    ) {
        composable(ROUTE_LIST) {
            PasswordListScreen(navController = navController)
        }

        composable(ROUTE_ADD) {
            AddPasswordScreen(navController = navController)
        }
    }
}