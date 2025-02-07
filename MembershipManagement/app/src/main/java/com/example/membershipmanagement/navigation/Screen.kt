package com.example.membershipmanagement.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHost
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.membershipmanagement.loginRegister.screens.LoginScreen
import com.example.membershipmanagement.loginRegister.screens.RegisterScreen
import com.example.membershipmanagement.membershipManagement.screens.ChangePasswordScreen
import com.example.membershipmanagement.membershipManagement.screens.EventScreen
import com.example.membershipmanagement.membershipManagement.screens.FinanceScreen
import com.example.membershipmanagement.membershipManagement.screens.HomeScreen
import com.example.membershipmanagement.membershipManagement.screens.MemberScreen
import com.example.membershipmanagement.membershipManagement.screens.ProfileScreen
import com.example.membershipmanagement.membershipManagement.screens.ReportScreen
import com.example.membershipmanagement.viewmodel.AuthViewModel
import com.example.membershipmanagement.viewmodel.ProfileViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun SetupNavGraph(navController: NavHostController, authViewModel: AuthViewModel, profileViewModel: ProfileViewModel) {

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Home.route) { HomeScreen(navController,profileViewModel) }
        composable(Screen.Members.route) { MemberScreen(navController) }
        composable(Screen.Events.route) { EventScreen(navController) }
        composable(Screen.Finance.route) { FinanceScreen(navController) }
        composable(Screen.Reports.route) { ReportScreen(navController) }
        composable(Screen.Login.route) { LoginScreen(navController, authViewModel)  }
        composable(Screen.Register.route) { RegisterScreen(navController)  }
        composable(Screen.Profile.route) { ProfileScreen(navController,profileViewModel)  }
        composable(Screen.ChangePassword.route) { ChangePasswordScreen(navController)  }
    }
}