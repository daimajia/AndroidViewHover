package com.daimajia.androidviewhover.proxy;

import android.view.View;
import android.view.animation.Interpolator;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.nineoldandroids.animation.Animator;

public class AnimationProxy implements Runnable {

    private YoYo.AnimationComposer composer = null;
    private Animator animator = null;

    private long delay;
    private long duration;
    private boolean invisibleWhenDelaying;
    private View targetView;
    private Interpolator interpolator;

    private long startTime = -1;

    private AnimationProxy(View hoverView, int resId, Techniques technique, long duration, long delay, boolean invisibleWhenDelaying, Interpolator interpolator, Animator.AnimatorListener... listeners){
        if(hoverView == null)
            throw new IllegalStateException("Hover view is null");

        View child = hoverView.findViewById(resId);

        if(child == null)
            throw new IllegalStateException("Can not find the child view");

        if(duration < 0)
            throw new IllegalArgumentException("Duration can not be less than 0");

        if(delay < 0)
            throw new IllegalArgumentException("Delay can not be less than 0");

        this.composer = YoYo.with(technique).duration(duration).delay(delay).interpolate(interpolator);

        this.targetView = child;
        this.interpolator = interpolator;
        this.delay = delay;
        this.duration = duration;
        this.invisibleWhenDelaying = invisibleWhenDelaying;

        for(Animator.AnimatorListener l : listeners)
            this.composer.withListener(l);
    }

    public static AnimationProxy build(View hoverView, int resId, Techniques technique, long duration, long delay, boolean invisibleWhenDelaying, Interpolator interpolator, Animator.AnimatorListener... listeners){
        return new AnimationProxy(hoverView, resId, technique, duration, delay, invisibleWhenDelaying, interpolator, listeners);
    }

    public static AnimationProxy build(View hoverView, int childId, Animator animator){
        return new AnimationProxy(hoverView, childId, animator);
    }

    private AnimationProxy(View hoverView, int childId, Animator animator){
        if(animator == null)
            throw new IllegalArgumentException("Animator can not be null");

        if(hoverView == null)
            throw new IllegalArgumentException("hoverView can not be null");

        View child = hoverView.findViewById(childId);
        if(child == null)
            throw new IllegalArgumentException("Can not find child");

        this.targetView = child;
        this.duration = animator.getDuration();
        this.delay = animator.getStartDelay();
        this.interpolator = null;
        this.animator = animator;
    }

    public void start(){
        startTime = System.currentTimeMillis();
        targetView.post(this);
    }

    public boolean isDelaying(){
        long current = System.currentTimeMillis();
        if(current - startTime <= delay)
            return true;
        else
            return false;
    }

    @Override
    public void run() {
        if(startTime == -1)
            throw new IllegalStateException("You can not call run directly, you should call start!");

        if(!isDelaying()){
            if(targetView.getVisibility() != View.VISIBLE)
                targetView.setVisibility(View.VISIBLE);

            if(composer!=null){
                composer.delay(0);
                composer.playOn(targetView);
            }
            if(animator != null){
                animator.setStartDelay(0);
                animator.start();
            }
        }else{
            if(invisibleWhenDelaying && targetView.getVisibility() != View.INVISIBLE){
                targetView.setVisibility(View.INVISIBLE);
            }
            targetView.post(this);
        }
    }

    public View getTarget(){
        return this.targetView;
    }

    public void withListener(Animator.AnimatorListener l){
        if(composer != null)
            composer.withListener(l);
        if(animator != null)
            animator.addListener(l);
    }
}
