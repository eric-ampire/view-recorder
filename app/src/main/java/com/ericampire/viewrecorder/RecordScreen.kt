package com.ericampire.viewrecorder

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ericampire.viewrecorder.ui.theme.ViewRecorderTheme
import kotlin.math.roundToInt

@Composable
fun RecordScreen(
  onStartRecord: () -> Unit,
  onStopRecord: () -> Unit,
) {
  var boxOffset by remember { mutableStateOf(Offset.Zero) }

  Column(modifier = Modifier.fillMaxSize()) {
    Row(
      modifier = Modifier
        .padding(8.dp)
        .weight(1f)
        .fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceEvenly,
      content = {
        Button(onClick = onStartRecord) {
          Text(text = "Start record")
        }

        Button(onClick = onStopRecord) {
          Text(text = "Stop record")
        }
      }
    )

    Box(modifier = Modifier
      .fillMaxSize()
      .padding(8.dp)
      .weight(9f)) {

      val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loader))
      val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE
      )
      LottieAnimation(
        composition = composition,
        progress = { progress },
      )
    }
  }
}

@Preview
@Composable
fun RecordScreenPrevious() {
  ViewRecorderTheme {
    RecordScreen(
      onStartRecord = {},
      onStopRecord = {},
    )
  }
}