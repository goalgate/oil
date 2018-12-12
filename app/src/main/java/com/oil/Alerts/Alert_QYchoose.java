package com.oil.Alerts;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.oil.R;

public class Alert_QYchoose {


    private AlertView alert;

    private ViewGroup QYchooseView;

    private Context context;

    private static Alert_QYchoose instance = null;

    public static Alert_QYchoose getInstance(Context context,QYCItemClickListener listener) {
        if (instance == null) {
            instance = new Alert_QYchoose(context,listener);
        }
        return instance;
    }

    private Alert_QYchoose(Context context, final QYCItemClickListener listener) {
        this.context = context;
        alert =   new AlertView("请选择所需的汽油编号", null, "取消",
                new String[]{"92号汽油","95号汽油","98号汽油","柴油"}, null,
                context, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                listener.onItemClick(o,position);
            }
        });
    }

    public interface QYCItemClickListener{
        void onItemClick(Object o, int position);
    }

    public void show() {
        alert.show();
    }

    public void dismiss() {
        alert.dismiss();
    }

    public void release() {
        instance = null;
    }
}
