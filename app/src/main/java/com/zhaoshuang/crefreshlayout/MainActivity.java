package com.zhaoshuang.crefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CRefreshLayout cRefreshLayout;
    private TextView tv_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cRefreshLayout = (CRefreshLayout) findViewById(R.id.cRefreshLayout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int[] datas = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
        recyclerView.setAdapter(new MyAdapter(this, datas));

        cRefreshLayout.setOnRefreshListener(new CRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(), "触发刷新", Toast.LENGTH_SHORT).show();
                myHandler.sendEmptyMessageDelayed(0, 2000);
            }
        });

        View view = View.inflate(this, R.layout.foot_layout, null);
        tv_load = (TextView) view.findViewById(R.id.tv_load);
        int height = (int) getResources().getDimension(R.dimen.dp100);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        cRefreshLayout.bindLoadView(view);
        cRefreshLayout.setOnCustomLoadListener(new CRefreshLayout.OnCustomLoadListener() {
            @Override
            public void onStart(View loadView) {
                tv_load.setText("下拉加载");
            }

            @Override
            public void onMove(View loadView, int dy, int current, int max) {
                if(current >= loadView.getHeight()){
                    tv_load.setText("松手即可加载");
                }else{
                    tv_load.setText("下拉加载");
                }
            }

            @Override
            public void onUp(View loadView, int currentY) {
                Log.i("Log.i", "onUp: "+currentY);
            }

            @Override
            public void onLoad(View loadView) {
                tv_load.setText("正在加载中...");
                myHandler.sendEmptyMessageDelayed(1, 2000);
            }
        });
    }

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0){
                Toast.makeText(getApplicationContext(), "刷新成功", Toast.LENGTH_SHORT).show();
                cRefreshLayout.setRefreshing(false);
            }else{
                Toast.makeText(getApplicationContext(), "加载成功", Toast.LENGTH_SHORT).show();
                cRefreshLayout.setLoading(false);
            }
        }
    };
}
