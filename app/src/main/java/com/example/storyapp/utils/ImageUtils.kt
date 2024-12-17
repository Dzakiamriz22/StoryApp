package com.example.storyapp.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path) ?: return file
    var compressQuality = 100
    var streamLength: Int

    do {
        val byteArrayOutputStream = ByteArrayOutputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, this)
        }

        streamLength = byteArrayOutputStream.size()
        compressQuality -= 5
    } while (streamLength > 1000000 && compressQuality > 0)

    try {
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, outputStream)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }

    return file
}
