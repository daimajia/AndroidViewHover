# Android View Hover

In my opinion, jump to a new activity to show your menu is a kind of wasting time and life.

So,

I think, we need a hover view, to show menu, to show messages.

## Demo

[![IMAGE ALT TEXT HERE](http://img.youtube.com/vi/bsDQbMTtPvM/0.jpg)](http://www.youtube.com/watch?v=bsDQbMTtPvM)

If you can not watch this video, please click this link:

## Usage

### Step1

#### Gradle
```groovy
dependencies {
	compile "com.android.support:support-v4:20.+"
    compile 'com.nineoldandroids:library:2.4.0'
    compile 'com.daimajia.easing:library:1.0.0@aar'
    compile 'com.daimajia.androidanimations:library:1.1.2@aar'
}
```

#### Maven


#### Eclipse

### Step2

![](http://ww4.sinaimg.cn/mw690/610dc034jw1ej5giogymhj20dw085q36.jpg)

1. Create an original view, and make sure it was wrapped by `BlurLayout`

	for example:
	```xml
		<com.daimajia.androidviewhover.BlurLayout
				android:id="@+id/sample"
	            android:layout_width="wrap_content"
	            android:layout_height="wrap_content">
	            <ImageView
	                android:layout_centerInParent="true"
	                android:scaleType="fitXY"
	                android:src="@drawable/kid"
	                android:layout_width="match_parent"/>
		</com.daimajia.androidviewhover.BlurLayout>
	```
	Note: `BlurLayout` is entended from `RelativeLayout`. You can use the `RelativeLayout` rules to layout your view.


2. 	Create a hover view, there is no rules to obey. Just please remember that this view will be stretched as large as the original view you have created.

3.	Bind a hover view to `BlurLayout` 


	```java
		BlurLayout sampleLayout = (BlurLayout)findViewById(R.id.sample);
		View hover = LayoutInflater.from(mContext).inflate(R.layout.hover, null);
		sampleLayout.setHoverView(hover);
	```

	and don't forget that you can add various animations just in one line code. For example:
	```java
		//View (R.id.heart) appear animation.
		sampleLayout.addChildAppearAnimator(hover, R.id.heart, Techniques.FlipInX);
		//View (R.id.heart) disappear animation.
		sampleLayout.addChildDisappearAnimator(hover, R.id.heart, Techniques.FlipOutX);
	```

You can view the samples in my preset examples.

# Thanks

- [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids) by JakeWharton
- [AndroidViewAnimations](https://github.com/daimajia/AndroidViewAnimations) by me
- [AnimationEasingFunctions](https://github.com/daimajia/AnimationEasingFunctions) by me

# About me

A student in mainland China. 

Welcome to [offer me an internship](mailto:daimajia@gmail.com).
If you have any new idea about this project, feel free to [contact me](mailto:daimajia@gmail.com).


