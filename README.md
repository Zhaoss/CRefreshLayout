# CRefreshLayout
**C = custom(自定义)   
CRefreshLayout = 可自定义的下拉刷新框架**
* * *
<br /> 
   
## 如何导入到项目中:
**1:** [![](https://jitpack.io/v/Zhaoss/CRefreshLayout.svg)](https://jitpack.io/#Zhaoss/CRefreshLayout)
```
//在你的project级build.gradle添加
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

//在你的module级build.gradle添加
dependencies {
    compile 'com.github.chenBingX:SuperTextView:v1.1'
}
```
**2:** 下载源码后, 将crefreshlayout文件夹作为modle导入进来.
* * *   
<br /> 
   
## 如何在项目中使用:
使用方法和SwipeRefreshLayout完全一致
```
//在布局文件中
<com.zhaoshuang.crefreshlayout.CRefreshLayout
    android:id="@+id/cRefreshLayout"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">
    <android.support.v7.widget.RecyclerView
        android:id="@+id/recyclerView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
</com.zhaoshuang.crefreshlayout.CRefreshLayout>

//在代码里监听刷新
cRefreshLayout.setOnRefreshListener(new CRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        Toast.makeText(getApplicationContext(), "触发刷新", Toast.LENGTH_SHORT).show();
        myHandler.sendEmptyMessageDelayed(0, 2000);
    }
});
```
* * *   
<br />  
  
## 进阶使用, 自定义刷新布局
```
//传入自定义view
cRefreshLayout.bindRefreshView(view);
//监听下拉事件
cRefreshLayout.setOnCustomRefreshListener(new CRefreshLayout.OnCustomRefreshListener() {
    @Override
    public void onStart(View refreshView) {
        //开始滑动
    }
    @Override
    public void onMove(View refreshView, int dy, int current, int max) {
        //滑动中
    }
    @Override
    public void onUp(View refreshView, int currentY) {
        //手指抬起, 此回调有可能和onRefresh()同时被调用
    }
    @Override
    public void onRefresh(View refreshView) {
        //触发刷新, 此回调有可能和onUp()同时被调用
    }
});
```
* * *   
<br />  
     
## API
```
//设置刷新状态
setRefreshing(boolean mRefreshing)

//判断是否刷新中
ismRefreshing()

//设置最大拖拽距离
//@param max 值<0 表示不限制拖拽距离
setMaxScroll(int max))

//设置拖拽速度比, 即下拉拖拽时, 距离移动距离比率
//@param scrollRatio 数值小于1, 下拉慢, 数值大于1, 下拉速度快
setMaxScroll(float scrollRatio)

//设置刷新触发点, 拖拽距离超过此值, 即满足刷新条件
//默认值为刷新控件的高度
setRefreshY(int refreshY)

//设置是否响应刷新操作
setRefreshState(boolean mRefreshState)
```
* * *   
<br /> 

### 如果此项目对你有帮助, 可以star一下, 如果有bug 欢迎lssues, 代码定期维护, 也会添加新的功能

