package com.example.smartbudgetplanner.data

import java.time.LocalDate

enum class TransactionType {
    INCOME, EXPENSE
}

enum class Category(val displayName: String) {
    FOOD("Food"),
    TRANSPORT("Transport"),
    SHOPPING("Shopping"),
    RENT("Rent"),
    ENTERTAINMENT("Entertainment"),
    SALARY("Salary"),
    OTHER("Other")
}

data class Transaction(
    val id: String,
    val name: String,
    val amount: Double,
    val date: LocalDate,
    val category: Category,
    val type: TransactionType
)
