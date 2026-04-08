package com.example.smartbudgetplanner.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartbudgetplanner.data.Category
import com.example.smartbudgetplanner.data.Transaction
import com.example.smartbudgetplanner.data.TransactionType
import com.example.smartbudgetplanner.data.User
import com.example.smartbudgetplanner.data.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class UiState(
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val budget: Double = 2000.0, // Monthly budget constraint (Story 7)
    val savingsGoal: Double = 1000.0, // Savings goal (Story 8)
    val savingsGoalName: String = "PS5 & Travel",
    val currentUser: User? = null,
    val authError: String? = null
)

class MainViewModel(private val userDao: UserDao) : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun registerUser(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        val trimmedEmail = email.trim().lowercase()
        viewModelScope.launch {
            try {
                val existingUser = userDao.getUserByEmail(trimmedEmail)
                if (existingUser != null) {
                    _uiState.update { it.copy(authError = "User already exists") }
                } else {
                    val newUser = User(trimmedEmail, pass, name)
                    userDao.insertUser(newUser)
                    _uiState.update { it.copy(currentUser = newUser, authError = null) }
                    onSuccess()
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(authError = "Registration failed: ${e.message}") }
            }
        }
    }

    fun loginUser(email: String, pass: String, onSuccess: () -> Unit) {
        val trimmedEmail = email.trim().lowercase()
        viewModelScope.launch {
            val user = userDao.getUserByEmail(trimmedEmail)
            if (user != null && user.password == pass) {
                _uiState.update { it.copy(currentUser = user, authError = null) }
                onSuccess()
            } else {
                _uiState.update { it.copy(authError = "Invalid email or password") }
            }
        }
    }

    fun clearAuthError() {
        _uiState.update { it.copy(authError = null) }
    }

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
