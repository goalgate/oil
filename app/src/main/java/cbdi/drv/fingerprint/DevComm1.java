package cbdi.drv.fingerprint;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.util.Log;

import cbdi.drv.fingerprint.android_serialport_api.ComBean;
import cbdi.drv.fingerprint.android_serialport_api.SerialHelper;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by KMS on 2016/8/23.
 */
public class DevComm1 {
    private final static String TAG = "DevComm";
    public	static final int MAX_DATA_LEN              = 498;
    public static final int IMAGE_DATA_UNIT           = 496;
    public	static final int ID_NOTE_SIZE                = 64;
    public	static final int MODULE_SN_LEN               = 16;

    private static final int SCSI_TIMEOUT              = 5000; //ms
    private static final int COMM_SLEEP_TIME           = 40;	//ms

    private static final int CMD_PACKET_LEN			= 26;
    private static final int RCM_PACKET_LEN			= 26;
    private static final int RCM_DATA_OFFSET		= 10;

    /***************************************************************************/
    /***************************************************************************/
    private static final int CMD_PREFIX_CODE		= 0xAA55;
    private static final int CMD_DATA_PREFIX_CODE	= 0xA55A;
    private static final int RCM_PREFIX_CODE		= 0x55AA;
    private static final int RCM_DATA_PREFIX_CODE	= 0x5AA5;

    /***************************************************************************
     * System Code (0x0000 ~ 0x001F, 0x0000 : Reserved)
     ***************************************************************************/
    private static final short CMD_TEST_CONNECTION		= 0x0001;
    private static final short CMD_SET_PARAM			= 0x0002;
    private static final short CMD_GET_PARAM			= 0x0003;
    private static final short CMD_GET_DEVICE_INFO		= 0x0004;
    private static final short CMD_ENTER_ISPMODE		= 0x0005;
    private static final short CMD_SET_ID_NOTE			= 0x0006;
    private static final short CMD_GET_ID_NOTE			= 0x0007;
    private static final short CMD_SET_MODULE_SN		= 0x0008;
    private static final short CMD_GET_MODULE_SN		= 0x0009;

    /***************************************************************************
     * Sensor Code (0x0020 ~ 0x003F)
     ***************************************************************************/
    private static final short CMD_GET_IMAGE			= 0x0020;
    private static final short CMD_FINGER_DETECT		= 0x0021;
    private static final short CMD_UP_IMAGE				= 0x0022;
    private static final short CMD_DOWN_IMAGE			= 0x0023;
    private static final short CMD_SLED_CTRL			= 0x0024;

    /***************************************************************************
     * Template Code (0x0040 ~ 0x005F)
     ***************************************************************************/
    private static final short CMD_STORE_CHAR			= 0x0040;
    private static final short CMD_LOAD_CHAR			= 0x0041;
    private static final short CMD_UP_CHAR				= 0x0042;
    private static final short CMD_DOWN_CHAR			= 0x0043;
    private static final short CMD_DEL_CHAR				= 0x0044;
    private static final short CMD_GET_EMPTY_ID			= 0x0045;
    private static final short CMD_GET_STATUS			= 0x0046;
    private static final short CMD_GET_BROKEN_ID		= 0x0047;
    private static final short CMD_GET_ENROLL_COUNT		= 0x0048;

    /***************************************************************************
     * FingerPrint Alagorithm Code (0x0060 ~ 0x007F)
     ***************************************************************************/
    private static final short CMD_GENERATE				= 0x0060;
    private static final short CMD_MERGE				= 0x0061;
    private static final short CMD_MATCH				= 0x0062;
    private static final short CMD_SEARCH				= 0x0063;
    private static final short CMD_VERIFY				= 0x0064;

    /***************************************************************************
     * Unknown Command
     ***************************************************************************/
    private static final short RCM_INCORRECT_COMMAND	= 0x00FF;

    /***************************************************************************
     * Error Code
     ***************************************************************************/
    public static final int ERR_SUCCESS				= 0;
    public static final int ERR_FAIL				= 1;
    public static final int ERR_CONNECTION			= 2;
    public static final int ERR_VERIFY				= 0x10;
    public static final int ERR_IDENTIFY			= 0x11;
    public static final int ERR_TMPL_EMPTY			= 0x12;
    public static final int ERR_TMPL_NOT_EMPTY		= 0x13;
    public static final int ERR_ALL_TMPL_EMPTY		= 0x14;
    public static final int ERR_EMPTY_ID_NOEXIST	= 0x15;
    public static final int ERR_BROKEN_ID_NOEXIST	= 0x16;
    public static final int ERR_INVALID_TMPL_DATA	= 0x17;
    public static final int ERR_DUPLICATION_ID		= 0x18;
    public static final int ERR_BAD_QUALITY			= 0x19;
    public static final int ERR_MERGE_FAIL			= 0x1A;
    public static final int ERR_NOT_AUTHORIZED		= 0x1B;
    public static final int ERR_MEMORY				= 0x1C;
    public static final int ERR_INVALID_TMPL_NO		= 0x1D;
    public static final int ERR_INVALID_PARAM		= 0x22;
    public static final int ERR_GEN_COUNT			= 0x25;
    public static final int ERR_INVALID_BUFFER_ID	= 0x26;
    public static final int ERR_INVALID_OPERATION_MODE	= 0x27;
    public static final int ERR_FP_NOT_DETECTED		= 0x28;

    /***************************************************************************
     * Parameter Index
     ***************************************************************************/
    public static final int DP_DEVICE_ID				= 0;
    public static final int DP_SECURITY_LEVEL			= 1;
    public static final int DP_DUP_CHECK				= 2;
    public static final int DP_BAUDRATE					= 3;
    public static final int DP_AUTO_LEARN				= 4;

    /***************************************************************************
     * Device ID, Security Level
     ***************************************************************************/
    public static final int MIN_DEVICE_ID				= 1;
    public static final int MAX_DEVICE_ID				= 255;
    public static final int MIN_SECURITY_LEVEL			= 1;
    public static final int MAX_SECURITY_LEVEL			= 5;

    public static final int GD_TEMPLATE_NOT_EMPTY	= 0x01;
    public static final int GD_TEMPLATE_EMPTY		= 0x00;

    //--------------- For Usb Communication ------------//
    public int		m_nPacketSize;
    public byte		m_bySrcDeviceID = 1, m_byDstDeviceID = 1;
    public byte[]	m_abyPacket = new byte[64 * 1024];
    public byte[]	m_abyPacket2 = new byte[64 * 1024];
    //--------------------------------------------------//

//    private final Context mApplicationContext;
    private Activity m_parentAcitivity;
    private static final int VID = 0x2009;
    private static final int PID = 0x7638;

    private UsbController   m_usbBase;

    // Serial Port
    private DispQueueThread DispQueue;
    private SerialControl m_SerialPort;

    // uart variables
    public byte[] m_pWriteBuffer;
    public byte[] m_pReadBuffer;
    public byte[] m_pUARTReadBuf;
    public int m_nUARTReadLen;
    public boolean m_bBufferHandle = false;

    // Connection
    public byte m_nConnected;	// 0 : Not Connected, 1 : ttyUART, 2 : USB

