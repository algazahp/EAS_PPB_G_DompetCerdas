package com.example.dompetcerdas.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.dompetcerdas.ui.screens.*
import com.example.dompetcerdas.viewmodel.AuthViewModel
import com.example.dompetcerdas.viewmodel.DashboardViewModel

enum class Screen {
    DASHBOARD, TRANSFER, HISTORY, PROFILE, TOP_UP, PAY_QR, ADD_CARD, SAVED_CARDS, LOGIN, SIGN_UP, POCKETS
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel,
    authViewModel: AuthViewModel,
    isLoggedIn: Boolean
) {
    if (isLoggedIn) {
        val bottomNavRoutes = setOf(Screen.DASHBOARD.name, Screen.HISTORY.name, Screen.POCKETS.name, Screen.PROFILE.name)
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        val shouldShowBottomBar = currentRoute in bottomNavRoutes

        Scaffold(
            bottomBar = {
                if (shouldShowBottomBar) {
                    AppBottomNavigationBar(navController = navController)
                }
            }
        ) { innerPadding ->
            MainAppNavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                dashboardViewModel = dashboardViewModel,
                authViewModel = authViewModel
            )
        }
    } else {
        AuthNavHost(navController = navController, authViewModel = authViewModel)
    }
}

@Composable
fun MainAppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    dashboardViewModel: DashboardViewModel,
    authViewModel: AuthViewModel
) {
    val balance by dashboardViewModel.formattedBalance.observeAsState("Rp0")
    val insight by dashboardViewModel.financialInsight.observeAsState("Menganalisis keuangan...")
    val transactions by dashboardViewModel.recentTransactions.observeAsState(emptyList())
    val savedCards by dashboardViewModel.savedCards.observeAsState(emptyList())

    NavHost(navController = navController, startDestination = Screen.DASHBOARD.name, modifier = modifier) {
        composable(Screen.DASHBOARD.name) {
            DashboardScreen(
                balance = balance,
                insight = insight,
                transactions = transactions,
                onNavigateToTransfer = { navController.navigate(Screen.TRANSFER.name) },
                onNavigateToHistory = { navController.navigate(Screen.HISTORY.name) },
                onNavigateToTopUp = { navController.navigate(Screen.TOP_UP.name) },
                onNavigateToPayQr = { navController.navigate(Screen.PAY_QR.name) },
                onNavigateToSavedCards = { navController.navigate(Screen.SAVED_CARDS.name) }
            )
        }
        composable(Screen.POCKETS.name) {
            PocketsScreen(navController = navController, viewModel = dashboardViewModel)
        }
        composable(Screen.TRANSFER.name) { TransferScreen(navController, dashboardViewModel) }
        composable(Screen.HISTORY.name) { HistoryScreen(navController, transactions) }
        composable(Screen.PROFILE.name) { ProfileScreen(navController, authViewModel) }
        composable(Screen.TOP_UP.name) { TopUpScreen(navController, dashboardViewModel) }
        composable(Screen.PAY_QR.name) { PayQrScreen(navController) }
        composable(Screen.ADD_CARD.name) { AddCardScreen(navController, dashboardViewModel) }
        composable(Screen.SAVED_CARDS.name) { SavedCardsScreen(navController, savedCards) }
    }
}

@Composable
fun AuthNavHost(navController: NavHostController, authViewModel: AuthViewModel) {
    NavHost(navController, startDestination = Screen.LOGIN.name) {
        composable(Screen.LOGIN.name) { LoginScreen(navController, authViewModel) }
        composable(Screen.SIGN_UP.name) { SignUpScreen(navController, authViewModel) }
    }
}

// --- Bottom Navigation Bar ---
sealed class BottomNavItem(val route: String, val icon: ImageVector, val label: String) {
    object Home : BottomNavItem(Screen.DASHBOARD.name, Icons.Default.Home, "Beranda")
    object History : BottomNavItem(Screen.HISTORY.name, Icons.Default.History, "Riwayat")
    object Pockets : BottomNavItem(Screen.POCKETS.name, Icons.Default.AccountBalanceWallet, "Dompet")
    object Profile : BottomNavItem(Screen.PROFILE.name, Icons.Default.Person, "Profil")
}

@Composable
fun AppBottomNavigationBar(navController: NavController) {
    val items = listOf(BottomNavItem.Home, BottomNavItem.History, BottomNavItem.Pockets, BottomNavItem.Profile)
    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { popUpTo(it) { saveState = true } }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
