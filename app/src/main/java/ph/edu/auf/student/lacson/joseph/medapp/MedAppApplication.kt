package ph.edu.auf.student.lacson.joseph.medapp

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import ph.edu.auf.student.lacson.joseph.medapp.data.local.MedAppDatabase
import ph.edu.auf.student.lacson.joseph.medapp.data.repository.MedAppRepository

class MedAppApplication : Application() {

    lateinit var database: MedAppDatabase
        private set

    lateinit var repository: MedAppRepository
        private set

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        database = MedAppDatabase.getDatabase(this)

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        repository = MedAppRepository(database, auth, firestore)
    }
}