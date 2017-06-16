package com.zhaoshuang.crefreshlayout;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.widget.FrameLayout;

/**
 * Created by zhaoshuang on 17/6/15.
 */

public abstract class CRefreshView extends FrameLayout{

    public CRefreshView(@NonNull Context context) {
        super(context);
    }

    public CRefreshView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CRefreshView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public abstract void startAnim();

    public abstract void stopAnim(OnAnimationListener animationListener);

    public abstract void initRefreshView();

    public static class OnAnimationListener implements  Animation.AnimationListener {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    }
}
