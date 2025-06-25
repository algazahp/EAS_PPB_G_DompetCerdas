package com.example.dompetcerdas.data

// Enum untuk merepresentasikan jenis kartu
enum class CardType {
    VISA, MASTERCARD, UNKNOWN
}

// Data class untuk kartu debit yang disimpan
data class DebitCard(
    val id: String,
    val cardNumber: String,
    val expiryDate: String, // Disimpan sebagai MM/YY
    val cvv: String, // Seharusnya tidak disimpan dalam produksi nyata
    val cardType: CardType = CardType.UNKNOWN
) {
    // Fungsi untuk mendapatkan 4 digit terakhir kartu
    fun getLast4Digits(): String {
        return cardNumber.takeLast(4)
    }

    // Fungsi untuk menyembunyikan nomor kartu
    fun getMaskedCardNumber(): String {
        return "**** **** **** ${getLast4Digits()}"
    }
}
