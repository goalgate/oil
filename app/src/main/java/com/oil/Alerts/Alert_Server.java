package com.oil.Alerts;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.RegexUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.oil.Connect.test;
import com.oil.R;
import com.oil.Tools.DAInfo;
import com.oil.Tools.NetInfo;

import java.net.URI;
import java.util.List;


public class Alert_Server {

    public static final String REGEX_IP = "((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

    public static final String REGEX_PORT = ":[0-9]+";

    private Context context;

    private SPUtils config = SPUtils.getInstance("config");

    private AlertView inputServerView;

    private EditText etName;

    private TextView daid;

    private TextView mac;

    private TextView software;

    private TextView ip;

    private Button connect;

    Server_Callback callback;

    private ImageView QRview;

    public Alert_Server(Context context) {
        this.context = context;
    }

    public void serverInit(final Server_Callback callback) {
        this.callback = callback;
        ViewGroup extView1 = (ViewGroup) LayoutInflater.from(this.context).inflate(R.layout.inputserver_form, null);
        etName = (EditText) extView1.findViewById(R.id.server_input);
        QRview = (ImageView) extView1.findViewById(R.id.QRimage);
        daid = (TextView) extView1.findViewById(R.id.tv_daid);
        mac = (TextView) extView1.findViewById(R.id.tv_mac);
        software = (TextView) extView1.findViewById(R.id.tv_software);
        ip = (TextView) extView1.findViewById(R.id.tv_ip);
        connect = (Button) extView1.findViewById(R.id.connect);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            List<String> ip = RegexUtils.getMatches(REGEX_IP, etName.getText().toString());
                            List<String> port = RegexUtils.getMatches(REGEX_PORT, etName.getText().toString());
                            if (!(ip.size() > 0 && port.size() > 0)){
                                handler.sendEmptyMessage(0x123);
                            } else {
                                if (new test().testIpPort(ip.get(0), Integer.parseInt(port.get(0).substring(1, port.get(0).length())))) {
                                    handler.sendEmptyMessage(0x234);
                                } else {
                                    handler.sendEmptyMessage(0x345);
                                }
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
        inputServerView = new AlertView("服务器设置", null, "关闭", null, null, this.context, AlertView.Style.Alert, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {

                }
            }
        });
        inputServerView.addExtView(extView1);
    }


    public void show() {
        daid.setText(config.getString("daid"));
        software.setText(AppUtils.getAppVersionName());
        ip.setText(NetworkUtils.getIPAddress(true));
        mac.setText(new NetInfo().getMac());
        etName.setText(config.getString("ServerId"));
        Bitmap mBitmap = null;
        DAInfo di = new DAInfo();
        try {
            di.setId(config.getString("daid"));
            di.setName("数据采集器");
            di.setModel("CBDI-RK3368");
            di.setSoftwareVer(AppUtils.getAppVersionName());
            di.setProject("SZY");
            mBitmap = di.daInfoBmp();
        } catch (Exception e){
            e.printStackTrace();
        }
        if (mBitmap != null) {
            QRview.setImageBitmap(mBitmap);
        }
        inputServerView.show();
    }

    public interface Server_Callback {
        void setNetworkBmp();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    ToastUtils.showLong("服务器地址输入有误，请输入正确的服务器地址");
                    break;
                case 0x234:
                    callback.setNetworkBmp();
                    config.put("ServerId", etName.getText().toString());
                    ToastUtils.showLong("服务器连接成功，新的服务器地址已保存");
                    break;
                case 0x345:
                    ToastUtils.showLong("服务器连接失败，请确认服务器是否输入正确");
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };
}
