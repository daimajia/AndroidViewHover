# Android View Hover [![Build Status](https://travis-ci.org/daimajia/AndroidViewHover.svg)](https://travis-ci.org/daimajia/AndroidViewHover)

In my opinion, jumping to a new activity to show your menu is a kind of wasting time and life.

So,

I think, we need a hover view, to show menu, to show messages.

## Demo

![](http://ww2.sinaimg.cn/mw690/610dc034jw1ej5iihjtl5g208z0f2npd.gif)

Watch HD in [YouTube](http://www.youtube.com/watch?v=bsDQbMTtPvM).

Download [Apk](https://github.com/daimajia/AndroidViewHover/releases/download/v1.0.0/AndroidViewHover-v1.0.0.apk)

## Usage

### Step0

Set up RenderScript

- Eclipse, please visit [official tutorial](http://developer.android.com/guide/topics/renderscript/compute.html#access-rs-apis).
- Android Studio, add 
	```groovy
	        
	        renderscriptTargetApi 19
        	renderscriptSupportMode true
	```
	in `build.gradle` `defaultConfig`, here is a [sample](https://github.com/daimajia/AndroidViewHover/blob/master/library/build.gradle#L12-L13)
	

### Step1

#### Gradle
```groovy
dependencies {
	compile "com.android.support:support-v4:20.+"
	compile 'com.nineoldandroids:library:2.4.0'
	compile 'com.daimajia.easing:library:1.0.0@aar'
	compile 'com.daimajia.androidanimations:library:1.1.2@aar'
	compile 'com.daimajia.androidviewhover:library:1.0.4@aar'
}
```

#### Maven

```xml
	<dependency>
	    <groupId>com.nineoldandroids</groupId>
	    <artifactId>library</artifactId>
	    <version>2.4.0</version>
	</dependency>
	<dependency>
	    <groupId>com.daimajia.androidanimation</groupId>
	    <artifactId>library</artifactId>
	    <version>1.1.2</version>
	    <type>apklib</type>
	</dependency>
	<dependency>
	    <groupId>com.daimajia.easing</groupId>
	    <artifactId>library</artifactId>
	    <version>1.0.0</version>
	    <type>apklib</type>
	</dependency>
	<dependency>
	    <groupId>com.daimajia.androidviewhover</groupId>
	    <artifactId>library</artifactId>
	    <version>1.0.4</version>
	    <type>apklib</type>
	</dependency>
```


#### Eclipse
- SupportLibrary v4
- [NineOldAndroids-2.4.0](https://github.com/downloads/JakeWharton/NineOldAndroids/nineoldandroids-2.4.0.jar)
- [AndroidViewAnimations-1.1.2](https://github.com/daimajia/AndroidViewAnimations/releases/download/v1.1.2/AndroidViewAnimations-1.1.2.jar)
- [AndroidEasingFunctions-1.0.0](https://github.com/daimajia/AndroidViewAnimations/releases/download/v1.0.6/AndroidEasingFunctions-1.0.0.jar)
- [AndroidViewHover-1.0.3](https://github.com/daimajia/AndroidViewHover/releases/download/v1.0.3/AndroidViewHover-v1.0.3.jar)

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

You can view the samples in my [preset examples](https://github.com/daimajia/AndroidViewHover/blob/master/demo/src/main/java/com/daimajia/androidviewhover/demo/MainActivity.java).

# Animations

This project provides a lot of animations you can choose. Animations are from my another open-source project [AndroidViewAnimations](https://github.com/daimajia/AndroidViewAnimations#effects). And you can aslo using [easing funcitons](https://github.com/daimajia/AnimationEasingFunctions) to make your animations more real. Please enjoy it.

# Thanks

- [NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids) by JakeWharton
- [AndroidViewAnimations](https://github.com/daimajia/AndroidViewAnimations) by me
- [AnimationEasingFunctions](https://github.com/daimajia/AnimationEasingFunctions) by me

# About me

A student in mainland China. 

Welcome to [offer me an internship](mailto:daimajia@gmail.com).
If you have any new idea about this project, feel free to [contact me](mailto:daimajia@gmail.com).


