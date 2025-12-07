package com.example.cardetectormobile.data.model

import com.google.gson.annotations.SerializedName

data class DetectionResponseItem(
    val brand: String,
    @SerializedName("model_name") val modelName: String,
    val year: Int, // <--- CAMBIO IMPORTANTE: Int, no String
    val lat: Float,
    val lng: Float
)

data class MetadataResponseItem(
    @SerializedName("has_gps") val hasGps: Boolean,
    val lat: Float?,
    val lon: Float?,
    val source: String?,
    val note: String?
)

data class DetectionResponse(
    val message: DetectionResponseItem,
    val metadata: MetadataResponseItem
)