package com.dennisvandalen.choozze;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class RotateAnimator {
    public static void rotate360(View view) {
        // Create an animation instance
        Animation an = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        // Set the animation's parameters
        an.setDuration(1000);
        an.setRepeatCount(-1);
        an.setRepeatMode(Animation.INFINITE);
        an.setInterpolator(new LinearInterpolator());

        view.startAnimation(an);
    }
}
