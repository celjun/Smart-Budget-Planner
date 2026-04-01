package com.example.budgetplanner

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.budgetplanner.databinding.FragmentAddEntryBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

/**
 * SCREEN: Add New Entry (Income or Expense)
 *
 * Shown as a BottomSheetDialogFragment over HomeActivity.
 *
 * Features:
 *  - Income / Expense toggle (MaterialButtonToggleGroup)
 *  - Name, Amount, Date text fields
 *  - Category chips (Food, Transport, Shopping, etc.)
 *  - "Add Entry" button color matches the selected type
 *  - DatePickerDialog for the Date field
 *  - Fires `onEntryAdded` callback back to HomeActivity
 *
 * Usage from HomeActivity:
 *   val sheet = AddEntryBottomSheet.newInstance(TransactionType.INCOME)
 *   sheet.onEntryAdded = { tx -> /* handle new transaction */ }
 *   sheet.show(supportFragmentManager, AddEntryBottomSheet.TAG)
 */
class AddEntryBottomSheet : BottomSheetDialogFragment() {

    // ── Companion / Factory ──────────────────────────────────────────────────
    companion object {
        const val TAG = "AddEntryBottomSheet"
        private const val ARG_TYPE = "arg_type"

        /**
         * @param preselect Optionally pre-select Income or Expense toggle.
         */
        fun newInstance(preselect: TransactionType?): AddEntryBottomSheet {
            val args = Bundle()
            preselect?.let { args.putString(ARG_TYPE, it.name) }
            return AddEntryBottomSheet().apply { arguments = args }
        }
    }

    // ── State ─────────────────────────────────────────────────────────────────
    private var _binding: FragmentAddEntryBinding? = null
    private val binding get() = _binding!!

    private var selectedType: TransactionType = TransactionType.EXPENSE
    private var selectedCategory: String = "Food"

    private val colorGreen = Color.parseColor("#27AE60")
    private val colorRed   = Color.parseColor("#E74C3C")

    /** Called by HomeActivity to receive the created transaction. */
    var onEntryAdded: ((Transaction) -> Unit)? = null

    // ── Lifecycle ─────────────────────────────────────────────────────────────
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEntryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        applyPreselection()
        setupToggle()
        setupDatePicker()
        setupChips()
        setupButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ── Setup helpers ─────────────────────────────────────────────────────────

    /** Apply the type passed from HomeActivity (Income / Expense / none). */
    private fun applyPreselection() {
        val typeName = arguments?.getString(ARG_TYPE)
        if (typeName != null) {
            selectedType = TransactionType.valueOf(typeName)
        }
        updateToggleUI()
    }

    private fun setupToggle() {
        binding.btnToggleIncome.setOnClickListener {
            selectedType = TransactionType.INCOME
            updateToggleUI()
        }
        binding.btnToggleExpense.setOnClickListener {
            selectedType = TransactionType.EXPENSE
            updateToggleUI()
        }
    }

    /**
     * Visually activates the selected toggle button and updates
     * the "Add Entry" button color to match.
     */
    private fun updateToggleUI() {
        when (selectedType) {
            TransactionType.INCOME -> {
                activateButton(binding.btnToggleIncome, colorGreen)
                deactivateButton(binding.btnToggleExpense, colorRed)
                binding.btnAddEntry.setBackgroundColor(colorGreen)
            }
            TransactionType.EXPENSE -> {
                activateButton(binding.btnToggleExpense, colorRed)
                deactivateButton(binding.btnToggleIncome, colorGreen)
                binding.btnAddEntry.setBackgroundColor(colorRed)
            }
        }
    }

    private fun activateButton(btn: MaterialButton, color: Int) {
        btn.setBackgroundColor(color)
        btn.setTextColor(Color.WHITE)
    }

    private fun deactivateButton(btn: MaterialButton, accentColor: Int) {
        btn.setBackgroundColor(Color.WHITE)
        btn.setTextColor(accentColor)
    }

    private fun setupDatePicker() {
        binding.etDate.setOnClickListener { showDatePicker() }
        binding.tilDate.setEndIconOnClickListener { showDatePicker() }
    }

    private fun showDatePicker() {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val formatted = "%04d-%02d-%02d".format(year, month + 1, day)
                binding.etDate.setText(formatted)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun setupChips() {
        // Set default selection
        binding.chipFood.isChecked = true
        selectedCategory = "Food"

        binding.chipGroupCategory.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.isNotEmpty()) {
                selectedCategory = when (checkedIds.first()) {
                    R.id.chipFood      -> "Food"
                    R.id.chipTransport -> "Transport"
                    R.id.chipShopping  -> "Shopping"
                    R.id.chipEtc       -> "Other"
                    else               -> "Other"
                }
            }
        }
    }

    private fun setupButtons() {
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnAddEntry.setOnClickListener { validateAndSubmit() }
    }

    // ── Validation & submission ───────────────────────────────────────────────

    private fun validateAndSubmit() {
        val name   = binding.etName.text?.toString()?.trim() ?: ""
        val amtStr = binding.etAmount.text?.toString()?.trim() ?: ""
        val date   = binding.etDate.text?.toString()?.trim() ?: ""

        // Reset errors
        binding.tilName.error   = null
        binding.tilAmount.error = null
        binding.tilDate.error   = null

        var valid = true

        if (name.isEmpty()) {
            binding.tilName.error = "Name is required"
            valid = false
        }

        val amount = amtStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            binding.tilAmount.error = "Enter a valid amount"
            valid = false
        }

        if (date.isEmpty()) {
            binding.tilDate.error = "Select a date"
            valid = false
        }

        if (!valid) return

        val transaction = Transaction(
            name     = name,
            amount   = amount!!,
            date     = date,
            category = selectedCategory,
            type     = selectedType
        )

        onEntryAdded?.invoke(transaction)

        Toast.makeText(
            requireContext(),
            "${selectedType.name.lowercase().replaceFirstChar { it.uppercase() }} added",
            Toast.LENGTH_SHORT
        ).show()

        dismiss()
    }
}