    public DevComm1(Context context, IUsbConnState usbConnState){
//        m_parentAcitivity = parentActivity;
//        mApplicationContext = parentActivity.getApplicationContext();

        // usb init
        m_usbBase = new UsbController(context, usbConnState, VID, PID);

        // init variables
        m_nConnected = 0;
        m_nUARTReadLen = 0;
        m_pWriteBuffer = new byte[DevComm.MAX_DATA_LEN];
        m_pReadBuffer = new byte[DevComm.MAX_DATA_LEN];
        m_pUARTReadBuf = new byte[DevComm.MAX_DATA_LEN];

        // uart thread initialize
        DispQueue = new DispQueueThread();
        DispQueue.start();
        m_SerialPort = new SerialControl();
    }

    public boolean IsInit(){
        if (m_nConnected == 0)
            return false;
        else if (m_nConnected == 1)
            return true;
        else if (m_nConnected == 2)
            return m_usbBase.IsInit();

        return false;
    }

    public boolean  OpenComm(String p_szDevice, int p_nBaudrate){
        if (m_nConnected != 0) {
            Log.e(TAG,"OpenComm  m_nConnected err:"+m_nConnected);
            return false;
        }
        if (p_szDevice == "USB")  // usb mode
        {
            if (!m_usbBase.IsInit())
                m_usbBase.init();
            if (!m_usbBase.IsInit()) {
                Log.e(TAG,"OpenComm m_usbBase.init err");
                return false;
            }
            m_nConnected = 2;
        }
        else  // tty uart mode
        {
            m_SerialPort.setPort(p_szDevice);
            m_SerialPort.setBaudRate(p_nBaudrate);
            try
            {
                m_SerialPort.open();
            } catch (SecurityException e) {
                //Toast.makeText(mApplicationContext, "Open ttyUART device failed!", Toast.LENGTH_SHORT).show();
                return false;
            } catch (IOException e) {
                //Toast.makeText(mApplicationContext, "Open ttyUART device failed!", Toast.LENGTH_SHORT).show();
                return false;
            } catch (InvalidParameterException e) {
                //Toast.makeText(mApplicationContext, "Open ttyUART device failed!", Toast.LENGTH_SHORT).show();
                return false;
            }
            m_nConnected = 1;
        }

        return true;
    }

    public boolean  CloseComm(){
        if (m_nConnected == 0) {
            return false;
        }
        else if (m_nConnected == 1) {  // tty uart mode
            m_SerialPort.stopSend();
            m_SerialPort.close();
        }
        else {  // usb mode
            m_usbBase.uninit();
        }

        m_nConnected = 0;
        return true;
    }

