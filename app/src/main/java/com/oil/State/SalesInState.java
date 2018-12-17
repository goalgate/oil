package com.oil.State;

import android.widget.TextView;

public class SalesInState extends Operation {
    @Override
    public void onHandle(State state, TextView textView) {
        state.setOperation(new WaitUploadState());
        textView.setText("请录入购买信息后点击提交");
    }
}
