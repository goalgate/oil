package com.oil;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.SPUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.oil.Alerts.Alarm;
import com.oil.Alerts.Alert_IP;
import com.oil.Alerts.Alert_Password;
import com.oil.Alerts.Alert_QYchoose;
import com.oil.Alerts.Alert_ReadCard;
import com.oil.Alerts.Alert_Server;
import com.oil.Alerts.Alert_addxs;
import com.oil.Bean.ZhiwenBean;
import com.oil.Connect.RetrofitGenerator;
import com.oil.Connect.test;
import com.oil.State.CommonState;
import com.oil.State.PicCheckState;
import com.oil.State.SalesInState;
import com.oil.State.State;
import com.oil.State.WaitFingerPrintState;
import com.oil.State.WaitUploadState;
import com.oil.Tools.FileUtils;
import com.oil.Tools.MyObserver;
import com.oil.Bean.Order;
import com.oil.UI.NormalWindow;
import com.oil.UI.SuperWindow;
import com.oil.greendao.DaoSession;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cbdi.drv.card.ICardInfo;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;


public class MainActivity extends FunctionActivity {

    State state = new State(new CommonState());

    private SPUtils config = SPUtils.getInstance("config");

    private DaoSession daoSession = AppInit.getInstance().getDaoSession();

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    SimpleDateFormat formatter2 = new SimpleDateFormat("yyyyMMddHHmmss");

