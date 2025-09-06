package org.yarhooshmand.smartv3.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.yarhooshmand.smartv3.viewmodel.ReminderViewModel

@Composable
fun AppNav(navController: NavHostController, reminderViewModel: ReminderViewModel) {
    NavHost(navController = navController, startDestination = "reminders") {

        composable("reminders") {
            RemindersScreen(viewModel = reminderViewModel, navController = navController)
        }

        composable("today") {
            TodayScreen(viewModel = reminderViewModel)
        }

        composable("week") {
            WeekScreen(viewModel = reminderViewModel)
        }

        composable("dashboard") {
            DashboardScreen(navController = navController)
        }
    }
}
