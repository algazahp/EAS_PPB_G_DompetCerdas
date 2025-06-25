package com.example.dompetcerdas.data

// Data class untuk merepresentasikan anggaran bulanan per kategori
data class Budget(
    val category: String,
    val limit: Double,
    var spent: Double = 0.0
)