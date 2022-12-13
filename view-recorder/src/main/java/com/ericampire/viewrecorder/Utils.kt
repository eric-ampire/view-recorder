package com.ericampire.viewrecorder

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

fun File.getUriFromFile(context: Context): Uri {
  val authority = "${context.packageName}.provider"
  return FileProvider.getUriForFile(context, authority, this)
}

suspend fun Bitmap.getFile(context: Context): File {
  return withContext(Dispatchers.IO) {
    val bos = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 0, bos)
    val bitmapData = bos.toByteArray()

    val file = File(context.cacheDir, "${UUID.randomUUID()}.png").apply {
      createNewFile()
    }
    FileOutputStream(file).apply {
      write(bitmapData)
      flush()
      close()
    }
    file
  }
}