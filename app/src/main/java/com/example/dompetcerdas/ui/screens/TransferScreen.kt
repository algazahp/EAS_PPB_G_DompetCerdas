package com.example.dompetcerdas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dompetcerdas.data.Transaction
import com.example.dompetcerdas.data.TransactionType
import com.example.dompetcerdas.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.*

private enum class TransferMode { BANK, PHONE, NONE }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferScreen(navController: NavController, viewModel: DashboardViewModel) {
    var recipient by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var transferMode by remember { mutableStateOf(TransferMode.NONE) }
    var category by remember { mutableStateOf("") } // State untuk kategori manual

    val pockets by viewModel.pockets.observeAsState(emptyList())
    var expanded by remember { mutableStateOf(false) }
    var selectedPocket by remember { mutableStateOf(pockets.firstOrNull()) }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer Dana") },
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (transferMode != TransferMode.NONE) {
                // Dropdown untuk memilih sumber dompet
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPocket?.name ?: "Pilih Dompet",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Sumber Dana") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        pockets.forEach { pocket ->
                            DropdownMenuItem(
                                text = { Text("${pocket.name} (${formatToRupiah(pocket.balance)})") },
                                onClick = {
                                    selectedPocket = pocket
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = recipient,
                    onValueChange = {
                        val maxChar = if (transferMode == TransferMode.BANK) 17 else 15
                        if (it.all { char -> char.isDigit() } && it.length <= maxChar) recipient = it
                    },
                    label = { Text(if (transferMode == TransferMode.BANK) "Nomor Rekening Tujuan" else "Nomor HP Tujuan") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { if (it.all { char -> char.isDigit() }) amount = it },
                    label = { Text("Jumlah Transfer (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )

                // Kolom input manual untuk kategori
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Kategori Transfer") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )

                OutlinedTextField(
                    value = notes, onValueChange = { notes = it },
                    label = { Text("Catatan (Opsional)") },
                    modifier = Modifier.fillMaxWidth(), singleLine = true
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { transferMode = TransferMode.BANK }) { Text("Ke Rek. Bank") }
                Button(onClick = { transferMode = TransferMode.PHONE }) { Text("Ke No. HP") }
            }

            Button(
                onClick = {
                    val transferAmount = amount.toDoubleOrNull()
                    val sourcePocketId = selectedPocket?.id
                    if (recipient.isNotBlank() && category.isNotBlank() && transferAmount != null && transferAmount > 0 && sourcePocketId != null) {
                        val newTransaction = Transaction(
                            id = UUID.randomUUID().toString(),
                            amount = transferAmount,
                            category = category, // Menggunakan kategori dari input manual
                            notes = "Kirim ke $recipient. ${notes}",
                            date = Date(), type = TransactionType.EXPENSE,
                            storeName = recipient
                        )
                        val success = viewModel.addTransaction(newTransaction, sourcePocketId)
                        if (success) {
                            Toast.makeText(context, "Transfer berhasil!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } else {
                            Toast.makeText(context, "Saldo di dompet tidak mencukupi!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Data transfer tidak valid! Pastikan semua kolom terisi.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = recipient.isNotBlank() && amount.isNotBlank() && category.isNotBlank() && transferMode != TransferMode.NONE
            ) {
                Text("Kirim Sekarang", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

private fun formatToRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID).apply { maximumFractionDigits = 0 }
    return format.format(amount)
}
