package com.oil;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceView;

import com.blankj.utilcode.util.BarUtils;
import com.oil.Func_Camera.mvp.presenter.PhotoPresenter;
import com.oil.Func_Camera.mvp.view.IPhotoView;
import com.oil.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.oil.Func_IDCard.mvp.view.IIDCardView;
import com.trello.rxlifecycle2.components.RxActivity;

public abstract class FunctionActivity extends RxActivity implements IPhotoView, IIDCardView {
    public IDCardPresenter idp = IDCardPresenter.getInstance();

    public PhotoPresenter pp = PhotoPresenter.getInstance();

    public SurfaceView surfaceView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarVisibility(this,false);
        idp.idCardOpen();
        pp.initCamera();

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
        pp.setDisplay(surfaceView.getHolder());
    }

    @Override
    public void onPause() {
        super.onPause();
        idp.IDCardPresenterSetView(null);

        pp.PhotoPresenterSetView(null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        idp.idCardClose();
    }
}