package org.yarhooshmand.smartv3.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import org.yarhooshmand.smartv3.viewmodel.ReminderViewModel

@Composable
fun AppNav(viewModel: ReminderViewModel, navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = "home") {
        composable("home") { RemindersScreen(viewModel) }
        composable("today") { TodayScreen(viewModel) }
        composable("week") { WeekScreen(viewModel) }
        composable("dashboard") { DashboardScreen(navController) }
    }
}
