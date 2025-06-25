package com.example.dompetcerdas.repository

import com.example.dompetcerdas.data.Transaction
import com.example.dompetcerdas.data.TransactionType
import java.util.Date
import java.util.UUID

// Diubah dari 'object' menjadi 'class' untuk memungkinkan beberapa instance jika diperlukan
// dan untuk mengelola daftar transaksi yang dapat berubah (mutable).
class TransactionRepository {

    // Menggunakan mutable list agar kita bisa menambah transaksi baru secara dinamis.
    private val transactions = mutableListOf(
        Transaction(UUID.randomUUID().toString(), 12500000.0, "Gaji", "Gaji Bulanan", Date(), TransactionType.INCOME),
        Transaction(UUID.randomUUID().toString(), 75500.0, "Belanja", "Belanja bulanan", Date(System.currentTimeMillis() - 86400000 * 1), TransactionType.EXPENSE, "Supermarket"),
        Transaction(UUID.randomUUID().toString(), 25000.0, "Transportasi", "Ojek online", Date(System.currentTimeMillis() - 86400000 * 2), TransactionType.EXPENSE),
        Transaction(UUID.randomUUID().toString(), 22000.0, "Kopi", "Ngopi sore", Date(System.currentTimeMillis() - 86400000 * 3), TransactionType.EXPENSE, "Kopi Kenangan"),
        Transaction(UUID.randomUUID().toString(), 350000.0, "Hiburan", "Nonton bioskop", Date(System.currentTimeMillis() - 86400000 * 4), TransactionType.EXPENSE),
        Transaction(UUID.randomUUID().toString(), 150000.0, "Makanan", "Makan malam", Date(System.currentTimeMillis() - 86400000 * 5), TransactionType.EXPENSE)
    )

    fun getRecentTransactions(): List<Transaction> {
        // Mengembalikan daftar yang diurutkan berdasarkan tanggal terbaru.
        return transactions.sortedByDescending { it.date }
    }

    fun addTransaction(transaction: Transaction) {
        transactions.add(0, transaction) // Menambahkan transaksi baru di awal daftar
    }
}
