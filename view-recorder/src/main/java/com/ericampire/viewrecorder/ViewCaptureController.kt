package com.ericampire.viewrecorder

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dev.shreyaspatil.capturable.controller.CaptureController
import dev.shreyaspatil.capturable.controller.rememberCaptureController
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.jcodec.api.android.AndroidSequenceEncoder
import org.jcodec.common.io.NIOUtils
import org.jcodec.common.io.SeekableByteChannel
import org.jcodec.common.model.Rational
import java.io.File
import java.util.UUID


class ViewCaptureController internal constructor(
  val captureController: CaptureController,
) {

  private lateinit var onCaptureComplete: () -> Unit

  suspend fun capture() {
    try {
      withTimeout(2000) {
        while (true) {
          captureController.capture()
          delay(60)
        }
      }
    } catch (e: CancellationException) {
      onCaptureComplete.invoke()
    }
  }



  suspend fun onRecorded(
    context: Context,
    images: List<Bitmap>,
    block: (File?) -> Unit
  ) {
    withContext(Dispatchers.IO) {
      val outputFile = File(context.cacheDir, "${UUID.randomUUID()}.mp4")
      var out: SeekableByteChannel? = null
      try {
        out = NIOUtils.writableFileChannel(outputFile.path)
        val encoder = AndroidSequenceEncoder(out, Rational.R(60, 5))
        images.map { imageBitmap ->
          encoder.encodeImage(imageBitmap)
        }
        encoder.finish()
        block(outputFile)
      } catch (e: Exception) {
        block(null)
        Log.e("ericampire", e.localizedMessage)
      } finally {
        NIOUtils.closeQuietly(out)
      }
    }
  }

  fun setOnCaptureCompleted(onCaptureComplete: () -> Unit) {
    this.onCaptureComplete = onCaptureComplete
  }
}

@Composable
fun rememberViewCaptureController(): ViewCaptureController {
  val captureController = rememberCaptureController()
  return remember {
    ViewCaptureController(captureController)
  }
}