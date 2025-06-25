package com.example.dompetcerdas.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.dompetcerdas.data.Pocket
import com.example.dompetcerdas.viewmodel.DashboardViewModel
import java.text.NumberFormat
import java.util.Locale

private enum class DialogType { NONE, ADD_POCKET, TRANSFER, TOP_UP }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PocketsScreen(navController: NavController, viewModel: DashboardViewModel) {
    val pockets by viewModel.pockets.observeAsState(emptyList())
    val totalBalance by viewModel.formattedBalance.observeAsState("Rp0")
    var showDialog by remember { mutableStateOf(DialogType.NONE) }
    val context = LocalContext.current

    when (showDialog) {
        DialogType.ADD_POCKET -> AddPocketDialog(
            onDismiss = { showDialog = DialogType.NONE },
            onConfirm = { name -> viewModel.addPocket(name); showDialog = DialogType.NONE }
        )
        DialogType.TRANSFER -> TransferPocketDialog(pockets = pockets, onDismiss = { showDialog = DialogType.NONE },
            onConfirm = { from, to, amount ->
                if (viewModel.transferBetweenPockets(from, to, amount)) Toast.makeText(context, "Transfer berhasil!", Toast.LENGTH_SHORT).show()
                else Toast.makeText(context, "Saldo tidak cukup!", Toast.LENGTH_SHORT).show()
                showDialog = DialogType.NONE
            }
        )
        DialogType.TOP_UP -> TopUpPocketDialog(pockets = pockets, onDismiss = { showDialog = DialogType.NONE },
            onConfirm = { pocket, amount ->
                viewModel.topUpPocket(pocket, amount)
                Toast.makeText(context, "Isi saldo berhasil!", Toast.LENGTH_SHORT).show()
                showDialog = DialogType.NONE
            }
        )
        DialogType.NONE -> {}
    }

    Scaffold(
        floatingActionButton = { FloatingActionButton(onClick = { showDialog = DialogType.ADD_POCKET }) { Icon(Icons.Default.Add, "Tambah Dompet") } }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            // Kartu Total Saldo
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Total Saldo Semua Dompet", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(totalBalance, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { showDialog = DialogType.TOP_UP }, modifier = Modifier.weight(1f)) { Text("Isi Saldo") }
                Button(onClick = { showDialog = DialogType.TRANSFER }, modifier = Modifier.weight(1f)) { Text("Pindah Dana") }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Daftar Dompet", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(pockets) { pocket ->
                    PocketItem(pocket = pocket, onDelete = {
                        if (pocket.balance == 0.0) viewModel.deletePocket(pocket.id)
                        else Toast.makeText(context, "Saldo harus Rp0 untuk menghapus!", Toast.LENGTH_LONG).show()
                    })
                }
            }
        }
    }
}

@Composable
fun PocketItem(pocket: Pocket, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = pocket.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(text = formatToRupiah(pocket.balance), style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
            }
            if (pocket.name != "Dompet Utama") { // Dompet utama tidak bisa dihapus
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Hapus Dompet", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

// --- Dialogs ---

@Composable
fun AddPocketDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var text by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buat Dompet Baru") },
        text = { OutlinedTextField(value = text, onValueChange = { text = it }, label = { Text("Nama Dompet") }, singleLine = true) },
        confirmButton = { Button(onClick = { if (text.isNotBlank()) onConfirm(text) }, enabled = text.isNotBlank()) { Text("Buat") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopUpPocketDialog(pockets: List<Pocket>, onDismiss: () -> Unit, onConfirm: (String, Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedPocket by remember { mutableStateOf(pockets.firstOrNull()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Isi Saldo ke Dompet") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedPocket?.name ?: "Pilih Dompet",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Dompet Tujuan") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        pockets.forEach { pocket ->
                            DropdownMenuItem(text = { Text(pocket.name) }, onClick = {
                                selectedPocket = pocket
                                expanded = false
                            })
                        }
                    }
                }
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Jumlah (Rp)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = { Button(onClick = { selectedPocket?.let { onConfirm(it.id, amount.toDoubleOrNull() ?: 0.0) } }) { Text("Konfirmasi") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferPocketDialog(pockets: List<Pocket>, onDismiss: () -> Unit, onConfirm: (String, String, Double) -> Unit) {
    var amount by remember { mutableStateOf("") }
    var expandedFrom by remember { mutableStateOf(false) }
    var selectedFrom by remember { mutableStateOf(pockets.firstOrNull()) }
    var expandedTo by remember { mutableStateOf(false) }
    var selectedTo by remember { mutableStateOf(pockets.getOrNull(1)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pindahkan Dana Antar Dompet") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Dropdown "Dari Dompet"
                ExposedDropdownMenuBox(expanded = expandedFrom, onExpandedChange = { expandedFrom = !expandedFrom }) {
                    OutlinedTextField(value = selectedFrom?.name ?: "Pilih Dompet", onValueChange = {}, readOnly = true, label = { Text("Dari Dompet") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) }, modifier = Modifier.menuAnchor())
                    ExposedDropdownMenu(expanded = expandedFrom, onDismissRequest = { expandedFrom = false }) {
                        pockets.forEach { pocket -> DropdownMenuItem(text = { Text(pocket.name) }, onClick = { selectedFrom = pocket; expandedFrom = false }) }
                    }
                }
                // Dropdown "Ke Dompet"
                ExposedDropdownMenuBox(expanded = expandedTo, onExpandedChange = { expandedTo = !expandedTo }) {
                    OutlinedTextField(value = selectedTo?.name ?: "Pilih Dompet", onValueChange = {}, readOnly = true, label = { Text("Ke Dompet") }, trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) }, modifier = Modifier.menuAnchor())
                    ExposedDropdownMenu(expanded = expandedTo, onDismissRequest = { expandedTo = false }) {
                        pockets.filter { it.id != selectedFrom?.id }.forEach { pocket -> DropdownMenuItem(text = { Text(pocket.name) }, onClick = { selectedTo = pocket; expandedTo = false }) }
                    }
                }
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Jumlah (Rp)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
            }
        },
        confirmButton = {
            Button(onClick = {
                val fromId = selectedFrom?.id
                val toId = selectedTo?.id
                if (fromId != null && toId != null) {
                    onConfirm(fromId, toId, amount.toDoubleOrNull() ?: 0.0)
                }
            }) { Text("Pindahkan") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Batal") } }
    )
}


private fun formatToRupiah(amount: Double): String {
    val localeID = Locale("in", "ID")
    val format = NumberFormat.getCurrencyInstance(localeID)
    format.maximumFractionDigits = 0
    return format.format(amount)
}
