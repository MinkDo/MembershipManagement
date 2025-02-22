package com.example.membershipmanagement

import UserViewModel
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.membershipmanagement.data.remote.ApiService
import com.example.membershipmanagement.data.remote.RetrofitClient
import com.example.membershipmanagement.data.repository.AchievementRepository
import com.example.membershipmanagement.data.repository.AuthRepository
import com.example.membershipmanagement.data.repository.ChangePasswordRepository
import com.example.membershipmanagement.data.repository.CreateAchievementRepository
import com.example.membershipmanagement.data.repository.CreateFinanceRepository
import com.example.membershipmanagement.data.repository.EditEventRepository
import com.example.membershipmanagement.data.repository.EventRegistrationRepository
import com.example.membershipmanagement.data.repository.EventRepository
import com.example.membershipmanagement.data.repository.FinanceRepository
import com.example.membershipmanagement.data.repository.ProfileRepository
import com.example.membershipmanagement.data.repository.ReportRepository
import com.example.membershipmanagement.data.repository.UserRepository

import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.navigation.SetupNavGraph

import com.example.membershipmanagement.ui.theme.MembershipManagementTheme
import com.example.membershipmanagement.utils.GenericViewModelFactory
import com.example.membershipmanagement.utils.UserPreferences
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

import java.util.Locale

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = RetrofitClient.apiService // Tạo đối tượng ApiService
        val userPreferences = UserPreferences(this)  // Khởi tạo UserPreferences
        val authRepository  = AuthRepository(apiService, userPreferences)
        val profileRepository = ProfileRepository(apiService,userPreferences)
        val userRepository = UserRepository(apiService, userPreferences)
        val changePasswordRepository = ChangePasswordRepository(apiService, userPreferences)
        val eventRepository = EventRepository(apiService, userPreferences)
        val financeRepository = FinanceRepository(apiService, userPreferences)
        val achievementRepository = AchievementRepository(apiService, userPreferences)
        val reportRepository = ReportRepository(apiService, userPreferences)
        val editEventRepository = EditEventRepository(apiService, userPreferences)
        val createAchievementRepository = CreateAchievementRepository(apiService, userPreferences)
        val createFinanceRepository = CreateFinanceRepository(apiService, userPreferences)
        val eventRegistrationRepository = EventRegistrationRepository(apiService, userPreferences)
        enableEdgeToEdge()
        setContent {
            MembershipManagementTheme {

                    MainScreen(authRepository,
                        profileRepository,
                        userRepository,
                        changePasswordRepository,
                        eventRepository,
                        financeRepository,
                        achievementRepository,
                        reportRepository,
                        editEventRepository,
                        createAchievementRepository,
                        createFinanceRepository,
                        eventRegistrationRepository)

            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(authRepository: AuthRepository,
               profileRepository: ProfileRepository,
               userRepository: UserRepository,
               changePasswordRepository: ChangePasswordRepository,
               eventRepository: EventRepository,
               financeRepository: FinanceRepository,
               achievementRepository: AchievementRepository,
               reportRepository: ReportRepository,
               editEventRepository: EditEventRepository,
               createAchievementRepository: CreateAchievementRepository,
               createFinanceRepository: CreateFinanceRepository,
               eventRegistrationRepository: EventRegistrationRepository) {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(factory = GenericViewModelFactory { AuthViewModel(
        authRepository
    )})
    val profileViewModel: ProfileViewModel = viewModel(factory = GenericViewModelFactory { ProfileViewModel(
        profileRepository
    )})
    val userViewModel: UserViewModel = viewModel(factory = GenericViewModelFactory { UserViewModel(
        userRepository
    )})
    val changeViewModel: ChangePasswordViewModel = viewModel(factory = GenericViewModelFactory { ChangePasswordViewModel(
        changePasswordRepository
    )})
    val eventViewModel: EventViewModel = viewModel(factory = GenericViewModelFactory { EventViewModel(
        eventRepository
    )})
    val financeViewModel: FinanceViewModel =  viewModel(factory = GenericViewModelFactory { FinanceViewModel(
        financeRepository
    )})
    val achievementViewModel: AchievementViewModel =  viewModel(factory = GenericViewModelFactory { AchievementViewModel(
        achievementRepository
    )})
    val createEventViewModel: CreateEventViewModel = viewModel(factory = GenericViewModelFactory { CreateEventViewModel(
        eventRepository
    )})
    val reportViewModel: ReportViewModel = viewModel(factory = GenericViewModelFactory { ReportViewModel(
        reportRepository
    )})
    val editEventViewModel: EditEventViewModel = viewModel(factory = GenericViewModelFactory { EditEventViewModel(
        editEventRepository
    )})
    val createAchievementViewModel: CreateAchievementViewModel= viewModel(factory = GenericViewModelFactory { CreateAchievementViewModel(
        createAchievementRepository
    )})
    val createFinanceViewModel: CreateFinanceViewModel = viewModel(factory = GenericViewModelFactory { CreateFinanceViewModel(
        createFinanceRepository
    )})
    val eventRegistrationViewModel: EventRegistrationViewModel = viewModel(factory = GenericViewModelFactory { EventRegistrationViewModel(
        eventRegistrationRepository
    )})
    SetupNavGraph(navController = navController,
        authViewModel,
        profileViewModel,
        userViewModel,
        changeViewModel,
        eventViewModel,
        financeViewModel,
        achievementViewModel,
        createEventViewModel,
        reportViewModel,
        editEventViewModel,
        createAchievementViewModel,
        createFinanceViewModel,
        eventRegistrationViewModel)

}

