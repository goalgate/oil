package com.oil.Tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oil.Bean.XiaoshouJiLUBean;
import com.oil.Bean.ZhiwenBean;
import com.oil.R;

import java.util.List;

public class XSJLRecycleAdapter extends RecyclerView.Adapter<XSJLRecycleAdapter.XSJLViewHolder>{

    private Context mContext;
    List<XiaoshouJiLUBean> list;

    public XSJLRecycleAdapter(Context context, List<XiaoshouJiLUBean> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public XSJLRecycleAdapter.XSJLViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        XSJLRecycleAdapter.XSJLViewHolder holder = new XSJLRecycleAdapter.XSJLViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.layout_xsjlitemunit, parent,
                false));
        return holder;
    }


    @Override
    public void onBindViewHolder(XSJLRecycleAdapter.XSJLViewHolder holder, final int position)
    {
        holder.tv_xiaoshouren.setText(list.get(position).getSellername());
        holder.tv_goumairen.setText(list.get(position).getBuyername());
        holder.tv_qiyoushuliang.setText(list.get(position).getVolume());
        holder.tv_goumairiqi.setText(list.get(position).getSalesDate());

    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }






    class XSJLViewHolder extends RecyclerView.ViewHolder
    {

        TextView tv_xiaoshouren;
        TextView tv_goumairen;
        TextView tv_qiyoushuliang;
        TextView tv_goumairiqi;


        public XSJLViewHolder(View view)
        {
            super(view);
            tv_xiaoshouren = (TextView) view.findViewById(R.id.tv_xiaoshouren);
            tv_goumairen = (TextView) view.findViewById(R.id.tv_goumairen);
            tv_qiyoushuliang = (TextView) view.findViewById(R.id.tv_qiyoushuliang);
            tv_goumairiqi = (TextView) view.findViewById(R.id.tv_goumairiqi);
        }

    }
}
