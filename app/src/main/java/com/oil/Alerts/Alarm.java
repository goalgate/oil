package com.oil.Alerts;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.oil.R;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;


public class Alarm {

    private TextView alarmText;

    private AlertView alert;

    private ViewGroup alarmView;

    private Context context;

    private static Alarm instance = null;

    public static Alarm getInstance(Context context) {
        if (instance == null) {
            instance = new Alarm(context);
        }
        return instance;
    }

    private Alarm(Context context) {
        this.context = context;
        alarmView = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.alarm_text, null);
        alarmText = (TextView) alarmView.findViewById(R.id.alarmText);
        alert = new AlertView(null, null, null, new String[]{"确定"}, null, context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {

            }
        });
        alert.addExtView(alarmView);
    }

    public void message(String msg) {
        alarmText.setText(msg);
        alert.show();
    }
    public void messageDelay(String msg) {
        alarmText.setText(msg);
        Observable.timer(1,TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        alert.show();
                    }
                });
    }

    public void release(){
        instance = null;
    }


}
