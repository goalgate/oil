package com.oil.State;

import android.widget.TextView;

import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;

public class GetFingerprintState extends Operation{

    @Override
    public void onHandle(State state, TextView textView) {

        state.setOperation(new SalesState());
        textView.setText("销售人记录成功，请确认汽油标号和数量是否正确，点击提交上传销售记录");

    }
}
