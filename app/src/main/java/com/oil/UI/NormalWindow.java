package com.oil.UI;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.oil.R;


/**
 * Created by zbsz on 2017/7/16.
 */


public class NormalWindow extends PopupWindow implements View.OnClickListener{

    private View mContentView;
    private Activity mActivity;
    OptionTypeListener listener;
    Button server;
    Button staticIP;


    public NormalWindow(Activity activity) {
        mActivity = activity;
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mContentView = LayoutInflater.from(activity).inflate(R.layout.window_normal, null);
        setContentView(mContentView);
        server = (Button) mContentView.findViewById(R.id.btn_Server);
        staticIP = (Button) mContentView.findViewById(R.id.btn_StaticIP);
        server.setOnClickListener(this);
        staticIP.setOnClickListener(this);
        setFocusable(true);
        setOutsideTouchable(true);
        setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
        setAnimationStyle(R.style.Person_type_Popup);
        setOnDismissListener(new OnDismissListener(){
            @Override
            public void onDismiss() {
                lighton();
            }
        });
    }


    private void lighton() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();
        lp.alpha = 1.0f;
        mActivity.getWindow().setAttributes(lp);
    }

    private void lightoff() {
        WindowManager.LayoutParams lp = mActivity.getWindow().getAttributes();

        lp.alpha = 0.3f;
        mActivity.getWindow().setAttributes(lp);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        lightoff();
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        lightoff();
        super.showAtLocation(parent, gravity, x, y);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_Server:
                listener.onOptionType(server,1);
                break;
            case R.id.btn_StaticIP :
                listener.onOptionType(staticIP,2);
                break;

        }
    }

    public interface OptionTypeListener {
        void onOptionType(Button view, int type);
    }

    public void setOptionTypeListener(OptionTypeListener listener){
        this.listener = listener;
    }

}

