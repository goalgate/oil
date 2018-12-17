package com.oil.State;

import android.widget.TextView;

import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;

public class WaitFingerPrintState extends Operation{


    @Override
    public void onHandle(State state, TextView textView) {
        FingerprintPresenter.getInstance().fpCancel();
        state.setOperation(new SalesInState());
        textView.setText("请销售人员认证身份（读身份证或指纹识别）");
        FingerprintPresenter.getInstance().fpIdentify();
    }
}
