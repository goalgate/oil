package com.oil.Func_FingerPrint.mvp.powerControl;

/**
 * Created by mac-2 on 6/6/18.
 */
public class PowerControl {


    public static final String CMD_NOEMHOST_RESET = "AAAAAA9669A4044445A1";
    public static final String CMD_VIDEO_RESET = "AAAAAA9669A3033362F1";
    public static final String RESP_NOEMHOST_RESET = "AAAAAA9669000112FFEC";
    public static final String RESP_VIDEO_RESET = "AAAAAA9669000112FFEC";

    public static final int ERR_SUCCESS				= 0;
    public static final int ERR_OPEN			= 1;
    public static final int ERR_WRITE			= 2;
    public static final int ERR_READ			= 3;

    private String g_uartName = "dev/ttyS2";
    private int g_audrate = 115200;
    private Comm comm;



    public PowerControl(String g_uartName, int g_audrate) {
        super();
        this.g_uartName = g_uartName;
        this.g_audrate = g_audrate;
        comm = new CommUart(g_uartName, g_audrate);
    }

    public int open(){
        if(0 == comm.open()){
            return ERR_SUCCESS;
        }
        return ERR_OPEN;
    }
    public int close(){
        comm.close();
        return ERR_OPEN;
    }
    public int resetNoemhost(){
        byte[] btSend = Ulitily.hexStringToByteArray(CMD_NOEMHOST_RESET);

        if(comm.write(btSend, btSend.length, 1000) < 0){
            return ERR_WRITE;
        }
        byte[] buffer=new byte[512];
        if((1 == comm.read(buffer, 1, 1000))){
            return ERR_SUCCESS;
        }
        return ERR_READ;
    }

    public int resetVideohost(){
        byte[] btSend = Ulitily.hexStringToByteArray(CMD_VIDEO_RESET);

        if(comm.write(btSend, btSend.length, 1000) < 0){
            return ERR_WRITE;
        }
        byte[] buffer=new byte[512];
        if((1 == comm.read(buffer, 1, 1000))){
            return ERR_SUCCESS;
        }
        return ERR_READ;
    }

    /*
    //如果串口可靠，应该改用下面这个方法
    public int resetNoemhost(){
        byte[] btSend = Ulitily.hexStringToByteArray(CMD_NOEMHOST_RESET);

        if(comm.write(btSend, btSend.length, 3000) < 0){
            return ERR_WRITE;
        }
        byte[] buffer=new byte[512];
        if((10 == comm.read(buffer, 10, 3000)) && (Ulitily.byteArrayToHexString(buffer).equalsIgnoreCase(RESP_NOEMHOST_RESET))){
            return ERR_SUCCESS;
        }
        return ERR_READ;
    }

    public int resetVideohost(){
        byte[] btSend = Ulitily.hexStringToByteArray(CMD_VIDEO_RESET);

        if(comm.write(btSend, btSend.length, 3000) < 0){
            return ERR_WRITE;
        }
        byte[] buffer=new byte[512];
        if((10 == comm.read(buffer, 10, 3000)) && (Ulitily.byteArrayToHexString(buffer).equalsIgnoreCase(RESP_VIDEO_RESET))){
            return ERR_SUCCESS;
        }
        return ERR_READ;
    }
    */
}
