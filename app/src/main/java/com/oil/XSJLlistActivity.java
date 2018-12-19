package com.oil;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.oil.Alerts.Alarm;
import com.oil.Bean.XiaoshouJiLUBean;
import com.oil.Connect.RetrofitGenerator;
import com.oil.Tools.XSJLRecycleAdapter;
import com.oil.Tools.XSRecycleAdapter;
import com.oil.UI.DividerGridItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class XSJLlistActivity extends Activity {

    private SPUtils config = SPUtils.getInstance("config");

    private List<XiaoshouJiLUBean> xsjlList = new ArrayList<XiaoshouJiLUBean>();

    XSJLRecycleAdapter adapter;

    @BindView(R.id.et_startDate)
    EditText et_startDate;

    @BindView(R.id.et_stopDate)
    EditText et_stopDate;

    @BindView(R.id.btn_check)
    Button btn_check;

    @BindView(R.id.id_recyclerview)
    RecyclerView mRecyclerView;

    ProgressDialog progressDialog;

    @OnClick(R.id.btn_check)
    void check() {
        if (TextUtils.isEmpty(et_startDate.getText().toString())) {
            Alarm.getInstance(this).message("请先输入开始日期");
        } else if (TextUtils.isEmpty(et_stopDate.getText().toString())) {
            Alarm.getInstance(this).message("请先输入结束日期");
        } else {
            RetrofitGenerator.getConnectApi().xsRecordData("list", config.getString("daid"), et_startDate.getText().toString(), et_stopDate.getText().toString())
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            progressDialog = new ProgressDialog(XSJLlistActivity.this);
                            progressDialog.setTitle("正在查询中");
                            progressDialog.show();
                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try {
                                String result = responseBody.string();
                                if (xsjlList.size() != 0) {
                                    xsjlList.clear();
                                }
                                if (result.equals("err")) {
                                    Alarm.getInstance(XSJLlistActivity.this).message("时间输入有误");
                                } else if (result.equals("dataNo")) {
                                    Alarm.getInstance(XSJLlistActivity.this).message("这段时间没有销售数据");
                                } else {
                                    String[] xsStrings = result.split("\\|");
                                    for (String xs : xsStrings) {
                                        String[] detail = xs.split(",");
                                        xsjlList.add(new XiaoshouJiLUBean(detail[0], detail[1], detail[2], detail[3], detail[4]));
                                    }
                                    adapter = new XSJLRecycleAdapter(XSJLlistActivity.this, xsjlList);
                                    adapter.setOnItemClickListener(onItemClickListener);
                                    mRecyclerView.setAdapter(adapter);
                                }
                                if (adapter != null) {
                                    adapter.notifyDataSetChanged();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                Alarm.getInstance(XSJLlistActivity.this).message("IOException");
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                                Alarm.getInstance(XSJLlistActivity.this).message("ArrayIndexOutOfBoundsException");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                Alarm.getInstance(XSJLlistActivity.this).message("NullPointerException");
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Alarm.getInstance(XSJLlistActivity.this).message("无法连接到服务器，请检查网络设置");
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onComplete() {
                            progressDialog.dismiss();
                        }
                    });
        }
    }

    @OnClick(R.id.btn_turnback)
    void back() {
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Alarm.getInstance(this).release();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarVisibility(this, false);
        setContentView(R.layout.activity_xsjl);
        ButterKnife.bind(this);
        recycleViewInit();
    }

    private void recycleViewInit() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(XSJLlistActivity.this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(XSJLlistActivity.this, DividerItemDecoration.VERTICAL));
    }


    XSJLRecycleAdapter.OnItemClickListener onItemClickListener = new XSJLRecycleAdapter.OnItemClickListener(){
        @Override
        public void onClick(int position) {
            RetrofitGenerator.getConnectApi().xsRecordData("detail",config.getString("daid"),xsjlList.get(position).getZhujian())
                    .subscribeOn(Schedulers.io()).unsubscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ResponseBody>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ResponseBody responseBody) {
                            try{
                                String result = responseBody.string();
                                ToastUtils.showLong(result);
                            } catch (IOException e) {
                                e.printStackTrace();
                                Alarm.getInstance(XSJLlistActivity.this).message("IOException");
                            } catch (ArrayIndexOutOfBoundsException e) {
                                e.printStackTrace();
                                Alarm.getInstance(XSJLlistActivity.this).message("ArrayIndexOutOfBoundsException");
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                                Alarm.getInstance(XSJLlistActivity.this).message("NullPointerException");
                            } catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            Alarm.getInstance(XSJLlistActivity.this).message("无法连接到服务器，请检查网络设置");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }
    };
}
