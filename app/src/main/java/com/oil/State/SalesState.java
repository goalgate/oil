package com.oil.State;

import android.widget.TextView;

import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;

public class SalesState extends Operation{


    @Override
    public void onHandle(State state, TextView textView) {
        FingerprintPresenter.getInstance().fpCancel();
        state.setOperation(new CommonState());
        textView.setText("等待购买人输入信息");
    }
}
