package com.oil.Alerts;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.oil.AppInit;
import com.oil.Func_FingerPrint.mvp.Presenter.FingerprintPresenter;
import com.oil.Func_IDCard.mvp.presenter.IDCardPresenter;
import com.oil.R;

public class Alert_fingerprintReg {

    private Context context;

    private AlertView fingerprintRegView;

    TextView tv_mainInfo;

    TextView tv_fingerInfo;

    ImageView iv_fingerprint;

    public Alert_fingerprintReg(Context context) {
        this.context = context;
    }

    public void fingerRegInit() {
        ViewGroup extView1 = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.fingerreg_form, null);
        tv_mainInfo = (TextView) extView1.findViewById(R.id.tv_mainInfo);
        tv_fingerInfo = (TextView) extView1.findViewById(R.id.tv_fingerInfo);
        iv_fingerprint = (ImageView) extView1.findViewById(R.id.iv_fingerprint) ;
        fingerprintRegView = new AlertView("销售人指纹登记", null, "关闭", null, null, this.context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                FingerprintPresenter.getInstance().fpCancel();
                //IDCardPresenter.getInstance().stopReadCard();
            }
        });
        fingerprintRegView.addExtView(extView1);
    }

    public void show() {
        FingerprintPresenter.getInstance().fpCancel();
        tv_mainInfo.setText("请刷取身份证验证所选择销售人身份信息");
        tv_fingerInfo.setText("等待获取指纹编号");
        iv_fingerprint.setImageBitmap(BitmapFactory.decodeResource(AppInit.getContext().getResources(), R.drawable.zw_icon));
        fingerprintRegView.show();
    }

    public TextView getTv_mainInfo() {
        return tv_mainInfo;
    }

    public TextView getTv_fingerInfo() {
        return tv_fingerInfo;
    }

    public ImageView getIv_fingerprint() {
        return iv_fingerprint;
    }

    public boolean isShowing(){
        return fingerprintRegView.isShowing();
    }
}