    test test = new test();

    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.network)
    ImageView iv_network;

    @BindView(R.id.tv_tips)
    TextView tv_tips;

    @BindView(R.id.tv_QIYOUBIAOHAO)
    TextView tv_QIYOUBIAOHAO;


    String string_QIYOUBIAOHAO;

    @OnClick(R.id.tv_QIYOUBIAOHAO)
    void chooseQIYOUBIAOHAO() {
        Alert_QYchoose.getInstance(MainActivity.this, new Alert_QYchoose.QYCItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    string_QIYOUBIAOHAO = "92";
                    tv_QIYOUBIAOHAO.setText("92号汽油");
                } else if (position == 1) {
                    string_QIYOUBIAOHAO = "95";
                    tv_QIYOUBIAOHAO.setText("95号汽油");
                } else if (position == 2) {
                    string_QIYOUBIAOHAO = "98";
                    tv_QIYOUBIAOHAO.setText("98号汽油");
                }
            }
        }).show();

    }

    @BindView(R.id.et_volume)
    EditText et_volume;

    @BindView(R.id.tv_customer)
    TextView tv_customer;

    @BindView(R.id.tv_seller)
    TextView tv_seller;

    Order order;

    private NormalWindow normalWindow;

    private SuperWindow superWindow;

    @OnClick(R.id.network)
    void option() {
        alert_password.show();
    }

    @OnClick(R.id.menu)
    void menu() {
        ActivityUtils.startActivity(getPackageName(), getPackageName() + ".XSJLlistActivity");
    }

    @OnClick(R.id.cancel)
    void event_cancel() {
        init();
    }

    @OnClick(R.id.readcard)
    void event_readcard() {
        if (getState(CommonState.class)) {
            idp.readCard();
            Alert_ReadCard.getInstance(this).message("请购买人刷身份证录入信息");
        } else if (getState(PicCheckState.class)) {
            Alarm.getInstance(this).message("现场照片不合格，请点击重拍拍取照片");
        } else if (getState(WaitFingerPrintState.class)) {
            Alarm.getInstance(this).message("购买人尚未登记指纹数据，请购买人进行单次指纹登记");
        } else if (getState(SalesInState.class)) {
            idp.readCard();
            Alert_ReadCard.getInstance(this).message("请销售人刷身份证录入信息");
        } else if (getState(WaitUploadState.class)) {
            if (TextUtils.isEmpty(tv_QIYOUBIAOHAO.getText().toString()) || TextUtils.isEmpty(et_volume.getText().toString())) {
                Alarm.getInstance(this).message("请检查汽油编号和数量是否已填写完毕");
            } else {
                Alarm.getInstance(this).message("流程信息录入完毕，请提交信息或点击取消");
            }
        }
    }

    @BindView(R.id.recapture)
    Button recapture;

    void event_recapture() {
        if (getState(CommonState.class)) {
            Alarm.getInstance(this).message("请先刷取购买人信息，再选择是否需要重拍");
        } else if (getState(PicCheckState.class)) {
            pp.capture();
        } else {
            Alarm.getInstance(this).message("现场照片已确定，如人员照片与身份证不符，需点击取消后再次登记");
        }

    }

    @BindView(R.id.submit)
    Button submit;

    @OnClick(R.id.submit)
    void event_submit() {
        order.setSulian(et_volume.getText().toString());
        order.setQiyoubiaohao(string_QIYOUBIAOHAO);
        if (getState(WaitUploadState.class)) {
            if (TextUtils.isEmpty(tv_QIYOUBIAOHAO.getText().toString())) {
                Alarm.getInstance(this).message("请先选择汽油编号再进行提交");
            } else if (TextUtils.isEmpty(et_volume.getText().toString())) {
                Alarm.getInstance(this).message("请先输入汽油数量");
            } else {
                final ProgressDialog progressDialog = new ProgressDialog(this);
                try {
                    RetrofitGenerator.getConnectApi().inputDataGZ(
                            config.getString("daid"),
                            "data", order.getCardid(),
                            URLEncoder.encode(order.getName(), "GBK"),
                            URLEncoder.encode(order.getGoumaizenhao(), "GBK"),
                            order.getQiyoubiaohao(),
                            order.getSulian(),
                            order.getGoumairenzhaopian(),
                            order.getCardpic(),
                            order.getXiaoshourenhao(),
                            URLEncoder.encode(order.getXiaoshouren(), "GBK"),
                            order.getZhiwen(), true)
                            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<ResponseBody>() {
                                @Override
                                public void onSubscribe(Disposable d) {
                                    progressDialog.setMessage("数据上传中，请稍候");
                                    progressDialog.show();
                                    submit.setClickable(false);
                                }

                                @Override
                                public void onNext(ResponseBody responseBody) {
                                    try {
                                        String s = responseBody.string();
                                        if (s.equals("true")) {
                                            Alarm.getInstance(MainActivity.this).message("提交数据成功");
                                            init();
                                        } else {
                                            Alarm.getInstance(MainActivity.this).message("数据提交不成功");
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        Alarm.getInstance(MainActivity.this).message("IOException");
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        e.printStackTrace();
                                        Alarm.getInstance(MainActivity.this).message("ArrayIndexOutOfBoundsException");
                                    } catch (NullPointerException e) {
                                        e.printStackTrace();
                                        Alarm.getInstance(MainActivity.this).message("NullPointerException");
                                    }
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Alarm.getInstance(MainActivity.this).messageDelay("连接服务器失败，无法提交数据");
                                    progressDialog.dismiss();
                                    submit.setClickable(true);
                                    daoSession.insert(order);

                                }

                                @Override
                                public void onComplete() {
                                    progressDialog.dismiss();
                                    submit.setClickable(true);
                                }
                            });
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Alarm.getInstance(MainActivity.this).message("UnsupportedEncodingException");
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else {
            Alarm.getInstance(this).message("信息尚未录入完整，无法提交信息");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        surfaceView = findViewById(R.id.surfaceView);
        ButterKnife.bind(this);
        AutoUpdate();
        ready();
        ReUpload();

    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    @Override
    public void onPause() {
        super.onPause();
        Alert_ReadCard.getInstance(this).release();
        Alarm.getInstance(this).release();
        Alert_QYchoose.getInstance(this, null).release();
    }

    ICardInfo cardInfo;

    @Override
    public void onsetCardInfo(final ICardInfo cardInfo) {
        this.cardInfo = cardInfo;
        if (getState(CommonState.class)) {
            tv_customer.setText(cardInfo.name());
            order.setCardid(cardInfo.cardId());
            order.setGoumaizenhao(config.getString("daid") + formatter2.format(new Date(System.currentTimeMillis())));
            order.setName(cardInfo.name());
            pp.capture();
        } else if (getState(SalesInState.class)) {
            RetrofitGenerator.getConnectApi().getInfo("xiaoshou", cardInfo.cardId(), config.getString("daid"))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver(MainActivity.this, new MyObserver.Callback() {
                        @Override
                        public void onResponseString(String s) {
                            tv_seller.setText(cardInfo.name());
                            order.setXiaoshouren(cardInfo.name());
                            order.setXiaoshourenhao(cardInfo.cardId());
                            state.doNext(tv_tips);
                        }
                    }));
        }
    }


    @Override
    public void onsetCardImg(Bitmap bmp) {
        if (getState(CommonState.class)) {
            order.setCardpic(FileUtils.bitmapToBase64(bmp));
            idp.stopReadCard();
            state.doNext(tv_tips);
        }
        Alert_ReadCard.getInstance(this).dismiss();
    }

    @Override
    public void onGetwltlib(byte[] bytes) {

    }

    @Override
    public void onGetPhoto(Bitmap bmp) {
        order.setGoumairenzhaopian(FileUtils.bitmapToBase64(bmp));
    }

    @Override
    public void onCaremaText(String s) {
        if (!s.equals("拍照成功")) {
            tv_tips.setText("现场照片不合格，请点击重拍");
            Alarm.getInstance(this).message(s);
        } else {
            state.doNext(tv_tips);
        }
    }

    @Override
    public void onOptionType(Button view, int type) {
        normalWindow.dismiss();
        if (type == 1) {
            alert_server.show();
        } else if (type == 2) {
            alert_ip.show();
        }
    }

    @Override
    public void onSuperOptionType(Button view, int type) {
        superWindow.dismiss();
        if (type == 1) {
            ActivityUtils.startActivity(getPackageName(), getPackageName() + ".PersonActivity");
        } else if (type == 2) {
            normalWindow = new NormalWindow(MainActivity.this);
            normalWindow.setOptionTypeListener(MainActivity.this);
            normalWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
        }
    }

    @Override
    public void onText(String msg) {

    }

    ZhiwenBean xsZhiwenBean;

    @Override
    public void onFpSucc(String msg) {
        try {
            String fp_id = msg.substring(3, msg.length());
            xsZhiwenBean = daoSession.queryRaw(ZhiwenBean.class, "where zhiwen_Id=" + fp_id).get(0);
            tv_seller.setText(xsZhiwenBean.getName());
            order.setXiaoshouren(xsZhiwenBean.getName());
            order.setXiaoshourenhao(xsZhiwenBean.getCardid());
            state.doNext(tv_tips);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onRegSuccess() {

    }

    @Override
    public void onCapturing(Bitmap bmp) {
        if (getState(WaitFingerPrintState.class)) {
            order.setZhiwen(FileUtils.bitmapToBase64(bmp));
            //Alarm.getInstance(this).message("购买人指纹已记录成功");
            state.doNext(tv_tips);
        }
    }

    @Override
    public void onSetImg(Bitmap bmp) {

    }

    private boolean getState(Class mClass) {
        if (state.getOperation().getClass().getName().equals(mClass.getName())) {
            return true;
        }
        return false;
    }

    private void init() {
        tv_tips.setText("请读购买人身份证");
        fpp.fpCancel();
        pp.setDisplay(surfaceView.getHolder());
        order = new Order();
        state.setOperation(new CommonState());
        tv_QIYOUBIAOHAO.setText(null);
        tv_customer.setText(null);
        tv_seller.setText(null);
        et_volume.setText(null);
        string_QIYOUBIAOHAO = null;
    }

    private void AutoUpdate() {

    }

    private void ReUpload() {
        List<Order> orderList = daoSession.loadAll(Order.class);
        Log.e("reuploadSize", String.valueOf(orderList.size()));
        if (orderList.size() > 50) {
            //if(orderList.size() == 0 ){
            Alarm.getInstance(this).message("未上传销售记录已超过50条，请查看网络是否已联通");
        }
        for (Order order : orderList) {
            submitData(order);
        }
    }

    Alert_Server alert_server;
    Alert_IP alert_ip;
    Alert_Password alert_password;
    private void ready() {

        Observable.interval(2, 60, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        if (test.testIpPort("www.baidu.com", 80)) {
                            if (test.testIpPort("14.23.69.2", 1036)) {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.network));
                                        //ToastUtils.showLong("连接服务器成功");
                                    }
                                });

                            } else {
                                MainActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_network));
                                        //ToastUtils.showLong("连接服务器失败");

                                    }
                                });
                            }
                        } else {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.no_network));
                                    //ToastUtils.showLong("公网连接失败");
                                }
                            });
                        }
                    }
                });

        Observable.interval(0, 1, TimeUnit.SECONDS)
                .compose(this.<Long>bindUntilEvent(ActivityEvent.DESTROY))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        tv_time.setText(formatter.format(new Date(System.currentTimeMillis())));
                    }
                });

        RxView.clicks(recapture).throttleFirst(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Object>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Object o) {
                        event_recapture();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        alert_server = new Alert_Server(this);
        alert_server.serverInit(new Alert_Server.Server_Callback() {
            @Override
            public void setNetworkBmp() {
                iv_network.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.network));
            }
        });

        alert_ip = new Alert_IP(this);
        alert_ip.IpviewInit();
        alert_password = new Alert_Password(this);
        alert_password.PasswordViewInit(new Alert_Password.Callback() {
            @Override
            public void normal_call() {
                superWindow = new SuperWindow(MainActivity.this);
                superWindow.setOptionTypeListener(MainActivity.this);
                superWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);

            }

            @Override
            public void super_call() {

            }
        });
    }

    private void submitData(final Order order) {
        try {
            RetrofitGenerator.getConnectApi().inputDataGZ(
                    config.getString("daid"),
                    "data", order.getCardid(),
                    URLEncoder.encode(order.getName(), "GBK"),
                    URLEncoder.encode(order.getGoumaizenhao(), "GBK"),
                    order.getQiyoubiaohao(),
                    order.getSulian(),
                    order.getGoumairenzhaopian(),
                    order.getCardpic(),
                    order.getXiaoshourenhao(),
                    URLEncoder.encode(order.getXiaoshouren(), "GBK"),
                    order.getZhiwen(), true)
                    .subscribeOn(Schedulers.single())
                    .unsubscribeOn(Schedulers.single())
                    .observeOn(Schedulers.single())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String s = responseBody.string();
                                if (s.equals("true")) {
                                    daoSession.delete(order);
                                }
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
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
