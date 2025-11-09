package ph.edu.auf.student.lacson.joseph.medapp.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import ph.edu.auf.student.lacson.joseph.medapp.ui.screens.*
import ph.edu.auf.student.lacson.joseph.medapp.ui.viewmodels.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    authViewModel: AuthViewModel,
    profileViewModel: ProfileViewModel,
    healthLogViewModel: HealthLogViewModel,
    healthTipsViewModel: HealthTipsViewModel
) {
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem("Log", Screen.HealthLog.route, Icons.Filled.Add),
        BottomNavItem("Analytics", Screen.Analytics.route, Icons.Filled.BarChart),
        BottomNavItem("Tips", Screen.HealthTips.route, Icons.Filled.LocalHospital),
        BottomNavItem("Profile", Screen.Profile.route, Icons.Filled.Person)
    )

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            if (currentDestination?.route != Screen.Auth.route) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Auth.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.HealthLog.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.HealthLog.route) {
                HealthLogScreen(viewModel = healthLogViewModel)
            }

            composable(Screen.Analytics.route) {
                AnalyticsScreen(viewModel = healthLogViewModel)
            }

            composable(Screen.HealthTips.route) {
                HealthTipsScreen(viewModel = healthTipsViewModel)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    profileViewModel = profileViewModel,
                    authViewModel = authViewModel,
                    onLogout = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)