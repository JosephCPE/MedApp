package ph.edu.auf.student.lacson.joseph.medapp.ui.navigation

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
    object HealthLog : Screen("health_log")
    object Analytics : Screen("analytics")
    object HealthTips : Screen("health_tips")
    object Profile : Screen("profile")
}