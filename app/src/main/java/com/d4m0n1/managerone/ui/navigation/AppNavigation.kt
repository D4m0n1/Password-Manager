package com.d4m0n1.managerone.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.d4m0n1.managerone.ui.screens.PasswordListScreen
import com.d4m0n1.managerone.ui.screens.AddPasswordScreen
import com.d4m0n1.managerone.ui.screens.PasswordDetailScreen

const val ROUTE_LIST = "password_list"
const val ROUTE_ADD = "add_password"
const val ROUTE_DETAIL = "password_detail/{id}"

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

        composable(
            route = ROUTE_DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            PasswordDetailScreen(navController = navController, passwordId = id)
        }
    }
}

