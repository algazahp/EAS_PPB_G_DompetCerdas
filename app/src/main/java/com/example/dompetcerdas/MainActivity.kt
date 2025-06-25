package com.example.dompetcerdas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.dompetcerdas.navigation.AppNavigation
import com.example.dompetcerdas.ui.theme.DompetcerdasTheme
import com.example.dompetcerdas.viewmodel.AuthViewModel
import com.example.dompetcerdas.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
    // Inisialisasi ViewModel yang diperlukan
    private val dashboardViewModel: DashboardViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DompetcerdasTheme {
                // Controller untuk mengelola navigasi antar layar
                val navController = rememberNavController()
                // Mengamati status login dari AuthViewModel
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

                // AppNavigation akan menangani layar mana yang harus ditampilkan
                // berdasarkan status login dan rute navigasi saat ini.
                AppNavigation(
                    navController = navController,
                    dashboardViewModel = dashboardViewModel,
                    authViewModel = authViewModel,
                    isLoggedIn = isLoggedIn
                )
            }
        }
    }
}
