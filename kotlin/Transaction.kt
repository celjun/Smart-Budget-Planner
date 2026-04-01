package com.example.budgetplanner

import java.io.Serializable

/**
 * Data model representing a single budget transaction.
 *
 * @param id         Unique identifier (use System.currentTimeMillis() or UUID)
 * @param name       Description of the transaction (e.g. "Rent", "Salary")
 * @param amount     Absolute monetary value (always positive; sign determined by type)
 * @param date       Display date string (e.g. "2024-03-15")
 * @param category   Category label (e.g. "Food", "Transport", "Shopping", "etc.")
 * @param type       Either TransactionType.INCOME or TransactionType.EXPENSE
 */
data class Transaction(
    val id: Long = System.currentTimeMillis(),
    val name: String,
    val amount: Double,
    val date: String,
    val category: String,
    val type: TransactionType
) : Serializable

enum class TransactionType {
    INCOME,
    EXPENSE
}
