package com.ericampire.viewrecorder

import android.R
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.ericampire.viewrecorder.ui.theme.ViewRecorderTheme
import kotlinx.coroutines.launch


object ShareUtil {
  @JvmStatic
  fun shareVideo(context: Context, uriToImage: Uri?) {
    val shareIntent: Intent = Intent().apply {
      action = Intent.ACTION_SEND
      uriToImage?.let { putExtra(Intent.EXTRA_STREAM, it) }
      addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
      type = "video/*"
    }

    val chooser = Intent.createChooser(shareIntent, "Share Video")

    val resInfoList: List<ResolveInfo> = context.packageManager
      .queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

    for (resolveInfo in resInfoList) {
      val packageName = resolveInfo.activityInfo.packageName
      context.grantUriPermission(
        packageName,
        uriToImage,
        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
      )
    }

    context.startActivity(chooser)
  }
}

class MainActivity : ComponentActivity() {

  @RequiresApi(Build.VERSION_CODES.R)
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      ViewRecorderTheme {
        Surface(modifier = Modifier.fillMaxSize().aspectRatio(0.58f), color = MaterialTheme.colorScheme.background) {
          val controller = rememberViewCaptureController()
          val coroutineScope = rememberCoroutineScope()
          ViewRecorder(
            controller = controller,
            onRecorded = { uri, errors ->
              if (errors.isNotEmpty()) {
                Toast.makeText(this, "${errors.first().localizedMessage}", Toast.LENGTH_SHORT).show()
              } else {
                ShareUtil.shareVideo(this, uri)
              }
            },
            content = {
              RecordScreen(
                onStartRecord = {
                  coroutineScope.launch {
                    controller.capture()
                  }
                },
                onStopRecord = {

                }
              )
            }
          )
        }
      }
    }
  }
}