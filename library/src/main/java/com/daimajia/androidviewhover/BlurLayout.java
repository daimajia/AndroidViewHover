package com.daimajia.androidviewhover;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.androidviewhover.proxy.AnimationProxy;
import com.daimajia.androidviewhover.tools.Blur;
import com.daimajia.androidviewhover.tools.Util;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlurLayout extends RelativeLayout {


    private View mHoverView;

    private boolean enableBlurBackground = true;
    private int mBlurRadius = 10;
    private ImageView mBlurImage;

    private static long DURATION = 500;

    private ArrayList<Animator> mPlayingAnimators = new ArrayList<Animator>();

    private ArrayList<Animator> mAppearingAnimators = new ArrayList<Animator>();
    private ArrayList<Animator> mDisappearingAnimators = new ArrayList<Animator>();

    private ArrayList<AppearListener> mAppearListeners = new ArrayList<AppearListener>();
    private ArrayList<DisappearListener> mDisappearListeners = new ArrayList<DisappearListener>();

    private boolean enableBackgroundZoom = false;
    private float mZoomRatio = 1.14f;

    private boolean enableTouchEvent = true;

    private Animator mHoverAppearAnimator;
    private YoYo.AnimationComposer mHoverAppearAnimationComposer;

    private Animator mHoverDisappearAnimator;
    private YoYo.AnimationComposer mHoverDisappearAnimationComposer;

    private HashMap<View, ArrayList<AnimationProxy>> mChildAppearAnimators = new HashMap<View, ArrayList<AnimationProxy>>();
    private HashMap<View, ArrayList<AnimationProxy>> mChildDisappearAnimators = new HashMap<View, ArrayList<AnimationProxy>>();

    private long mBlurDuration = DURATION;

    public enum HOVER_STATUS {
        APPEARING, APPEARED, DISAPPEARING, DISAPPEARED
    };

    private HOVER_STATUS mHoverStatus = HOVER_STATUS.DISAPPEARED;

    public BlurLayout(Context context) {
        super(context);
    }

    public BlurLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BlurLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return enableTouchEvent && gestureDetector.onTouchEvent(event);
    }

    private GestureDetector gestureDetector = new GestureDetector(getContext(), new BlurLayoutDetector());

    class BlurLayoutDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            if(hover())
                return true;
            else
                return super.onSingleTapConfirmed(e);
        }
    };

    public void showHover(){
        hover();
    }

    /**
     * Let hover show.
     * @return
     */
    private boolean hover(){
        if(mHoverView == null)  return false;

        if(getHoverStatus() != HOVER_STATUS.DISAPPEARED || !mPlayingAnimators.isEmpty())    return true;

        removeView(mBlurImage);
        if(enableBlurBackground)
            addBlurImage();

        if(mHoverView.getParent() != null){
            ((ViewGroup)(mHoverView.getParent())).removeView(mHoverView);
        }

        addView(mHoverView, getFullParentSizeLayoutParams());

        mHoverView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                startChildrenAppearAnimations();

                startBlurImageAppearAnimator();

                startHoverAppearAnimator();

                if(Build.VERSION.SDK_INT >= 16)
                    mHoverView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    mHoverView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        });
        return true;

    }

    /**
     * Let hover view dismiss.
     * Notice: only when hover view status is appeared, then, this may work.
     */
    public void dismissHover(){
        if(getHoverStatus() != HOVER_STATUS.APPEARED || !mPlayingAnimators.isEmpty())
            return;

        startBlurImageDisappearAnimator();

        startHoverDisappearAnimator();

        startChildrenDisappearAnimations();
    }

    public void toggleHover(){
        if(getHoverStatus() == HOVER_STATUS.DISAPPEARED)
            showHover();
        else if(getHoverStatus() == HOVER_STATUS.APPEARED)
            dismissHover();
    }

    /**
     * get currently hover status.
     * @return
     */
    public HOVER_STATUS getHoverStatus(){
        return mHoverStatus;
    }

    private void addBlurImage(){
        Bitmap b = Util.getViewBitmap(this);
        if(b == null)
            return;
        Bitmap bm = Blur.apply(getContext(), b, mBlurRadius);
        ImageView im = new ImageView(getContext());
        im.setImageBitmap(bm);
        mBlurImage = im;
        this.addView(im);
    }

    /**
     * set background blur duration.
     * @param duration
     */
    public void setBlurDuration(long duration){
        if(duration > 100)
            mBlurDuration = duration;
    }


    /**
     * set background blur radius.
     * @param radius radius to be used for the gaussian blur operation, integer between 0 and 25 (inclusive)
     */
    public void setBlurRadius(int radius) {
        if(radius < 0 || radius > 25){
            throw new IllegalArgumentException("Radius must be between 0 and 25 (inclusive)");
        }
        this.mBlurRadius = radius;
    }

    /**
     * bind a hover view with BlurLayout.
     * @param hover
     */
    public void setHoverView(final View hover){
        mHoverView = hover;

        if(mHoverView == null)  return;

        if(mHoverAppearAnimator != null)
            mHoverAppearAnimator.setTarget(mHoverView);

        mHoverView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                dismissHover();

            }
        });
    }

    /**
     * Sets whether or not touching the BlurLayout will trigger the Hover View and blur effect
     * @param enableTouchEvent
     */
    public void enableTouchEvent(boolean enableTouchEvent) {
        this.enableTouchEvent = enableTouchEvent;
    }

    public void enableBlurBackground(boolean enable){
        enableBlurBackground = enable;
    }

    public void enableZoomBackground(boolean enable) {
        enableBackgroundZoom = enable;
    }

    public void setBlurZoomRatio(float ratio){
        if(ratio < 0)
            throw new IllegalArgumentException("Can not set ratio less than 0");
        mZoomRatio = ratio;
    }

    private void startBlurImageAppearAnimator(){
        if(!enableBlurBackground || mBlurImage == null)    return;

        AnimatorSet set = new AnimatorSet();
         if(enableBackgroundZoom){
            set.playTogether(
                    ObjectAnimator.ofFloat(mBlurImage, "alpha", 0.8f, 1f),
                    ObjectAnimator.ofFloat(mBlurImage, "scaleX", 1f, mZoomRatio),
                    ObjectAnimator.ofFloat(mBlurImage, "scaleY", 1f, mZoomRatio)
            );
        }
        else{
            set.playTogether(
                    ObjectAnimator.ofFloat(mBlurImage, "alpha", 0f, 1f)
            );
        }
        set.addListener(mGlobalListener);
        set.addListener(mGlobalAppearingAnimators);
        set.setDuration(mBlurDuration);
        set.start();
    }

    private void startBlurImageDisappearAnimator(){
        if(!enableBlurBackground || mBlurImage == null)    return;

        AnimatorSet set = new AnimatorSet();
        if(enableBackgroundZoom)
            set.playTogether(
                    ObjectAnimator.ofFloat(mBlurImage, "alpha", 1f, 0.8f),
                    ObjectAnimator.ofFloat(mBlurImage, "scaleX", mZoomRatio, 1f),
                    ObjectAnimator.ofFloat(mBlurImage, "scaleY", mZoomRatio, 1f)
            );
        else
            set.playTogether(
                    ObjectAnimator.ofFloat(mBlurImage, "alpha", 1f, 0f)
            );

        set.addListener(mGlobalListener);
        set.addListener(mGlobalDisappearAnimators);
        set.setDuration(mBlurDuration);
        set.start();
    }

    private void startHoverAppearAnimator(){
        if(mHoverAppearAnimator != null)
            mHoverAppearAnimator.start();

        if(mHoverAppearAnimationComposer != null)
            mHoverAppearAnimationComposer.playOn(mHoverView);
    }

    private void startHoverDisappearAnimator(){
        if(mHoverDisappearAnimator != null)
            mHoverDisappearAnimator.start();
        if(mHoverDisappearAnimationComposer != null)
            mHoverDisappearAnimationComposer.playOn(mHoverView);
    }

    private void startChildrenAppearAnimations(){
        for(Map.Entry<View, ArrayList<AnimationProxy>> entry : mChildAppearAnimators.entrySet()){
            for(AnimationProxy animator : entry.getValue()){
                animator.start();
            }
        }
    }

    private void startChildrenDisappearAnimations(){
        for(View view : mChildDisappearAnimators.keySet()){
            for(AnimationProxy animator : mChildDisappearAnimators.get(view)){
                animator.start();
            }
        }
    }

    public interface AppearListener {
        public void onStart();
        public void onEnd();
    }

    public interface DisappearListener {
        public void onStart();
        public void onEnd();
    }

    public void addAppearListener(AppearListener l){
        mAppearListeners.add(l);
    }

    public void removeAppearListener(AppearListener l){
        mAppearListeners.remove(l);
    }

    public void addDisappearListener(DisappearListener l){
        mDisappearListeners.add(l);
    }

    public void removeDisappearListener(DisappearListener l){
        mDisappearingAnimators.remove(l);
    }

    public Animator.AnimatorListener mGlobalAppearingAnimators = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mAppearingAnimators.add(animation);
            if(mAppearingAnimators.size() == 1){
                mHoverStatus = HOVER_STATUS.APPEARING;
                for(AppearListener l : mAppearListeners){
                    l.onStart();
                }
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mAppearingAnimators.remove(animation);
            mHoverStatus = HOVER_STATUS.APPEARED;
            if(!mAppearListeners.isEmpty()){
                for(AppearListener l : mAppearListeners){
                    l.onEnd();
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public Animator.AnimatorListener mGlobalDisappearAnimators = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mDisappearingAnimators.add(animation);
            if(mDisappearListeners.size() == 1){
                for(DisappearListener l : mDisappearListeners){
                    mHoverStatus = HOVER_STATUS.DISAPPEARING;
                    l.onStart();
                }
            }
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mDisappearingAnimators.remove(animation);
            if(mPlayingAnimators.isEmpty()){
                mHoverStatus = HOVER_STATUS.DISAPPEARED;
                removeView(mBlurImage);
                removeView(mHoverView);
                for(DisappearListener l : mDisappearListeners){
                    l.onEnd();
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public Animator.AnimatorListener mGlobalListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
            mPlayingAnimators.add(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mPlayingAnimators.remove(animation);
        }

        @Override
        public void onAnimationCancel(Animator animation) {

        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    public void setHoverAppearAnimator(Techniques technique){
        setHoverAppearAnimator(technique, DURATION);
    }

    public void setHoverAppearAnimator(Techniques technique, long duration){
        setHoverAppearAnimator(technique, duration, 0, null);
    }

    public void setHoverAppearAnimator(Techniques technique, long duration, long delay, Interpolator interpolator){
        setHoverAppearAnimator(technique, duration, delay, interpolator, new Animator.AnimatorListener[]{});
    }

    public void setHoverAppearAnimator(Techniques technique, long duration, long delay, Interpolator interpolator, Animator.AnimatorListener... listeners){
        mHoverAppearAnimator = null;
        mHoverAppearAnimationComposer = YoYo.with(technique).delay(delay).duration(duration).interpolate(interpolator);

        for(Animator.AnimatorListener l : listeners)
            mHoverAppearAnimationComposer.withListener(l);
        mHoverAppearAnimationComposer.withListener(mGlobalListener);
        mHoverAppearAnimationComposer.withListener(mGlobalAppearingAnimators);
    }

    public void setHoverAppearAnimator(Animator animator){
        mHoverAppearAnimationComposer = null;
        mHoverAppearAnimator = animator;

        animator.addListener(mGlobalListener);
        animator.addListener(mGlobalAppearingAnimators);
    }



    public void setHoverDisappearAnimator(Techniques technique){
        setHoverDisappearAnimator(technique, DURATION);
    }

    public void setHoverDisappearAnimator(Techniques technique, long duration){
        setHoverDisappearAnimator(technique, duration, 0, null);
    }

    public void setHoverDisappearAnimator(Techniques technique, long duration, long delay, Interpolator interpolator){
        setHoverDisappearAnimator(technique, duration, delay, interpolator, new Animator.AnimatorListener[]{});
    }

    public void setHoverDisappearAnimator(Techniques technique, long duration, long delay, Interpolator interpolator, Animator.AnimatorListener... listeners){
        mHoverDisappearAnimator = null;
        mHoverDisappearAnimationComposer = YoYo.with(technique).delay(delay).duration(duration).interpolate(interpolator);

        for(Animator.AnimatorListener l : listeners)
            mHoverDisappearAnimationComposer.withListener(l);

        mHoverDisappearAnimationComposer.withListener(mGlobalListener);
        mHoverDisappearAnimationComposer.withListener(mGlobalDisappearAnimators);
    }

    public void setHoverDisappearAnimator(Animator animator){
        mHoverDisappearAnimationComposer = null;
        mHoverDisappearAnimator = animator;
        mHoverDisappearAnimator.addListener(mGlobalListener);
        mHoverDisappearAnimator.addListener(mGlobalDisappearAnimators);
    }



    public void addChildAppearAnimator(View hoverView, int resId, Techniques technique){
        addChildAppearAnimator(hoverView, resId, technique, DURATION);
    }

    public void addChildAppearAnimator(View hoverView, int resId, Techniques technique, long duration){
        addChildAppearAnimator(hoverView, resId, technique, duration, 0);
    }

    public void addChildAppearAnimator(View hoverView, int resId, Techniques technique, long duration, long delay){
        addChildAppearAnimator(hoverView, resId, technique, duration, delay, true, null);
    }

    public void addChildAppearAnimator(View hoverView, int resId, Techniques technique, long duration, long delay, boolean hiddenWhenDelaying){
        addChildAppearAnimator(hoverView, resId, technique, duration, delay, hiddenWhenDelaying, null);
    }

    public void addChildAppearAnimator(View hoverView, int resId, Techniques technique, long duration, long delay, boolean hiddenWhenDelaying, Interpolator interpolator){
        addChildAppearAnimator(hoverView, resId, technique, duration, delay, hiddenWhenDelaying, interpolator, new Animator.AnimatorListener[]{});
    }

    public void addChildAppearAnimator(View hoverView, int resId, Techniques technique, long duration, long delay, boolean hiddenWhenDelaying, Interpolator interpolator, Animator.AnimatorListener... listeners){
        AnimationProxy executor = AnimationProxy.build(hoverView, resId, technique, duration, delay, hiddenWhenDelaying, interpolator, listeners);

        View child = executor.getTarget();

        if(mChildAppearAnimators.get(child) == null)
            mChildAppearAnimators.put(child, new ArrayList<AnimationProxy>());

        executor.withListener(mGlobalListener);
        executor.withListener(mGlobalAppearingAnimators);
        child.setVisibility(INVISIBLE);

        mChildAppearAnimators.get(child).add(executor);
    }

    public void addChildAppearAnimator(View hoverView, int childId, Animator animator){
        AnimationProxy executor = AnimationProxy.build(hoverView, childId, animator);

        View child = executor.getTarget();

        if(mChildAppearAnimators.get(child) == null)
            mChildAppearAnimators.put(child, new ArrayList<AnimationProxy>());

        executor.withListener(mGlobalListener);
        executor.withListener(mGlobalAppearingAnimators);
        mChildAppearAnimators.get(child).add(executor);
    }

    public void addChildDisappearAnimator(View hoverView, int resId, Techniques technique){
        addChildDisappearAnimator(hoverView, resId, technique, DURATION);
    }

    public void addChildDisappearAnimator(View hoverView, int resId, Techniques technique, long duration){
        addChildDisappearAnimator(hoverView, resId, technique, duration, 0, false);
    }

    public void addChildDisappearAnimator(View hoverView, int resId, Techniques technique, long duration, long delay){
        addChildDisappearAnimator(hoverView, resId, technique, duration, delay, false, null);
    }

    public void addChildDisappearAnimator(View hoverView, int resId, Techniques technique, long duration, long delay, boolean invisibleWhenDelaying){
        addChildDisappearAnimator(hoverView, resId, technique, duration, delay,invisibleWhenDelaying, null);
    }

    public void addChildDisappearAnimator(View hoverView, int resId, Techniques technique, long duration, long delay, boolean invisibleWhenDelaying, Interpolator interpolator){
        addChildDisappearAnimator(hoverView, resId, technique, duration, delay, invisibleWhenDelaying, interpolator, new Animator.AnimatorListener[]{});
    }

    public void addChildDisappearAnimator(View hoverView, int resId, Techniques technique, long duration, long delay, boolean invisibleWhenDelaying, Interpolator interpolator, Animator.AnimatorListener... listeners){

        AnimationProxy executor = AnimationProxy.build(hoverView, resId, technique, duration, delay, invisibleWhenDelaying, interpolator, listeners);

        View child = executor.getTarget();
        if(mChildDisappearAnimators.containsKey(child) == false)
            mChildDisappearAnimators.put(child, new ArrayList<AnimationProxy>());

        executor.withListener(mGlobalListener);
        executor.withListener(mGlobalDisappearAnimators);
        mChildDisappearAnimators.get(child).add(executor);
    }

    public void addChildDisappearAnimator(View hoverView, int childId, Animator animator){
        AnimationProxy executor = AnimationProxy.build(hoverView, childId, animator);

        View child = executor.getTarget();

        if(mChildDisappearAnimators.get(child) == null)
            mChildDisappearAnimators.put(child, new ArrayList<AnimationProxy>());

        executor.withListener(mGlobalListener);
        executor.withListener(mGlobalDisappearAnimators);
        mChildDisappearAnimators.get(child).add(executor);
    }

    public LayoutParams getFullParentSizeLayoutParams(){
        LayoutParams pm = (LayoutParams)this.generateDefaultLayoutParams();
        pm.width = this.getWidth();
        pm.height = this.getHeight();
        return pm;
    }

    public static void setGlobalDefaultDuration(long duration){
        if(duration < 100)
            throw new IllegalArgumentException("Duration can not be set to less than 100");
        DURATION = duration;
    }


}
