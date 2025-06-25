package com.example.dompetcerdas.services

// Memastikan hanya Transaction dari package 'data' yang diimpor
import com.example.dompetcerdas.data.Transaction
import com.example.dompetcerdas.data.TransactionType
import java.text.NumberFormat
import java.util.Locale

// Kelas simulasi untuk Asisten Keuangan Pribadi
object FinancialAssistant {

    // Fungsi untuk memformat angka menjadi format mata uang Rupiah
    private fun formatToRupiah(amount: Double): String {
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID)
        format.maximumFractionDigits = 0 // Menghilangkan desimal
        return format.format(amount)
    }

    fun analyzeSpending(transactions: List<Transaction>): String {
        if (transactions.isEmpty()) {
            return "Belum ada transaksi untuk dianalisis. Mulai catat keuanganmu!"
        }

        val totalIncome = transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpenses = transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }

        if (totalExpenses == 0.0) {
            return "Kerja bagus! Kamu belum melakukan pengeluaran sama sekali."
        }

        // Analisis #1: Menemukan kategori pengeluaran terbesar
        val topSpendingCategory = transactions
            .filter { it.type == TransactionType.EXPENSE }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .maxByOrNull { it.value }

        topSpendingCategory?.let { (category, amount) ->
            if (amount > totalExpenses / 2) { // Jika satu kategori lebih dari 50% total pengeluaran
                return "Pengeluaran terbesarmu ada di kategori '${category}' yaitu sebesar ${formatToRupiah(amount)}. Mungkin bisa dievaluasi lagi."
            }
        }

        // Analisis #2: Membandingkan pengeluaran dengan pemasukan
        if (totalIncome > 0) {
            val expenseRatio = totalExpenses / totalIncome
            if (expenseRatio > 0.8) {
                return "Hati-hati, pengeluaranmu sudah lebih dari 80% pemasukan. Waktunya mengerem pengeluaran!"
            }
            if (expenseRatio < 0.5) {
                return "Manajemen keuanganmu sangat baik! Pengeluaranmu kurang dari 50% pemasukan. Pertimbangkan untuk menabung atau berinvestasi."
            }
        }

        // Analisis #3: Pengeluaran spesifik (seperti yang sudah ada)
        val coffeeExpenses = transactions
            .filter { it.type == TransactionType.EXPENSE && it.category.equals("Kopi", ignoreCase = true) }
            .sumOf { it.amount }

        if (coffeeExpenses > 200000) {
            return "Pengeluaran kopimu bulan ini ${formatToRupiah(coffeeExpenses)}. Mungkin bisa dikurangi dengan membuat kopi sendiri di rumah."
        }

        // Pesan default jika tidak ada insight khusus
        return "Manajemen keuanganmu terlihat cukup baik. Terus pertahankan!"
    }
}
