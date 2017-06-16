package com.zhaoshuang.crefreshlayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

/**
 * Created by zhaoshuang on 17/6/7.
 */

public class RefreshView extends CRefreshView {

    private TextView textView;

    public RefreshView(Context context) {
        super(context);
        initRefreshView();
    }

    public RefreshView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initRefreshView();
    }

    public RefreshView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initRefreshView();
    }

    public void initRefreshView() {

        int textViewWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

        textView = new TextView(getContext());
        LayoutParams layoutParams = new LayoutParams(textViewWidth, textViewWidth);
        layoutParams.gravity = Gravity.CENTER;
        textView.setLayoutParams(layoutParams);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(20);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.RED);

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100, getResources().getDisplayMetrics());
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, width));

        addView(textView);
    }

    /**
     * 开启刷新动画
     */
    public void startAnim(){

        Rotate3dAnimation rotate3dAnimation = new Rotate3dAnimation(0, 360, textView.getHeight()/2, textView.getHeight()/2, 0, false);
        rotate3dAnimation.setDuration(300);
        rotate3dAnimation.setRepeatCount(Integer.MAX_VALUE);
        textView.startAnimation(rotate3dAnimation);
    }

    /**
     * 关闭刷新动画
     */
    public void stopAnim(final CRefreshView.OnAnimationListener animationListener){

        textView.setText("✔");
        textView.clearAnimation();
        Rotate3dAnimation rotate3dAnimation = new Rotate3dAnimation(0, 360, textView.getHeight()/2, textView.getHeight()/2, 0, false);
        rotate3dAnimation.setDuration(1000);
        rotate3dAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Message msg = myHandler.obtainMessage();
                msg.obj = animationListener;
                myHandler.sendMessageDelayed(msg, 500);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        textView.startAnimation(rotate3dAnimation);
    }

    private Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Animation.AnimationListener animationListener = (Animation.AnimationListener) msg.obj;
            animationListener.onAnimationEnd(null);
            textView.setText("");
        }
    };
}
