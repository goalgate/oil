package com.oil.Alerts;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.oil.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.oil.R;

public class Alert_ReadCard {

    private TextView alarmText;

    private AlertView alert;

    private ViewGroup alarmView;

    private Context context;

    private static Alert_ReadCard instance = null;

    public static Alert_ReadCard getInstance(Context context) {
        if (instance == null) {
            instance = new Alert_ReadCard(context);
        }
        return instance;
    }

    private Alert_ReadCard(Context context) {
        this.context = context;
        alarmView = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.alarm_readcard, null);
        alarmText = (TextView) alarmView.findViewById(R.id.alarmText);
        alert = new AlertView(null, null, "取消", null, null, context, AlertView.Style.ActionSheet, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                IDCardPresenter.getInstance().stopReadCard();
            }
        });
        alert.addExtView(alarmView);
    }

    public void message(String msg) {
        alarmText.setText(msg);
        alert.show();
    }
    public void dismiss(){
        alert.dismiss();
    }

    public void release(){
        instance = null;
    }
}
