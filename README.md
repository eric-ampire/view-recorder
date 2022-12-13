# view-recorder

## 💡Introduction 

In the previous View system, drawing Bitmap Image from `View` was very straightforward. But that's not the case with Jetpack Compose since it's different in many aspects from previous system. This library helps easy way to achieve the same results. 
It's built upon the `ComposeView` and uses `View`'s APIs to draw the Bitmap image.

## 🚀 Implementation

You can check [/app](/app) directory which includes example application for demonstration. 

### Gradle setup

In `build.gradle` of app module, include this dependency

```gradle
dependencies {
    implementation "dev.shreyaspatil:capturable:1.0.3"
}
```

_You can find latest version and changelogs in the [releases](https://github.com/PatilShreyas/Capturable/releases)_.

### Usage

#### 1. Setup the controller


```kotlin
@Composable
fun Screen() {
     val controller = rememberViewCaptureController(timeMillis = 3000)
     val coroutineScope = rememberCoroutineScope()
     
     ViewRecorder(
        controller = controller,
        onRecorded = { uri, errors ->
            if (errors.isNotEmpty()) {
                Toast.makeText(this, "${errors.first().localizedMessage}", Toast.LENGTH_SHORT).show()
            } else {
                // uri (the recorder mp4)
            }
        },
        content = {
            HomeScreen()
        }
    )
}
```

#### 2. Start recording the screen

```kotlin

coroutineScope.launch {
    controller.capture()
}

```

