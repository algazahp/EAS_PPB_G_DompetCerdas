package com.example.dompetcerdas.services

import android.util.Log
import com.example.dompetcerdas.data.SavingGoal
import com.example.dompetcerdas.data.Transaction
import com.example.dompetcerdas.data.TransactionType

// Objek untuk mengelola logika gamifikasi terkait menabung.
object SavingsChallengeService {

    private const val TAG = "SavingsChallenge"

    /**
     * Memulai tantangan menabung 52 minggu.
     *
     * @param goal Objek SavingGoal yang akan menjadi target tantangan ini.
     * @param startingAmount Jumlah uang yang ditabung di minggu pertama.
     */
    fun start52WeekChallenge(goal: SavingGoal, startingAmount: Double) {
        Log.i(TAG, "Memulai 'Tantangan 52 Minggu' untuk tujuan '${goal.name}' dengan nominal awal Rp$startingAmount")
        // TODO:
        // 1. Buat jadwal atau job di server/lokal (menggunakan WorkManager) untuk berjalan setiap minggu.
        // 2. Setiap minggu, jalankan auto-debet dari rekening utama ke rekening tujuan (goal).
        // 3. Nominal yang didebet akan bertambah sebesar 'startingAmount' setiap minggunya.
        //    (Minggu 1: 10.000, Minggu 2: 20.000, dst.)
    }

    /**
     * Memproses sebuah transaksi untuk "dibulatkan" dan menyimpan selisihnya.
     *
     * @param transaction Transaksi pengeluaran yang baru saja dibuat.
     * @param targetGoal Tujuan menabung (SavingGoal) untuk menyimpan uang pembulatan.
     * @return Double Mengembalikan jumlah uang yang berhasil ditabung dari pembulatan. 0 jika tidak ada.
     */
    fun roundUpAndSave(transaction: Transaction, targetGoal: SavingGoal): Double {
        // Pastikan hanya memproses transaksi pengeluaran
        if (transaction.type != TransactionType.EXPENSE) {
            return 0.0
        }

        // Tentukan target pembulatan (misal: ke 10.000 atau 5.000 terdekat)
        val roundToNearest = 10000.0

        val transactionAmount = transaction.amount
        val remainder = transactionAmount % roundToNearest

        // Jika tidak ada sisa, berarti jumlahnya pas, tidak ada yang perlu dibulatkan
        if (remainder == 0.0) {
            return 0.0
        }

        val amountToSave = roundToNearest - remainder

        Log.i(TAG, "Pembulatan dari transaksi ${transaction.getFormattedAmount()}: Menabung sebesar Rp${"%,.0f".format(amountToSave)} ke '${targetGoal.name}'")

        // TODO:
        // 1. Panggil API/Repository untuk melakukan transfer 'amountToSave' dari rekening utama ke 'targetGoal'.
        // 2. Pastikan proses ini atomic (jika gagal, batalkan semua).
        // updateSavingGoal(targetGoal.id, amountToSave)

        return amountToSave
    }
}