package com.oil.Tools;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oil.PersonActivity;
import com.oil.R;

import java.util.List;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> nameDatas;
    private List<String> cardidDatas;
    private List<String> professionalDatas;

    public RecycleAdapter(Context context, List<String> list1,List<String> list2,List<String> list3) {
        this.mContext = context;
        this.nameDatas = list1;
        cardidDatas = list2;
        professionalDatas = list3;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        MyViewHolder holder = new MyViewHolder(LayoutInflater.from(
                mContext).inflate(R.layout.layout_itemunit, parent,
                false));
        return holder;
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        holder.name.setText(nameDatas.get(position));
        holder.cardid.setText(cardidDatas.get(position));
        holder.professional.setText(professionalDatas.get(position));
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
        return nameDatas.size();
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





    class MyViewHolder extends RecyclerView.ViewHolder
    {

        TextView name;
        TextView cardid;
        TextView professional;

        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            cardid = (TextView) view.findViewById(R.id.cardid);
            professional = (TextView)view.findViewById(R.id.professional);
        }

    }
}

