package ph.edu.auf.student.lacson.joseph.medapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_logs")
data class HealthLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: String = "",
    val date: Long = 0L,
    val systolic: Int = 0,
    val diastolic: Int = 0,
    val heartRate: Int = 0,
    val temperature: Double = 0.0,
    val weight: Double = 0.0,
    val timestamp: Long = 0L
)