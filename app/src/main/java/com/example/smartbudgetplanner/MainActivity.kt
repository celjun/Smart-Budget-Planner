package com.example.smartbudgetplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartbudgetplanner.data.AppDatabase
import com.example.smartbudgetplanner.ui.MainViewModel
import com.example.smartbudgetplanner.ui.screens.AddEntryScreen
import com.example.smartbudgetplanner.ui.screens.DashboardScreen
import com.example.smartbudgetplanner.ui.screens.LoginScreen
import com.example.smartbudgetplanner.ui.screens.RegisterScreen
import com.example.smartbudgetplanner.ui.theme.SmartBudgetPlannerTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val database = AppDatabase.getDatabase(applicationContext)
        val userDao = database.userDao()
        
        enableEdgeToEdge()
        setContent {
            SmartBudgetPlannerTheme {
                val navController = rememberNavController()
                
                // Simple ViewModel Factory
                val viewModel: MainViewModel = viewModel(
                    factory = object : ViewModelProvider.Factory {
                        override fun <T : ViewModel> create(modelClass: Class<T>): T {
                            return MainViewModel(userDao) as T
                        }
                    }
                )
                
                val uiState by viewModel.uiState.collectAsState()
                var showAddSheet by remember { mutableStateOf(false) }

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { email, password ->
                                viewModel.loginUser(email, password) {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            onRegisterClick = {
                                viewModel.clearAuthError()
                                navController.navigate("register")
                            },
                            error = uiState.authError
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = { name, email, password ->
                                viewModel.registerUser(name, email, password) {
                                    navController.navigate("dashboard") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            },
                            onBackToLogin = {
                                viewModel.clearAuthError()
                                navController.popBackStack()
                            },
                            error = uiState.authError
                        )
                    }
                    composable("dashboard") {
                        DashboardScreen(
                            uiState = uiState,
                            onAddClick = { showAddSheet = true },
                            onDeleteTransaction = { viewModel.deleteTransaction(it) },
                            onLogout = {
                                navController.navigate("login") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            }
                        )

                        if (showAddSheet) {
                            ModalBottomSheet(
                                onDismissRequest = { showAddSheet = false }
                            ) {
                                AddEntryScreen(
                                    onDismiss = { showAddSheet = false },
                                    onSave = { name, amount, date, category, type ->
                                        viewModel.addTransaction(name, amount, date, category, type)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
