package com.example.cardetectormobile.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileUtils(private val context: Context) {

    fun createTempPictureUri(): Uri {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_${timeStamp}_"

        val image = File.createTempFile(
            imageFileName,
            ".jpg",
            context.externalCacheDir
        )

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // OJO: Esto debe coincidir con tu Manifest
            image
        )
    }
}