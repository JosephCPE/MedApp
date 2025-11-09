package ph.edu.auf.student.lacson.joseph.medapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey
    val userId: String = "",
    val name: String = "",
    val age: Int = 0,
    val weight: Double = 0.0,
    val healthConditions: String = "",
    val lastSyncTimestamp: Long = 0L
)