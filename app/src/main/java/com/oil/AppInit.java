package com.oil;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.blankj.utilcode.util.Utils;
import com.oil.greendao.DaoMaster;
import com.oil.greendao.DaoSession;
import com.squareup.leakcanary.LeakCanary;
import com.ys.myapi.MyManager;

public class AppInit extends Application {

    private DaoMaster.DevOpenHelper mHelper;

    private SQLiteDatabase db;

    private DaoMaster mDaoMaster;

    private DaoSession mDaoSession;

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

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);
        instance = this;

        manager = MyManager.getInstance(this);

        manager.bindAIDLService(this);

        Utils.init(getContext());

        setDatabase();
    }

    private void setDatabase() {
        mHelper = new DaoMaster.DevOpenHelper(this, "reUpload-db", null);
        db = mHelper.getWritableDatabase();
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }
}
