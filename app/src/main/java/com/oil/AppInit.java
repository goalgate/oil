package com.oil;

import android.app.Application;
import android.content.Context;
import com.blankj.utilcode.util.Utils;
import com.ys.myapi.MyManager;

public class AppInit extends Application {

    protected static MyManager manager;

    protected static AppInit instance;

    public static AppInit getInstance() {
        return instance;
    }

    public static MyManager getMyManager() {
        return manager;
    }

    public static Context getContext() {
        return getInstance().getApplicationContext();
    }
    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        manager = MyManager.getInstance(this);

        manager.bindAIDLService(this);

        Utils.init(getContext());
    }
}
