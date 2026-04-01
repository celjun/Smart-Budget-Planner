package com.example.smartbudgetplanner.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.smartbudgetplanner.data.Category
import com.example.smartbudgetplanner.data.TransactionType
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntryScreen(
    onDismiss: () -> Unit,
    onSave: (String, Double, LocalDate, Category, TransactionType) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var selectedCategory by remember { mutableStateOf(Category.FOOD) }
    var expanded by remember { mutableStateOf(false) }

    val categories = if (selectedType == TransactionType.INCOME) {
        listOf(Category.SALARY, Category.OTHER)
    } else {
        listOf(Category.FOOD, Category.TRANSPORT, Category.SHOPPING, Category.RENT, Category.ENTERTAINMENT, Category.OTHER)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .navigationBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (selectedType == TransactionType.EXPENSE) "Add Expense" else "Add Income",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .background(Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                .padding(4.dp)
        ) {
            Button(
                onClick = { 
                    selectedType = TransactionType.EXPENSE 
                    selectedCategory = Category.FOOD
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == TransactionType.EXPENSE) Color(0xFFC62828) else Color.Transparent,
                    contentColor = if (selectedType == TransactionType.EXPENSE) Color.White else Color.Black
                ),
                shape = RoundedCornerShape(6.dp),
                elevation = null
            ) {
                Text("Expense")
            }
            Button(
                onClick = { 
                    selectedType = TransactionType.INCOME 
                    selectedCategory = Category.SALARY
                },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedType == TransactionType.INCOME) Color(0xFF2E7D32) else Color.Transparent,
                    contentColor = if (selectedType == TransactionType.INCOME) Color.White else Color.Black
                ),
                shape = RoundedCornerShape(6.dp),
                elevation = null
            ) {
                Text("Income")
            }
        }

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        TextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // Category Selector
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
        ) {
            TextField(
                value = selectedCategory.displayName,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.displayName) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Button(
            onClick = {
                val amountVal = amount.toDoubleOrNull() ?: 0.0
                if (name.isNotEmpty() && amountVal > 0) {
                    onSave(name, amountVal, LocalDate.now(), selectedCategory, selectedType)
                    onDismiss()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selectedType == TransactionType.EXPENSE) Color(0xFFC62828) else Color(0xFF2E7D32)
            )
        ) {
            Text("Add Entry")
        }
    }
}
