package com.oil.State;

import android.widget.TextView;

import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;

public class CommonState extends Operation {

    @Override
    public void onHandle(State state, TextView textView) {
//        FingerprintPresenter.getInstance().fpCancel();
//        state.setOperation(new BuyerState());
//        textView.setText("购买人信息已录入，请购买人输入指纹信息");
//        FingerprintPresenter.getInstance().fpCaptureImg();

        state.setOperation(new PicCheckState());
    }

}
