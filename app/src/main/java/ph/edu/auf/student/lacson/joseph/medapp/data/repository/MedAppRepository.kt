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
import kotlin.random.Random

class MedAppRepository(
    private val database: MedAppDatabase,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {

    private val userProfileDao = database.userProfileDao()
    private val healthLogDao = database.healthLogDao()
    private val healthTipDao = database.healthTipDao()

    companion object {

        private const val NEWS_API_KEY = "92e80bb46e804fb695e6dc4698f3cf3c"
    }

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

            try {
                if (NEWS_API_KEY.isNotBlank()) {
                    // Randomize page for fresh articles each refresh (adjust range as needed)
                    val page = Random.nextInt(1, 5)
                    val newsResponse = RetrofitClient.newsApiService.getHealthNews(
                        apiKey = NEWS_API_KEY,
                        page = page
                    )

                    // Shuffle to vary ordering
                    newsResponse.articles.shuffled().forEach { article ->
                        tips.add(
                            HealthTip(
                                title = article.title,
                                description = article.description ?: "No description available",
                                category = "Health News",
                                imageUrl = article.imageUrl,   // may be null; UI handles it
                                sourceUrl = article.url,
                                cachedTimestamp = System.currentTimeMillis()
                            )
                        )
                    }
                } else {
                    throw Exception("News API key not configured")
                }
            } catch (e: Exception) {
                // Fallback to API Ninjas or sample tips
                repeat(10) { index ->
                    try {
                        val response = RetrofitClient.apiService.getHealthTip()
                        tips.add(
                            HealthTip(
                                title = "Daily Health Tip ${index + 1}",
                                description = response.tip,
                                category = "General Health",
                                imageUrl = getPlaceholderImageUrl(index),
                                sourceUrl = null,
                                cachedTimestamp = System.currentTimeMillis()
                            )
                        )
                    } catch (apiError: Exception) {
                        tips.add(
                            HealthTip(
                                title = getSampleHealthTipTitle(index),
                                description = getSampleHealthTipDescription(index),
                                category = "General Health",
                                imageUrl = getPlaceholderImageUrl(index),
                                sourceUrl = null,
                                cachedTimestamp = System.currentTimeMillis()
                            )
                        )
                    }
                }
            }

            if (tips.isNotEmpty()) {
                healthTipDao.deleteAllHealthTips()
                healthTipDao.insertHealthTips(tips)
            }

            Result.success(tips)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun getPlaceholderImageUrl(index: Int): String {
        val healthImages = listOf(
            "https://images.unsplash.com/photo-1505751172876-fa1923c5c528?w=800",
            "https://images.unsplash.com/photo-1559757148-5c350d0d3c56?w=800",
            "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800",
            "https://images.unsplash.com/photo-1505576399279-565b52d4ac71?w=800",
            "https://images.unsplash.com/photo-1584515933487-779824d29309?w=800",
            "https://images.unsplash.com/photo-1506126613408-eca07ce68773?w=800",
            "https://images.unsplash.com/photo-1498837167922-ddd27525d352?w=800",
            "https://images.unsplash.com/photo-1490645935967-10de6ba17061?w=800",
            "https://images.unsplash.com/photo-1512621776951-a57141f2eefd?w=800",
            "https://images.unsplash.com/photo-1511690656952-34342bb7c2f2?w=800"
        )
        return healthImages[index % healthImages.size]
    }

    private fun getSampleHealthTipTitle(index: Int): String {
        val titles = listOf(
            "Stay Hydrated",
            "Get Regular Exercise",
            "Eat a Balanced Diet",
            "Get Enough Sleep",
            "Manage Stress",
            "Regular Health Checkups",
            "Practice Good Hygiene",
            "Limit Screen Time",
            "Stay Socially Connected",
            "Practice Mindfulness"
        )
        return titles[index % titles.size]
    }

    private fun getSampleHealthTipDescription(index: Int): String {
        val descriptions = listOf(
            "Drinking adequate water throughout the day helps maintain body temperature, keeps joints lubricated, prevents infections, and delivers nutrients to cells.",
            "Regular physical activity can improve your muscle strength and boost your endurance. Exercise helps deliver oxygen and nutrients to your tissues.",
            "Eating a variety of foods from all food groups helps your body get the nutrients it needs. Focus on fruits, vegetables, whole grains, and lean proteins.",
            "Quality sleep is essential for good health. Adults should aim for 7-9 hours of sleep per night for optimal health and wellbeing.",
            "Chronic stress can affect your health. Practice relaxation techniques like deep breathing, meditation, or yoga to manage stress effectively.",
            "Regular health screenings can help detect problems before they start. Schedule regular checkups with your healthcare provider.",
            "Washing hands regularly, maintaining dental hygiene, and keeping your environment clean can prevent many illnesses and infections.",
            "Excessive screen time can lead to eye strain, poor posture, and sleep issues. Take regular breaks and limit recreational screen time.",
            "Maintaining strong social connections can improve mental health, boost immune function, and increase longevity.",
            "Mindfulness and meditation can reduce stress, improve focus, and enhance emotional wellbeing. Practice daily for best results."
        )
        return descriptions[index % descriptions.size]
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
