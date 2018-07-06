package blocker.call.wallpaper.screen.caller.ringtones.callercolor.utils;

import android.animation.Animator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;

/**
 * Created by js1219 on 17-5-22.
 */

public class AnimatorUtils {

    public static ValueAnimator newValueAnimator(long duration, TypeEvaluator typeEvaluator) {
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(duration);
        valueAnimator.setObjectValues("");
        valueAnimator.setEvaluator(typeEvaluator);
        return valueAnimator;
    }
    public static ValueAnimator newValueAnimator(long duration, TypeEvaluator typeEvaluator, Animator.AnimatorListener listener){
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(duration);
        valueAnimator.setObjectValues("");
        valueAnimator.setEvaluator(typeEvaluator);
        if (listener!=null) {
            valueAnimator.addListener(listener);
        }
        return valueAnimator;
    }
    public static ValueAnimator newValueAnimator(long duration, TypeEvaluator typeEvaluator, final OnAnimatorEnd end){
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setDuration(duration);
        valueAnimator.setObjectValues("");
        valueAnimator.setEvaluator(typeEvaluator);
        if (end!=null) {
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {

                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    end.onAnimatorEnd();
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
        }
        return valueAnimator;
    }

    public interface OnAnimatorEnd{
        void onAnimatorEnd();
    }

}
