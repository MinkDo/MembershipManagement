package com.example.membershipmanagement.navigation

import EditProfileScreen
import UserViewModel
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
import com.example.membershipmanagement.account.screens.AccountRoleScreen
import com.example.membershipmanagement.achievements.screens.AchievementScreen
import com.example.membershipmanagement.achievements.screens.CreateAchievementScreen
import com.example.membershipmanagement.events.screens.EditEventScreen
import com.example.membershipmanagement.events.screens.EventRegistrationScreen
import com.example.membershipmanagement.events.screens.EventScreen
import com.example.membershipmanagement.finances.screens.CreateFinanceScreen
import com.example.membershipmanagement.finances.screens.EditFinanceScreen
import com.example.membershipmanagement.finances.screens.FinanceScreen
import com.example.membershipmanagement.loginRegister.screens.LoginScreen
import com.example.membershipmanagement.loginRegister.screens.RegisterScreen
import com.example.membershipmanagement.membershipManagement.screens.ChangePasswordScreen
import com.example.membershipmanagement.membershipManagement.screens.CreateEventScreen
import com.example.membershipmanagement.membershipManagement.screens.EditAchievementScreen


import com.example.membershipmanagement.membershipManagement.screens.HomeScreen
import com.example.membershipmanagement.membershipManagement.screens.MemberScreen
import com.example.membershipmanagement.membershipManagement.screens.ProfileScreen
import com.example.membershipmanagement.reports.screens.ReportScreen

import com.example.membershipmanagement.viewmodel.AchievementViewModel
import com.example.membershipmanagement.viewmodel.AuthViewModel
import com.example.membershipmanagement.viewmodel.ChangePasswordViewModel
import com.example.membershipmanagement.viewmodel.CreateAchievementViewModel
import com.example.membershipmanagement.viewmodel.CreateEventViewModel
import com.example.membershipmanagement.viewmodel.CreateFinanceViewModel
import com.example.membershipmanagement.viewmodel.EditEventViewModel
import com.example.membershipmanagement.viewmodel.EventRegistrationViewModel
import com.example.membershipmanagement.viewmodel.EventViewModel
import com.example.membershipmanagement.viewmodel.FinanceViewModel
import com.example.membershipmanagement.viewmodel.ProfileViewModel
import com.example.membershipmanagement.viewmodel.ReportViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable

fun SetupNavGraph(navController: NavHostController,
                  authViewModel: AuthViewModel,
                  profileViewModel: ProfileViewModel,
                  userViewModel: UserViewModel,
                  changePasswordViewModel: ChangePasswordViewModel,
                  eventViewModel: EventViewModel,
                  financeViewModel: FinanceViewModel,
                  achievementViewModel: AchievementViewModel,
                  createEventViewModel: CreateEventViewModel,
                  reportViewModel: ReportViewModel,
                  editEventViewModel: EditEventViewModel,
                  createAchievementViewModel: CreateAchievementViewModel,
                  createFinanceModel: CreateFinanceViewModel,
                  eventRegistrationViewModel: EventRegistrationViewModel
) {

    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(Screen.Home.route) { HomeScreen(navController,profileViewModel,userViewModel,eventViewModel, financeViewModel, achievementViewModel, authViewModel) }
        composable(Screen.Members.route) { MemberScreen(navController,userViewModel,profileViewModel) }
        composable(Screen.Events.route) { EventScreen(navController,eventViewModel,profileViewModel, editEventViewModel, eventRegistrationViewModel) }
        composable(Screen.Finance.route) { FinanceScreen(navController, financeViewModel,createFinanceModel) }
        composable(Screen.Reports.route) { ReportScreen(navController, reportViewModel) }
        composable(Screen.Login.route) { LoginScreen(navController, authViewModel, profileViewModel )  }
        composable(Screen.Register.route) { RegisterScreen(navController, authViewModel)  }
        composable(Screen.Profile.route) { ProfileScreen(navController,profileViewModel)  }
        composable(Screen.ChangePassword.route) { ChangePasswordScreen(navController, changePasswordViewModel, profileViewModel)  }
        composable(Screen.EditProfile.route) { EditProfileScreen(navController,profileViewModel)  }
        composable(Screen.Achievement.route) { AchievementScreen(navController, achievementViewModel, createAchievementViewModel, eventViewModel, userViewModel) }
        composable(Screen.CreateEvent.route) { CreateEventScreen(navController, createEventViewModel, eventViewModel)  }
        composable(Screen.EditEvent.route) { EditEventScreen(navController, editEventViewModel, eventViewModel)  }
        composable(Screen.CreateAchievement.route) { CreateAchievementScreen(navController, createAchievementViewModel, eventViewModel, userViewModel, achievementViewModel)  }
        composable(Screen.EditAchievement.route) { EditAchievementScreen(navController,createAchievementViewModel,eventViewModel, userViewModel, achievementViewModel) }
        composable(Screen.CreateFinance.route) { CreateFinanceScreen(navController, createFinanceModel, financeViewModel)  }
        composable(Screen.EditFinance.route) { EditFinanceScreen(navController, createFinanceModel,financeViewModel) }
        composable(Screen.EventRegistration.route) { EventRegistrationScreen(navController, eventRegistrationViewModel) }
        composable(Screen.AccountRole.route) { AccountRoleScreen(navController,userViewModel,profileViewModel) }
    }
}