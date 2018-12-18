package com.oil;

import android.os.Bundle;
import android.view.SurfaceView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.oil.Func_Camera.mvp.presenter.PhotoPresenter;
import com.oil.Func_Camera.mvp.view.IPhotoView;
import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.oil.Func_FingerPrint.mvp.View.IFingerPrintView2;
import com.oil.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.oil.Func_IDCard.mvp.view.IIDCardView;

import com.oil.UI.NormalWindow;
import com.oil.UI.SuperWindow;
import com.trello.rxlifecycle2.components.RxActivity;

public abstract class FunctionActivity extends RxActivity implements IPhotoView, IIDCardView,IFingerPrintView2,NormalWindow.OptionTypeListener, SuperWindow.OptionTypeListener{
    public IDCardPresenter idp = IDCardPresenter.getInstance();

    public PhotoPresenter pp = PhotoPresenter.getInstance();

    public FingerprintPresenter fpp = FingerprintPresenter.getInstance();

    public SurfaceView surfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarVisibility(this,false);
        idp.idCardOpen();
        pp.initCamera();
        fpp.fpInit(AppInit.getContext());
        fpp.fpOpen();
        if (SPUtils.getInstance("config").getBoolean("firstUseFp", true)) {
            fpp.fpRemoveAll();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        pp.setParameter(surfaceView.getHolder());
    }

    @Override
    public void onRestart() {
        super.onRestart();
        pp.initCamera();

    }

    @Override
    public void onResume() {
        super.onResume();
        idp.IDCardPresenterSetView(this);
        pp.PhotoPresenterSetView(this);
        fpp.FingerPrintPresenterSetView(this);
        pp.setDisplay(surfaceView.getHolder());
    }

    @Override
    public void onPause() {
        super.onPause();
        idp.IDCardPresenterSetView(null);
        fpp.FingerPrintPresenterSetView(null);
        pp.PhotoPresenterSetView(null);
        fpp.fpCancel();
        idp.stopReadCard();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        idp.idCardClose();
        pp.close_Camera();
        fpp.fpClose();
    }
}