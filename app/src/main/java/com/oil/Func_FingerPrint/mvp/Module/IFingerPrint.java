package com.oil.Func_FingerPrint.mvp.Module;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

/**
 * Created by zbsz on 2017/6/2.
 */

public interface IFingerPrint {
    void onInit(Context context, IFPListener2 listener);
    boolean onOpen(IFPListener2 listener);
    void onClose(IFPListener2 listener);
    void onCancel();
    void onEnroll(String id, IFPListener2 listener);
    void onVerify(String id, IFPListener2 listener);
    void onIdentify(IFPListener2 listener);
    void onGetEnrollCount(IFPListener2 listener);
    int onGetEmptyID(IFPListener2 listener);
    void onCaptureImg(IFPListener2 listener);
    void onRemoveTmpl(String TmplId, IFPListener2 listener);
    void onRemoveAll(IFPListener2 listener);
    void onUpTemplate(String id, IFPListener2 listener);
    void onDownTemplate(String id, IFPListener2 listener);
//    void onReset( Activity activity);
    void onReSetUSB();

    interface IFPListener{

        void onSetImg(Bitmap bmp);

        void onSetID(String id);

        void onSetInitialState();

        void onBtnCancelEnable(boolean bEnable);

        void onBtnOpenEnable(boolean bEnable);

        void onBtnCloseEnable(boolean bEnable);

        void onEnableCtrl(boolean bEnable);

        void onText(String msg);

        void onResetTxt(String s);

    }

    interface IFPListener2{

        void onSetImg(Bitmap bmp);

        void onText(String msg);

        void onRegSuccess();

        void onCapturing(Bitmap bmp);

        void onFpSucc(String msg);
    }
}
