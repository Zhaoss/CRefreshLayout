package com.zhaoshuang.crefreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CRefreshLayout cRefreshLayout;

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
                myHandler.sendEmptyMessageDelayed(0, 2000);
            }
        });
    }

    Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), "刷新成功", Toast.LENGTH_SHORT).show();
            cRefreshLayout.setRefreshing(false);
        }
    };
}
