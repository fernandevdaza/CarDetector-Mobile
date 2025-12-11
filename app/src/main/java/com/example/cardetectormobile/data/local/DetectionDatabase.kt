package com.example.cardetectormobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.cardetectormobile.data.local.dao.DetectionDao
import com.example.cardetectormobile.data.local.entity.DetectionHistoryEntity

@Database(
    entities = [DetectionHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class DetectionDatabase : RoomDatabase() {
    abstract fun detectionDao(): DetectionDao
}
