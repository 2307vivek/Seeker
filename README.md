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

### Read Ahead indicator
A read ahead indicator shows the amount of content which is already ready to use. It is particularly useful in media streaming apps where some media is downloaded ahead of time to avoid bufferring. The `readAheadValue` property of the Seeker composable can be used to display the read ahead indicator.

```kotlin
var value by remember { mutableStateOf(0f) }
var readAheadValue by remember { mutableStateOf(0f) }

Seeker(
    value = value,
    readAheadValue = readAheadValue,
    range = 1f..100f,
    onValueChange = { value = it }
)
```

### Creating Segments
Seeker can break the track into different sections, which can be used to display different parts in the range. To create segments, a list of `Segment` needs to be passed in the `Seeker` composable.

```kotlin
val segments = listOf(
    Segment(name = "Intro", start = 1f),
    Segment(name = "Part 1", start = 40f),
    Segment(name = "Part 2", start = 88f),
)

Seeker(
    value = value,
    readAheadValue = readAheadValue,
    range = 1f..100f,
    segments = segments,
    onValueChange = { value = it }
)
```
The `Segment` takes the `name` and the `start` value form which the segments shlould start. You can also pass an optional color parameter to each segment.

The first segment in the list **must start from the start point of the range, and all the segments must lie in the range** of the seeker, otherwise an `IllegalArgumentException` will be thrown to avoid unexpected behavior.

**Segments** are by default seperated by a gap in the track, which can be customized by passing a `dimensions` parameter in the composable.
```kotlin
Seeker(
    value = value,
    readAheadValue = readAheadValue,
    range = 1f..100f,
    segments = segments,
    dimensions = SeekerDefaults.seekerDimensions(gap = 4.dp),
    onValueChange = { value = it }
)
```

## Customizing Seeker
Seeker is highly customizable in terms of its dimensions and colors. The `seekerColors()` and `seekerDimensions()` functions can be used to customise the colors and dimensions of the different parts of seeker.

The `seekerColors()` and `seekerDimensions()` functions are as follows:

```kotlin
@Composable
fun seekerColors(
    progressColor: Color = MaterialTheme.colors.primary,
    trackColor: Color = TrackColor,
    disabledProgressColor: Color = MaterialTheme.colors.onSurface.copy(alpha = DisabledProgressAlpha),
    disabledTrackColor: Color = disabledProgressColor.copy(alpha = DisabledTrackAlpha).compositeOver(MaterialTheme.colors.onSurface),
    thumbColor: Color = MaterialTheme.colors.primary,
    disabledThumbColor: Color = MaterialTheme.colors.onSurface.copy(alpha = ContentAlpha.disabled).compositeOver(MaterialTheme.colors.surface),
    readAheadColor: Color = ReadAheadColor
): SeekerColors

@Composable
fun seekerDimensions(
    trackHeight: Dp = TrackHeight,
    progressHeight: Dp = trackHeight,
    thumbRadius: Dp = ThumbRadius,
    gap: Dp = Gap
): SeekerDimensions
```
The seeker composable has parameters `colors` and `dimensions` which can be used to customize the colors and dimensions of the seeker respectively.

```kotlin
Seeker(
    value = value,
    readAheadValue = readAheadValue,
    range = 1f..100f,
    segments = segments,
    colors = SeekerDefaults.colors(trackColor = customColor, thumbColor = customThumbColor, ...)
    dimensions = SeekerDefaults.seekerDimensions(gap = 4.dp, thumbRadius = 12.dp, ...),
    onValueChange = { value = it }
)
```
> **Note**: As of the current version, unexpected behaviors are noted when colors with an alpha value less than 1f are used in the seeker. You shluld avoid using transparent colors in seeker. See issue [#12](https://github.com/2307vivek/Seeker/issues/12).

The above functions are `@Composables` which means it will be recomposed when the parameters change. It can be used to animate the colors and dimensions of seeker.
