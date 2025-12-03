package ph.edu.auf.student.lacson.joseph.medapp.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "health_tips")
data class HealthTip(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val imageUrl: String?,
    val sourceUrl: String?,
    val cachedTimestamp: Long = 0L
)