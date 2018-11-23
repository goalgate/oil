package com.oil;

import android.app.Activity;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
/**
 * Created by zbsz on 2017/12/8.
 */

public class SplashActivity extends Activity {

    private static final String PREFS_NAME = "config";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SPUtils SP_Config = SPUtils.getInstance(PREFS_NAME);

        if (SP_Config.getBoolean("firstStart", true)) {
            ActivityUtils.startActivity(getPackageName(),getPackageName()+".StartActivity");
            this.finish();
        }else {
            ActivityUtils.startActivity(getPackageName(), getPackageName() + ".MainActivity");
            this.finish();
        }
    }
}
