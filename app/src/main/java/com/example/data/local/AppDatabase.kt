package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AdminConfig
import com.example.data.model.EsportsMatch
import com.example.data.model.MatchTeamResult
import com.example.data.model.Player
import com.example.data.model.Team
import com.example.data.model.Tournament

@Database(
    entities = [
        Tournament::class,
        Team::class,
        Player::class,
        EsportsMatch::class,
        MatchTeamResult::class,
        AdminConfig::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun esportsDao(): EsportsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "esports_scoreboard.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
