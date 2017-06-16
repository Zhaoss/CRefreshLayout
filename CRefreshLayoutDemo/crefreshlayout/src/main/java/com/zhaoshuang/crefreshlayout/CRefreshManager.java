package com.zhaoshuang.crefreshlayout;

import android.content.Context;

/**
 * Created by zhaoshuang on 17/6/15.
 */

public class CRefreshManager {

    public static void bindView(Context context, CRefreshLayout refreshLayout){

        RefreshView refreshView = new RefreshView(context);
        refreshLayout.bindRefreshView(refreshView);
    }
}
