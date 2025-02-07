package com.example.membershipmanagement

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
import com.example.membershipmanagement.data.repository.AuthRepository
import com.example.membershipmanagement.data.repository.ProfileRepository

import com.example.membershipmanagement.navigation.Screen
import com.example.membershipmanagement.navigation.SetupNavGraph
import com.example.membershipmanagement.ui.theme.MembershipManagementTheme
import com.example.membershipmanagement.utils.GenericViewModelFactory
import com.example.membershipmanagement.utils.UserPreferences
import com.example.membershipmanagement.viewmodel.AuthViewModel
import com.example.membershipmanagement.viewmodel.ProfileViewModel
import java.util.Locale

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val apiService = RetrofitClient.apiService // Tạo đối tượng ApiService
        val userPreferences = UserPreferences(this)  // Khởi tạo UserPreferences
        val authRepository  = AuthRepository(apiService, userPreferences)
        val profileRepository = ProfileRepository(apiService,userPreferences)
        enableEdgeToEdge()
        setContent {
            MembershipManagementTheme {

                    MainScreen(authRepository,profileRepository)

            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(authRepository: AuthRepository, profileRepository: ProfileRepository) {
    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel(factory = GenericViewModelFactory { AuthViewModel(
        authRepository
    )})
    val profileViewModel: ProfileViewModel = viewModel(factory = GenericViewModelFactory { ProfileViewModel(
        profileRepository
    )})
    SetupNavGraph(navController = navController, authViewModel, profileViewModel)

}
@Composable
fun BottomNavBar(navController: NavController) {
    val items = listOf(
        Screen.Home, Screen.Members, Screen.Events, Screen.Finance, Screen.Reports
    )
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = { navController.navigate(screen.route) },
                icon = { Icon(Icons.Default.Home, contentDescription = screen.route) },
                label = { Text(screen.route.replaceFirstChar {
                    if (it.isLowerCase()) it.titlecase(
                        Locale.ROOT
                    ) else it.toString()
                }) }
            )
        }
    }
}
