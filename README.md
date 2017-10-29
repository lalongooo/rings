# Android Rings
A simple chart for Android with three indicators and one more to indicate overall summary.
They get highlighted if you click on the ring or text.

![Rings Demo](https://i.imgur.com/Khwxkyi.png)

# Setup
## 1. Provide the gradle dependency

For now, you have the specify the maven repository URL in your project level `build.gradle` file
since it is being synchronized with `jcenter()`. This should not be necessary in the coming weeks (maybe in 1-2 weeks)

```
maven {
    url 'https://dl.bintray.com/lalongooo/maven/'
}
```

Add the gradle dependency to your `app` module `build.gradle` file:

```
dependencies {
    compile 'com.lalongooo:rings:1.0.0'
}
```

## 2. Add the `Rings` custom view to your layout xml file

Make sure `layout_width` and `layout_height` are equal so rings can be a perfect circle inside a square, otherwise
it'd look like an ellipse inside a rectangle.

``` xml
<com.lalongooo.Rings
    android:id="@+id/rings"
    android:layout_width="200dp"
    android:layout_height="200dp" />
```

## 3. Add the custom attributes as needed


Text size. Default is `18sp`.

``` xml
app:rings_text_size
```

Margin left of the text. Default is `10dp`.

``` xml
app:rings_text_margin_left
```

The three inner rings stroke width. Default is `8dp`.

``` xml
app:rings_inner_stroke_width
```

The three inner rings stroke width when unfinished or incomplete, if value is the same as `app:rings_inner_stroke_width`, it will be invisible. Default is `10dp`.

``` xml
app:rings_inner_stroke_width_unfinished
```

The outer ring stroke width. Default is `12dp`.

``` xml
app:rings_outer_stroke_width
```

The outer ring stroke width when unfinished or incomplete, if value is the same as `app:rings_outer_stroke_width_unfinished`, it will be inviisble. Default is `12dp`.

``` xml
app:rings_outer_stroke_width_unfinished
```

Default unfinished/incomplete background color for all rings.

``` xml
app:rings_unfinished_color
```

Default finished/progress color for all the inner rings. It is overriden by  `app:rings_inner_first_color`, `app:rings_inner_second_color`, `app:rings_inner_third_color` when specified.

``` xml
app:rings_default_filled_color
```

Finished/progress color of the first inner ring.

``` xml
app:rings_inner_first_color
```

Finished/progress color of the second inner ring.

``` xml
app:rings_inner_second_color
```

Finished/progress color of the third inner ring.

``` xml
app:rings_inner_third_color
```

Finished/progress color of the outer ring.

``` xml
app:rings_overall_color
```

Progress of the first inner ring. Between 0 and 100. Default is 0.

``` xml
app:rings_inner_first_progress
```

Progress of the second inner ring. Between 0 and 100. Default is 0.

``` xml
app:rings_inner_second_progress
```

Progress of the third inner ring. Between 0 and 100. Default is 0.

``` xml
app:rings_inner_third_progress
```

Progress of the outer ring. Between 0 and 100. Default is 0.

``` xml
app:rings_overall_progress
```

Text of the first inner ring.

``` xml
app:rings_inner_first_text
```

Text of the second inner ring.

``` xml
app:rings_inner_second_text
```

Text of the third inner ring.

``` xml
app:rings_inner_third_text
```

Text of the outer ring.

``` xml
app:rings_overall_text
```

## Example

``` xml
<com.lalongooo.Rings
    android:id="@+id/rings"
    android:layout_width="200dp"
    android:layout_height="200dp"
    app:rings_inner_first_color="#FF9F1C"
    app:rings_inner_first_progress="30"
    app:rings_inner_first_text="Java"
    app:rings_inner_second_color="#4BC6B9"
    app:rings_inner_second_progress="75"
    app:rings_inner_second_text="Kotlin"
    app:rings_inner_third_color="#757780"
    app:rings_inner_third_progress="85"
    app:rings_inner_third_text="Android"
    app:rings_overall_color="#EA3546"
    app:rings_overall_progress="100"
    app:rings_overall_text="Overall"
    app:rings_text_size="20sp"
    app:rings_unfinished_color="#f2f2f2" />
```

### Result

![Rings Example](https://i.imgur.com/BoP3tIy.png)
