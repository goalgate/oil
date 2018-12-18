package com.oil;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.oil.Tools.AssetsUtils;


import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zbsz on 2017/12/8.
 */

public class StartActivity extends Activity {

    private String regEx = "^\\d{4}$";

    private SPUtils config = SPUtils.getInstance("config");

    Pattern pattern = Pattern.compile(regEx);

    @BindView(R.id.dev_prefix)
    TextView dev_prefix;

    @BindView(R.id.devid_input)
    EditText dev_suffix;

    @OnClick(R.id.next)
    void next() {
        if (pattern.matcher(dev_suffix.getText().toString()).matches()) {
            config.put("firstStart", false);
            //config.put("ServerId", "http://61.144.19.121:88/");
            config.put("ServerId", "http://14.23.69.2:1036/");
            config.put("daid", dev_prefix.getText().toString() + dev_suffix.getText().toString());
            ActivityUtils.startActivity(getPackageName(),getPackageName()+".MainActivity");
            StartActivity.this.finish();
            ToastUtils.showLong("设备ID设置成功");
            AssetsUtils.getInstance(AppInit.getContext()).copyAssetsToSD("wltlib","wltlib");
        } else {
            ToastUtils.showLong("设备ID输入错误，请重试");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_form);
        ButterKnife.bind(this);
        dev_prefix.setText("800800");

    }
}
