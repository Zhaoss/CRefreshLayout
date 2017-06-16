package com.zhaoshuang.crefreshlayout;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.NestedScrollingChild;
import android.support.v4.view.NestedScrollingChildHelper;
import android.support.v4.view.NestedScrollingParent;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;

/**
 * Created by zhaoshuang on 17/4/20.
 * 可自定义的刷新控件
 */
public class CRefreshLayout extends FrameLayout implements NestedScrollingParent, NestedScrollingChild {

    private View mTarget;
    private View mRefreshView;
    private NestedScrollingParentHelper mNestedScrollingParentHelper;
    private boolean mRefreshing;
    private OnRefreshListener onRefreshListener;
    private ValueAnimator valueAnimator;
    private OnCustomRefreshListener onCustomRefreshListener;
    private int maxScrollY = -1;
    private float scrollRatio = 0.4f;
    private int refreshY;
    private boolean mRefreshState = true;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;

    public CRefreshLayout(Context context) {
        super(context);
        init();
    }

    public CRefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CRefreshLayout(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {

        mNestedScrollingParentHelper = new NestedScrollingParentHelper(this);
        mNestedScrollingChildHelper = new NestedScrollingChildHelper(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTarget = getChildAt(0);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(mRefreshView == null){
            CRefreshManager.bindView(getContext(), this);
        }
        setRefreshLayout(false);
    }

    /**
     * 判断控件是否可以滑动
     */
    private boolean canChildScrollUp(View view) {
        return ViewCompat.canScrollVertically(view, -1);
    }

    private void openAnim(){

        if(valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(mRefreshView.getY(), 0);
            valueAnimator.setDuration(100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mRefreshView.setY(value);
                    mTarget.setY(mRefreshView.getHeight() + value);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    valueAnimator = null;
                }
            });
            valueAnimator.start();
        }
    }

