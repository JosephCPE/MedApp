package ph.edu.auf.student.lacson.joseph.medapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import ph.edu.auf.student.lacson.joseph.medapp.data.api.RetrofitClient
import ph.edu.auf.student.lacson.joseph.medapp.data.local.MedAppDatabase
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.HealthLog
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.HealthTip
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.UserProfile

class MedAppRepository(
    private val database: MedAppDatabase,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val userProfileDao = database.userProfileDao()
    private val healthLogDao = database.healthLogDao()
    private val healthTipDao = database.healthTipDao()

    fun getCurrentUserId(): String? = auth.currentUser?.uid

    suspend fun signUpWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<String> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<String> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val userId = result.user?.uid ?: throw Exception("User ID is null")
            Result.success(userId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun signOut() {
        auth.signOut()
    }

    suspend fun saveUserProfile(profile: UserProfile) {
        userProfileDao.insertUserProfile(profile)
        syncUserProfileToFirestore(profile)
    }

    suspend fun getUserProfile(userId: String): UserProfile? {
        return userProfileDao.getUserProfileOnce(userId)
    }

    fun getUserProfileFlow(userId: String): Flow<UserProfile?> {
        return userProfileDao.getUserProfile(userId)
    }

    private suspend fun syncUserProfileToFirestore(profile: UserProfile) {
        try {
            val data = hashMapOf(
                "userId" to profile.userId,
                "name" to profile.name,
                "age" to profile.age,
                "weight" to profile.weight,
                "healthConditions" to profile.healthConditions,
                "lastSyncTimestamp" to System.currentTimeMillis()
            )
            firestore.collection("userProfiles")
                .document(profile.userId)
                .set(data)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun saveHealthLog(log: HealthLog) {
        healthLogDao.insertHealthLog(log)
        syncHealthLogToFirestore(log)
    }

    suspend fun getHealthLogs(userId: String): List<HealthLog> {
        return healthLogDao.getHealthLogsOnce(userId)
    }

    fun getHealthLogsFlow(userId: String): Flow<List<HealthLog>> {
        return healthLogDao.getHealthLogs(userId)
    }

    private suspend fun syncHealthLogToFirestore(log: HealthLog) {
        try {
            val data = hashMapOf(
                "userId" to log.userId,
                "date" to log.date,
                "systolic" to log.systolic,
                "diastolic" to log.diastolic,
                "heartRate" to log.heartRate,
                "temperature" to log.temperature,
                "weight" to log.weight,
                "timestamp" to log.timestamp
            )
            firestore.collection("healthLogs")
                .document(log.id.toString())
                .set(data)
                .await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun fetchHealthTipsFromApi(): Result<List<HealthTip>> {
        return try {
            val tips = mutableListOf<HealthTip>()
            repeat(5) {
                val response = RetrofitClient.apiService.getHealthTip()
                val tip = HealthTip(
                    title = "Health Tip ${it + 1}",
                    description = response.tip,
                    category = "General Health",
                    cachedTimestamp = System.currentTimeMillis()
                )
                tips.add(tip)
            }
            healthTipDao.insertHealthTips(tips)
            Result.success(tips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCachedHealthTips(): List<HealthTip> {
        return healthTipDao.getHealthTipsOnce()
    }

    fun getHealthTipsFlow(): Flow<List<HealthTip>> {
        return healthTipDao.getHealthTips()
    }

    suspend fun syncFromFirestore(userId: String) {
        try {
            val profileSnapshot = firestore.collection("userProfiles")
                .document(userId)
                .get()
                .await()

            if (profileSnapshot.exists()) {
                val profile = UserProfile(
                    userId = userId,
                    name = profileSnapshot.getString("name") ?: "",
                    age = profileSnapshot.getLong("age")?.toInt() ?: 0,
                    weight = profileSnapshot.getDouble("weight") ?: 0.0,
                    healthConditions = profileSnapshot.getString("healthConditions") ?: "",
                    lastSyncTimestamp = profileSnapshot.getLong("lastSyncTimestamp") ?: 0L
                )
                userProfileDao.insertUserProfile(profile)
            }

            val logsSnapshot = firestore.collection("healthLogs")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            val logs = logsSnapshot.documents.mapNotNull { doc ->
                HealthLog(
                    id = doc.id.toIntOrNull() ?: 0,
                    userId = userId,
                    date = doc.getLong("date") ?: 0L,
                    systolic = doc.getLong("systolic")?.toInt() ?: 0,
                    diastolic = doc.getLong("diastolic")?.toInt() ?: 0,
                    heartRate = doc.getLong("heartRate")?.toInt() ?: 0,
                    temperature = doc.getDouble("temperature") ?: 0.0,
                    weight = doc.getDouble("weight") ?: 0.0,
                    timestamp = doc.getLong("timestamp") ?: 0L
                )
            }
            healthLogDao.insertHealthLogs(logs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}