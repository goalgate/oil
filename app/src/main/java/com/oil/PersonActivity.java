package com.oil;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.oil.Alerts.Alert_fingerprintReg;
import com.oil.Bean.ZhiwenBean;
import com.oil.Connect.RetrofitGenerator;
import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.oil.Func_FingerPrint.mvp.View.IFingerPrintView2;
import com.oil.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.oil.Func_IDCard.mvp.view.IIDCardView;
import com.oil.Tools.MyObserver;
import com.oil.Tools.RecycleAdapter;
import com.oil.UI.DividerItemDecoration;
import com.oil.greendao.DaoSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cbdi.drv.card.ICardInfo;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class PersonActivity extends Activity implements IIDCardView,IFingerPrintView2 {
    public IDCardPresenter idp = IDCardPresenter.getInstance();

    public FingerprintPresenter fpp = FingerprintPresenter.getInstance();

    DaoSession mdaoSession = AppInit.getInstance().getDaoSession();

    private SPUtils config = SPUtils.getInstance("config");
    @BindView(R.id.id_recyclerview)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BarUtils.setStatusBarVisibility(this,false);
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
    ICardInfo cardInfo;
    @Override
    public void onsetCardInfo(ICardInfo cardInfo) {
        if (alert_fingerprintReg.isShowing()) {
            if (cardInfo.cardId().equals(chooseCardID)){
                this.cardInfo = cardInfo;
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
            }else {
                alert_fingerprintReg.getTv_mainInfo().setText("销售人员身份验证没有通过，请使用所选择销售人的身份证进行验证");
            }
        }
    }

    @Override
    public void onRegSuccess() {
        mdaoSession.insert(new ZhiwenBean(null,cardInfo.name(),chooseCardID,fingerprintId));
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

    }

    @Override
    public void onFpSucc(String msg) {

    }

    Alert_fingerprintReg alert_fingerprintReg;
    String chooseCardID;
    private void ready(){
        alert_fingerprintReg = new Alert_fingerprintReg(this);
        alert_fingerprintReg.fingerRegInit();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        RecycleAdapter adapter = new RecycleAdapter( this,nameDatas,cardidDatas,professionalDatas);
        adapter.setOnItemClickListener(new RecycleAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                alert_fingerprintReg.show();
                chooseCardID = cardidDatas.get(position);
            }
        });
        adapter.setOnItemLongClickListener(new RecycleAdapter.OnItemLongClickListener() {
            @Override
            public void onLongClick(int position) {
                List<ZhiwenBean> list = mdaoSession.queryRaw(ZhiwenBean.class,"where cardid="+cardidDatas.get(position));
                for (  ZhiwenBean zhiwenBean :list){
                    fpp.fpRemoveTmpl(String.valueOf(zhiwenBean.getZhiwenId()));
                    mdaoSession.delete(zhiwenBean);
                }
            }
        });
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL_LIST));
    }

    private List<String> nameDatas;
    private List<String> cardidDatas;
    private List<String> professionalDatas;
    private void initDatas(){
        nameDatas = new ArrayList<String>();
        cardidDatas = new ArrayList<String>();
        professionalDatas = new ArrayList<String>();

        nameDatas.add("王振文");
        nameDatas.add("庞文龙");
        nameDatas.add("肖军");
        cardidDatas.add("441302199308100538");
        cardidDatas.add("440982199104204312");
        cardidDatas.add("441302199308100538");
        professionalDatas.add("总经理");
        professionalDatas.add("总经理");
        professionalDatas.add("总经理");
    }


}


