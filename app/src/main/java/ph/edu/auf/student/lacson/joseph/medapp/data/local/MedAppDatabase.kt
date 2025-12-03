package ph.edu.auf.student.lacson.joseph.medapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ph.edu.auf.student.lacson.joseph.medapp.data.local.dao.HealthLogDao
import ph.edu.auf.student.lacson.joseph.medapp.data.local.dao.HealthTipDao
import ph.edu.auf.student.lacson.joseph.medapp.data.local.dao.UserProfileDao
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.HealthLog
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.HealthTip
import ph.edu.auf.student.lacson.joseph.medapp.data.local.entities.UserProfile

@Database(
    entities = [UserProfile::class, HealthLog::class, HealthTip::class],
    version = 2,
    exportSchema = false
)
abstract class MedAppDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun healthLogDao(): HealthLogDao
    abstract fun healthTipDao(): HealthTipDao

    companion object {
        @Volatile
        private var INSTANCE: MedAppDatabase? = null

        fun getDatabase(context: Context): MedAppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MedAppDatabase::class.java,
                    "medapp_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
