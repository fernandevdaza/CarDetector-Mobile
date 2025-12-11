package com.example.cardetectormobile.domain.repository

import com.example.cardetectormobile.data.local.entity.DetectionHistoryEntity
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getDetectionHistoryForUser(userId: String): Flow<List<DetectionHistoryEntity>>
    fun getAllDetectionHistory(): Flow<List<DetectionHistoryEntity>>
    suspend fun insertDetection(detection: DetectionHistoryEntity)
    suspend fun deleteDetection(id: Long)
    suspend fun clearForUser(userId: String)
    suspend fun clearAll()
}
