package com.oil.State;

import android.widget.TextView;

import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;

public class BuyerState extends Operation {

    @Override
    public void onHandle(State state, TextView textView) {
        FingerprintPresenter.getInstance().fpCancel();
        state.setOperation(new GetFingerprintState());
        textView.setText("购买人指纹信息已录入成功，请销售人完成身份验证");
        FingerprintPresenter.getInstance().fpIdentify();

    }
}
