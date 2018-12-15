package com.oil.Tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oil.Bean.ZhiwenBean;
import com.oil.R;

import java.util.List;

public class XSRecycleAdapter extends RecyclerView.Adapter<XSRecycleAdapter.XSViewHolder> {

    private Context mContext;
    List<ZhiwenBean> list;

    public XSRecycleAdapter(Context context, List<ZhiwenBean> list) {
        this.mContext = context;
        this.list = list;
    }

    @Override
    public XSViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        XSViewHolder holder = new XSViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.layout_xsitemunit, parent,
                false));
        return holder;
    }


    @Override
    public void onBindViewHolder(XSViewHolder holder, final int position)
    {
        holder.name.setText(list.get(position).getName());
        holder.cardid.setText(list.get(position).getCardid());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null)
                    onItemClickListener.onClick(position);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemLongClickListener != null)
                    onItemLongClickListener.onLongClick(position);
                //返回false会在长安结束后继续点击
                return true;
            }
        });


    }

    @Override
    public int getItemCount()
    {
        return list.size();
    }
    public interface OnItemClickListener {
        void onClick(int position);
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemLongClickListener {
        void onLongClick(int position);
    }

    OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }





    class XSViewHolder extends RecyclerView.ViewHolder
    {

        TextView name;
        TextView cardid;


        public XSViewHolder(View view)
        {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            cardid = (TextView) view.findViewById(R.id.cardid);
        }

    }
}

