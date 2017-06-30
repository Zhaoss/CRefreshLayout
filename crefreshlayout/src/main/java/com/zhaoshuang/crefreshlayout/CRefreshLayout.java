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
    private OnCustomLoadListener onCustomLoadListener;
    private int maxRefreshScrollY = -1;
    private int maxLoadScrollY = -1;
    private float scrollRatio = 0.4f;
    private int refreshY;
    private int loadY;
    private boolean mRefreshState = true;
    private NestedScrollingChildHelper mNestedScrollingChildHelper;
    private View mLoadLayout;
    private boolean mLoading;
    private boolean isFistLayout = true;

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
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(isFistLayout) {
            isFistLayout = false;
            setRefreshLayout(false);
            setLoadLayout(false);
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mTarget = getChildAt(0);
        bindRefreshView(new RefreshView(getContext()));
    }

    private void refreshOpenAnim(){

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

    private void loadOpenAnim(){

        if(valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(mLoadLayout.getY(), mTarget.getHeight()-mLoadLayout.getHeight());
            valueAnimator.setDuration(100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mLoadLayout.setY(value);
                    mTarget.setY(value-mTarget.getHeight());
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

    private void loadCloseAnim(){

        if(valueAnimator == null) {
            valueAnimator = ValueAnimator.ofFloat(mLoadLayout.getY(), mTarget.getHeight());
            valueAnimator.setDuration(100);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    float value = (float) animation.getAnimatedValue();
                    mLoadLayout.setY(value);
                    mTarget.setY(value-mTarget.getHeight());
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

    private void refreshCloseAnim(){

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
        if(onRefreshListener != null) {
            onRefreshListener.onRefresh();
        }
        if(onCustomRefreshListener != null) {
            onCustomRefreshListener.onRefresh(mRefreshView);
        }
        if(mRefreshView instanceof CRefreshView){
            CRefreshView anim = (CRefreshView) mRefreshView;
            anim.startAnim();
        }
    }

    private void onLoad(){
        if(onCustomLoadListener != null) {
            onCustomLoadListener.onLoad(mLoadLayout);
        }
    }

    private void setRefreshLayout(boolean isOpen){

        if(mTarget!=null && mRefreshView!=null) {
            ViewGroup.LayoutParams layoutParams = mRefreshView.getLayoutParams();
            if (isOpen) {
                mRefreshView.setY(0);
                mTarget.setY(layoutParams.height);
            } else {
                mRefreshView.setY(-layoutParams.height);
                mTarget.setY(0);
            }
        }
    }

    private void setLoadLayout(boolean isOpen){

        if(mTarget!=null && mLoadLayout!=null) {
            ViewGroup.LayoutParams layoutParams = mLoadLayout.getLayoutParams();
            if (isOpen) {
                mLoadLayout.setY(mTarget.getHeight()-layoutParams.height);
                mTarget.setY(-layoutParams.height);
            } else {
                mLoadLayout.setY(mTarget.getHeight());
                mTarget.setY(0);
            }
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

        refreshY = mRefreshView.getLayoutParams().height;
    }

    /**
     * 绑定loadView
     */
    public void bindLoadView(View loadView) {

        if(mLoadLayout != null){
            removeView(mLoadLayout);
        }
        this.mLoadLayout = loadView;
        addView(mLoadLayout);

        loadY = -mLoadLayout.getLayoutParams().height;
    }

    /**
     * 设置加载状态
     */
    public void setLoading(boolean loading){
        if(loading){
            loadOpenAnim();
        }else{
            loadCloseAnim();
        }
        this.mLoading = loading;
    }

    /**
     * 设置刷新状态
     */
    public void setRefreshing(boolean refreshing){
        if(refreshing){
            refreshOpenAnim();
            if(mRefreshView instanceof CRefreshView){
                CRefreshView cRefreshView = (CRefreshView) mRefreshView;
                cRefreshView.startAnim();
            }
        }else{
            if(mRefreshView instanceof CRefreshView){
                CRefreshView cRefreshView = (CRefreshView) mRefreshView;
                cRefreshView.stopAnim(new CRefreshView.OnAnimationListener(){
                    @Override
                    public void onAnimationEnd(Animation animation) {
                        refreshCloseAnim();
                    }
                });
            }else {
                refreshCloseAnim();
            }
        }
        this.mRefreshing = refreshing;
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
     * 设置上拉加载控件的生命周期监听
     */
    public void setOnCustomLoadListener(OnCustomLoadListener listener) {
        this.onCustomLoadListener = listener;
    }

    public interface OnCustomLoadListener{
        /**
         * 开始上拉刷新
         */
        void onStart(View loadView);

        /**
         * 上拉拖拽中
         * @param dy 本次移动距离
         * @param current 已经拖拽距离
         * @param max 总可拖拽距离
         */
        void onMove(View loadView, int dy, int current, int max);

        /**
         * 手势抬起
         * @param currentY 已经拖拽距离
         */
        void onUp(View loadView, int currentY);

        /**
         * 触发加载更多
         */
        void onLoad(View loadView);
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
    public boolean isRefreshing(){
        return mRefreshing;
    }

    /**
     * 判断是否加载中
     */
    public boolean isLoading(){
        return mLoading;
    }

    /**
     * 限制下拉刷新时最大拖拽距离
     * @param max 值<0 表示不限制拖拽距离
     */
    public void setRefreshMaxScroll(int max){
        this.maxRefreshScrollY = max;
    }

    /**
     * 限制上拉加载时最大拖拽距离
     * @param max 值<0 表示不限制拖拽距离
     */
    public void setLoadMaxScroll(int max){
        this.maxLoadScrollY = max;
    }

    /**
     * 设置拖拽速度比, 即下拉拖拽时, 距离移动距离比率
     * @param scrollRatio 数值小于1, 下拉速度慢, 数值大于1, 下拉速度快
     */
    public void setScrollRatio(float scrollRatio){
        this.scrollRatio = scrollRatio;
    }

    /**
     * 设置下拉刷新触发点, 拖拽距离超过此值, 即满足刷新条件
     * @param refreshY 默认值为刷新控件的高度
     */
    public void setRefreshY(int refreshY){
        this.refreshY = refreshY;
    }

    /**
     * 设置上拉加载触发点, 上拉距离超过此值, 即满足加载条件
     * @param loadY 默认值为加载控件的高度
     */
    public void setLoadY(int loadY){
        this.loadY = loadY;
    }

    /**
     * 设置是否响应刷新操作
     * @param mRefreshState false即代表不响应刷新操作
     */
    public void setRefreshState(boolean mRefreshState){
        this.mRefreshState = mRefreshState;
    }

    //------------------------------------ NestedScrollingParent ---------------------

    boolean isFirstMove = true;
    @Override
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {

        if(!mRefreshing && !mLoading) {
            if (mRefreshView != null) {
                float slideY = -dy * scrollRatio;
                if (slideY > 0 && !mTarget.canScrollVertically(-1)) {
                    scrollRefresh(slideY);
                    if (isFirstMove) {
                        isFirstMove = false;
                        if (onCustomRefreshListener != null)
                            onCustomRefreshListener.onStart(mRefreshView);
                    }
                } else if (slideY < 0 && mRefreshView.getY() > -mRefreshView.getHeight()) {
                    scrollRefresh(slideY);
                    consumed[1] = dy;//吃掉移动距离, 不给子view
                }
            }

            if (mLoadLayout != null) {
                float slideY = -dy * scrollRatio;
                if (slideY < 0 && !mTarget.canScrollVertically(1)) {
                    scrollLoad(slideY);
                    if (isFirstMove) {
                        isFirstMove = false;
                        if (onCustomLoadListener != null) onCustomLoadListener.onStart(mLoadLayout);
                    }
                } else if (slideY > 0 && mLoadLayout.getY() < mTarget.getHeight()) {
                    scrollLoad(slideY);
                    consumed[1] = dy;//吃掉移动距离, 不给子view
                }
            }
        }
    }

    private void scrollLoad(float slideY){

        float y1 = mLoadLayout.getY() + slideY;
        float y2 = mTarget.getY() + slideY;

        if(maxLoadScrollY > 0) {
            if (y1 < mTarget.getHeight() - mLoadLayout.getHeight()) {
                y1 = mTarget.getHeight() - mLoadLayout.getHeight();
            }
            if (y2 < -mLoadLayout.getHeight()) {
                y2 = -mLoadLayout.getHeight();
            }
        }



        mLoadLayout.setY(y1);
        mTarget.setY(y2);

        if(onCustomLoadListener!=null && !isFirstMove) {
            onCustomLoadListener.onMove(mLoadLayout, (int) slideY, (int)-mTarget.getY(), maxLoadScrollY);
        }
    }

    private void scrollRefresh(float slideY){

        float y1 = mRefreshView.getY() + slideY;
        float y2 = mTarget.getY() + slideY;

        if (y1 < -mRefreshView.getHeight()) y1 = -mRefreshView.getHeight();
        if (y2 < 0) y2 = 0;

        if(maxRefreshScrollY > 0) {
            if (y1 > maxRefreshScrollY - mRefreshView.getHeight()) {
                y1 = maxRefreshScrollY - mRefreshView.getHeight();
            }
            if (y2 > maxRefreshScrollY) {
                y2 = maxRefreshScrollY;
            }
        }

        mRefreshView.setY(y1);
        mTarget.setY(y2);

        if(onCustomRefreshListener!=null && !isFirstMove) {
            onCustomRefreshListener.onMove(mRefreshView, (int) slideY, (int)mTarget.getY(), maxRefreshScrollY);
        }
    }

    @Override
    public void onStopNestedScroll(View target){

        mNestedScrollingParentHelper.onStopNestedScroll(target);
        isFirstMove = true;

        if(!mRefreshing && !mLoading) {
            if (mTarget.getY() > 0) {
                if (mTarget.getY() >= refreshY) {
                    mRefreshing = true;
                    setRefreshLayout(true);
                    onRefresh();
                } else {
                    mRefreshing = false;
                    refreshCloseAnim();
                }
                if (onCustomRefreshListener != null && mTarget.getY() != 0) {
                    onCustomRefreshListener.onUp(mRefreshView, (int) mTarget.getY());
                }
            }

            if (mTarget.getY() < 0) {
                if (mTarget.getY() <= loadY) {
                    mLoading = true;
                    setLoadLayout(true);
                    onLoad();
                } else {
                    mLoading = false;
                    loadCloseAnim();
                }
                if (onCustomLoadListener != null && mTarget.getY() != 0) {
                    onCustomLoadListener.onUp(mLoadLayout, (int) -mTarget.getY());
                }
            }
        }
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
