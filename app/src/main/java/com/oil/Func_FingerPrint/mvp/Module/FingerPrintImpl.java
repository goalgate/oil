package com.oil.Func_FingerPrint.mvp.Module;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

//import com.drv.fingerprint.DevComm;


import com.oil.Func_FingerPrint.mvp.powerControl.PowerControl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import cbdi.drv.fingerprint.DevComm1;
import cbdi.drv.fingerprint.IUsbConnState;

/**
 * Created by zbsz on 2017/6/2.
 */

public class FingerPrintImpl implements IFingerPrint {
    private static final String TAG = "fingerprint";
    IFPListener2 listener;
    private static DevComm1 m_usbComm;
    final String TEMPLATE_PATH = "sdcard/template.bin";
    int m_nMaxFpCount = 1700;
    int m_nUserID, m_nImgWidth, m_nImgHeight;
    long m_nPassedTime;
    byte[] m_binImage, m_bmpImage;
    String m_strPost;

    String m_strReset;

    int m_reset = 0;
    boolean m_bCancel;


    //重点，重点，重点
    private PowerControl m_powerControlDev;

    private void InitdevPowerControl(Context context) {
        m_powerControlDev = new PowerControl("/dev/ttyS2", 115200);
        if (m_powerControlDev.open() == PowerControl.ERR_OPEN) {
            Toast.makeText(context, "电源控制设备连接失败！", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(context, "电源控制设备连接成功！", Toast.LENGTH_SHORT).show();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resetFpDev() {
        m_usbComm.CloseComm();
        OnResetFingerPrintBtn();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        onOpen(listener);
        onIdentify(listener);

    }

    private Boolean OnResetFingerPrintBtn() {
        Log.i("", "OnResetFingerPrintBtn");
        if (m_powerControlDev.resetNoemhost() != PowerControl.ERR_SUCCESS) {
            return false;
        }
        return true;
    }

    @Override
    public void onInit(Activity activity, IFPListener2 fplistener) {
        this.listener = fplistener;
        InitdevPowerControl(activity);
        if (m_usbComm == null) {
            m_usbComm = new DevComm1(activity, m_IConnectionHandler);
        }
        m_binImage = new byte[1024 * 100];
        m_bmpImage = new byte[1024 * 100];
    }

    @Override
    public boolean onOpen(IFPListener2 fplistener) {
        this.listener = fplistener;

        String[] w_strInfo = new String[1];

        if (m_usbComm != null) {
            if (!m_usbComm.IsInit()) {
                if (!m_usbComm.OpenComm("USB", 115200)) {
                    //OnResetFingerPrintBtn();
                    Log.e("消息", "初始化USB失败1!");
                    if (!m_usbComm.OpenComm("USB", 115200)) {
                        Log.e("消息", "初始化USB失败2!");
                        if (!m_usbComm.OpenComm("USB", 115200)) {
                            Log.e("消息", "初始化USB失败3!");
                            return false;
                        } else {
                            listener.onText("初始化USB成功!");
                        }
                    }
                } else {
                    if (m_usbComm.Run_TestConnection() == DevComm1.ERR_SUCCESS) {
                        if (m_usbComm.Run_GetDeviceInfo(w_strInfo) == DevComm1.ERR_SUCCESS) {
                            listener.onText("指纹机打开成功");
//                            listener.onEnableCtrl(true);
//                            listener.onBtnOpenEnable(false);
//                            listener.onBtnCloseEnable(true);
                            return true;
                        } else
                            listener.onText("无法连接到指纹机");
                        return false;
                    } else
                        listener.onText("无法连接到指纹机");
                    return false;
                }
            }
        }
        m_usbComm.Run_SLEDControl(1);
        return true;
    }

    private static String usb5V = "/sys/devices/5v_power_en.29/five";

    @Override
    public void onReSetUSB() {
        handler.sendEmptyMessage(6);
    }

    private static void writeIOFile(String str, String path) {
        File file = new File(path);
        file.setExecutable(true);
        file.setReadable(true);
        file.setWritable(true);
        if (str.equals("0")) {
            do_exec("busybox echo 0 > " + path);
        } else {
            do_exec("busybox echo 1 > " + path);
        }
    }

    private static void do_exec(String cmd) {
        try {
            /* Missing read/write permission, trying to chmod the file */
            Process su;
            su = Runtime.getRuntime().exec("su");
            String str = cmd + "\n" + "exit\n";
            su.getOutputStream().write(str.getBytes());
            if ((su.waitFor() != 0)) {
                throw new SecurityException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//    @Override
//    public void onOpen(IFPListener fplistener) {
//        this.listener = fplistener;
//
//        String[] w_strInfo = new String[1];
//
//        if(m_usbComm != null) {
//            if (!m_usbComm.IsInit()) {
//
//                if (!m_usbComm.OpenComm("USB", 115200)) {
//                    Log.i(TAG, "OnOpenDeviceBtn fail 1");
//                    if (!m_usbComm.OpenComm("USB", 115200)) {
//                        Log.i(TAG, "OnOpenDeviceBtn fail 2");
//                        if (m_usbComm.OpenComm("USB", 115200)) {
//                            Log.i(TAG, "OnOpenDeviceBtn fail 3");
//                            //初始化设备失败
//                        }
//                    }
//                }
//                else{
//                    listener.onText("无法打开usb!");
//                }
//            } else {
//                if (m_usbComm.Run_TestConnection() == DevComm.ERR_SUCCESS)
//                {
//                    if (m_usbComm.Run_GetDeviceInfo(w_strInfo) == DevComm.ERR_SUCCESS)
//                    {
//                        listener.onText("打开指纹模块成功");
//                        listener.onEnableCtrl(true);
//                        listener.onBtnOpenEnable(false);
//                        listener.onBtnCloseEnable(true);
//                    }
//                    else
//                        listener.onText("不能连接到指纹模块");
//                }
//                else
//                    listener.onText("不能连接到指纹模块");
//            }
//        }
//    }


    @Override
    public void onClose(IFPListener2 listener) {
        m_usbComm.CloseComm();
        m_usbComm = null;
        //listener.onSetInitialState();
    }

    @Override
    public void onCancel() {
        m_bCancel = true;
    }

    @Override
    public void onEnroll(String id, IFPListener2 listener) {
        int w_nRet;
        int[] w_nState = new int[1];

        if (!m_usbComm.IsInit())
            return;

        if (!CheckUserID(id))
            return;

        // Check if fp is exist
        w_nRet = m_usbComm.Run_GetStatus(m_nUserID, w_nState);

        if (w_nRet != DevComm1.ERR_SUCCESS) {
            listener.onText(GetErrorMsg(w_nRet));
            return;
        }

        if (w_nState[0] == DevComm1.GD_TEMPLATE_NOT_EMPTY) {
            listener.onText("模板已经存在了");
            return;
        }

        listener.onText("请放置手指 : " + m_nUserID);
//        listener.onEnableCtrl(false);
//        listener.onBtnCloseEnable(false);
//        listener.onBtnCancelEnable(true);
        m_usbComm.Run_SLEDControl(1);
        m_bCancel = false;

        new Thread(new Runnable() {
            int w_nRet, w_nUserID, w_nEnrollStep = 0, w_nGenCount = 3;
            int[] w_nDupID = new int[1];
            int[] w_nWidth = new int[1];
            int[] w_nHeight = new int[1];

            @Override
            public void run() {

                w_nUserID = m_nUserID;

                while (w_nEnrollStep < w_nGenCount) {
                    m_strPost = String.format("请放置手指 #%d!", w_nEnrollStep + 1);


                    handler.sendEmptyMessage(0x234);

                    // Capture
                    if (Capturing() < 0)
                        return;

                    m_strPost = "松开手指";
                    handler.sendEmptyMessage(0x234);

                    // Up Cpatured Image
                    w_nRet = m_usbComm.Run_UpImage(0, m_binImage, w_nWidth, w_nHeight);

                    if (w_nRet != DevComm1.ERR_SUCCESS) {
                        m_strPost = GetErrorMsg(w_nRet);
                        handler.sendEmptyMessage(0x123);
                        return;
                    }

                    // Draw image
                    m_nImgWidth = w_nWidth[0];
                    m_nImgHeight = w_nHeight[0];
                    handler.sendEmptyMessage(0x345);

                    // Create Template
                    w_nRet = m_usbComm.Run_Generate(w_nEnrollStep);

                    if (w_nRet != DevComm1.ERR_SUCCESS) {
                        if (w_nRet == DevComm1.ERR_BAD_QUALITY) {
                            m_strPost = "合成失败，请重试";
                            handler.sendEmptyMessage(0x234);
                            continue;
                        } else {
                            m_strPost = GetErrorMsg(w_nRet);
                            handler.sendEmptyMessage(0x123);
                            return;
                        }
                    }

                    /*
                    if(w_nEnrollStep == 0)
            		{
            			if (w_nGenCount == 3)
            				m_strPost = "Two More";
            			else
            				m_strPost = "One More";
            		}
            		else if(w_nEnrollStep == 1)
            			m_strPost = "One More";

                    m_FpImageViewer.post(runShowStatus);
                    */

                    w_nEnrollStep++;
                }

                //m_strPost = "Release Finger";
                //m_FpImageViewer.post(runShowStatus);

                // Merge
                if (w_nGenCount != 1) {
                    //. Merge Template
                    w_nRet = m_usbComm.Run_Merge(0, w_nGenCount);

                    if (w_nRet != DevComm1.ERR_SUCCESS) {
                        m_strPost = GetErrorMsg(w_nRet);
                        handler.sendEmptyMessage(0x123);
                        return;
                    }
                }

                //. Store template
                w_nRet = m_usbComm.Run_StoreChar(w_nUserID, 0, w_nDupID);

                if (w_nRet != DevComm1.ERR_SUCCESS) {
                    if (w_nRet == DevComm1.ERR_DUPLICATION_ID)
                        m_strPost = String.format("登记失败，已有相同模板ID = %d ,请再刷取身份证重试", w_nDupID[0]);
                    else
                        m_strPost = GetErrorMsg(w_nRet);
                } else{
                    m_strPost = String.format("模板 No : %d 登记成功, 再次刷取身份证信息可进行新的登记操作", m_nUserID);
                    handler.sendEmptyMessage(0x567);
                }
                handler.sendEmptyMessage(0x123);
            }
        }).start();
    }


    @Override
    public void onVerify(String id, IFPListener2 listener) {
        int w_nRet;
        int[] w_nState = new int[1];

        if (!m_usbComm.IsInit())
            return;

        if (!CheckUserID(id))
            return;

        w_nRet = m_usbComm.Run_GetStatus(m_nUserID, w_nState);

        if (w_nRet != DevComm1.ERR_SUCCESS) {
            listener.onText(GetErrorMsg(w_nRet));
            return;
        }

        if (w_nState[0] == DevComm1.GD_TEMPLATE_EMPTY) {
            listener.onText("模板空白");
            return;
        }

        listener.onText("放置手指");
//        listener.onEnableCtrl(false);
//        listener.onBtnCloseEnable(false);
//        listener.onBtnCancelEnable(true);
        m_usbComm.Run_SLEDControl(1);
        m_bCancel = false;

        new Thread(new Runnable() {
            int w_nRet;
            int[] w_nLearned = new int[1];
            int[] w_nWidth = new int[1];
            int[] w_nHeight = new int[1];

            @Override
            public void run() {

                if (Capturing() < 0)
                    return;

                m_strPost = "松开手指";
                handler.sendEmptyMessage(0x234);


                // Up Cpatured Image
                w_nRet = m_usbComm.Run_UpImage(0, m_binImage, w_nWidth, w_nHeight);

                if (w_nRet != DevComm1.ERR_SUCCESS) {
                    m_strPost = GetErrorMsg(w_nRet);
                    handler.sendEmptyMessage(0x123);
                    return;
                }

                // Draw image
                m_nImgWidth = w_nWidth[0];
                m_nImgHeight = w_nHeight[0];
                handler.sendEmptyMessage(0x345);

                // Create template
                m_nPassedTime = SystemClock.elapsedRealtime();
                w_nRet = m_usbComm.Run_Generate(0);

                if (w_nRet != DevComm1.ERR_SUCCESS) {
                    m_strPost = GetErrorMsg(w_nRet);
                    handler.sendEmptyMessage(0x123);
                    return;
                }

                // Verify
                w_nRet = m_usbComm.Run_Verify(m_nUserID, 0, w_nLearned);
                m_nPassedTime = SystemClock.elapsedRealtime() - m_nPassedTime;

                if (w_nRet == DevComm1.ERR_SUCCESS)
                    m_strPost = String.format("校验模板编号 No : %d 成功，匹配时间 : %dms", m_nUserID, w_nLearned[0], m_nPassedTime);
                else
                    m_strPost = GetErrorMsg(w_nRet);

                handler.sendEmptyMessage(0x123);
            }
        }).start();
    }

//    @Override
//    public void onIdentify(IFPListener listener) {
//
//        if (!m_usbComm.IsInit())
//            return;
//
//        listener.onEnableCtrl(false);
//        listener.onBtnCloseEnable(false);
//        listener.onBtnCancelEnable(true);
//        m_usbComm.Run_SLEDControl(1);
//        m_bCancel = false;
//
//        m_strPost = "";
//
//        new Thread(new Runnable() {
//            int w_nRet;
//            int[] w_nID = new int[1];
//            int[] w_nLearned = new int[1];
//            int[] w_nWidth = new int[1];
//            int[] w_nHeight = new int[1];
//
//            @Override
//            public void run() {
//
//                while (true) {
//                    if (m_strPost.isEmpty())
//                        m_strPost = "Input your finger.";
//                    else
//                        //m_strPost = m_strPost + "\r\nInput your finger.";
//                        handler.sendEmptyMessage(0x234);
//
//                    if (Capturing() < 0)
//                        return;
//
//                    m_strPost = "松开手指";
//                    handler.sendEmptyMessage(0x234);
//
//                    // Up Cpatured Image
//                    w_nRet = m_usbComm.Run_UpImage(0, m_binImage, w_nWidth, w_nHeight);
//
//                    if (w_nRet != DevComm.ERR_SUCCESS) {
//                        m_strPost = GetErrorMsg(w_nRet);
//                        handler.sendEmptyMessage(0x123);
//                        return;
//                    }
//
//                    // Draw image
//                    m_nImgWidth = w_nWidth[0];
//                    m_nImgHeight = w_nHeight[0];
//                    handler.sendEmptyMessage(0x345);
//
//                    // Create template
//                    m_nPassedTime = SystemClock.elapsedRealtime();
//                    w_nRet = m_usbComm.Run_Generate(0);
//
//                    if (w_nRet != DevComm.ERR_SUCCESS) {
//                        m_strPost = GetErrorMsg(w_nRet);
//                        handler.sendEmptyMessage(0x234);
//
//                        if (w_nRet == DevComm.ERR_CONNECTION)
//                            return;
//                        else {
//                            SystemClock.sleep(1000);
//                            continue;
//                        }
//                    }
//
//                    // Identify
//                    w_nRet = m_usbComm.Run_Search(0, 1, m_nMaxFpCount, w_nID, w_nLearned);
//                    m_nPassedTime = SystemClock.elapsedRealtime() - m_nPassedTime;
//
//                    if (w_nRet == DevComm.ERR_SUCCESS) {
//                        m_strPost = String.format("TAG%d", w_nID[0]);
//
//                    } else {
//                        //m_strPost = String.format("\r\nMatch Time : %dms", m_nPassedTime);
//                        //m_strPost = GetErrorMsg(w_nRet) + m_strPost;
//                        m_strPost = GetErrorMsg(w_nRet);
//                    }
//                }
//            }
//        }).start();
//    }

    @Override
    public void onIdentify(IFPListener2 listener) {
//        if (!m_usbComm.IsInit())
//            return;

//        listener.onEnableCtrl(false);
//        listener.onBtnCloseEnable(false);
//        listener.onBtnCancelEnable(true);
        m_usbComm.Run_SLEDControl(1);
        m_bCancel = false;

        m_strPost = "";

        new Thread(new Runnable() {
            int w_nRet;
            int[] w_nID = new int[1];
            int[] w_nLearned = new int[1];
            int[] w_nWidth = new int[1];
            int[] w_nHeight = new int[1];

            @Override
            public void run() {

                while (true) {
                    if (!m_usbComm.IsInit()) {
                        Log.e("TAG", "OnIdentifyBtn  m_devComm.IsInit fail");
                        OnResetFingerPrintBtn();
                    }
                    m_usbComm.Run_SLEDControl(1);
                    if (m_strPost.isEmpty())
                        m_strPost = "放置手指";
                    else
                        m_strPost = m_strPost + "\r\n放置手指";
                    handler.sendEmptyMessage(0x234);

                    if (Capturing() < 0) {
                        Log.e("TAG", "OnIdentifyBtn  Capturing fail");
                        if (!m_bCancel) {
                            handler.sendEmptyMessage(0x456);
                        }
                        return;
                    }


                    m_strPost = "松开手指";
                    handler.sendEmptyMessage(0x234);

                    // Up Cpatured Image
                    w_nRet = m_usbComm.Run_UpImage(0, m_binImage, w_nWidth, w_nHeight);

                    if (w_nRet != DevComm1.ERR_SUCCESS) {
                        m_strPost = GetErrorMsg(w_nRet);
                        handler.sendEmptyMessage(0x123);
                        return;
                    }

                    // Draw image
                    m_nImgWidth = w_nWidth[0];
                    m_nImgHeight = w_nHeight[0];
                    handler.sendEmptyMessage(0x345);

                    // Create template
                    m_nPassedTime = SystemClock.elapsedRealtime();
                    w_nRet = m_usbComm.Run_Generate(0);

                    if (w_nRet != DevComm1.ERR_SUCCESS) {
                        m_strPost = GetErrorMsg(w_nRet);
                        handler.sendEmptyMessage(0x234);

                        if (w_nRet != DevComm1.ERR_CONNECTION) {
                            SystemClock.sleep(1000);
                            continue;
                        } else {
                            return;
                        }
                    }

                    // Identify
                    w_nRet = m_usbComm.Run_Search(0, 1, m_nMaxFpCount, w_nID, w_nLearned);
                    m_nPassedTime = SystemClock.elapsedRealtime() - m_nPassedTime;

                    if (w_nRet == DevComm1.ERR_SUCCESS){
                        m_strPost = String.format("TAG%d", w_nID[0]);
                        handler.sendEmptyMessage(0x789);
                    }

                       // m_strPost = String.format("校验模板编号 No : %d 成功，匹配时间 : %dms", w_nID[0], w_nLearned[0], m_nPassedTime);
                    else {
                        m_strPost = String.format("\r\n匹配时间 : %dms", m_nPassedTime);
                        m_strPost = GetErrorMsg(w_nRet) + m_strPost;
                    }
                }
            }
        }).start();
    }

    @Override
    public void onGetEnrollCount(IFPListener2 listener) {
        int w_nRet;
        int[] w_nEnrollCount = new int[1];

        if (!m_usbComm.IsInit())
            return;

        w_nRet = m_usbComm.Run_GetEnrollCount(1, m_nMaxFpCount, w_nEnrollCount);

        if (w_nRet != DevComm1.ERR_SUCCESS) {
            listener.onText(GetErrorMsg(w_nRet));
            return;
        }

        listener.onText(String.format("Result : Success\r\nEnroll Count = %d", w_nEnrollCount[0]));
    }

    @Override
    public int onGetEmptyID(IFPListener2 listener) {
        int w_nRet;
        int[] w_nEmptyID = new int[1];

        if (!m_usbComm.IsInit())
            return 0;

        w_nRet = m_usbComm.Run_GetEmptyID(1, m_nMaxFpCount, w_nEmptyID);

        if (w_nRet != DevComm1.ERR_SUCCESS) {
            listener.onText(GetErrorMsg(w_nRet));
            return 0;
        }
        //listener.onText(String.format("获得空ID = %d", w_nEmptyID[0]));
        return w_nEmptyID[0];
        //listener.onSetID(String.format("%d", w_nEmptyID[0]));
    }

    @Override
    public void onCaptureImg(IFPListener2 listener) {
//        listener.onEnableCtrl(false);
//        listener.onBtnCloseEnable(false);
//        listener.onBtnCancelEnable(true);
        m_usbComm.Run_SLEDControl(1);
        m_bCancel = false;
        listener.onText("放置手指");
        new Thread(new Runnable() {
            int w_nRet;
            int[] width = new int[1];
            int[] height = new int[1];

            @Override
            public void run() {
                while (true) {
                    if (Capturing() < 0) {
                        return;
                    }
                    if (m_usbComm != null) {
                        w_nRet = m_usbComm.Run_UpImage(0, m_binImage, width, height);
                    }
                    if (w_nRet != DevComm1.ERR_SUCCESS) {
                        m_strPost = GetErrorMsg(w_nRet);
                        handler.sendEmptyMessage(0x123);
                        return;
                    }

                    m_nImgWidth = width[0];
                    m_nImgHeight = height[0];
                    m_strPost = "获得指纹图片成功";
                    handler.sendEmptyMessage(0x678);
                }
            }
        }).start();
    }

    @Override
    public void onRemoveTmpl(String TmplId, IFPListener2 listener) {
        int w_nRet;

        if (!m_usbComm.IsInit())
            return;

        if (!CheckUserID(TmplId))
            return;

        w_nRet = m_usbComm.Run_DelChar(m_nUserID, m_nUserID);

        if (w_nRet != DevComm1.ERR_SUCCESS) {
            listener.onText(GetErrorMsg(w_nRet));
            return;
        }

        listener.onText("Delete OK !");
    }

    @Override
    public void onRemoveAll(IFPListener2 listener) {
        int w_nRet;

        if (!m_usbComm.IsInit())
            return;

        w_nRet = m_usbComm.Run_DelChar(1, m_nMaxFpCount);

        if (w_nRet != DevComm1.ERR_SUCCESS) {
            listener.onText(GetErrorMsg(w_nRet));
            return;
        }
        listener.onText("Delete all OK !");
    }

    private final IUsbConnState m_IConnectionHandler = new IUsbConnState() {
        @Override
        public void onUsbConnected() {
            String[] w_strInfo = new String[1];

            if (m_usbComm.Run_TestConnection() == DevComm1.ERR_SUCCESS) {
                if (m_usbComm.Run_GetDeviceInfo(w_strInfo) == DevComm1.ERR_SUCCESS) {
//                    listener.onEnableCtrl(true);
//                    listener.onBtnOpenEnable(false);
//                    listener.onBtnCloseEnable(true);
                    listener.onText("打开设备成功");
                }
            }
        }

        @Override
        public void onUsbPermissionDenied() {
            listener.onText("Permission denied!");
        }

        @Override
        public void onDeviceNotFound() {
            listener.onText("Can not find usb device!");
        }
    };


    @Override
    public void onUpTemplate(String id, IFPListener2 listener) {
        int w_nRet;
        byte[] w_pTemplate = new byte[DevComm1.MAX_DATA_LEN];

        // Check USB Connection
        if (!m_usbComm.IsInit())
            return;

        // Check User ID
        if (!CheckUserID(id))
            return;

        do {
//            listener.onEnableCtrl(false);
//            listener.onBtnCloseEnable(false);
//            listener.onBtnCancelEnable(true);

            // Load Template to Buffer
            w_nRet = m_usbComm.Run_LoadChar((short) m_nUserID, 0);
            if (w_nRet != DevComm1.ERR_SUCCESS) {
                listener.onText(GetErrorMsg(w_nRet));
                break;
            }

            // Up Template
            w_nRet = m_usbComm.Run_UpChar(0, w_pTemplate);
            if (w_nRet != DevComm1.ERR_SUCCESS) {
                listener.onText(GetErrorMsg(w_nRet));
                break;
            }

            ////////////////////////////////////////////////////////////////////
            // Save Template (/FPData/01.fpt)
            // Create Directory
            String w_szSaveDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FPData";
            File w_fpDir = new File(w_szSaveDirPath);
            if (!w_fpDir.exists())
                w_fpDir.mkdirs();

            // Create Template File
            File w_fpTemplate = new File(w_szSaveDirPath + "/" + String.valueOf(m_nUserID) + ".fpt");
            if (!w_fpTemplate.exists()) {
                try {
                    w_fpTemplate.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            String s = Base64.encodeToString(w_pTemplate, Base64.DEFAULT);
            byte[] bytes = Base64.decode(s, Base64.DEFAULT);

            // Save Template Data
            FileOutputStream w_foTemplate = null;

            try {
                w_foTemplate = new FileOutputStream(w_fpTemplate);
                w_foTemplate.write(w_pTemplate, 0, DevComm1.MAX_DATA_LEN);
                w_foTemplate.close();
                // Show Status
                listener.onText(String.format("Result : Get Template Success.\r\nDir : %s", w_szSaveDirPath + "/" + String.valueOf(m_nUserID) + ".fpt"));
                break;
            } catch (Exception e) {
                e.printStackTrace();
            }
            ////////////////////////////////////////////////////////////////////

            listener.onText(String.format("Result : Get Template Success.\r\nSave Template Failed."));
        } while (false);

//        listener.onEnableCtrl(true);
//        listener.onBtnCloseEnable(true);
//        listener.onBtnCancelEnable(false);
    }

    @Override
    public void onDownTemplate(String id, IFPListener2 listener) {
        int w_nRet;
        int[] w_nDupTmplNo = new int[1];
        byte[] w_pTemplate = new byte[DevComm1.MAX_DATA_LEN];

        // Check USB Connection
        if (!m_usbComm.IsInit())
            return;

        // Check User ID
        if (!CheckUserID(id))
            return;

        ////////////////////////////////////////////////////////////////////
        // Load Template (/FPData/01.fpt)
        // Check Directory
        String w_szLoadDirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FPData";
        File w_fpDir = new File(w_szLoadDirPath);
        if (!w_fpDir.exists()) {
            listener.onText(String.format("Result : Can't load template data.\r\nDir : %s", w_szLoadDirPath + "/" + String.valueOf(m_nUserID) + ".fpt"));
            return;
        }

        // Check Template File
        File w_fpTemplate = new File(w_szLoadDirPath + "/" + String.valueOf(m_nUserID) + ".fpt");
        if (!w_fpTemplate.exists()) {
            listener.onText(String.format("Result : Can't load template data.\r\nDir : %s", w_szLoadDirPath + "/" + String.valueOf(m_nUserID) + ".fpt"));
            return;
        }

        // Load Template Data
        FileInputStream w_fiTemplate = null;
        try {
            w_fiTemplate = new FileInputStream(w_fpTemplate);
            w_fiTemplate.read(w_pTemplate, 0, DevComm1.MAX_DATA_LEN);
            w_fiTemplate.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onText(String.format("Result : Can't load template data.\r\nDir : %s", w_szLoadDirPath + "/" + String.valueOf(m_nUserID) + ".fpt"));
            return;
        }
        ////////////////////////////////////////////////////////////////////

        do {

            // Download Template to Buffer
            w_nRet = m_usbComm.Run_DownChar(0, w_pTemplate);
            if (w_nRet != DevComm1.ERR_SUCCESS) {
                listener.onText(GetErrorMsg(w_nRet));
                break;
            }

            // Store Template
            w_nRet = m_usbComm.Run_StoreChar(m_nUserID, 0, w_nDupTmplNo);
            if (w_nRet != DevComm1.ERR_SUCCESS) {
                if (w_nRet == DevComm1.ERR_DUPLICATION_ID) {
                    listener.onText(String.format("Result : Fail\r\nDuplication ID = %d", w_nDupTmplNo[0]));
                } else {
                    listener.onText(GetErrorMsg(w_nRet));
                }
                break;
            }

            listener.onText(String.format("Result : Set Template Success.\r\nUserID = %d", m_nUserID));
        } while (false);
    }


    private String GetErrorMsg(int nErrorCode) {
        String str = new String("");

        switch (nErrorCode) {
            case DevComm1.ERR_SUCCESS:
                str = "Succcess";
                break;
            case DevComm1.ERR_VERIFY:
                str = "Verify NG";
                break;
            case DevComm1.ERR_IDENTIFY:
                str = "请确认指纹是否已登记";
                break;
            case DevComm1.ERR_EMPTY_ID_NOEXIST:
                str = "Empty Template no Exist";
                break;
            case DevComm1.ERR_BROKEN_ID_NOEXIST:
                str = "Broken Template no Exist";
                break;
            case DevComm1.ERR_TMPL_NOT_EMPTY:
                str = "Template of this ID Already Exist";
                break;
            case DevComm1.ERR_TMPL_EMPTY:
                str = "This Template is Already Empty";
                break;
            case DevComm1.ERR_INVALID_TMPL_NO:
                str = "Invalid Template No";
                break;
            case DevComm1.ERR_ALL_TMPL_EMPTY:
                str = "请确认指纹是否已登记";
                break;
            case DevComm1.ERR_INVALID_TMPL_DATA:
                str = "Invalid Template Data";
                break;
            case DevComm1.ERR_DUPLICATION_ID:
                str = "已有重复指纹ID : ";
                break;
            case DevComm1.ERR_BAD_QUALITY:
                str = "指纹图片质量差";
                break;
            case DevComm1.ERR_MERGE_FAIL:
                str = "指纹合成失败，重新刷取身份证可再次操作";
                break;
            case DevComm1.ERR_NOT_AUTHORIZED:
                str = "Device not authorized.";
                break;
            case DevComm1.ERR_MEMORY:
                str = "Memory Error ";
                break;
            case DevComm1.ERR_INVALID_PARAM:
                str = "Invalid Parameter";
                break;
            case DevComm1.ERR_GEN_COUNT:
                str = "Generation Count is invalid";
                break;
            case DevComm1.ERR_INVALID_BUFFER_ID:
                str = "Ram Buffer ID is invalid.";
                break;
            case DevComm1.ERR_INVALID_OPERATION_MODE:
                str = "Invalid Operation Mode!";
                break;
            case DevComm1.ERR_FP_NOT_DETECTED:
                str = "Finger is not detected.";
                break;
            default:
                str = String.format("Fail, error code=%d", nErrorCode);
                break;
        }

        return str;
    }

    private boolean CheckUserID(String TmplId) {
        if (TmplId == "") {
            listener.onText("Please input user id");
            return false;
        }

        try {
            m_nUserID = Integer.parseInt(TmplId);
        } catch (NumberFormatException e) {
            listener.onText("Please input correct user id(1~" + m_nMaxFpCount + ")");
            return false;
        }

        if (m_nUserID > (m_nMaxFpCount) || m_nUserID < 1) {
            listener.onText("Please input correct user id(1~" + m_nMaxFpCount + ")");
            return false;
        }

        return true;
    }


    private int Capturing() {
        int w_nRet;
        while (true) {

            w_nRet = m_usbComm.Run_GetImage();

            if (w_nRet == DevComm1.ERR_CONNECTION) {
//                m_strPost = "通信失败";
//                handler.sendEmptyMessage(0x234);
//                return -1;
                m_strReset = "指纹重启次数：" + ++m_reset;
                return -1;
            } else if (w_nRet == DevComm1.ERR_SUCCESS)
                break;

            if (m_bCancel) {
                StopOperation();
                return -1;
            }
        }

        return 0;
    }

    private void StopOperation() {
        m_strPost = "操作取消";
        handler.sendEmptyMessage(0x123);
    }

    private void MakeBMPBuf(byte[] Input, byte[] Output, int iImageX, int iImageY) {

        byte[] w_bTemp = new byte[4];
        byte[] head = new byte[1078];
        byte[] head2 = {
                /***************************/
                //file header
                0x42, 0x4d,//file type
                //0x36,0x6c,0x01,0x00, //file size***
                0x0, 0x0, 0x0, 0x00, //file size***
                0x00, 0x00, //reserved
                0x00, 0x00,//reserved
                0x36, 0x4, 0x00, 0x00,//head byte***
                /***************************/
                //infoheader
                0x28, 0x00, 0x00, 0x00,//struct size

                //0x00,0x01,0x00,0x00,//map width***
                0x00, 0x00, 0x0, 0x00,//map width***
                //0x68,0x01,0x00,0x00,//map height***
                0x00, 0x00, 0x00, 0x00,//map height***

                0x01, 0x00,//must be 1
                0x08, 0x00,//color count***
                0x00, 0x00, 0x00, 0x00, //compression
                //0x00,0x68,0x01,0x00,//data size***
                0x00, 0x00, 0x00, 0x00,//data size***
                0x00, 0x00, 0x00, 0x00, //dpix
                0x00, 0x00, 0x00, 0x00, //dpiy
                0x00, 0x00, 0x00, 0x00,//color used
                0x00, 0x00, 0x00, 0x00,//color important
        };

        int i, j, num, iImageStep;

        Arrays.fill(w_bTemp, (byte) 0);

        System.arraycopy(head2, 0, head, 0, head2.length);

        if ((iImageX % 4) != 0)
            iImageStep = iImageX + (4 - (iImageX % 4));
        else
            iImageStep = iImageX;

        num = iImageX;
        head[18] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[19] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[20] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[21] = (byte) (num & (byte) 0xFF);

        num = iImageY;
        head[22] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[23] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[24] = (byte) (num & (byte) 0xFF);
        num = num >> 8;
        head[25] = (byte) (num & (byte) 0xFF);

        j = 0;
        for (i = 54; i < 1078; i = i + 4) {
            head[i] = head[i + 1] = head[i + 2] = (byte) j;
            head[i + 3] = 0;
            j++;
        }

        System.arraycopy(head, 0, Output, 0, 1078);

        if (iImageStep == iImageX) {
            for (i = 0; i < iImageY; i++) {
                System.arraycopy(Input, i * iImageX, Output, 1078 + i * iImageX, iImageX);
            }
        } else {
            iImageStep = iImageStep - iImageX;

            for (i = 0; i < iImageY; i++) {
                System.arraycopy(Input, i * iImageX, Output, 1078 + i * (iImageX + iImageStep), iImageX);
                System.arraycopy(w_bTemp, 0, Output, 1078 + i * (iImageX + iImageStep) + iImageX, iImageStep);
            }
        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                listener.onText(m_strPost);
                m_usbComm.Run_SLEDControl(0);
            } else if (msg.what == 0x234) {
                listener.onText(m_strPost);
            } else if (msg.what == 0x345) {
                listener.onText(m_strPost);
                int nSize;
                MakeBMPBuf(m_binImage, m_bmpImage, m_nImgWidth, m_nImgHeight);
                if ((m_nImgWidth % 4) != 0)
                    nSize = m_nImgWidth + (4 - (m_nImgWidth % 4));
                else
                    nSize = m_nImgWidth;

                nSize = 1078 + nSize * m_nImgHeight;

                //DebugManage.WriteBmp(m_bmpImage, nSize);

                Bitmap image = BitmapFactory.decodeByteArray(m_bmpImage, 0, nSize);

                listener.onSetImg(image);
            } else if (msg.what == 0x456) {
                resetFpDev();
               //listener.onResetTxt(m_strReset);
            }else if (msg.what == 6){
                writeIOFile("0", usb5V);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                writeIOFile("1", usb5V);
            }else if(msg.what == 0x567){
                listener.onRegSuccess();
            }else if(msg.what == 0x678){
                int nSize;
                MakeBMPBuf(m_binImage, m_bmpImage, m_nImgWidth, m_nImgHeight);
                if ((m_nImgWidth % 4) != 0)
                    nSize = m_nImgWidth + (4 - (m_nImgWidth % 4));
                else
                    nSize = m_nImgWidth;

                nSize = 1078 + nSize * m_nImgHeight;

                //DebugManage.WriteBmp(m_bmpImage, nSize);

                Bitmap image = BitmapFactory.decodeByteArray(m_bmpImage, 0, nSize);
                listener.onCapturing(image);
            }else if(msg.what == 0x789){
                listener.onFpSucc(m_strPost);
            }
        }
    };

//    @Override
//    public void onReset(Activity activity) {
//        InitdevPowerControl(activity);
//    }
}
