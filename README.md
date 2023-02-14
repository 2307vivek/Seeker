# Seeker
**Seeker** is a highly customisable seekbar/slider for android with support for **readahead indicator** and **segments**. Made with Jetpack Compose ❤.

![seeker (2)](https://user-images.githubusercontent.com/67380664/218636012-ac49ae15-0f7f-4cfa-94be-0add7a9182c3.png)

## Including in your project
[![Maven Central](https://img.shields.io/maven-central/v/io.github.2307vivek/seeker?style=for-the-badge)](https://search.maven.org/search?q=g:io.github.2307vivek)
### Gradle
Add the dependency below to your **module**'s `build.gradle` file:
```gradle
dependencies {
    implementation 'io.github.2307vivek:seeker:1.0.1'
}
```

## How to use
You can create a Seeker with the `Seeker` composable.
```kotlin
@Composable
fun Seeker(
    modifier: Modifier = Modifier,
    state: SeekerState = rememberSeekerState(),
    value: Float,
    range: ClosedFloatingPointRange<Float> = 0f..1f,
    readAheadValue: Float = range.start,
    onValueChange: (Float) -> Unit,
    onValueChangeFinished: (() -> Unit)? = null,
    segments: List<Segment> = emptyList(),
    enabled: Boolean = true,
    colors: SeekerColors = SeekerDefaults.seekerColors(),
    dimensions: SeekerDimensions = SeekerDefaults.seekerDimensions(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
)
```

In its simplest form **seeker** can be used as a regular slider for selecting values from a range or showing progress in a range.

```kotlin
var value by remember { mutableStateOf(0f) }

Seeker(
    value = value,
    range = 1f..100f,
    onValueChange = { value = it }
)
```