    /************************************************************************/
    /************************************************************************/
    public int	Run_TestConnection()
    {
        boolean	w_bRet;

        InitCmdPacket(CMD_TEST_CONNECTION, m_bySrcDeviceID, m_byDstDeviceID, m_abyPacket2, (short)0);

        w_bRet = Send_Command(CMD_TEST_CONNECTION);

        if(!w_bRet)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_SetParam(int p_nParamIndex, int p_nParamValue)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[5];

        w_abyData[0] = (byte)p_nParamIndex;

        //memcpy(&w_abyData[1], &p_nParamValue, 4);
        w_abyData[1] = (byte)(p_nParamValue & 0x000000ff);
        w_abyData[2] = (byte)((p_nParamValue & 0x0000ff00) >> 8);
        w_abyData[3] = (byte)((p_nParamValue & 0x00ff0000) >> 16);
        w_abyData[4] = (byte)((p_nParamValue & 0xff000000) >> 24);

        InitCmdPacket(CMD_SET_PARAM, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, (short)5);

        w_bRet = Send_Command(CMD_SET_PARAM);

        if (!w_bRet)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_GetParam(int p_nParamIndex, int[] p_pnParamValue)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[1];

        w_abyData[0] = (byte)p_nParamIndex;

        InitCmdPacket(CMD_GET_PARAM, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, (short)1);

        w_bRet = Send_Command(CMD_GET_PARAM);

        if(!w_bRet)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        //memcpy(p_pnParamValue, g_pRcmPacket->m_abyData, 4);
        p_pnParamValue[0] = (int)(  (int)((m_abyPacket[RCM_DATA_OFFSET+3] << 24) & 0xFF000000)	|
                (int)((m_abyPacket[RCM_DATA_OFFSET+2] << 16) & 0x00FF0000)	|
                (int)((m_abyPacket[RCM_DATA_OFFSET+1] << 8) & 0x0000FF00)	|
                (int)(m_abyPacket[RCM_DATA_OFFSET+0] & 0x000000FF));

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    public int Run_GetDeviceInfo(String[] p_szDevInfo)
    {
        int		w_nDevInfoLen;
        boolean	w_bRet;
        String w_strTmp;
        byte[] w_szDevInfo;

        InitCmdPacket(CMD_GET_DEVICE_INFO, m_bySrcDeviceID, m_byDstDeviceID, m_abyPacket2, 0);

        w_bRet = Send_Command(CMD_GET_DEVICE_INFO);

        if (!w_bRet)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        w_nDevInfoLen = MAKEWORD(m_abyPacket[RCM_DATA_OFFSET+0], m_abyPacket[RCM_DATA_OFFSET+1]);

        w_bRet = Receive_DataPacket(CMD_GET_DEVICE_INFO);

        if(w_bRet == false)
            return ERR_CONNECTION;

        if ( GetRetCode() != ERR_SUCCESS )
            return GetRetCode();

        //memcpy(p_szDevInfo, g_pRcmPacket->m_abyData, w_wDevInfoLen);
        w_szDevInfo = new byte[w_nDevInfoLen];
        System.arraycopy(m_abyPacket, RCM_DATA_OFFSET, w_szDevInfo, 0, w_nDevInfoLen);
        w_strTmp = new String(w_szDevInfo);
        p_szDevInfo[0] = w_strTmp;

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_SetIDNote(int p_nTmplNo, String p_pstrNote)
    {
        boolean	w_bRet = false;
        byte[]	w_abyData = new byte[ID_NOTE_SIZE+2];
        byte[]	w_abyData2 = new byte[2];
        byte[]	w_abyNoteBuf = p_pstrNote.getBytes();

        //. Assemble command packet
        w_abyData2[0] = LOBYTE((short)(ID_NOTE_SIZE + 2));
        w_abyData2[1] = HIBYTE((short)(ID_NOTE_SIZE + 2));

        InitCmdPacket(CMD_SET_ID_NOTE, m_bySrcDeviceID, m_byDstDeviceID, w_abyData2, 2);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_SET_ID_NOTE);

        if(w_bRet == false)
            return ERR_CONNECTION;

        if( GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        //. Assemble data packet
        memset(w_abyData, (byte)0, ID_NOTE_SIZE+2);
        w_abyData[0] = LOBYTE((short)p_nTmplNo);
        w_abyData[1] = HIBYTE((short)p_nTmplNo);
        System.arraycopy(w_abyNoteBuf, 0, w_abyData, 2, w_abyNoteBuf.length);

        InitCmdDataPacket(CMD_SET_ID_NOTE, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, ID_NOTE_SIZE+2);

        //. Send data packet to target
        w_bRet = Send_DataPacket(CMD_SET_ID_NOTE);

        if (w_bRet == false)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_GetIDNote(int p_nTmplNo, String[] p_pstrNote)
    {
        boolean		w_bRet = false;
        byte[]		w_abyData = new byte[2];
        String w_strTmp;

        //. Assemble command packet
        w_abyData[0] = LOBYTE((short)p_nTmplNo);
        w_abyData[1] = HIBYTE((short)p_nTmplNo);
        InitCmdPacket(CMD_GET_ID_NOTE, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 2);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_GET_ID_NOTE);

        if(w_bRet == false)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        w_bRet = Receive_DataPacket(CMD_GET_ID_NOTE);

        if (w_bRet == false)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        //memset(m_abyPacket2, (byte)0, ID_NOTE_SIZE+1);
        memset(m_abyPacket2, (byte)0, 512);
        System.arraycopy(m_abyPacket, RCM_DATA_OFFSET, m_abyPacket2, 0, ID_NOTE_SIZE);

        w_strTmp = new String(m_abyPacket2);
        p_pstrNote[0] = w_strTmp;

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_SetModuleSN(String p_pstrModuleSN)
    {
        boolean	w_bRet = false;
        byte[]	w_abyData = p_pstrModuleSN.getBytes();
        byte[]	w_abyModuleSN = new byte[MODULE_SN_LEN];
        byte[]	w_abyData2 = new byte[2];

        memset(w_abyModuleSN, (byte)0, MODULE_SN_LEN);
        System.arraycopy(w_abyData, 0, w_abyModuleSN, 0, w_abyData.length);

        //. Assemble command packet
        w_abyData2[0] = LOBYTE((short)(MODULE_SN_LEN));
        w_abyData2[1] = HIBYTE((short)(MODULE_SN_LEN));

        InitCmdPacket(CMD_SET_MODULE_SN, m_bySrcDeviceID, m_byDstDeviceID, w_abyData2, 2);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_SET_MODULE_SN);

        if(w_bRet == false)
            return ERR_CONNECTION;

        if( GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        //. Assemble data packet
        InitCmdDataPacket(CMD_SET_MODULE_SN, m_bySrcDeviceID, m_byDstDeviceID, w_abyModuleSN, MODULE_SN_LEN);

        //. Send data packet to target
        w_bRet = Send_DataPacket(CMD_SET_MODULE_SN);

        if (w_bRet == false)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_GetModuleSN(String[] p_pstrModuleSN)
    {
        boolean		w_bRet = false;
        String w_strTmp;

        //. Assemble command packet
        InitCmdPacket(CMD_GET_MODULE_SN, m_bySrcDeviceID, m_byDstDeviceID, m_abyPacket2, 0);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_GET_MODULE_SN);

        if(w_bRet == false)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        w_bRet = Receive_DataPacket(CMD_GET_MODULE_SN);

        if (w_bRet == false)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        //memset(m_abyPacket2, (byte)0, MODULE_SN_LEN+1);
        memset(m_abyPacket2, (byte)0, 512);
        System.arraycopy(m_abyPacket, RCM_DATA_OFFSET, m_abyPacket2, 0, MODULE_SN_LEN);

        w_strTmp = new String(m_abyPacket2);
        p_pstrModuleSN[0] = w_strTmp;

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_GetImage()
    {
        boolean	w_bRet;

        InitCmdPacket(CMD_GET_IMAGE, m_bySrcDeviceID, m_byDstDeviceID, m_abyPacket2, 0);

        w_bRet = Send_Command(CMD_GET_IMAGE);

        if(!w_bRet)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_FingerDetect(int[] p_pnDetectResult)
    {
        boolean	w_bRet;

        InitCmdPacket(CMD_FINGER_DETECT, m_bySrcDeviceID, m_byDstDeviceID, m_abyPacket2, 0);

        w_bRet = Send_Command(CMD_FINGER_DETECT);

        if (!w_bRet)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        p_pnDetectResult[0] = m_abyPacket[RCM_DATA_OFFSET];

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_UpImage(int p_nType, byte[] p_pFpData, int[] p_pnImgWidth, int[] p_pnImgHeight)
    {
        int		i, n, r, w, h, size;
        boolean	w_bRet;
        byte[]	w_abyData = new byte[1];

        w_abyData[0] = (byte)p_nType;

        InitCmdPacket(CMD_UP_IMAGE, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 1);

        w_bRet = Send_Command(CMD_UP_IMAGE);

        if (!w_bRet)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        w = MAKEWORD(m_abyPacket[RCM_DATA_OFFSET+0], m_abyPacket[RCM_DATA_OFFSET+1]);
        h = MAKEWORD(m_abyPacket[RCM_DATA_OFFSET+2], m_abyPacket[RCM_DATA_OFFSET+3]);

        size = w * h;
        n = size / IMAGE_DATA_UNIT;
        r = size % IMAGE_DATA_UNIT;

        if (m_nConnected == 1) {
            for (i=0; i<n; i++)
            {
                w_bRet = Receive_DataPacket(CMD_UP_IMAGE);
                if(w_bRet == false)
                    return ERR_CONNECTION;

                if (GetRetCode() != ERR_SUCCESS)
                    return GetRetCode();

                System.arraycopy(m_abyPacket, RCM_DATA_OFFSET + 2, p_pFpData, i * IMAGE_DATA_UNIT, IMAGE_DATA_UNIT);
            }

            if (r > 0)
            {
                w_bRet = Receive_DataPacket(CMD_UP_IMAGE);
                if(w_bRet == false)
                    return ERR_CONNECTION;

                if (GetRetCode() != ERR_SUCCESS)
                    return GetRetCode();

                System.arraycopy(m_abyPacket, RCM_DATA_OFFSET + 2, p_pFpData, i * IMAGE_DATA_UNIT, r);
            }
        }
        else if (m_nConnected == 2) {
            w_bRet = USB_ReceiveImage(p_pFpData, w * h);
            if (w_bRet == false)
                return ERR_CONNECTION;
        }
        else {
            return ERR_CONNECTION;
        }

        p_pnImgWidth[0] = w;
        p_pnImgHeight[0] = h;

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_DownImage(byte[] p_pData, int p_nWidth, int p_nHeight)
    {
    	/*
    	int		i, n, r, w, h;
    	boolean	w_bRet;
    	BYTE	w_abyData[840];

    	w = p_nWidth;
    	h = p_nHeight;

    	w_abyData[0] = LOBYTE(p_nWidth);
    	w_abyData[1] = HIBYTE(p_nWidth);
    	w_abyData[2] = LOBYTE(p_nHeight);
    	w_abyData[3] = HIBYTE(p_nHeight);

    	//. Assemble command packet
    	InitCmdPacket(CMD_DOWN_IMAGE, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);

    	Send_Command(CMD_DOWN_IMAGE, w_bRet, m_bySrcDeviceID, m_byDstDeviceID);

    	if(!w_bRet)
    		return ERR_CONNECTION;

    	if (GetRetCode() != ERR_SUCCESS)
    		return GetRetCode();

    	n = (w*h)/DOWN_IMAGE_DATA_UINT;
    	r = (w*h)%DOWN_IMAGE_DATA_UINT;

		w_bRet = USB_DownImage(m_hUsbHandle, p_pData, p_nWidth*p_nHeight);

		if(w_bRet == false)
			return ERR_CONNECTION;
		*/
        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_SLEDControl(int p_nState)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[2];

        w_abyData[0] = LOBYTE((short)p_nState);
        w_abyData[1] = HIBYTE((short)p_nState);

        InitCmdPacket(CMD_SLED_CTRL, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 2);

        w_bRet = Send_Command(CMD_SLED_CTRL);

        if(!w_bRet)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_StoreChar(int p_nTmplNo, int p_nRamBufferID, int[] p_pnDupTmplNo)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[4];

        w_abyData[0] = LOBYTE((short)p_nTmplNo);
        w_abyData[1] = HIBYTE((short)p_nTmplNo);
        w_abyData[2] = LOBYTE((short)p_nRamBufferID);
        w_abyData[3] = HIBYTE((short)p_nRamBufferID);

        InitCmdPacket(CMD_STORE_CHAR, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_STORE_CHAR);

        if (!w_bRet)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
        {
            if (GetRetCode() == ERR_DUPLICATION_ID)
                p_pnDupTmplNo[0] = MAKEWORD(m_abyPacket[RCM_DATA_OFFSET+0], m_abyPacket[RCM_DATA_OFFSET+1]);

            return GetRetCode();
        }

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_LoadChar(int p_nTmplNo, int p_nRamBufferID)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[4];

        w_abyData[0] = LOBYTE((short)p_nTmplNo);
        w_abyData[1] = HIBYTE((short)p_nTmplNo);
        w_abyData[2] = LOBYTE((short)p_nRamBufferID);
        w_abyData[3] = HIBYTE((short)p_nRamBufferID);

        InitCmdPacket(CMD_LOAD_CHAR, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_LOAD_CHAR);

        if (!w_bRet)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_UpChar(int p_nRamBufferID, byte[] p_pbyTemplate)
    {
        boolean		w_bRet = false;
        byte[]		w_abyData = new byte[2];

        //. Assemble command packet
        w_abyData[0] = LOBYTE((short)p_nRamBufferID);
        w_abyData[1] = HIBYTE((short)p_nRamBufferID);
        InitCmdPacket(CMD_UP_CHAR, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 2);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_UP_CHAR);

        if(w_bRet == false)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        w_bRet = Receive_DataPacket(CMD_UP_CHAR);

        if (w_bRet == false)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        //memcpy(p_pbyTemplate, &g_pRcmPacket->m_abyData[0], GD_RECORD_SIZE);
        System.arraycopy(m_abyPacket, RCM_DATA_OFFSET, p_pbyTemplate, 0, MAX_DATA_LEN);

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_DownChar(int p_nRamBufferID, byte[] p_pbyTemplate)
    {
        boolean	w_bRet = false;
        byte[]	w_abyData = new byte[MAX_DATA_LEN+2];
        byte[]	w_abyData2 = new byte[2];

        //. Assemble command packet
        w_abyData2[0] = LOBYTE((short)(MAX_DATA_LEN + 2));
        w_abyData2[1] = HIBYTE((short)(MAX_DATA_LEN + 2));

        InitCmdPacket(CMD_DOWN_CHAR, m_bySrcDeviceID, m_byDstDeviceID, w_abyData2, 2);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_DOWN_CHAR);

        if(w_bRet == false)
            return ERR_CONNECTION;

        if( GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        SystemClock.sleep(10);

        //. Assemble data packet
        w_abyData[0] = LOBYTE((short)p_nRamBufferID);
        w_abyData[1] = HIBYTE((short)p_nRamBufferID);
        //memcpy(&w_abyData[2], p_pbyTemplate, GD_RECORD_SIZE);
        System.arraycopy(p_pbyTemplate, 0, w_abyData, 2, MAX_DATA_LEN);

        InitCmdDataPacket(CMD_DOWN_CHAR, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, MAX_DATA_LEN+2);

        //. Send data packet to target
        w_bRet = Send_DataPacket(CMD_DOWN_CHAR);

        if (w_bRet == false)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    public int Run_DelChar(int p_nSTmplNo, int p_nETmplNo)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[4];

        w_abyData[0] = LOBYTE((short)p_nSTmplNo);
        w_abyData[1] = HIBYTE((short)p_nSTmplNo);
        w_abyData[2] = LOBYTE((short)p_nETmplNo);
        w_abyData[3] = HIBYTE((short)p_nETmplNo);

        //. Assemble command packet
        InitCmdPacket(CMD_DEL_CHAR, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);

        w_bRet = Send_Command(CMD_DEL_CHAR);

        if(!w_bRet)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_GetEmptyID(int p_nSTmplNo, int p_nETmplNo, int[] p_pnEmptyID)
    {
        boolean	w_bRet;
        byte[]	w_abyData =  new byte[4];

        w_abyData[0] = LOBYTE((short)p_nSTmplNo);
        w_abyData[1] = HIBYTE((short)p_nSTmplNo);
        w_abyData[2] = LOBYTE((short)p_nETmplNo);
        w_abyData[3] = HIBYTE((short)p_nETmplNo);

        //. Assemble command packet
        InitCmdPacket(CMD_GET_EMPTY_ID, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);

        w_bRet = Send_Command(CMD_GET_EMPTY_ID);

        if(!w_bRet)
            return ERR_CONNECTION;

        if ( GetRetCode() != ERR_SUCCESS )
            return GetRetCode();

        p_pnEmptyID[0] = MAKEWORD(m_abyPacket[RCM_DATA_OFFSET+0], m_abyPacket[RCM_DATA_OFFSET+1]);

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_GetStatus(int p_nTmplNo, int[] p_pnStatus)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[2];

        w_abyData[0] = LOBYTE((short)p_nTmplNo);
        w_abyData[1] = HIBYTE((short)p_nTmplNo);

        InitCmdPacket(CMD_GET_STATUS, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 2);

        w_bRet = Send_Command(CMD_GET_STATUS);

        if(!w_bRet)
            return ERR_CONNECTION;

        if ( GetRetCode() != ERR_SUCCESS )
            return GetRetCode();

        p_pnStatus[0] = m_abyPacket[RCM_DATA_OFFSET+0];

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_GetBrokenID(int p_nSTmplNo, int p_nETmplNo, int[] p_pnCount, int[] p_pnFirstID)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[4];

        w_abyData[0] = LOBYTE((short)p_nSTmplNo);
        w_abyData[1] = HIBYTE((short)p_nSTmplNo);
        w_abyData[2] = LOBYTE((short)p_nETmplNo);
        w_abyData[3] = HIBYTE((short)p_nETmplNo);

        //. Assemble command packet
        InitCmdPacket(CMD_GET_BROKEN_ID, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);

        w_bRet = Send_Command(CMD_GET_BROKEN_ID);

        if(!w_bRet)
            return ERR_CONNECTION;

        if (GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        p_pnCount[0]		= MAKEWORD(m_abyPacket[RCM_DATA_OFFSET+0], m_abyPacket[RCM_DATA_OFFSET+1]);
        p_pnFirstID[0]		= MAKEWORD(m_abyPacket[RCM_DATA_OFFSET+2], m_abyPacket[RCM_DATA_OFFSET+3]);

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_GetEnrollCount(int p_nSTmplNo, int p_nETmplNo, int[] p_pnEnrollCount)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[4];

        w_abyData[0] = LOBYTE((short)p_nSTmplNo);
        w_abyData[1] = HIBYTE((short)p_nSTmplNo);
        w_abyData[2] = LOBYTE((short)p_nETmplNo);
        w_abyData[3] = HIBYTE((short)p_nETmplNo);

        //. Assemble command packet
        InitCmdPacket(CMD_GET_ENROLL_COUNT, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);

        w_bRet = Send_Command(CMD_GET_ENROLL_COUNT);

        if (!w_bRet)
            return ERR_CONNECTION;

        if(GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        p_pnEnrollCount[0] = MAKEWORD(m_abyPacket[RCM_DATA_OFFSET+0], m_abyPacket[RCM_DATA_OFFSET+1]);

        return ERR_SUCCESS;
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_Generate(int p_nRamBufferID)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[2];

        w_abyData[0] = LOBYTE((short)p_nRamBufferID);
        w_abyData[1] = HIBYTE((short)p_nRamBufferID);

        InitCmdPacket(CMD_GENERATE, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 2);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_GENERATE);

        if(!w_bRet)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_Merge(int p_nRamBufferID, int p_nMergeCount)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[3];

        w_abyData[0] = LOBYTE((short)p_nRamBufferID);
        w_abyData[1] = HIBYTE((short)p_nRamBufferID);
        w_abyData[2] = (byte)p_nMergeCount;

        InitCmdPacket(CMD_MERGE, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 3);

        w_bRet = Send_Command(CMD_MERGE);

        if (!w_bRet)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    int	Run_Match(int p_nRamBufferID0, int p_nRamBufferID1)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[4];

        w_abyData[0] = LOBYTE((short)p_nRamBufferID0);
        w_abyData[1] = HIBYTE((short)p_nRamBufferID0);
        w_abyData[2] = LOBYTE((short)p_nRamBufferID1);
        w_abyData[3] = HIBYTE((short)p_nRamBufferID1);

        InitCmdPacket(CMD_MATCH, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);

        w_bRet = Send_Command(CMD_MATCH);

        if (!w_bRet)
            return ERR_CONNECTION;

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_Search(int p_nRamBufferID, int p_nStartID, int p_nSearchCount, int[] p_pnTmplNo, int[] p_pnLearnResult)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[6];

        w_abyData[0] = LOBYTE((short)p_nRamBufferID);
        w_abyData[1] = HIBYTE((short)p_nRamBufferID);
        w_abyData[2] = LOBYTE((short)p_nStartID);
        w_abyData[3] = HIBYTE((short)p_nStartID);
        w_abyData[4] = LOBYTE((short)p_nSearchCount);
        w_abyData[5] = HIBYTE((short)p_nSearchCount);

        InitCmdPacket(CMD_SEARCH, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 6);

        //. Send command packet to target
        w_bRet = Send_Command(CMD_SEARCH);

        if (!w_bRet)
            return ERR_CONNECTION;

        if(GetRetCode() != ERR_SUCCESS)
            return GetRetCode();

        p_pnTmplNo[0]		= MAKEWORD(m_abyPacket[RCM_DATA_OFFSET+0], m_abyPacket[RCM_DATA_OFFSET+1]);
        p_pnLearnResult[0]	= m_abyPacket[RCM_DATA_OFFSET+2];

        return GetRetCode();
    }
    /************************************************************************/
    /************************************************************************/
    public int	Run_Verify(int p_nTmplNo, int p_nRamBufferID, int[] p_pnLearnResult)
    {
        boolean	w_bRet;
        byte[]	w_abyData = new byte[4];

        w_abyData[0] = LOBYTE((short)p_nTmplNo);
        w_abyData[1] = HIBYTE((short)p_nTmplNo);
        w_abyData[2] = LOBYTE((short)p_nRamBufferID);
        w_abyData[3] = HIBYTE((short)p_nRamBufferID);

        //. Assemble command packet
        InitCmdPacket(CMD_VERIFY, m_bySrcDeviceID, m_byDstDeviceID, w_abyData, 4);

        w_bRet = Send_Command(CMD_VERIFY);

        if (!w_bRet)
            return ERR_CONNECTION;

        p_pnLearnResult[0] = m_abyPacket[RCM_DATA_OFFSET+2];

        return GetRetCode();
    }

    public  boolean GetDeviceInformation(String[] deviceInfo)
    {
        int[]  w_nRecvLen = new int[1];
        byte[] w_abyPCCmd = new byte[6];
        byte[] w_abyData = new byte[32];

        String w_strTmp;
        boolean w_bRet;

        Arrays.fill(w_abyPCCmd, (byte) 0);

        w_abyPCCmd[2] = 0x04;

        w_bRet = SendPackage(w_abyPCCmd, w_abyData);

        //Toast.makeText(mApplicationContext, "GetDeviceInformation, SendPackage ret = " + w_bRet, Toast.LENGTH_SHORT).show();

        if (!w_bRet)
        {
            return  false;
        }

        w_bRet = RecvPackage(w_abyData, w_nRecvLen);

        //Toast.makeText(mApplicationContext, "GetDeviceInformation, RecvPackage : " + w_bRet, Toast.LENGTH_SHORT).show();

        if (!w_bRet)
        {
            return  false;
        }

        w_strTmp = new String(w_abyData);
        deviceInfo[0] = w_strTmp;

        //Toast.makeText(mApplicationContext, "GetDeviceInformation, Recv Data : " + w_strTmp, Toast.LENGTH_SHORT).show();

        return true;
    }



    private  boolean SendPackage(byte[] pPCCmd, byte[] pData)
    {
        int    nDataLen;

        pPCCmd[0] = (byte)0xEF;
        pPCCmd[1] = 0x01;

        nDataLen = (int)((int)((pPCCmd[5] << 8) & 0x0000FF00) | (int)(pPCCmd[4] & 0x000000FF));

        return m_usbBase.UsbSCSIWrite(pPCCmd, 6, pData, nDataLen, 5000);
    }

    private  boolean RecvPackage(byte[] pData, int[] pLevRen)
    {
        int    w_nLen;
        byte[] w_abyPCCmd = new byte[6];
        byte[] w_abyRespond = new byte[4];
        boolean w_bRet;

        w_abyPCCmd[0] = (byte)0xEF;
        w_abyPCCmd[1] = 0x02;
        w_abyPCCmd[2] = 0;
        w_abyPCCmd[3] = 0;
        w_abyPCCmd[4] = 0;
        w_abyPCCmd[5] = 0;

        // receive status
        w_bRet = m_usbBase.UsbSCSIRead(w_abyPCCmd, 6, w_abyRespond, 4, 5000);

        if (!w_bRet)
            return false;

        // receive data
        //w_nLen = (int)((w_abyRespond[3] << 8) | w_abyRespond[2]);
        w_nLen = (int)((int)((w_abyRespond[3] << 8) & 0x0000FF00) | (int)(w_abyRespond[2] & 0x000000FF));

        if (w_nLen > 0)
        {
            //w_nTime = SystemClock.elapsedRealtime();

            w_abyPCCmd[1] = 0x03;
            w_bRet = m_usbBase.UsbSCSIRead(w_abyPCCmd, 6, pData, w_nLen, 5000);

            //w_nTime = SystemClock.elapsedRealtime() - w_nTime;

            if (!w_bRet)
                return false;

            pLevRen[0] = w_nLen;
        }

        return  true;
    }

    /***************************************************************************
     * Get Return Code
     ***************************************************************************/
    private short GetRetCode()
    {
        return (short)((int)((m_abyPacket[9] << 8) & 0x0000FF00) | (int)(m_abyPacket[8] & 0x000000FF));
    }

    /***************************************************************************
     * Get Data Length
     ***************************************************************************/
    private short GetDataLen()
    {
        return (short)(((m_abyPacket[7] << 8) & 0x0000FF00) | (m_abyPacket[6] & 0x000000FF));
    }

    /***************************************************************************
     * Set Data Length
     ***************************************************************************/
    private void SetDataLen(short p_wDataLen)
    {
        m_abyPacket[6] = (byte)(p_wDataLen & 0xFF);
        m_abyPacket[7] = (byte)(((p_wDataLen & 0xFF00) >> 8) & 0xFF);
    }

    /***************************************************************************
     * Set Command Data
     ***************************************************************************/
    private void SetCmdData(short p_wData, boolean p_bFirst)
    {
        if (p_bFirst)
        {
            m_abyPacket[8] = (byte)(p_wData & 0xFF);
            m_abyPacket[9] = (byte)(((p_wData & 0xFF00) >> 8) & 0xFF);
        }
        else
        {
            m_abyPacket[10] = (byte)(p_wData & 0xFF);
            m_abyPacket[11] = (byte)(((p_wData & 0xFF00) >> 8) & 0xFF);
        }
    }

    /***************************************************************************
     * Get Command Data
     ***************************************************************************/
    private short GetCmdData(boolean p_bFirst)
    {
        if (p_bFirst)
        {
            return (short)(((m_abyPacket[9] << 8) & 0x0000FF00) | (m_abyPacket[8] & 0x000000FF));
        }
        else
        {
            return (short)(((m_abyPacket[11] << 8) & 0x0000FF00) | (m_abyPacket[10] & 0x000000FF));
        }
    }

    /***************************************************************************
     * Get 2bytes packet checksum(pDataPkt[0] + pDataPkt[1] + ....)
     ***************************************************************************/
    private short CalcChkSumOfPkt(byte[] pDataPkt, int nSize)
    {
        int     i, nChkSum = 0;

        for(i=0;i<nSize;i++)
        {
            if ((int)pDataPkt[i] < 0)
                nChkSum = nChkSum + ((int)pDataPkt[i] + 256);
            else
                nChkSum = nChkSum + pDataPkt[i];
        }

        return (short)nChkSum;
    }

    /***************************************************************************
     * Make Command Packet
     ***************************************************************************/
    void InitCmdPacket(short wCMDCode, byte bySrcDeviceID, byte byDstDeviceID, byte[] pbyData, int nDataLen)
    {
        short	w_wCheckSum;

        memset(m_abyPacket, (byte)0, CMD_PACKET_LEN);

        //g_pCmdPacket->m_wPrefix = CMD_PREFIX_CODE;
        m_abyPacket[0] = (byte)(CMD_PREFIX_CODE & 0xFF);
        m_abyPacket[1] = (byte)((CMD_PREFIX_CODE >> 8) & 0xFF);

        //g_pCmdPacket->m_bySrcDeviceID = p_bySrcDeviceID;
        m_abyPacket[2] = bySrcDeviceID;

        //g_pCmdPacket->m_byDstDeviceID = p_byDstDeviceID;
        m_abyPacket[3] = byDstDeviceID;

        //g_pCmdPacket->m_wCMDCode = p_wCMDCode;
        m_abyPacket[4] = (byte)(wCMDCode & 0xFF);
        m_abyPacket[5] = (byte)((wCMDCode >> 8) & 0xFF);

        //g_pCmdPacket->m_wDataLen = p_wDataLen;
        m_abyPacket[6] = (byte)(nDataLen & 0xFF);
        m_abyPacket[7] = (byte)((nDataLen >> 8) & 0xFF);

        if (nDataLen > 0)
            //memcpy(g_pCmdPacket->m_abyData, p_pbyData, wDataLen);
            System.arraycopy(pbyData, 0, m_abyPacket, 8, nDataLen);

        w_wCheckSum = CalcChkSumOfPkt(m_abyPacket, CMD_PACKET_LEN-2);

        //g_pCmdPacket->m_wCheckSum = w_wCheckSum;
        m_abyPacket[24] = (byte)(w_wCheckSum & 0xFF);
        m_abyPacket[25] = (byte)((w_wCheckSum >> 8) & 0xFF);

        m_nPacketSize = CMD_PACKET_LEN;
    }
    /***************************************************************************
     * Make Data Packet
     ***************************************************************************/
    void	InitCmdDataPacket(short wCMDCode, byte bySrcDeviceID, byte byDstDeviceID, byte[] pbyData, int nDataLen)
    {
        short	w_wCheckSum;

        //g_pCmdPacket->m_wPrefix = CMD_DATA_PREFIX_CODE;
        m_abyPacket[0] = (byte)(CMD_DATA_PREFIX_CODE & 0xFF);
        m_abyPacket[1] = (byte)((CMD_DATA_PREFIX_CODE >> 8) & 0xFF);

        //g_pCmdPacket->m_bySrcDeviceID = p_bySrcDeviceID;
        m_abyPacket[2] = bySrcDeviceID;

        //g_pCmdPacket->m_byDstDeviceID = p_byDstDeviceID;
        m_abyPacket[3] = byDstDeviceID;

        //g_pCmdPacket->m_wCMDCode = p_wCMDCode;
        m_abyPacket[4] = (byte)(wCMDCode & 0xFF);
        m_abyPacket[5] = (byte)((wCMDCode >> 8) & 0xFF);

        //g_pCmdPacket->m_wDataLen = p_wDataLen;
        m_abyPacket[6] = (byte)(nDataLen & 0xFF);
        m_abyPacket[7] = (byte)((nDataLen >> 8) & 0xFF);

        //memcpy(&g_pCmdPacket->m_abyData[0], p_pbyData, p_wDataLen);
        System.arraycopy(pbyData, 0, m_abyPacket, 8, nDataLen);

        //. Set checksum
        w_wCheckSum = CalcChkSumOfPkt(m_abyPacket, nDataLen + 8);

        m_abyPacket[nDataLen+8] = (byte)(w_wCheckSum & 0xFF);
        m_abyPacket[nDataLen+9] = (byte)((w_wCheckSum >> 8) & 0xFF);

        m_nPacketSize = nDataLen + 10;
    }
    /***************************************************************************
     * Check Packet
     ***************************************************************************/
    boolean CheckReceive( byte[] pbyPacket, int nPacketLen, short wPrefix, short wCMDCode )
    {
        short			w_wCalcCheckSum, w_wCheckSum, w_wTmp;

        //. Check prefix code
        w_wTmp = (short)((int)((pbyPacket[1] << 8) & 0x0000FF00) | (int)(pbyPacket[0] & 0x000000FF));

        if (wPrefix != w_wTmp)
        {
            return false;
        }

        //. Check checksum
        w_wCheckSum = (short)((int)((pbyPacket[nPacketLen-1] << 8) & 0x0000FF00) | (int)(pbyPacket[nPacketLen-2] & 0x000000FF));

        w_wCalcCheckSum = CalcChkSumOfPkt(pbyPacket, nPacketLen-2);

        if (w_wCheckSum != w_wCalcCheckSum)
        {
            return false;
        }

        //. Check Command Code
        w_wTmp = (short)((int)((pbyPacket[5] << 8) & 0x0000FF00) | (int)(pbyPacket[4] & 0x000000FF));
        if (wCMDCode != w_wTmp)
        {
            return false;
        }

        return true;
    }

    //--------------------------- Send, Receive Communication Packet Functions ---------------------//
    public boolean Send_Command(short p_wCmd)
    {
        if (m_nConnected == 1)
            return UART_SendCommand(p_wCmd);
        else if (m_nConnected == 2)
            return USB_SendPacket(p_wCmd);
        else
            return false;
    }

    public boolean Send_DataPacket(short p_wCmd)
    {
        if (m_nConnected == 1)
            return UART_SendDataPacket(p_wCmd);
        else if (m_nConnected == 2)
            return USB_SendDataPacket(p_wCmd);
        else
            return false;
    }

    public boolean Receive_DataPacket(short p_wCmd)
    {
        if (m_nConnected == 1)
            return UART_ReceiveDataPacket(p_wCmd);
        else if (m_nConnected == 2)
            return USB_ReceiveDataPacket(p_wCmd);
        else
            return false;
    }

    //-------------------------------- USB Communication Functions --------------------------------//
    private  boolean USB_SendPacket(short wCMD)
    {
        byte[]	btCDB = new byte[8];
        boolean	w_bRet;

        Arrays.fill(btCDB, (byte)0);

        btCDB[0] = (byte)0xEF; btCDB[1] = 0x11; btCDB[4] = (byte)m_nPacketSize;

        w_bRet = m_usbBase.UsbSCSIWrite(btCDB, 8, m_abyPacket, m_nPacketSize, SCSI_TIMEOUT);

        if (!w_bRet)
        {
            return false;
        }

        return USB_ReceiveAck( wCMD );
    }

    private boolean USB_ReceiveAck(short wCMD)
    {
        int		c, w_nLen, w_nReadCount = 0;
        byte[]	btCDB = new byte[8];
        byte[]	w_abyWaitPacket = new byte[CMD_PACKET_LEN];

        Arrays.fill(btCDB, (byte)0);

        //w_nReadCount = GetReadWaitTime(p_byCMD);

        c = 0;
        Arrays.fill(w_abyWaitPacket, (byte)0xAF);

        do
        {
            Arrays.fill(m_abyPacket, (byte)0);

            btCDB[0] = (byte)0xEF; btCDB[1] = (byte)0x12;

            w_nLen = RCM_PACKET_LEN;

            if (!m_usbBase.UsbSCSIRead(btCDB, 8, m_abyPacket, w_nLen, SCSI_TIMEOUT))
            {
                return false;
            }

            SystemClock.sleep(COMM_SLEEP_TIME);

            c++;

            //if ( c > w_nReadCount)
            //{
            //	return false;
            //}
        } while ( memcmp(m_abyPacket, w_abyWaitPacket, CMD_PACKET_LEN) == true );

        m_nPacketSize = w_nLen;

        if (!CheckReceive(m_abyPacket, m_nPacketSize, (short)RCM_PREFIX_CODE, wCMD ))
            return false;

        return true;
    }

    boolean USB_ReceiveDataAck(short wCMD)
    {
        byte[]	btCDB = new byte[8];
        byte[]	w_WaitPacket = new byte[10];
        int		w_nLen;

        memset(btCDB, (byte)0, 8);
        memset(w_WaitPacket, (byte)0xAF, 10);

        do
        {
            btCDB[0] = (byte)0xEF; btCDB[1] = 0x15;
            w_nLen = 8;

            if (!m_usbBase.UsbSCSIRead(btCDB, 8, m_abyPacket, w_nLen, SCSI_TIMEOUT))
            {
                return false;
            }

            SystemClock.sleep(COMM_SLEEP_TIME);

        }while(memcmp(m_abyPacket, w_WaitPacket, 8) == true);

        //w_nLen = g_pRcmPacket->m_wDataLen + 2;
        w_nLen = (short)((int)((m_abyPacket[7] << 8) & 0x0000FF00) | (int)(m_abyPacket[6] & 0x000000FF)) + 2;

        if (USB_ReceiveRawData(m_abyPacket2, w_nLen) == false)
        {
            return false;
        }

        System.arraycopy(m_abyPacket2, 0, m_abyPacket, 8, w_nLen);

        m_nPacketSize = 8 + w_nLen;

        if (!CheckReceive(m_abyPacket, m_nPacketSize, (short)RCM_DATA_PREFIX_CODE, wCMD ))
        {
            return false;
        }

        return true;
    }

    boolean USB_SendDataPacket(short wCMD)
    {
        byte[]	btCDB = new byte[8];

        memset(btCDB, (byte)0, 8);

        btCDB[0] = (byte)0xEF; btCDB[1] = 0x13;

        btCDB[4] = (byte)(m_nPacketSize & 0xFF);
        btCDB[5] = (byte)((m_nPacketSize >> 8) & 0xFF);

        if (!m_usbBase.UsbSCSIWrite(btCDB, 8, m_abyPacket, m_nPacketSize, SCSI_TIMEOUT ) )
            return false;

        return USB_ReceiveDataAck(wCMD);
    }

    boolean USB_ReceiveDataPacket(short wCMD)
    {
        return USB_ReceiveDataAck(wCMD);
    }

    boolean USB_ReceiveRawData(byte[] pBuffer, int nDataLen)
    {
        byte[] btCDB = new byte[8];

        memset(btCDB, (byte)0, 8);

        btCDB[0] = (byte)0xEF; btCDB[1] = 0x14;

        if (!m_usbBase.UsbSCSIRead(btCDB, 8, pBuffer, nDataLen, SCSI_TIMEOUT ) )
            return false;

        return true;
    }

    boolean USB_ReceiveImage(byte[] p_pBuffer, int nDataLen )
    {
        byte[]	btCDB = new byte[8];
        byte[]	w_WaitPacket = new byte[8];

        memset( btCDB, (byte)0, 8 );
        memset( w_WaitPacket, (byte)0xAF, 8 );

        if (nDataLen < 1024*64)
        {
            btCDB[0] = (byte)0xEF; btCDB[1] = 0x16;

            if (!m_usbBase.UsbSCSIRead(btCDB, 8, p_pBuffer, nDataLen, SCSI_TIMEOUT ))
                return false;
        }
        else if (nDataLen == 256*288)
        {
            btCDB[0] = (byte)0xEF; btCDB[1] = 0x16; btCDB[2] = 0x00;

            if (!m_usbBase.UsbSCSIRead(btCDB, 8, p_pBuffer, nDataLen/2, SCSI_TIMEOUT))
                return false;

            btCDB[0] = (byte)0xEF; btCDB[1] = 0x16; btCDB[2] = 0x01;

            if (!m_usbBase.UsbSCSIRead(btCDB, 8, m_abyPacket2, nDataLen/2, SCSI_TIMEOUT ))
                return false;

            System.arraycopy(m_abyPacket2, 0, p_pBuffer, nDataLen/2, nDataLen/2);
        }

        return true;
    }

    //-------------------------------- UART Communication Functions --------------------------------//
    public boolean UART_SendCommand(short p_wCmd)
    {
        int	w_nResult = 0;

        if (m_nConnected == 1)
        {
            byte[] w_pData = new byte[m_nPacketSize];
            System.arraycopy(m_abyPacket, 0, w_pData, 0, m_nPacketSize);
            m_SerialPort.send(w_pData, m_nPacketSize);
        }

        return UART_ReceiveAck(p_wCmd);
    }

    public boolean UART_ReceiveAck(short p_wCmd)
    {
        if (!UART_ReadDataN(m_abyPacket, 0, CMD_PACKET_LEN))
            return false;

        if (!CheckReceive(m_abyPacket, CMD_PACKET_LEN, (short)RCM_PREFIX_CODE, p_wCmd))
            return false;

        return true;
    }

    public boolean UART_ReceiveDataAck(short p_wCmd)
    {
        if (!UART_ReadDataN(m_abyPacket, 0, 8))
            return false;

        if (!UART_ReadDataN(m_abyPacket, 8, GetDataLen() + 2))
            return false;

        return CheckReceive(m_abyPacket, GetDataLen() + 10, (short)RCM_DATA_PREFIX_CODE, p_wCmd);
    }

    public boolean UART_SendDataPacket(short p_wCmd)
    {
        int		w_nSendCnt = 0;

        if (m_nConnected == 1)
        {
            byte[] w_pData = new byte[m_nPacketSize];
            System.arraycopy(m_abyPacket, 0, w_pData, 0, m_nPacketSize);
            m_SerialPort.send(w_pData, m_nPacketSize);
        }

        return UART_ReceiveDataAck(p_wCmd);
    }
    /***************************************************************************/
    /***************************************************************************/
    public boolean UART_ReceiveDataPacket(short p_wCmd)
    {
        return UART_ReceiveDataAck(p_wCmd);
    }
    /***************************************************************************/
    /***************************************************************************/
    public boolean UART_ReceiveData(short p_wCmd, int p_nDataLen, byte[] p_pBuffer)
    {
        int		w_nReceivedCnt;
        int		w_wPacketDataLen = 0;

        for (w_nReceivedCnt = 0; w_nReceivedCnt < p_nDataLen; w_nReceivedCnt += w_wPacketDataLen)
        {
            w_wPacketDataLen = p_nDataLen - w_nReceivedCnt;
            if (w_wPacketDataLen > MAX_DATA_LEN) w_wPacketDataLen = MAX_DATA_LEN;
            if (UART_ReceiveDataPacket(p_wCmd) == false)
                return false;
            System.arraycopy(m_abyPacket, 8, p_pBuffer, w_nReceivedCnt, GetDataLen() + 4);
        }
        return true;
    }
    /***************************************************************************/
    /***************************************************************************/
    boolean UART_ReadDataN(byte[] p_pData, int p_nStart, int p_nLen)
    {
//    	int		w_nAckCnt = 0;
        int		w_nRecvLen, w_nTotalRecvLen;
        int 	w_nTmpLen;
        long	w_nTime;
        int     i;

        w_nRecvLen = p_nLen;
        w_nTotalRecvLen = 0;

        w_nTime = System.currentTimeMillis();

        while(w_nTotalRecvLen < p_nLen )
        {
            if (System.currentTimeMillis() - w_nTime > 10000)
            {
                m_nUARTReadLen = 0;
                return false;
            }

            i = 0;
            while (m_bBufferHandle)
            {
                i++;
                if (i < 10000)
                    break;
            }

            m_bBufferHandle = true;

            if (m_nUARTReadLen <= 0) {
                m_bBufferHandle = false;
                continue;
            }

            if (p_nLen - w_nTotalRecvLen < m_nUARTReadLen)
            {
                w_nTmpLen = p_nLen - w_nTotalRecvLen;
                System.arraycopy(m_pUARTReadBuf, 0, p_pData, p_nStart + w_nTotalRecvLen, w_nTmpLen);
                w_nRecvLen = w_nRecvLen - w_nTmpLen;
                w_nTotalRecvLen = w_nTotalRecvLen + w_nTmpLen;
                m_nUARTReadLen = m_nUARTReadLen - w_nTmpLen;
                System.arraycopy(m_pUARTReadBuf, w_nTmpLen, m_abyPacket2, 0, m_nUARTReadLen);
                System.arraycopy(m_abyPacket2, 0, m_pUARTReadBuf, 0, m_nUARTReadLen);
            }
            else
            {
                System.arraycopy(m_pUARTReadBuf, 0, p_pData, p_nStart + w_nTotalRecvLen, m_nUARTReadLen);
                w_nRecvLen = w_nRecvLen - m_nUARTReadLen;
                w_nTotalRecvLen = w_nTotalRecvLen + m_nUARTReadLen;
                m_nUARTReadLen = 0;
            }

            m_bBufferHandle = false;
        }

        return true;
    }

    private boolean memcmp(byte[] p1, byte[] p2, int nLen)
    {
        int		i;

        for (i=0; i<nLen; i++)
        {
            if (p1[i] != p2[i])
                return false;
        }

        return true;
    }

    private void memset(byte[] p1, byte nValue, int nLen)
    {
        Arrays.fill(p1, 0, nLen, nValue);
    }

    private void memcpy(byte[] p1, byte nValue, int nLen)
    {
        Arrays.fill(p1, 0, nLen, nValue);
    }

    private short MAKEWORD(byte low, byte high)
    {
        short s;
        s = (short)((int)((high << 8) & 0x0000FF00) | (int)(low & 0x000000FF));
        return s;
    }

    private byte LOBYTE(short s)
    {
        return (byte)(s & 0xFF);
    }

    private byte HIBYTE(short s)
    {
        return (byte)((s >> 8) & 0xFF);
    }


    // uart control classes ======================================================
    private class SerialControl extends SerialHelper {

        public SerialControl(){
        }

        @Override
        protected void onDataReceived(final ComBean ComRecData)
        {
            DispQueue.AddQueue(ComRecData);
        }
    }

    private class DispQueueThread extends Thread {
        private Queue<ComBean> QueueList = new LinkedList<ComBean>();
        @Override
        public void run() {
            super.run();
            while(!isInterrupted()) {
                int i;
                while(true)
                {
                    final ComBean ComData;
                    if ((ComData=QueueList.poll())==null)
                        break;

                    i = 0;
                    while (m_bBufferHandle)
                    {
                        i++;
                        if (i > 10000)
                            break;
                    }
                    m_bBufferHandle = true;
                    System.arraycopy(ComData.bRec, 0, m_pUARTReadBuf, m_nUARTReadLen, ComData.nSize);
                    m_nUARTReadLen = m_nUARTReadLen + ComData.nSize;
                    m_bBufferHandle = false;
//		        	break;
                }
            }
        }

        public synchronized void AddQueue(ComBean ComData){
            QueueList.add(ComData);
        }
    }
    // ! uart control classes ====================================================
}
