package com.ericampire.viewrecorder

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import dev.shreyaspatil.capturable.Capturable
import kotlinx.coroutines.launch

@Composable
fun ViewRecorder(
  modifier: Modifier = Modifier,
  controller: ViewCaptureController,
  onRecorded: (video: Uri?, errors: List<Throwable>) -> Unit,
  content: @Composable () -> Unit
) {

  val images = remember {
    mutableStateListOf<ImageBitmap>()
  }
  val errors = remember {
    mutableStateListOf<Throwable>()
  }

  val coroutineScope = rememberCoroutineScope()
  val context = LocalContext.current

  val onCaptureComplete = {
    val job = coroutineScope.launch {
      controller.onRecorded(
        context = context,
        images = getImages(images),
        block = { file ->
          val uri = file?.getUriFromFile(context)
          onRecorded(uri, errors)
        }
      )
    }
  }


  LaunchedEffect(true) {
    controller.setOnCaptureCompleted(onCaptureComplete)
  }

  Capturable(
    modifier = modifier,
    content = content,
    onCaptured = { imageBitmap, throwable ->
      if (imageBitmap != null) {
        images.add(imageBitmap)
      }
      if (throwable != null) {
        errors.add(throwable)
      }
    },
    controller = controller.captureController
  )
}

