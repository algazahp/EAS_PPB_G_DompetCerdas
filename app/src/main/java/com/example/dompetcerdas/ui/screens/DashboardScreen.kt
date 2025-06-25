package com.example.dompetcerdas.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dompetcerdas.data.Transaction
import com.example.dompetcerdas.data.TransactionType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    balance: String,
    insight: String,
    transactions: List<Transaction>,
    onNavigateToTransfer: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToTopUp: () -> Unit,
    onNavigateToPayQr: () -> Unit,
    onNavigateToSavedCards: () -> Unit // Navigasi baru
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { BalanceCard(balance) }
        item {
            ActionMenu(
                onTransferClick = onNavigateToTransfer,
                onTopUpClick = onNavigateToTopUp,
                onPayQrClick = onNavigateToPayQr,
                onMoreClick = onNavigateToSavedCards // Diperbarui
            )
        }
        item { InsightCard(insight) }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaksi Terakhir",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = onNavigateToHistory) {
                    Text("Lihat Semua")
                }
            }
        }
        items(transactions.take(5)) { transaction ->
            TransactionItem(transaction = transaction)
        }
    }
}

@Composable
fun BalanceCard(balance: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(
                text = "Total Saldo",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = balance,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
fun ActionMenu(
    onTransferClick: () -> Unit,
    onTopUpClick: () -> Unit,
    onPayQrClick: () -> Unit,
    onMoreClick: () -> Unit // Diperbarui
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ActionButton(icon = Icons.Default.CompareArrows, label = "Transfer", onClick = onTransferClick)
        ActionButton(icon = Icons.Default.ArrowUpward, label = "Isi Saldo", onClick = onTopUpClick)
        ActionButton(icon = Icons.Default.QrCodeScanner, label = "Bayar", onClick = onPayQrClick)
        // Menggunakan ikon 'Apps' dan mengarahkan ke halaman kartu
        ActionButton(icon = Icons.Default.CreditCard, label = "Kartu", onClick = onMoreClick)
    }
}

@Composable
fun ActionButton(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Medium)
    }
}

// ... (Sisa kode DashboardScreen tetap sama) ...
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightCard(insight: String) {
    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Insights, contentDescription = "Insight Icon", modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.width(16.dp))
            Column {
                Text(text = "Insight Keuangan", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = insight, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionItem(transaction: Transaction) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
    val amountColor = if (transaction.type == TransactionType.INCOME) Color(0xFF28A745) else Color(0xFFDC3545)

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = transaction.category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = transaction.notes, fontSize = 14.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = transaction.getFormattedAmount(), fontWeight = FontWeight.Bold, color = amountColor, fontSize = 16.sp)
                Text(text = dateFormat.format(transaction.date), fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}
