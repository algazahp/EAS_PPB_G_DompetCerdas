package com.example.dompetcerdas.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dompetcerdas.navigation.Screen

// Data class untuk merepresentasikan item di bottom navigation
data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)

@Composable
fun AppBottomNavigationBar(navController: NavController) {
    // Daftar item navigasi
    val navItems = listOf(
        BottomNavItem("Home", Icons.Default.Home, Screen.DASHBOARD.name),
        BottomNavItem("Riwayat", Icons.Default.List, Screen.HISTORY.name),
        BottomNavItem("Profil", Icons.Default.AccountCircle, Screen.PROFILE.name)
    )

    NavigationBar {
        // Mengambil entri back stack saat ini untuk mengetahui rute aktif
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = backStackEntry?.destination?.route

        // Membuat setiap item di navigation bar
        navItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    // Navigasi ke rute yang dipilih
                    navController.navigate(item.route) {
                        // Konfigurasi untuk menghindari tumpukan navigasi yang besar
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) }
            )
        }
    }
}
