package com.example.cardetectormobile.data.repository

import com.example.cardetectormobile.data.local.dao.DetectionDao
import com.example.cardetectormobile.data.local.entity.DetectionHistoryEntity
import com.example.cardetectormobile.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow

class HistoryRepositoryImpl(
    private val detectionDao: DetectionDao
) : HistoryRepository {

    override fun getDetectionHistoryForUser(userId: String): Flow<List<DetectionHistoryEntity>> =
        detectionDao.getDetectionsForUser(userId)

    override fun getAllDetectionHistory(): Flow<List<DetectionHistoryEntity>> =
        detectionDao.getAllDetections()

    override suspend fun insertDetection(detection: DetectionHistoryEntity) {
        detectionDao.insertDetection(detection)
    }

    override suspend fun deleteDetection(id: Long) {
        detectionDao.deleteDetection(id)
    }

    override suspend fun clearForUser(userId: String) {
        detectionDao.clearForUser(userId)
    }

    override suspend fun clearAll() {
        detectionDao.clearAll()
    }

    override suspend fun getDetectionsCountForUser(userId: String): Int {
        return detectionDao.getDetectionsCountForUser(userId)
    }

    override suspend fun getLastDetectionForUser(userId: String): DetectionHistoryEntity? {
        return detectionDao.getLastDetectionForUser(userId)
    }
}
