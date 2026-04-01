package com.example.smartbudgetplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartbudgetplanner.ui.MainViewModel
import com.example.smartbudgetplanner.ui.screens.AddEntryScreen
import com.example.smartbudgetplanner.ui.screens.DashboardScreen
import com.example.smartbudgetplanner.ui.screens.LoginScreen
import com.example.smartbudgetplanner.ui.theme.SmartBudgetPlannerTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartBudgetPlannerTheme {
                val navController = rememberNavController()
                val viewModel: MainViewModel = viewModel()
                val uiState by viewModel.uiState.collectAsState()
                var showAddSheet by remember { mutableStateOf(false) }

                NavHost(navController = navController, startDestination = "login") {
                    composable("login") {
                        LoginScreen(onLoginSuccess = {
                            navController.navigate("dashboard") {
                                popUpTo("login") { inclusive = true }
                            }
                        })
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
