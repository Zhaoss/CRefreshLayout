package com.zhaoshuang.crefreshlayout;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by zhaoshuang on 17/6/20.
 */

public class MyAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private int[] datas;

    public MyAdapter(Context mContext, int[] datas){
        this.mContext = mContext;
        this.datas = datas;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(View.inflate(mContext, R.layout.item, null));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyViewHolder vh = (MyViewHolder) holder;
        vh.textView.setText(datas[position]+"");
    }

    @Override
    public int getItemCount() {
        return datas.length;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        private TextView textView;

        public MyViewHolder(View itemView) {
            super(itemView);

            itemView.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, 400));

            textView = (TextView) itemView.findViewById(R.id.textView);
        }
    }
}
