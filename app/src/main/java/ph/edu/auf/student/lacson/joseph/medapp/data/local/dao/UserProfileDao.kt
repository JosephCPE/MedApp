package ph.edu.auf.student.lacson.joseph.medapp.data.local.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.UserProfile

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    fun getUserProfile(userId: String): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles WHERE userId = :userId LIMIT 1")
    suspend fun getUserProfileOnce(userId: String): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(profile: UserProfile)

    @Update
    suspend fun updateUserProfile(profile: UserProfile)

    @Delete
    suspend fun deleteUserProfile(profile: UserProfile)
}