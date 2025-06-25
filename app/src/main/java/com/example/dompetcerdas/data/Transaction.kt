package com.example.dompetcerdas.data // PASTIKAN PACKAGE SESUAI

import java.text.NumberFormat
import java.util.Date
import java.util.Locale

enum class TransactionType {
    INCOME, EXPENSE
}

data class Transaction(
    val id: String,
    val amount: Double,
    val category: String,
    val notes: String,
    val date: Date,
    val type: TransactionType,
    val storeName: String? = null
) {
    fun getFormattedAmount(): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getCurrencyInstance(localeID)
        numberFormat.minimumFractionDigits = 0
        return numberFormat.format(this.amount)
    }
}