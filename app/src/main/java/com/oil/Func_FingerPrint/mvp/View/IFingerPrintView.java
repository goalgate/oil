package com.oil.Func_FingerPrint.mvp.View;

import android.graphics.Bitmap;

/**
 * Created by zbsz on 2017/6/2.
 */

public interface IFingerPrintView {
    void onSetImg(Bitmap bmp);
    void onBtnCancelEnable(boolean bEnable);
    void onSetID(String id);
    void onSetInitialState();
    void onEnableCtrl(boolean bEnable);
    void onText(String msg);
    void onBtnOpenEnable(boolean bEnable);
    void onBtnCloseEnable(boolean bEnable);
    void onResetTxt(String s);
}