    private void closeAnim(){

        if(valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(mRefreshView.getY(), -mRefreshView.getHeight());
            valueAnimator.setDuration(100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mRefreshView.setY(value);
                    mTarget.setY(mRefreshView.getHeight() + value);
                }
            });
            valueAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    valueAnimator = null;
                }
            });
            valueAnimator.start();
        }
    }

    private void onRefresh(){
        if(onRefreshListener != null) onRefreshListener.onRefresh();
        if(onCustomRefreshListener != null) onCustomRefreshListener.onRefresh(mRefreshView);
        if(mRefreshView instanceof CRefreshView){
            CRefreshView anim = (CRefreshView) mRefreshView;
            anim.startAnim();
        }
    }

    private void setRefreshLayout(boolean isOpen){
        ViewGroup.LayoutParams layoutParams = mRefreshView.getLayoutParams();
        if(isOpen){
            mRefreshView.setY(0);
            mTarget.setY(layoutParams.height);
        }else{
            mRefreshView.setY(-layoutParams.height);
            mTarget.setY(0);
        }
    }

    //------------------------------------ public ---------------------

    /**
     * 绑定刷新控件
     */
    public void bindRefreshView(View refreshView) {

        if(mRefreshView != null){
            removeView(mRefreshView);
        }
        this.mRefreshView = refreshView;
        addView(refreshView);
    }

    /**
     * 设置刷新状态
     */
    public void setRefreshing(boolean mRefreshing){
        if(mRefreshing){
            openAnim();
        }else{
            if(mRefreshView instanceof CRefreshView){
                CRefreshView cRefreshView = (CRefreshView) mRefreshView;
                cRefreshView.stopAnim(new CRefreshView.OnAnimationListener(){
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        closeAnim();
                    }
                });
            }else {
                closeAnim();
            }
        }
        this.mRefreshing = mRefreshing;
    }

    /**
     * 设置刷新监听
     * @param listener
     */
    public void setOnRefreshListener(OnRefreshListener listener) {
        this.onRefreshListener = listener;
    }

    public interface OnRefreshListener{

        /**
         * 触发刷新
         */
        void onRefresh();
    }

    /**
     * 设置刷新控件的生命周期监听
     */
    public void setOnCustomRefreshListener(OnCustomRefreshListener listener) {
        this.onCustomRefreshListener = listener;
    }

    public interface OnCustomRefreshListener {

        /**
         * 触发拖拽操作
         */
        void onStart(View refreshView);

        /**
         * 下拉拖拽中
         * @param dy 本次移动距离
         * @param current 已经拖拽距离
         * @param max 总可拖拽距离
         */
        void onMove(View refreshView, int dy, int current, int max);

        /**
         * 手势抬起
         * @param currentY 已经拖拽距离
         */
        void onUp(View refreshView, int currentY);

        /**
         * 触发刷新
         */
        void onRefresh(View refreshView);
    }

    /**
     * 判断是否刷新中
     */
    public boolean ismRefreshing(){
        return mRefreshing;
    }

    /**
     * 设置最大拖拽距离
     * @param max 值<0 表示不限制拖拽距离
     */
    public void setMaxScroll(int max){
        this.maxScrollY = max;
    }

    /**
     * 设置拖拽速度比, 即下拉拖拽时, 距离移动距离比率
     * @param scrollRatio 数值小于1, 下拉慢, 数值大于1, 下拉速度快
     */
    public void setMaxScroll(float scrollRatio){
        this.scrollRatio = scrollRatio;
    }

    /**
     * 设置刷新触发点, 拖拽距离超过此值, 即满足刷新条件
     * @param refreshY 默认值为刷新控件的高度
     */
    public void setRefreshY(int refreshY){
        this.refreshY = refreshY;
    }

    /**
     * 设置是否响应刷新操作
     * @param mRefreshState false即代表不响应刷新操作
     */
    public void setRefreshState(boolean mRefreshState){
        this.mRefreshState = mRefreshState;
    }

    //------------------------------------ NestedScrollingParent ---------------------

    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        if(mRefreshView !=null) {
            float slideY = -dy*scrollRatio;
            if (slideY>0 && !canChildScrollUp(mTarget)) {
                scroll(slideY);
            }
            if(slideY<0 && mRefreshView.getY()>-mRefreshView.getHeight()){
                scroll(slideY);
                consumed[1]=dy;//吃掉移动距离, 不给子view
            }

            if(onCustomRefreshListener != null) onCustomRefreshListener.onMove(mRefreshView, dy, (int)mTarget.getY(), -1);
        }
    }

    private void scroll(float slideY){

        float y1 = mRefreshView.getY() + slideY;
        float y2 = mTarget.getY() + slideY;

        if (y1 < -mRefreshView.getHeight()) y1 = -mRefreshView.getHeight();
        if (y2 < 0) y2 = 0;

        if(maxScrollY > 0) {
            if (y1 > maxScrollY - mRefreshView.getHeight()) y1 = maxScrollY - mRefreshView.getHeight();
            if (y2 > maxScrollY) y2 = maxScrollY;
        }

        mRefreshView.setY(y1);
        mTarget.setY(y2);
    }

    @Override
    public void onStopNestedScroll(View target){

        mNestedScrollingParentHelper.onStopNestedScroll(target);

        if(refreshY == 0) {
            refreshY = mRefreshView.getHeight();
        }

        if(!mRefreshing) {
            if(mTarget.getY() >= refreshY){
                mRefreshing = true;
                setRefreshLayout(true);
                onRefresh();
            }else{
                closeAnim();
                mRefreshing = false;
            }
        }

        if(onCustomRefreshListener != null) onCustomRefreshListener.onUp(mRefreshView, (int)mTarget.getY());
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override
    public void onNestedScrollAccepted(View child, View target, int axes) {
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes);
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedScrollingParentHelper.getNestedScrollAxes();
    }

    @Override
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if(onCustomRefreshListener != null) onCustomRefreshListener.onStart(mRefreshView);
        return mRefreshState;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    //------------------------------------ NestedScrollingChild ---------------------

    @Override
    public void setNestedScrollingEnabled(boolean enabled) {
        mNestedScrollingChildHelper.setNestedScrollingEnabled(enabled);
    }

    @Override
    public boolean isNestedScrollingEnabled() {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled();
    }

    @Override
    public boolean startNestedScroll(int axes) {
        return mNestedScrollingChildHelper.startNestedScroll(axes);
    }

    @Override
    public void stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll();
    }

    @Override
    public boolean hasNestedScrollingParent() {
        return mNestedScrollingChildHelper.hasNestedScrollingParent();
    }

    @Override
    public boolean dispatchNestedScroll(int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedPreScroll(int dx, int dy, int[] consumed, int[] offsetInWindow) {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow);
    }

    @Override
    public boolean dispatchNestedFling(float velocityX, float velocityY, boolean consumed) {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed);
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY);
    }
}
