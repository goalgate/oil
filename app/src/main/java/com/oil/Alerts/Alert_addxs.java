package com.oil.Alerts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.oil.Connect.RetrofitGenerator;
import com.oil.R;
import com.oil.Tools.FileUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import cbdi.drv.card.ICardInfo;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

public class Alert_addxs {
    private Context context;

    private AlertView AddXsView;

    TextView tv_xsname;

    TextView tv_xssex;

    TextView tv_xscardid;

    TextView tv_xsnation;

    TextView tv_xsbirth;

    TextView tv_xshomeaddress;

    EditText et_phone;

    EditText et_home;

    EditText et_nowaddress;

    Button btn_addxs;

    Button btn_refresh;

    ImageView iv_headphoto;

    ICardInfo cardInfo;

    Bitmap headphoto;

    LinearLayout checkBoxlist;

    CheckBox cb_danweifuzeren;

    CheckBox cb_faren;

    CheckBox cb_zongjingli;

    CheckBox cb_jiayougong;

    CheckBox cb_shouyinyuan;

    public Alert_addxs(Context context) {
        this.context = context;
    }

    public void Init() {
        ViewGroup extView1 = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.addxs_form, null);
        tv_xsname = (TextView) extView1.findViewById(R.id.tv_xsname);
        tv_xssex = (TextView) extView1.findViewById(R.id.tv_xsgender);
        tv_xscardid = (TextView) extView1.findViewById(R.id.tv_xsgcardid);
        tv_xsnation = (TextView) extView1.findViewById(R.id.tv_xsnation);
        tv_xsbirth = (TextView) extView1.findViewById(R.id.tv_xsbirth);
        tv_xshomeaddress = (TextView) extView1.findViewById(R.id.tv_xshomeaddress);
        et_phone = (EditText) extView1.findViewById(R.id.et_xsphone);
        et_home = (EditText) extView1.findViewById(R.id.et_xshome);
        et_nowaddress = (EditText) extView1.findViewById(R.id.et_nowaddress);
        btn_addxs = (Button) extView1.findViewById(R.id.btn_xsadd);
        btn_refresh =  (Button) extView1.findViewById(R.id.btn_xsrefresh);
        iv_headphoto = (ImageView) extView1.findViewById(R.id.headphoto);

        checkBoxlist = (LinearLayout) extView1.findViewById(R.id.checkboxlist);
        cb_danweifuzeren = (CheckBox) extView1.findViewById(R.id.cb_danweifuzeren);
        cb_faren = (CheckBox) extView1.findViewById(R.id.cb_faren);
        cb_zongjingli = (CheckBox) extView1.findViewById(R.id.cb_zongjingli);
        cb_jiayougong = (CheckBox) extView1.findViewById(R.id.cb_jiayougong);
        cb_shouyinyuan = (CheckBox) extView1.findViewById(R.id.cb_shouyinyuan);
        btn_addxs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if(TextUtils.isEmpty(cardInfo.cardId())){
                        ToastUtils.showLong("请刷身份证录入信息");
                    }else if (TextUtils.isEmpty(et_home.getText().toString())){
                        ToastUtils.showLong("请输入籍贯");
                    }else if (TextUtils.isEmpty(et_nowaddress.getText().toString())){
                        ToastUtils.showLong("请输入现在住址");
                    }else if( TextUtils.isEmpty(et_phone.getText().toString()) ){
                        ToastUtils.showLong("请输入电话号码");
                    }else{
                        XSinsert(cardInfo,headphoto);
                    }
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        });
        btn_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                refresh();
            }
        });
        AddXsView = new AlertView("增加销售人", "刷取身份证录取部分数据", "关闭", null, null, this.context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {

            }
        });
        AddXsView.addExtView(extView1);
    }

    public void show() {
        AddXsView.show();
    }

    public void cardInfoIn(ICardInfo cardInfo){
        this.cardInfo =cardInfo;
        tv_xsname.setText(cardInfo.name());
        tv_xssex.setText(cardInfo.sex());
        tv_xscardid.setText(cardInfo.cardId());
        tv_xsnation.setText(cardInfo.nation());
        tv_xsbirth.setText(cardInfo.birthday());
        tv_xshomeaddress.setText(cardInfo.address());

    }

    public void headphoto_In(Bitmap bitmap) {
        this.headphoto = bitmap;
        iv_headphoto.setImageBitmap(bitmap);
    }

    public void refresh() {
        tv_xsname.setText(null);
        tv_xssex.setText(null);
        tv_xscardid.setText(null);
        tv_xsnation.setText(null);
        tv_xsbirth.setText(null);
        tv_xshomeaddress.setText(null);
        et_phone.setText(null);
        et_home.setText(null);
        et_nowaddress.setText(null);
        iv_headphoto.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.default_header));
        cardInfo = null;
        headphoto = null;
        cb_danweifuzeren.setChecked(false);
        cb_faren.setChecked(false);
        cb_zongjingli.setChecked(false);
        cb_jiayougong.setChecked(false);
        cb_shouyinyuan.setChecked(false);


    }

    public boolean showing(){
        return AddXsView.isShowing();
    }

    private SPUtils config = SPUtils.getInstance("config");
    public void XSinsert(ICardInfo cardInfo, Bitmap bitmap){
        try {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < checkBoxlist.getChildCount(); i++) {
                CheckBox cb = (CheckBox) checkBoxlist.getChildAt(i);
                if (cb.isChecked()) {
                    sb.append(i+1+",");
                }
            }
            RetrofitGenerator.getConnectApi().renyuanData(true, "insert", config.getString("daid"),
                    URLEncoder.encode(cardInfo.name(), "GBK"),
                    URLEncoder.encode(cardInfo.sex(), "GBK"),
                    URLEncoder.encode(cardInfo.birthday(), "GBK"),
                    URLEncoder.encode(cardInfo.nation(), "GBK"),
                    URLEncoder.encode(et_home.getText().toString(), "GBK"),
                    URLEncoder.encode(et_nowaddress.getText().toString(), "GBK"),
                    URLEncoder.encode(cardInfo.address(), "GBK"),
                    cardInfo.cardId(),
                    et_phone.getText().toString()
                    ,sb.substring(0,sb.length()-1).toString(),
                    FileUtils.bitmapToBase64(bitmap))
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
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}
