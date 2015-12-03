package com.rhcloud.stackframe;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;

/**
 * Created by Desone on 12/2/2015.
 */
public class UIAnimator
{
    View animated;

    public UIAnimator(View animated)
    {
        this.animated = animated;
    }

    public void shrinkView(int length, Position from, final boolean fade, final Runnable callback)
    {
        ValueAnimator loginShrink = ObjectAnimator.ofFloat(0f, 1f);
        loginShrink.setDuration(length);
        switch (from) {
            case RIGHT:
                animated.setPivotX(animated.getWidth());
                break;
            case LEFT:
                animated.setPivotX(0);
                break;
            case CENTER:
                animated.setPivotX(animated.getWidth()/2);
                break;
        }

        loginShrink.addUpdateListener(new ValueAnimator.AnimatorUpdateListener()
        {
            @Override
            public void onAnimationUpdate(ValueAnimator animation)
            {
                animated.setScaleX(1f - animation.getAnimatedFraction());
                if(fade) { animated.setAlpha(animation.getAnimatedFraction()); }
                animated.invalidate();
            }
        });

        loginShrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animated.setVisibility(View.GONE);
                if(callback != null) callback.run();
            }
        });
        loginShrink.start();
    }


    public void growView(int length, Position from, final boolean fade, final Runnable callback)
    {

        ValueAnimator loginShrink = ObjectAnimator.ofFloat(0f, 1f);
        loginShrink.setDuration(length);
        switch (from) {
            case RIGHT:
                animated.setPivotX(animated.getWidth());
                break;
            case LEFT:
                animated.setPivotX(0);
                break;
            case CENTER:
                animated.setPivotX(animated.getWidth()/2);
                break;
        }

        animated.setVisibility(View.VISIBLE);

        loginShrink.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                animated.setScaleX(animation.getAnimatedFraction());
                if (fade) {
                    animated.setAlpha(animation.getAnimatedFraction());
                }
                animated.invalidate();
            }
        });

        loginShrink.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                animated.setAlpha(1f);
                if(callback != null) callback.run();
            }
        });
        loginShrink.start();
    }



    public enum Position {LEFT, RIGHT, CENTER}

}

