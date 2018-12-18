package com.oil.Func_FingerPrint.mvp.Presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.oil.Func_FingerPrint.mvp.Module.FingerPrintImpl;
import com.oil.Func_FingerPrint.mvp.Module.IFingerPrint;
import com.oil.Func_FingerPrint.mvp.View.IFingerPrintView;
import com.oil.Func_FingerPrint.mvp.View.IFingerPrintView2;


/**
 * Created by zbsz on 2017/6/2.
 */

public class FingerprintPresenter {

    private IFingerPrintView2 view;

    private static FingerprintPresenter instance = null;

    private FingerprintPresenter() {
    }

    public static FingerprintPresenter getInstance() {
        if (instance == null)
            instance = new FingerprintPresenter();
        return instance;
    }

    public void FingerPrintPresenterSetView(IFingerPrintView2 view) {
        this.view = view;
    }

    IFingerPrint fpModule = new FingerPrintImpl();

    public void fpInit(Context context){
        fpModule.onInit(context,getFpListener());
    }

    public void fpOpen(){

        fpModule.onOpen(getFpListener());
    }

    public void fpClose(){
        fpModule.onClose(getFpListener());

    }

    public void fpCancel(){
        fpModule.onCancel();
    }

    public void fpEnroll(String id){
        fpModule.onEnroll(id,getFpListener());
    }

    public void fpVerify(String id){
        fpModule.onVerify(id,getFpListener());
    }

    public void fpIdentify(){
        fpModule.onIdentify(getFpListener());
    }

    public void fpGetEnrollCount(){
        fpModule.onGetEnrollCount(getFpListener());
    }

    public int fpGetEmptyID(){
        return fpModule.onGetEmptyID(getFpListener());
    }

    public void fpCaptureImg(){
        fpModule.onCaptureImg(getFpListener());
    }

    public void fpRemoveTmpl(String TmplId){
        fpModule.onRemoveTmpl(TmplId,getFpListener());
    }

    public void fpRemoveAll(){
        fpModule.onRemoveAll(getFpListener());
    }

    public void fpUpTemplate(String id) { fpModule.onUpTemplate(id,getFpListener());}

    public void fpDownTemplate(String id) { fpModule.onDownTemplate(id,getFpListener());}

    public void fpReSetUSB() { fpModule.onReSetUSB();}


    private IFingerPrint.IFPListener2 getFpListener() {
        return new IFingerPrint.IFPListener2() {

            @Override
            public void onSetImg(Bitmap bmp) {
                if (view != null) {
                    view.onSetImg(bmp);
                }
            }



            @Override
            public void onText(String msg) {
                if (view != null) {
                    view.onText(msg);
                }
            }

            @Override
            public void onRegSuccess() {
                if (view != null) {
                    view.onRegSuccess();
                }
            }

            @Override
            public void onCapturing(Bitmap bmp) {
                if (view != null) {
                    view.onCapturing(bmp);
                }
            }

            @Override
            public void onFpSucc(String msg) {
                if (view != null) {
                    view.onFpSucc(msg);
                }
            }
        };
    }
//    public void fpReset(Activity activity ){
//        fpModule.onReset(activity);
//    }

//    private IFingerPrint.IFPListener getFpListener() {
////        return new IFingerPrint.IFPListener() {
////
////            @Override
////            public void onSetImg(Bitmap bmp) {
////                view.onSetImg(bmp);
////            }
////
////            @Override
////            public void onBtnCancelEnable(boolean bEnable) {
////                if (view != null) {
////                    view.onBtnCancelEnable(bEnable);
////                }
////            }
////
////            @Override
////            public void onSetID(String id) {
////                if (view != null) {
////                    view.onSetID(id);
////                }
////            }
////
////            @Override
////            public void onSetInitialState() {
////                if (view != null) {
////                    view.onSetInitialState();
////                }
////            }
////
////            @Override
////            public void onBtnOpenEnable(boolean bEnable) {
////                if (view != null) {
////                    view.onBtnOpenEnable(bEnable);
////                }
////            }
////
////            @Override
////            public void onBtnCloseEnable(boolean bEnable) {
////                if (view != null) {
////                    view.onBtnCloseEnable(bEnable);
////                }
////            }
////
////            @Override
////            public void onEnableCtrl(boolean bEnable) {
////                if (view != null) {
////                    view.onEnableCtrl(bEnable);
////                }
////            }
////
////            @Override
////            public void onText(String msg) {
////                if (view != null) {
////                    view.onText(msg);
////                }
////            }
////
////            @Override
////            public void onResetTxt(String s) {
////                if (view != null) {
////                    view.onResetTxt(s);
////                }
////            }
////        };
////    }
}
