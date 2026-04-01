package com.example.smartbudgetplanner.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartbudgetplanner.data.Transaction
import com.example.smartbudgetplanner.data.TransactionType
import com.example.smartbudgetplanner.ui.UiState
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    uiState: UiState,
    onAddClick: () -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Budget Planner", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Transaction")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Balance Card
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Balance", fontSize = 14.sp)
                    Text(
                        "$${String.format(Locale.US, "%.2f", uiState.balance)}",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                // Income Card
                Card(
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFC8E6C9)) // Light Green
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Income", fontSize = 12.sp, color = Color(0xFF2E7D32))
                        Text(
                            "+$${String.format(Locale.US, "%.2f", uiState.totalIncome)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }
                }

                // Expense Card
                Card(
                    modifier = Modifier.weight(1f).padding(start = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFCDD2)) // Light Red
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Expenses", fontSize = 12.sp, color = Color(0xFFC62828))
                        Text(
                            "-$${String.format(Locale.US, "%.2f", uiState.totalExpense)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                    }
                }
            }

            // Savings Goal Card (Story 8)
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Goal: ${uiState.savingsGoalName}", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        val progress = (uiState.balance / uiState.savingsGoal).coerceIn(0.0, 1.0).toFloat()
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Text(
                            "$${String.format(Locale.US, "%.2f", uiState.balance)} of $${String.format(Locale.US, "%.0f", uiState.savingsGoal)}",
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Spending Habit Visualization (Story 9 & 10)
            if (uiState.totalIncome > 0 || uiState.totalExpense > 0) {
                Text("Monthly Budget Usage", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
                val ratio = (uiState.totalExpense / uiState.budget).coerceIn(0.0, 1.2).toFloat()
                Column {
                    LinearProgressIndicator(
                        progress = { ratio.coerceAtMost(1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = if (ratio > 1.0f) Color.Red else MaterialTheme.colorScheme.primary,
                        trackColor = Color.LightGray,
                        strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                    )
                    if (ratio > 1.0f) {
                        Text("Budget Exceeded!", color = Color.Red, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Text("${(ratio * 100).toInt()}% of $${uiState.budget} budget used", fontSize = 12.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text(
                "Recent Transactions",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.transactions) { transaction ->
                    TransactionItem(transaction, onDeleteTransaction)
                }
            }
        }
    }
}

@Composable
fun TransactionItem(transaction: Transaction, onDelete: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = if (transaction.type == TransactionType.INCOME) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                modifier = Modifier.size(40.dp)
            ) {
                // Placeholder for category icon
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(transaction.name, fontWeight = FontWeight.Bold)
                Text("${transaction.category.displayName} • ${transaction.date}", fontSize = 12.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = (if (transaction.type == TransactionType.INCOME) "+" else "-") + "$${String.format(Locale.US, "%.2f", transaction.amount)}",
                    color = if (transaction.type == TransactionType.INCOME) Color(0xFF2E7D32) else Color(0xFFC62828),
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onDelete(transaction.id) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
