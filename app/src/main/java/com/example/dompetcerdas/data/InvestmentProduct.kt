package com.example.dompetcerdas.data

// Data class untuk tujuan menabung
data class SavingGoal(
    val id: String,
    val name: String,
    val targetAmount: Double,
    var currentAmount: Double = 0.0
)