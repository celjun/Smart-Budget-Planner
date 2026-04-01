package com.example.budgetplanner

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

/**
 * RecyclerView adapter for the Recent Transactions list on HomeActivity.
 *
 * Uses ListAdapter + DiffUtil for efficient updates.
 * Each row is colored GREEN (income) or RED (expense).
 *
 * Usage:
 *   val adapter = TransactionAdapter { transaction -> /* handle item click */ }
 *   recyclerTransactions.adapter = adapter
 *   adapter.submitList(transactions)
 */
class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit = {}
) : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DiffCallback()) {

    // ── Colors ──────────────────────────────────────────────────────────────
    private val colorIncome  = Color.parseColor("#27AE60")
    private val colorExpense = Color.parseColor("#E74C3C")

    // ── ViewHolder ───────────────────────────────────────────────────────────
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val card: MaterialCardView = itemView.findViewById(R.id.cardTransaction)
            ?: itemView as MaterialCardView
        val tvName: TextView     = itemView.findViewById(R.id.tvTransactionName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvTransactionCategory)
        val tvAmount: TextView   = itemView.findViewById(R.id.tvTransactionAmount)
    }

    // ── Inflation ─────────────────────────────────────────────────────────────
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    // ── Binding ───────────────────────────────────────────────────────────────
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val tx = getItem(position)

        holder.tvName.text     = tx.name
        holder.tvCategory.text = tx.category

        when (tx.type) {
            TransactionType.INCOME -> {
                holder.tvAmount.text = "+$%.2f".format(tx.amount)
                holder.itemView.background?.let {
                    (holder.itemView as? MaterialCardView)?.setCardBackgroundColor(colorIncome)
                }
                // For MaterialCardView:
                (holder.itemView as? MaterialCardView)?.setCardBackgroundColor(colorIncome)
            }
            TransactionType.EXPENSE -> {
                holder.tvAmount.text = "-$%.2f".format(tx.amount)
                (holder.itemView as? MaterialCardView)?.setCardBackgroundColor(colorExpense)
            }
        }

        holder.itemView.setOnClickListener { onItemClick(tx) }
    }

    // ── DiffUtil ──────────────────────────────────────────────────────────────
    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(old: Transaction, new: Transaction) = old.id == new.id
        override fun areContentsTheSame(old: Transaction, new: Transaction) = old == new
    }
}
