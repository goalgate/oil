package com.oil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Button;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.oil.Alerts.Alert_addxs;
import com.oil.Alerts.Alert_fingerprintReg;
import com.oil.Bean.ZhiwenBean;
import com.oil.Connect.ConnectImpl;
import com.oil.Connect.RetrofitGenerator;
import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.oil.Func_FingerPrint.mvp.View.IFingerPrintView2;
import com.oil.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.oil.Func_IDCard.mvp.view.IIDCardView;
import com.oil.Tools.RecycleAdapter;
import com.oil.UI.DividerGridItemDecoration;
import com.oil.greendao.DaoSession;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cbdi.drv.card.ICardInfo;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class PersonActivity extends Activity implements IIDCardView, IFingerPrintView2 {
    public IDCardPresenter idp = IDCardPresenter.getInstance();

    private SPUtils config = SPUtils.getInstance("config");

    public FingerprintPresenter fpp = FingerprintPresenter.getInstance();

    DaoSession mdaoSession = AppInit.getInstance().getDaoSession();

    Alert_addxs alert_addxs;

    @BindView(R.id.id_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.add)
    Button btn_add;

    @BindView(R.id.turnback)
    Button btn_turnback;

    @OnClick(R.id.add)
    void xsAdd() {
        alert_addxs.refresh();
        alert_addxs.show();
    }

    @OnClick(R.id.turnback)
    void turnBack() {
        this.finish();
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarVisibility(this, false);
        setContentView(R.layout.activity_person);
        ButterKnife.bind(this);
        initDatas();
        ready();
    }

    @Override
    protected void onResume() {
        super.onResume();
        idp.readCard();
        idp.IDCardPresenterSetView(this);
        fpp.FingerPrintPresenterSetView(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        idp.stopReadCard();
        idp.IDCardPresenterSetView(null);
        fpp.FingerPrintPresenterSetView(null);
    }

    @Override
    public void onGetwltlib(byte[] bytes) {

    }


    int fingerprintId;
    ZhiwenBean chooseZhiwenBean;
    @Override
    public void onsetCardInfo(ICardInfo cardInfo) {
        if (alert_fingerprintReg.isShowing()) {
            if (cardInfo.cardId().equals(chooseZhiwenBean.getCardid())) {
                fpp.fpCancel();
                Observable.timer(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(Long aLong) throws Exception {
                                fingerprintId = fpp.fpGetEmptyID();
                                alert_fingerprintReg.getTv_mainInfo().setText("销售人员身份验证已通过，指纹号为 " + fingerprintId);
                                fpp.fpEnroll(String.valueOf(fingerprintId));
                            }
                        });
            } else {
                alert_fingerprintReg.getTv_mainInfo().setText("销售人员身份验证没有通过，请使用所选择销售人的身份证进行验证");
            }
        }else if (alert_addxs.showing()){
            alert_addxs.cardInfoIn(cardInfo);
        }
    }

    @Override
    public void onRegSuccess() {
        mdaoSession.insert(new ZhiwenBean(null, chooseZhiwenBean.getZhujian(), chooseZhiwenBean.getName(), chooseZhiwenBean.getCardid(), fingerprintId));
    }

    @Override
    public void onCapturing(Bitmap bmp) {

    }

    @Override
    public void onSetImg(Bitmap bmp) {
        if (alert_fingerprintReg.isShowing()) {
            alert_fingerprintReg.getIv_fingerprint().setImageBitmap(bmp);
        }
    }

    @Override
    public void onText(String msg) {
        if (alert_fingerprintReg.isShowing()) {
            alert_fingerprintReg.getTv_fingerInfo().setText(msg);
        }
    }

    @Override
    public void onsetCardImg(Bitmap bmp) {
        if (alert_addxs.showing()){
            alert_addxs.headphoto_In(bmp);
        }
    }

    @Override
    public void onFpSucc(String msg) {

    }

    Alert_fingerprintReg alert_fingerprintReg;

    private void ready() {
        alert_fingerprintReg = new Alert_fingerprintReg(this);
        alert_fingerprintReg.fingerRegInit();


        alert_addxs = new Alert_addxs(this);
        alert_addxs.Init();
    }

    List<ZhiwenBean> xslist;

    private void initDatas() {
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
                            xslist = new ArrayList<ZhiwenBean>();
                            String[] xsStrings = responseBody.string().split("\\|");
                            for (String xs : xsStrings) {
                                String[] detail = xs.split(",");
                                xslist.add(new ZhiwenBean(null, detail[0], detail[1], detail[3], 0));
                            }
                            RecycleAdapter adapter = new RecycleAdapter(PersonActivity.this, xslist);
                            adapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
                                @Override
                                public void onClick(int position) {
                                    alert_fingerprintReg.show();
                                    chooseZhiwenBean = xslist.get(position);
                                }
                            });
                            adapter.setOnItemLongClickListener(new RecycleAdapter.OnItemLongClickListener() {
                                @Override
                                public void onLongClick(int position) {
                                    new AlertView("确定删除" + xslist.get(position).getName() + "?", null, "取消", new String[]{"确定"}, null, PersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
                                        @Override
                                        public void onItemClick(Object o, int position) {
                                            if (position == 0) {
                                                XSZhuxiao(xslist.get(position).getZhujian());
                                            }
                                        }
                                    }).show();
                                }
                            });
                            mRecyclerView.setLayoutManager(new GridLayoutManager(PersonActivity.this, 4));
                            mRecyclerView.setAdapter(adapter);
                            mRecyclerView.addItemDecoration(new DividerGridItemDecoration(PersonActivity.this, 4));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        } catch (NullPointerException e) {
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
    public void XSZhuxiao(final String zhujian){
        RetrofitGenerator.getConnectApi().renyuanData("zhuxiao", config.getString("daid"), zhujian)
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
                            if(responseBody.string().equals("true")){
                                fpp.fpCancel();
                                List<ZhiwenBean> zw_list = mdaoSession.queryRaw(ZhiwenBean.class,"where zhujian='"+zhujian+"'");
                                for (ZhiwenBean zhiwenBean: zw_list){
                                    mdaoSession.delete(zhiwenBean);
                                    fpp.fpRemoveTmpl(String.valueOf(zhiwenBean.getZhiwenId()));
                                }
                                ToastUtils.showLong("已清除成功");
                            }
                        }catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }catch (IOException e){
                            e.printStackTrace();
                        }catch (NullPointerException e){
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