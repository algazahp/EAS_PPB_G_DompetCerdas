package com.example.dompetcerdas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dompetcerdas.data.DebitCard
import com.example.dompetcerdas.data.Transaction
import com.example.dompetcerdas.data.TransactionType
import com.example.dompetcerdas.viewmodel.DashboardViewModel
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpScreen(navController: NavController, viewModel: DashboardViewModel) {
    val nominals = listOf(20000.0, 50000.0, 100000.0, 200000.0, 500000.0, 1000000.0)
    var selectedNominal by remember { mutableStateOf<Double?>(null) }
    var customAmount by remember { mutableStateOf("") }

    val savedCards by viewModel.savedCards.observeAsState(emptyList())
    var cardExpanded by remember { mutableStateOf(false) }
    var selectedCard by remember { mutableStateOf<DebitCard?>(null) }

    val context = LocalContext.current
    val finalAmount = customAmount.toDoubleOrNull() ?: selectedNominal

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Isi Saldo") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Dropdown untuk memilih Kartu
            if (savedCards.isNotEmpty()) {
                ExposedDropdownMenuBox(expanded = cardExpanded, onExpandedChange = { cardExpanded = !cardExpanded }) {
                    OutlinedTextField(
                        value = selectedCard?.getMaskedCardNumber() ?: "Pilih Kartu",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sumber Dana") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = cardExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = cardExpanded, onDismissRequest = { cardExpanded = false }) {
                        savedCards.forEach { card ->
                            DropdownMenuItem(text = { Text("${card.cardType} - ${card.getMaskedCardNumber()}") }, onClick = {
                                selectedCard = card
                                cardExpanded = false
                            })
                        }
                    }
                }
            } else {
                Text("Tambahkan kartu debit terlebih dahulu untuk mengisi saldo.")
            }

            Text("Pilih Nominal", style = MaterialTheme.typography.titleMedium)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(nominals) { nominal ->
                    NominalButton(amount = nominal, isSelected = selectedNominal == nominal && customAmount.isEmpty()) {
                        selectedNominal = nominal
                        customAmount = ""
                    }
                }
            }

            OutlinedTextField(
                value = customAmount,
                onValueChange = {
                    customAmount = it
                    if (it.isNotEmpty()) selectedNominal = null
                },
                label = { Text("Atau Masukkan Jumlah Lain") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    if (finalAmount != null && finalAmount > 0) {
                        val newTransaction = Transaction(
                            id = UUID.randomUUID().toString(),
                            amount = finalAmount,
                            category = "Isi Saldo",
                            notes = "Top up dari ${selectedCard?.cardType ?: "kartu"}",
                            date = Date(),
                            type = TransactionType.INCOME
                        )
                        // Top up akan masuk ke dompet utama
                        val success = viewModel.addTransaction(newTransaction, null)
                        if (success) {
                            Toast.makeText(context, "Isi saldo berhasil!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = finalAmount != null && finalAmount > 0 && selectedCard != null
            ) {
                Text("Konfirmasi Isi Saldo", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

@Composable
private fun NominalButton(amount: Double, isSelected: Boolean, onClick: () -> Unit) {
    val localeID = Locale("in", "ID")
    val format = java.text.NumberFormat.getCurrencyInstance(localeID).apply { maximumFractionDigits = 0 }
    OutlinedButton(
        onClick = onClick,
        colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer) else ButtonDefaults.outlinedButtonColors(),
        border = BorderStroke(width = if (isSelected) 2.dp else 1.dp, color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline),
        contentPadding = PaddingValues(4.dp)
    ) {
        Text(text = format.format(amount), fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
    }
}
