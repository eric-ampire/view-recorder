package com.ericampire.viewrecorder

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.content.FileProvider
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
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
private suspend fun ImageBitmap.getResizedBitmapAsync(): Deferred<Bitmap> {
  val imageBitmap = this
  return coroutineScope {
    async(Dispatchers.IO) {
      val compressedBitmap = compress(imageBitmap.asAndroidBitmap())
      val width = compressedBitmap.width
      val height = compressedBitmap.height

      if (width.isEven() and height.isEven()) return@async compressedBitmap
      val newWidth = width.getNextEven()
      val newHeight = height.getNextEven()
      Bitmap.createScaledBitmap(compressedBitmap, newWidth, newHeight, true)
    }
  }
}

fun compress(rawBitmap: Bitmap): Bitmap {
  val options = BitmapFactory.Options().apply {
    inSampleSize = 2
  }
  val stream = ByteArrayOutputStream()
  rawBitmap.compress(Bitmap.CompressFormat.PNG, 50, stream)
  val byteArray = stream.toByteArray()
  return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size, options)
}


internal suspend fun getImages(images: SnapshotStateList<ImageBitmap>): List<Bitmap> {
  val tasks = images.map { image ->
    image.getResizedBitmapAsync()
  }
  return tasks.map { it.await() }
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