# Material CircularProgressView

| Indeterminate | Determinate |
|:-------------:|:-----------:|
| ![Sample Indeterminate GIF](https://raw.github.com/rahatarmanahmed/CircularProgressView/master/gif/sampleIndeterminate.gif) | ![Sample Determinate GIF](https://raw.github.com/rahatarmanahmed/CircularProgressView/master/gif/sampleDeterminate.gif) |

## Description

This CircularProgressView is a (surprisingly) circular progress bar Android View that is designed to imitate the Material versions of ProgressBar. These versions can be seen on [this page](http://www.google.com/design/spec/components/progress-activity.html#progress-activity-types-of-indicators) of the Material design spec under Circular indicators.

## Usage

To use CircularProgressView you must add it as a dependency in your Gradle build:

```groovy
dependencies {
    compile 'com.github.rahatarmanahmed:circularprogressview:2.5.0'
}
```

Then add the view to your layout:

```xml
<com.github.rahatarmanahmed.cpv.CircularProgressView
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:id="@+id/progress_view"
    android:layout_width="40dp"
    android:layout_height="40dp"
    app:cpv_animAutostart="true"
    app:cpv_indeterminate="true" />
```

That's all you need! If you don't want the CircularProgressView to automatically start animating, omit the app:cpv_animAutostart option and start it manually yourself:

```java
CircularProgressView progressView = (CircularProgressView) findViewById(R.id.progress_view);
progressView.startAnimation();
```

## XML attributes

| Name | Type | Default | Description |
|:----:|:----:|:-------:|:-----------:|
| cpv_progress | float | 0 | The current progress of the progress bar. |
| cpv_maxProgress | float | 100 | The maximum progress of the progress bar; what's considered as 100% of the bar. |
| cpv_thickness | dimension | 4px | The thickness of the progress bar. |
| cpv_color | color | Theme's accent color. If not available, Material Blue 500 (#2196F3) | The color of the progress bar. |
| cpv_indeterminate | boolean | false | Whether this progress bar is indeterminate or not. If indeterminate, the progress set on this view will not have any effect. |
| cpv_animDuration | integer | 4000 | The duration of the indeterminate progress bar animation in milliseconds. It is the duration of all "steps" of the indeterminate animation. (Indeterminate only) |
| cpv_animSwoopDuration | integer | 5000 | The duration of the initial swoop of the determinate animation. (Determinate only) |
| cpv_animSyncDuration | integer | 500 | The duration of the determinate progress update animation. When you use `setUpdate(int)`, this is how long it takes for the view to finish animating to that progress. (Determinate only) |
| cpv_animSteps | integer | 3 | The number of "steps" in the indeterminate animation (how many times it does the loopy thing before returning to its original position). It is recommended to use an odd number, as even numbers of steps look the same after half the number of steps. |
| cpv_animAutostart | boolean | false | Whether this progress bar should automatically start animating once it is initialized. |
| cpv_startAngle | float | 0 | The starting angle for progress bar. (Determinate only) |

## Public Methods

| Name | Description |
|:----:|:-----------:|
| isIndeterminate() | Returns true if the progress bar is indeterminate, false if determinate. |
| setIndeterminate(boolean) | Set whether this progress bar is indeterminate or not. Will reset the animation if the value changes |
| getThickness() | Gets the thickness of the progress bar. |
| setThickness(int) | Sets thickness of the progress bar. |
| getColor() | Gets the color of the progress bar. |
| setColor(int) | Sets the color of the progress bar. |
| getMaxProgress() | Gets the maximum progress of the progress bar. |
| setMaxProgress(float) | Sets the maximum progress of the progress bar. |
| getProgress() | Gets the current progress of the progress bar. |
| setProgress(float) | Sets the current progress of the progress bar. (Will linearly animate the update.) |
| startAnimation() | Starts the animation of the progress bar. (Alias of resetAnimation().) |
| resetAnimation() | Resets the animation of the progress bar. |
| stopAnimation() | Stops the animation of the progress bar. |
| addListener(CircularProgressViewListener) | Registers a CircularProgressViewListener with this view. |
| removeListener(CircularProgressViewListener) | Unregisters a CircularProgressViewListener with this view. |

## Listener Events.

A [`CircularProgressViewListener`](circularprogressview/src/main/java/com/github/rahatarmanahmed/cpv/CircularProgressViewListener.java) class is available for listening to some events (as well as a [`CircularProgressViewAdapter`](circularprogressview/src/main/java/com/github/rahatarmanahmed/cpv/CircularProgressViewAdapter.java)).

| Event | Description |
|:----:|:-----------:|
| onProgressUpdate(float) | Called when setProgress is called. (Determinate only) |
| onProgressUpdateEnd(float) | Called when this view finishes animating to the updated progress. (Determinate only) |
| onAnimationReset() | Called when resetAnimation() is called. |
| onModeChange(boolean) | Called when you switch between indeterminate and determinate modes. |


## Known Issues

### CircularProgressView flickers when phone is in battery saving mode
This happens because battery saving mode automatically ends all Animators, but the ones in CPV run in an endless loop. The best way to work around this right now is to use the native ProgressBar for API >21, since that is when the battery saver mode was introduced. See [this](https://github.com/rahatarmanahmed/CircularProgressView/issues/16#issuecomment-109169040) issue comment on how to accomplish this.


## Changelog

### v2.5.0

 * Added `stopAnimation()` method
 * Fixed view animating while not visible. Setting visibility to GONE or INVISIBLE will stop the animation. Setting to VISIBLE will restart it.

### v2.4.0

 * Added cpv_startAngle attribute

### v2.3.2

 * Fixed CPV stopping when View is recycled

### v2.3.1

 * Fixed memory leak

### v2.3.0

 * Removed application tag from manifest
 * Added [`CircularProgressViewListener`](circularprogressview/src/main/java/com/github/rahatarmanahmed/cpv/CircularProgressViewListener.java) and [`CircularProgressViewAdapter`](circularprogressview/src/main/java/com/github/rahatarmanahmed/cpv/CircularProgressViewAdapter.java)
 * Added animation duration options for determinate swoop and sync animations

### v2.2.1

 * Fixed ignoring the color #FFFFFF

### v2.2.0

 * Now it uses the actual theme's accent color if possible by default.

### v2.1.0

 * Fixed default thickness using 4px instead of 4dp

### v2.0.1

 * Possible fix for drawArc NullPointerError
 * Slight performance improvements

### v2.0.0

 * Removed unnecessary appcompat dependency from example
 * Fixed repaint issue by drawing smaller arcs

### v1.0.0

 * Initial release
