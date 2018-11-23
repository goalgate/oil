package com.oil;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.blankj.utilcode.util.SPUtils;
import com.jakewharton.rxbinding2.view.RxView;
import com.oil.Alerts.Alarm;
import com.oil.Alerts.Alert_IP;
import com.oil.Alerts.Alert_Password;
import com.oil.Alerts.Alert_ReadCard;
import com.oil.Alerts.Alert_Server;
import com.oil.Connect.RetrofitGenerator;
import com.oil.Connect.test;
import com.oil.State.BuyerState;
import com.oil.State.CommonState;
import com.oil.State.SalesState;
import com.oil.State.State;
import com.oil.Tools.MyObserver;
import com.oil.Tools.Order;
import com.oil.UI.NormalWindow;
import com.trello.rxlifecycle2.android.ActivityEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import sun.misc.BASE64Encoder;


public class MainActivity extends FunctionActivity implements NormalWindow.OptionTypeListener {

    State state = new State(new CommonState());

    private SPUtils config = SPUtils.getInstance("config");

    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    test test = new test();
    @BindView(R.id.tv_time)
    TextView tv_time;

    @BindView(R.id.network)
    ImageView iv_network;

    @BindView(R.id.buy_id)
    TextView buy_id;

    @BindView(R.id.consumer)
    TextView name_consumer;

    @BindView(R.id.seller)
    TextView name_seller;

    @BindView(R.id.volume)
    TextView volume;

    Order order;

    private NormalWindow normalWindow;

    @OnClick(R.id.network)
    void option() {
        alert_password.show();
    }

    @OnClick(R.id.cancel)
    void event_cancel() {
        init();
    }

    @OnClick(R.id.readcard)
    void event_readcard() {
        if (getState(CommonState.class)) {
            idp.readCard();
            //Alert_ReadCard.getInstance(this).message("请销售人刷身份证录入信息");
            Alert_ReadCard.getInstance(this).message("请购买人刷身份证录入信息");
        } else if (getState(BuyerState.class)) {
            idp.readCard();
            Alert_ReadCard.getInstance(this).message("请销售人刷身份证录入信息");

        } else {
            Alarm.getInstance(this).message("流程信息录入完毕，请提交信息或点击取消");
        }
    }

    @BindView(R.id.recapture)
    Button recapture;

    void event_recapture() {
        if (!getState(CommonState.class)) {
            pp.setDisplay(surfaceView.getHolder());
            Observable.timer(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(Long aLong) throws Exception {
                            pp.capture();
                        }
                    });
        } else {
            Alarm.getInstance(this).message("请先刷取购买人信息，再选择是否需要重拍");
        }
    }

    @BindView(R.id.submit)
    Button submit;

    @OnClick(R.id.submit)
    void event_submit() {
        if (getState(SalesState.class)) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            try {
                RetrofitGenerator.getConnectApi().inputDataGZ(
                        config.getString("daid"),
                        "data", order.getCardid(),
                        URLEncoder.encode(order.getName(), "GBK"),
                        //new String(order.getName().getBytes(), "GBK"),
                        URLEncoder.encode(order.getGoumaizenhao(), "GBK"),
                        //new String(order.getGoumaizenhao().getBytes(), "GBK"),
                        order.getQiyoubiaohao(),
                        order.getSulian(),
                        order.getGoumairenzhaopian(),
                        order.getCardpic(),
                        order.getXiaoshourenhao(),
                        URLEncoder.encode(order.getXiaoshouren(), "GBK"), true)
                        //new String(order.getXiaoshouren().getBytes(), "GBK"))
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
                                    String s = responseBody.string().toString();
                                    if (s.equals("true")) {
                                        Alarm.getInstance(MainActivity.this).message("提交数据成功");
                                        init();
                                    } else {
                                        Alarm.getInstance(MainActivity.this).message("数据提交不成功");
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            @Override
                            public void onError(Throwable e) {
                                Alarm.getInstance(MainActivity.this).messageDelay("连接服务器失败，无法提交数据");
                                progressDialog.dismiss();
                                submit.setClickable(true);

                            }
                            @Override
                            public void onComplete() {
                                progressDialog.dismiss();
                                submit.setClickable(true);
                            }
                        });
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            Alarm.getInstance(this).message("信息尚未录入完整，无法提交信息");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config.put("ServerId", "http://14.23.69.2:1036/");
        //config.put("ServerId", "http://192.168.12.239:7001/daServer/");
        surfaceView = findViewById(R.id.surfaceView);
        ButterKnife.bind(this);
        ready();
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
    }

    @Override
    public void onsetCardInfo(final ICardInfo cardInfo) {
        if (getState(CommonState.class)) {
            RetrofitGenerator.getConnectApi().getInfo("goumai", cardInfo.cardId(), config.getString("daid"))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver(MainActivity.this, new MyObserver.Callback() {
                        @Override
                        public void onResponseString(String s) {
                            String[] strings = s.split(",");
                            buy_id.setText(strings[0]);
                            volume.setText(strings[2]);
                            name_consumer.setText(cardInfo.name());
                            order.setCardid(cardInfo.cardId());
                            order.setGoumaizenhao(strings[0]);
                            order.setSulian(strings[2]);
                            order.setName(cardInfo.name());
                            order.setQiyoubiaohao(strings[1]);
                            pp.capture();
                            state.doNext();
                        }
                    }));
        } else if (getState(BuyerState.class)) {
            RetrofitGenerator.getConnectApi().getInfo("xiaoshou", cardInfo.cardId(), config.getString("daid"))
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new MyObserver(MainActivity.this, new MyObserver.Callback() {
                        @Override
                        public void onResponseString(String s) {
                            name_seller.setText(cardInfo.name());
                            order.setXiaoshouren(cardInfo.name());
                            order.setXiaoshourenhao(cardInfo.cardId());
                            state.doNext();
                        }
                    }));
        }
    }

    @Override
    public void onsetCardImg(Bitmap bmp) {
        idp.stopReadCard();
        Alert_ReadCard.getInstance(this).dismiss();
    }

    @Override
    public void onGetwltlib(byte[] bytes) {
        if (getState(CommonState.class)) {
            order.setCardpic(new BASE64Encoder().encode(bytes));
        }
    }

    @Override
    public void onGetPhoto(Bitmap bmp) {
        order.setGoumairenzhaopian(bitmapToBase64(bmp));
    }

    @Override
    public void onCaremaText(String s) {

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

    private boolean getState(Class mClass) {
        if (state.getOperation().getClass().getName().equals(mClass.getName())) {
            return true;
        }
        return false;
    }

    private void init() {
        pp.setDisplay(surfaceView.getHolder());
        order = new Order();
        state.setOperation(new CommonState());
        buy_id.setText(null);
        name_consumer.setText(null);
        name_seller.setText(null);
        volume.setText(null);
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
                            if (test.testIpPort("61.144.19.121", 88)) {
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
                                //System.out.println("连接服务器失败");
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

            }
        });
        alert_ip = new Alert_IP(this);
        alert_ip.IpviewInit();
        alert_password = new Alert_Password(this);
        alert_password.PasswordViewInit(new Alert_Password.Callback() {
            @Override
            public void normal_call() {
                normalWindow = new NormalWindow(MainActivity.this);
                normalWindow.setOptionTypeListener(MainActivity.this);
                normalWindow.showAtLocation(getWindow().getDecorView().findViewById(android.R.id.content), Gravity.CENTER, 0, 0);
            }

            @Override
            public void super_call() {

            }
        });
    }

    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}
