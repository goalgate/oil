package com.oil.Func_FingerPrint.mvp.View;

import android.graphics.Bitmap;

/**
 * Created by zbsz on 2017/6/2.
 */

public interface IFingerPrintView2 {
    void onSetImg(Bitmap bmp);

    void onText(String msg);

    void onRegSuccess();

    void onCapturing(Bitmap bmp);

    void onFpSucc(String msg);

}
