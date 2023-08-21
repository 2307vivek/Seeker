# Seeker
**Seeker** is a highly customisable seekbar/slider for android with support for **readahead indicator** and **segments**. Made with Jetpack Compose ❤.

[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/2307vivek/seeker/android.yml?style=for-the-badge)](https://github.com/2307vivek/Seeker/actions)
[![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/2307vivek/seeker/publish.yml?label=Publish&style=for-the-badge)]((https://github.com/2307vivek/Seeker/actions))

<a href="https://jetc.dev/issues/154.html"><img src="https://img.shields.io/badge/As_Seen_In-jetc.dev_Newsletter_Issue_%23154-blue?logo=Jetpack+Compose&amp;logoColor=white" alt="Featured In - jetc.dev Newsletter Issue #154"></a>

![seeker (2)](https://user-images.githubusercontent.com/67380664/218636012-ac49ae15-0f7f-4cfa-94be-0add7a9182c3.png)

## Including in your project
[![Maven Central](https://img.shields.io/maven-central/v/io.github.2307vivek/seeker?style=for-the-badge)](https://search.maven.org/search?q=g:io.github.2307vivek)
### Gradle
Add the dependency below to your **module**'s `build.gradle` file:
```gradle
dependencies {
    implementation 'io.github.2307vivek:seeker:1.1.1'
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
![Screenshot_20230214-232222_Seeker](https://user-images.githubusercontent.com/67380664/218842981-4495f951-acd6-4838-ba5c-4f656ac65a17.jpg)

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
![Screenshot_20230214-232319_Seeker](https://user-images.githubusercontent.com/67380664/218843315-5002d843-613c-4174-bd77-cec67c78c8da.jpg)

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
![Screenshot_20230214-232423_Seeker](https://user-images.githubusercontent.com/67380664/218843537-dcb59b6e-de88-493b-aaa1-af97126eafd0.jpg)

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

### Observing current segment
The current segment corresponding to the current seeker value, can be observed by using the `currentSegment` property of the `SeekerState` which can be created by using `rememberSeekerState()`.

```kotlin
val segments = listOf(
    Segment(name = "Intro", start = 1f),
    Segment(name = "Part 1", start = 40f),
    Segment(name = "Part 2", start = 88f),
)

val state = rememberSeekerState()

Seeker(
    state = state,
    value = value,
    readAheadValue = readAheadValue,
    range = 1f..100f,
    segments = segments,
    onValueChange = { value = it }
)

// observing the current segment
Text(state.currentSegment.name)
```
![segments-3 (online-video-cutter com)](https://user-images.githubusercontent.com/67380664/218962032-eb8ecf66-1df8-458e-9f1a-fdcd39523d9d.gif)

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

```kotlin
val interactionSource = remember { MutableInteractionSource() }
val isDragging by interactionSource.collectIsDraggedAsState()

val gap by animateDpAsState(if (isDragging) 2.dp else 0.dp)
val thumbRadius by animateDpAsState(if (isDragging) 10.dp else 0.dp)

Seeker(
    value = value,
    readAheadValue = readAheadValue,
    range = 1f..100f,
    segments = segments,
    interactionSource = interactionSource,
    colors = SeekerDefaults.colors(trackColor = customColor, thumbColor = customThumbColor)
    dimensions = SeekerDefaults.seekerDimensions(gap = gap, thumbRadius = thumbRadius),
    onValueChange = { value = it }
)
```
![interactions-1 (online-video-cutter com)](https://user-images.githubusercontent.com/67380664/218961219-d2db59e5-6219-4de7-80d4-c503b163182e.gif)

### Using different value for thumb
Seeker has the ability to provide a seperate value for the thumb, which makes it possible to move the thumb independent of the progress.
```kotlin
var value by remember{ mutableStateOf(0f) }
var thumbPosition by remember{ mutableStateOf(0f) }

val isDragging by interactionSource.collectIsDraggedAsState()

Seeker(
    value = value,
    thumbValue = if (isDragging) thumbPosition else value,
    onValueChange = { thumbPosition = it },
    onValueChangeFinished = { value = thumbPosition },
    readAheadValue = readAheadValue,
    interactionSource = interactionSource,
    range = 1f..100f,
    ...
)
```
![f4d2d702-68c4-4ebb-8dd5-40f5996aa37f](https://github.com/2307vivek/Seeker/assets/67380664/3a72c58e-f3b7-4039-b0e0-57aa2e419df4)


## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/2307vivek/seeker/stargazers)__ for this repository. :star: <br>
Also, follow me on __[github](https://github.com/2307vivek)__ and __[twitter](https://twitter.com/2307vivek)__ to stay updated with my creations! 🤩

# License
```xml
Copyright 2023 Vivek Singh

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
