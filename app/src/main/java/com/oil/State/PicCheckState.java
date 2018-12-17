package com.oil.State;

import android.widget.TextView;

import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;

public class PicCheckState extends Operation {


    @Override
    public void onHandle(State state, TextView textView) {
        FingerprintPresenter.getInstance().fpCancel();
        state.setOperation(new WaitFingerPrintState());
        textView.setText("请购买人按指纹");
        FingerprintPresenter.getInstance().fpCaptureImg();
    }
}
