package com.example.budgetplanner

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.budgetplanner.databinding.ActivityHomeBinding

/**
 * SCREEN: Home
 *
 * Displays:
 *  - Income and Expense shortcut buttons (open AddEntryBottomSheet pre-selected)
 *  - Current Balance card with total income / total expense summary
 *  - Recent Transactions RecyclerView
 *  - (+) button in the toolbar that opens AddEntryBottomSheet
 *
 * In a real app, replace the in-memory `transactions` list with a ViewModel + Room database.
 */
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val adapter = TransactionAdapter { transaction ->
        // Optional: open detail / edit dialog on row click
    }

    // In-memory list — replace with ViewModel + Room in production
    private val transactions = mutableListOf<Transaction>()

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        refreshUI()
    }

    // ── Setup ─────────────────────────────────────────────────────────────────

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupRecyclerView() {
        binding.recyclerTransactions.layoutManager = LinearLayoutManager(this)
        binding.recyclerTransactions.adapter = adapter
    }

    private fun setupClickListeners() {
        // (+) toolbar button → open AddEntry with no pre-selection
        binding.btnAddEntry.setOnClickListener {
            openAddEntry(preselect = null)
        }

        // Income shortcut → open AddEntry pre-selected on Income
        binding.btnIncome.setOnClickListener {
            openAddEntry(preselect = TransactionType.INCOME)
        }

        // Expense shortcut → open AddEntry pre-selected on Expense
        binding.btnExpense.setOnClickListener {
            openAddEntry(preselect = TransactionType.EXPENSE)
        }
    }

    // ── Add Entry bottom sheet ────────────────────────────────────────────────

    /**
     * Opens the AddEntryBottomSheet.
     * @param preselect Pre-selects Income or Expense toggle, or neither if null.
     */
    private fun openAddEntry(preselect: TransactionType?) {
        val sheet = AddEntryBottomSheet.newInstance(preselect)
        sheet.onEntryAdded = { transaction ->
            transactions.add(0, transaction)   // newest first
            refreshUI()
        }
        sheet.show(supportFragmentManager, AddEntryBottomSheet.TAG)
    }

    // ── UI refresh ────────────────────────────────────────────────────────────

    /**
     * Recalculates balance totals and updates all views.
     * Call this every time the transactions list changes.
     */
    private fun refreshUI() {
        val totalIncome  = transactions.filter { it.type == TransactionType.INCOME  }.sumOf { it.amount }
        val totalExpense = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val balance      = totalIncome - totalExpense

        binding.tvBalance.text      = "$%.2f".format(balance)
        binding.tvTotalIncome.text  = "+$%.2f".format(totalIncome)
        binding.tvTotalExpense.text = "-$%.2f".format(totalExpense)

        // Show/hide empty state
        if (transactions.isEmpty()) {
            binding.tvEmptyState.visibility = View.VISIBLE
            binding.recyclerTransactions.visibility = View.GONE
        } else {
            binding.tvEmptyState.visibility = View.GONE
            binding.recyclerTransactions.visibility = View.VISIBLE
        }

        // Show only the 10 most recent transactions
        adapter.submitList(transactions.take(10).toList())
    }
}
