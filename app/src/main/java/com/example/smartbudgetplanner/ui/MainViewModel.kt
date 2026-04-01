package com.example.smartbudgetplanner.ui

import androidx.lifecycle.ViewModel
import com.example.smartbudgetplanner.data.Category
import com.example.smartbudgetplanner.data.Transaction
import com.example.smartbudgetplanner.data.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.util.UUID

data class UiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val budget: Double = 2000.0, // Monthly budget constraint (Story 7)
    val savingsGoal: Double = 1000.0, // Savings goal (Story 8)
    val savingsGoalName: String = "PS5 & Travel"
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun addTransaction(name: String, amount: Double, date: LocalDate, category: Category, type: TransactionType) {
        val newTransaction = Transaction(
            id = UUID.randomUUID().toString(),
            name = name,
            amount = amount,
            date = date,
            category = category,
            type = type
        )
        _uiState.update { currentState ->
            val updatedTransactions = currentState.transactions + newTransaction
            calculateState(updatedTransactions, currentState)
        }
    }

    fun deleteTransaction(id: String) {
        _uiState.update { currentState ->
            val updatedTransactions = currentState.transactions.filter { it.id != id }
            calculateState(updatedTransactions, currentState)
        }
    }

    private fun calculateState(transactions: List<Transaction>, currentState: UiState): UiState {
        val income = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        return currentState.copy(
            transactions = transactions.sortedByDescending { it.date },
            totalIncome = income,
            totalExpense = expense,
            balance = income - expense
        )
    }
}
