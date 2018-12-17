package com.oil.Tools;

import android.app.ProgressDialog;
import android.content.Context;

import com.oil.Alerts.Alarm;

import java.io.IOException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.ResponseBody;

public class MyObserver implements Observer<ResponseBody> {

    private Context context;

    private Callback callback;

    ProgressDialog progressDialog;

    public MyObserver(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
        progressDialog = new ProgressDialog(context);
    }

    @Override
    public void onSubscribe(Disposable d) {
        progressDialog.setMessage("数据上传中，请稍候");
        progressDialog.show();
    }

    @Override
    public void onNext(ResponseBody responseBody) {
        try {
            String s = responseBody.string();
            if (!(s == null || "".equals(s))) {
                if (s.equals("caridErr")) {
                    Alarm.getInstance(context).messageDelay("身份证号不正确");
                } else if (s.equals("dbErr")) {
                    Alarm.getInstance(context).messageDelay("连接数据库出错");
                } else if (s.equals("dataNo")) {
                    Alarm.getInstance(context).messageDelay("没有找到购买证数据");
                } else if (s.equals("null")) {
                    Alarm.getInstance(context).messageDelay("没有找到购买证数据");
                } else if (s.equals("dataYes")) {
                    //Alarm.getInstance(context).messageDelay("有销售人员备案");
                    callback.onResponseString(s);
                } else {
                    callback.onResponseString(s);
                }
            } else {
                Alarm.getInstance(context).messageDelay("服务器返回参数为空，请确认网络是否已联通");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onError(Throwable e) {
        progressDialog.dismiss();
        Alarm.getInstance(context).messageDelay("服务器连接失败,无法确认销售员信息");

    }


    @Override
    public void onComplete() {
        progressDialog.dismiss();
    }

    public interface Callback {
        void onResponseString(String s);
    }
}
