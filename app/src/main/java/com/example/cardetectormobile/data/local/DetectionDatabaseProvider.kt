package com.example.cardetectormobile.data.local

import android.content.Context
import androidx.room.Room

object DetectionDatabaseProvider {

    @Volatile
    private var INSTANCE: DetectionDatabase? = null

    fun getDatabase(context: Context): DetectionDatabase {
        return INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                DetectionDatabase::class.java,
                "detection_db"
            ).build().also { INSTANCE = it }
        }
    }
}
