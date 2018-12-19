package com.oil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.oil.Alerts.Alarm;
import com.oil.Alerts.Alert_addxs;
import com.oil.Alerts.Alert_fingerprintReg;
import com.oil.Bean.ZhiwenBean;
import com.oil.Connect.RetrofitGenerator;
import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.oil.Func_FingerPrint.mvp.View.IFingerPrintView2;
import com.oil.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.oil.Func_IDCard.mvp.view.IIDCardView;
import com.oil.Tools.XSRecycleAdapter;
import com.oil.UI.DividerGridItemDecoration;
import com.oil.greendao.DaoSession;

import java.io.IOException;
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

    ProgressDialog progressDialog;

    @BindView(R.id.id_recyclerview)
    RecyclerView mRecyclerView;

    @BindView(R.id.add)
    Button btn_add;

    @BindView(R.id.turnback)
    Button btn_turnback;

    @BindView(R.id.swipe_refresh_widget)
    SwipeRefreshLayout swipeRefreshLayout;

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
        Alarm.getInstance(this).release();
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
        } else if (alert_addxs.showing()) {
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
        if (alert_addxs.showing()) {
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
        alert_addxs.Init(new Alert_addxs.addxs_CallBack() {
            @Override
            public void add_success() {
                dataRefresh();
            }
        });
    }

    List<ZhiwenBean> xslist;

    private void initDatas() {
        swipeRefreshLayout.setRefreshing(true);
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
                            recycleViewInit();
                            swipeRefreshLayout.setRefreshing(false);
                        } catch (IOException e) {
                            Alarm.getInstance(PersonActivity.this).message("IOException");
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Alarm.getInstance(PersonActivity.this).message("ArrayIndexOutOfBoundsException");
                        } catch (NullPointerException e) {
                            Alarm.getInstance(PersonActivity.this).message("NullPointerException");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Alarm.getInstance(PersonActivity.this).message("无法连接到服务器，请检查网络设置");
                        swipeRefreshLayout.setRefreshing(false);

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
    XSRecycleAdapter adapter;
    private void recycleViewInit() {
        adapter = new XSRecycleAdapter(PersonActivity.this, xslist);
        adapter.setOnItemClickListener(new XSRecycleAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                alert_fingerprintReg.show();
                chooseZhiwenBean = xslist.get(position);
            }
        });
        adapter.setOnItemLongClickListener(new XSRecycleAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position) {
                new AlertView("确定注销" + xslist.get(position).getName() + "?", null, "取消", new String[]{"确定"}, null, PersonActivity.this, AlertView.Style.Alert, new OnItemClickListener() {
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
        swipeRefreshLayout.setDistanceToTriggerSync(300);
        // 设定下拉圆圈的背景
        swipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        // 设置圆圈的大小
        swipeRefreshLayout.setSize(SwipeRefreshLayout.LARGE);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                dataRefresh();
            }
        });
    }

    public void XSZhuxiao(final String zhujian) {
        RetrofitGenerator.getConnectApi().renyuanData("zhuxiao", config.getString("daid"), zhujian)
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        progressDialog = new ProgressDialog(PersonActivity.this);
                        progressDialog.setMessage("数据获取中，请稍候");
                        progressDialog.show();
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        try {
                            String result = responseBody.string();
                            if (result.equals("true")) {
                                fpp.fpCancel();
                                List<ZhiwenBean> zw_list = mdaoSession.queryRaw(ZhiwenBean.class, "where zhujian='" + zhujian + "'");
                                for (ZhiwenBean zhiwenBean : zw_list) {
                                    mdaoSession.delete(zhiwenBean);
                                    fpp.fpRemoveTmpl(String.valueOf(zhiwenBean.getZhiwenId()));
                                }
                                Alarm.getInstance(PersonActivity.this).messageDelay("已注销成功");
                                dataRefresh();
                            } else {
                                Alarm.getInstance(PersonActivity.this).messageDelay(result);
                            }
                        } catch (IOException e) {
                            Alarm.getInstance(PersonActivity.this).messageDelay("IOException");
                        } catch (NullPointerException e) {
                            Alarm.getInstance(PersonActivity.this).messageDelay("NullPointerException");
                        } catch (IndexOutOfBoundsException e){
                            Alarm.getInstance(PersonActivity.this).messageDelay("IndexOutOfBoundsException");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Alarm.getInstance(PersonActivity.this).messageDelay("连接服务器失败，请检查网络设置");
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onComplete() {
                        progressDialog.dismiss();
                    }
                });
    }

    private void dataRefresh(){
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
                            if (xslist.size() != 0) {
                                xslist.clear();
                            }
                            String result = responseBody.string();
                            if (result.equals("err")) {
                                Alarm.getInstance(PersonActivity.this).message("err出错");
                            } else {
                                String[] xsStrings = result.split("\\|");
                                for (String xs : xsStrings) {
                                    String[] detail = xs.split(",");
                                    xslist.add(new ZhiwenBean(null, detail[0], detail[1], detail[3], 0));
                                }
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        } catch (IOException e) {
                            Alarm.getInstance(PersonActivity.this).message("IOException");
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Alarm.getInstance(PersonActivity.this).message("ArrayIndexOutOfBoundsException");
                        } catch (NullPointerException e) {
                            Alarm.getInstance(PersonActivity.this).message("NullPointerException");
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        swipeRefreshLayout.setRefreshing(false);
                        Alarm.getInstance(PersonActivity.this).message("无法连接到服务器，请检查网络设置");
                    }

                    @Override
                    public void onComplete() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
    }

}