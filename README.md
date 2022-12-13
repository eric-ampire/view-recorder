# view-recorder

## 💡Introduction 

todo ...

## 🚀 Implementation

You can check [/app](/app) directory which includes example application for demonstration. 

### Gradle setup

In `build.gradle` of the project include the repository

```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

In `build.gradle` of app module, include this dependency

```gradle

dependencies {
    com.github.eric-ampire:view-recorder:<version>"
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

