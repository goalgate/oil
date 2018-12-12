package com.oil.Connect;

import android.graphics.Bitmap;
import android.util.Log;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.oil.Tools.FileUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import cbdi.drv.card.ICardInfo;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class ConnectImpl {
    private SPUtils config = SPUtils.getInstance("config");

    public void XSinsert(ICardInfo cardInfo, Bitmap bitmap){
        try {
            RetrofitGenerator.getConnectApi().renyuanData(true, "insert", config.getString("daid"),
                    URLEncoder.encode(cardInfo.name(), "GBK"),
                    URLEncoder.encode(cardInfo.sex(), "GBK"),
                    URLEncoder.encode(cardInfo.birthday(), "GBK"),
                    URLEncoder.encode(cardInfo.nation(), "GBK"),
                    URLEncoder.encode("广东广州", "GBK"),
                    URLEncoder.encode("广东", "GBK"),
                    URLEncoder.encode(cardInfo.address(), "GBK"),
                    cardInfo.cardId(),
                    "12345678910"
                    , "5", FileUtils.bitmapToBase64(bitmap))
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                ToastUtils.showLong(responseBody.string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void XSlist(){
        RetrofitGenerator.getConnectApi().renyuanData("list", config.getString("daid"))
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Log.e("人员信息", responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void XSZhuxiao(){
        RetrofitGenerator.getConnectApi().renyuanData("zhuxiao", config.getString("daid"), "D9fc31223cbcb77bd8b0f3bc5a52fa858")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {

                        try {
                            Log.e("信息返回", responseBody.string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void XSJLlist(){
        RetrofitGenerator.getConnectApi().xsRecordData("list",config.getString("daid"),"20181211","20181211")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Log.e("gfgsf",responseBody.string());
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void XSJLdetail(){
        RetrofitGenerator.getConnectApi().xsRecordData("detail",config.getString("daid"),"Dcc400f333d91ac5faab674a1b6c8ebce")
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            Log.e("gfgsf",responseBody.string());
                        }catch (IOException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
