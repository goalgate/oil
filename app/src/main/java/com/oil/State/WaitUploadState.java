package com.oil.State;

import android.widget.TextView;

public class WaitUploadState extends Operation{

    @Override
    public void onHandle(State state, TextView textView) {
        state.setOperation(new CommonState());
        textView.setText("请读购买人身份证");
    }
}
