package ph.edu.auf.student.lacson.joseph.medapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import ph.edu.auf.student.lacson.joseph.medapp.ui.navigation.NavGraph
import ph.edu.auf.student.lacson.joseph.medapp.ui.theme.MedAppTheme
import ph.edu.auf.student.lacson.joseph.medapp.ui.viewmodels.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val application = applicationContext as MedAppApplication
        val repository = application.repository

        val authViewModel = AuthViewModel(repository)
        val profileViewModel = ProfileViewModel(repository)
        val healthLogViewModel = HealthLogViewModel(repository)
        val healthTipsViewModel = HealthTipsViewModel(repository)

        setContent {
            MedAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavGraph(
                        authViewModel = authViewModel,
                        profileViewModel = profileViewModel,
                        healthLogViewModel = healthLogViewModel,
                        healthTipsViewModel = healthTipsViewModel
                    )
                }
            }
        }
    }
}