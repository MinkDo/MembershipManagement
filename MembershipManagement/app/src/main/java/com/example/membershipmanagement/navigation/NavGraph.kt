package com.example.membershipmanagement.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Members : Screen("members_screen")
    object Events : Screen("events_screen")
    object Finance : Screen("finance_screen")
    object Reports : Screen("reports_screen")
    object Register : Screen("register_screen")
    object Login : Screen("login_screen")
    object Profile : Screen("profile_screen")
    object ChangePassword: Screen("changePassword_screen")
}
