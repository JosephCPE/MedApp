package ph.edu.auf.student.lacson.joseph.medapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.HealthLog

@Dao
interface HealthLogDao {
    @Query("SELECT * FROM health_logs WHERE userId = :userId ORDER BY date DESC")
    fun getHealthLogs(userId: String): Flow<List<HealthLog>>

    @Query("SELECT * FROM health_logs WHERE userId = :userId ORDER BY date DESC")
    suspend fun getHealthLogsOnce(userId: String): List<HealthLog>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthLog(log: HealthLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthLogs(logs: List<HealthLog>)

    @Delete
    suspend fun deleteHealthLog(log: HealthLog)

    @Query("DELETE FROM health_logs WHERE userId = :userId")
    suspend fun deleteAllHealthLogs(userId: String)
}