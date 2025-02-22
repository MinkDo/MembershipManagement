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
    object EditProfile: Screen("edit_screen")
    object CreateEvent: Screen("createEvent_screen")
    object Achievement: Screen("achievement_screen")
    object EditEvent: Screen("editEvent_screen")
    object CreateAchievement: Screen("creatAchievement_screen")
    object EditAchievement: Screen("editAchievement_screen")
    object CreateFinance: Screen("createFinance_screen")
    object EditFinance: Screen("editFinance_screen")
    object EventRegistration: Screen("eventRegistration_screen")
    object AccountRole: Screen("role_screen")
}
