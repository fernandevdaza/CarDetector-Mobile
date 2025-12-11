package com.example.cardetectormobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cardetectormobile.data.local.entity.DetectionHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DetectionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDetection(detection: DetectionHistoryEntity)

    @Query("SELECT * FROM detection_history WHERE userId = :userId ORDER BY createdAt DESC")
    fun getDetectionsForUser(userId: String): Flow<List<DetectionHistoryEntity>>

    @Query("SELECT * FROM detection_history ORDER BY createdAt DESC")
    fun getAllDetections(): Flow<List<DetectionHistoryEntity>>

    @Query("DELETE FROM detection_history WHERE id = :id")
    suspend fun deleteDetection(id: Long)

    @Query("DELETE FROM detection_history WHERE userId = :userId")
    suspend fun clearForUser(userId: String)

    @Query("DELETE FROM detection_history")
    suspend fun clearAll()
}
