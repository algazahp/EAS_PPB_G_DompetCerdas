package com.example.dompetcerdas.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dompetcerdas.data.*
import com.example.dompetcerdas.services.FinancialAssistant
import java.text.NumberFormat
import java.util.*

class DashboardViewModel : ViewModel() {

    // --- Pockets State ---
    private val _pockets = MutableLiveData<List<Pocket>>()
    val pockets: LiveData<List<Pocket>> = _pockets

    // --- Main Balance (Derived from Pockets) ---
    private val _formattedBalance = MutableLiveData("Rp0")
    val formattedBalance: LiveData<String> = _formattedBalance

    // --- Other States ---
    private val _savedCards = MutableLiveData<List<DebitCard>>(emptyList())
    val savedCards: LiveData<List<DebitCard>> = _savedCards
    private val _recentTransactions = MutableLiveData<List<Transaction>>(emptyList())
    val recentTransactions: LiveData<List<Transaction>> = _recentTransactions
    private val _financialInsight = MutableLiveData("Menganalisis keuangan...")
    val financialInsight: LiveData<String> = _financialInsight

    init {
        // Inisialisasi dengan satu dompet utama
        _pockets.value = listOf(Pocket(name = "Dompet Utama", balance = 500000.0))
        updateBalanceAndInsights()
    }

    // Fungsi ini dipanggil setiap kali ada perubahan saldo untuk me-refresh UI
    private fun updateBalanceAndInsights() {
        val totalBalance = _pockets.value?.sumOf { it.balance } ?: 0.0
        val localeID = Locale("in", "ID")
        val format = NumberFormat.getCurrencyInstance(localeID).apply { maximumFractionDigits = 0 }
        _formattedBalance.value = format.format(totalBalance)
        _financialInsight.value = FinancialAssistant.analyzeSpending(recentTransactions.value ?: emptyList())
        _pockets.value = _pockets.value
    }

    // --- Pockets Logic ---
    fun addPocket(name: String) {
        val currentPockets = _pockets.value?.toMutableList() ?: mutableListOf()
        currentPockets.add(Pocket(name = name, balance = 0.0))
        _pockets.value = currentPockets
        updateBalanceAndInsights() // Refresh
    }

    fun deletePocket(pocketId: String) {
        val currentPockets = _pockets.value?.toMutableList() ?: return
        val pocketToDelete = currentPockets.find { it.id == pocketId }
        if (pocketToDelete != null && pocketToDelete.balance == 0.0 && pocketToDelete.name != "Dompet Utama") {
            currentPockets.remove(pocketToDelete)
            _pockets.value = currentPockets
            updateBalanceAndInsights() // Refresh
        }
    }

    fun topUpPocket(pocketId: String, amount: Double) {
        val currentPockets = _pockets.value?.toMutableList() ?: return
        currentPockets.find { it.id == pocketId }?.let { it.balance += amount }
        _pockets.value = currentPockets
        updateBalanceAndInsights() // Refresh
    }

    fun transferBetweenPockets(fromPocketId: String, toPocketId: String, amount: Double): Boolean {
        val currentPockets = _pockets.value?.toMutableList() ?: return false
        val fromPocket = currentPockets.find { it.id == fromPocketId }
        val toPocket = currentPockets.find { it.id == toPocketId }

        if (fromPocket != null && toPocket != null && fromPocket.balance >= amount) {
            fromPocket.balance -= amount
            toPocket.balance += amount
            _pockets.value = currentPockets
            updateBalanceAndInsights() // Refresh
            return true
        }
        return false
    }

    // --- Transaction & Card Logic ---
    // sourcePocketId bisa null, terutama untuk top-up dari luar
    fun addTransaction(transaction: Transaction, sourcePocketId: String?): Boolean {
        val pocketToChange = if (transaction.type == TransactionType.EXPENSE) {
            // Jika pengeluaran, harus ada sumber dana yang jelas
            _pockets.value?.find { it.id == sourcePocketId }
        } else {
            // Jika pemasukan (top up), default ke Dompet Utama jika tidak ada yang dipilih
            _pockets.value?.find { it.id == sourcePocketId } ?: _pockets.value?.firstOrNull()
        } ?: return false // Gagal jika pocket tidak ditemukan

        if (transaction.type == TransactionType.EXPENSE) {
            if (pocketToChange.balance < transaction.amount) return false // Saldo tidak cukup
            pocketToChange.balance -= transaction.amount
        } else {
            pocketToChange.balance += transaction.amount
        }

        val currentTransactions = _recentTransactions.value?.toMutableList() ?: mutableListOf()
        currentTransactions.add(0, transaction)
        _recentTransactions.value = currentTransactions.sortedByDescending { it.date }
        updateBalanceAndInsights() // Refresh
        return true
    }

    fun addCard(card: DebitCard) {
        val currentCards = _savedCards.value?.toMutableList() ?: mutableListOf()
        currentCards.add(card)
        _savedCards.value = currentCards
    }
}
