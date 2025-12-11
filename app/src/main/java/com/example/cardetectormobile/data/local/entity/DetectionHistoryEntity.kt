package com.example.cardetectormobile.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "detection_history")
data class DetectionHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: String,                 // <- dueño del registro
    val brand: String,
    val modelName: String,
    val year: Int?,
    val lat: Double?,
    val lon: Double?,
    val imageUri: String?,
    val createdAt: Long = System.currentTimeMillis()
)
