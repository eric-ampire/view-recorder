package com.ericampire.viewrecorder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
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

private fun Int.isEven(): Boolean {
  return this % 2 == 0
}

private fun Int.getNextEven(): Int {
  return if (isEven()) this else this + 1
}
private fun Bitmap.getResizedBitmap(): Bitmap {
  val width = this.width
  val height = this.height

  if (width.isEven() and height.isEven()) return this

  val newWidth = width.getNextEven()
  val newHeight = height.getNextEven()

  val scaleWidth = newWidth.toFloat() / width
  val scaleHeight = newHeight.toFloat() / height

  val matrix = Matrix()
  matrix.postScale(scaleWidth, scaleHeight)
  return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}


internal fun getImages(images: SnapshotStateList<ImageBitmap>): List<Bitmap> {
  return images.map {
    it.asAndroidBitmap().getResizedBitmap()
  }
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