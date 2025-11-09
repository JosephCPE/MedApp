package ph.edu.auf.student.lacson.joseph.medapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.HealthTip

@Dao
interface HealthTipDao {
    @Query("SELECT * FROM health_tips ORDER BY cachedTimestamp DESC")
    fun getHealthTips(): Flow<List<HealthTip>>

    @Query("SELECT * FROM health_tips ORDER BY cachedTimestamp DESC")
    suspend fun getHealthTipsOnce(): List<HealthTip>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthTip(tip: HealthTip)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthTips(tips: List<HealthTip>)

    @Query("DELETE FROM health_tips")
    suspend fun deleteAllHealthTips()
}