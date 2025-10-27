package com.example.DangerBook.ui.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Utilidades para manejo de cámara y archivos de imagen
object CameraHelper {

    // Crear archivo temporal para guardar la foto en el caché
    fun createTempImageFile(context: Context): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(context.cacheDir, "images").apply {
            if (!exists()) mkdirs()
        }
        return File(storageDir, "IMG_${timeStamp}.jpg")
    }

    // Obtener Uri del archivo usando FileProvider (necesario para Android 7+)
    fun getImageUriForFile(context: Context, file: File): Uri {
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, file)
    }
}