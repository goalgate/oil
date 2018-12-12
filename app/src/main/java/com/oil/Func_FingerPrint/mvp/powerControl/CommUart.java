package com.oil.Func_FingerPrint.mvp.powerControl;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android_serialport_api.SerialPort;

;


public class CommUart implements Comm {

	private String g_uartName = "dev/ttyMT1";
	private int g_baudrate = 57600;
	
	
	private FileOutputStream mOutputStream;
	private FileInputStream mInputStream;
	private SerialPort sp;
	private String TAG = "CommUart";
	 
	/**
	 * @param uartName default to be "dev/ttyMT1".
	 * @param baudrate default to 57600.
	 */
	public CommUart(String uartName, int baudrate) {
		super();
		if((uartName != null) && (!uartName.isEmpty())){
			this.g_uartName = uartName;
		}
		this.g_baudrate = baudrate;
	}

	@Override
	public int open() {
		  try {
//				sp=new SerialPort(new File("/dev/ttyMT1"),115200);
				sp=new SerialPort(new File(g_uartName), g_baudrate, 0);
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					Log.i("SerialPort", "SecurityException");
					e.printStackTrace();
					return -1;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				}   
		  
		  mInputStream=(FileInputStream) sp.getInputStream();
		  mOutputStream=(FileOutputStream) sp.getOutputStream();
		  return 0;
	}

	@Override
	public int close() {
		if(sp!=null){
			try {
				if(mOutputStream != null){
					mOutputStream.close();
				}
				if(mInputStream != null){
					mInputStream.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sp.close();		  
		
			sp = null;
			mOutputStream = null;
			mInputStream = null;
		}
		  
		return 0;
	}

	@Override
	public int write(byte[] byData, int iDataLen, int timeOut) {
		if(iDataLen == 0){
			return 0;
		}
		
		flushUart();
		// TODO Auto-generated method stub
	    try {
			byte[] buffer = new byte[iDataLen];
			System.arraycopy(byData, 0, buffer, 0, iDataLen);
			Log.d(TAG , "-->"+ Ulitily.byteArrayToHexString(buffer));
			for(int i=0; i<buffer.length; i++){
				try {
					Thread.sleep(10);
					
					mOutputStream.write(buffer, i, 1);
					
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			Log.d(TAG , "send error");
			return -1;
		}
	  
		return 0;
	}
	
	
	  
	@Override
	public int read(byte[] byData, int iDataLen, int timeOut) {
		  int size = 0;

		  if(iDataLen == 0){
			return 0;
		}
		// TODO Auto-generated method stub
		try {
			long tick = System.currentTimeMillis();
			while(System.currentTimeMillis() - tick < timeOut){

				size = mInputStream.available();
				if(size > 0){
					Log.d(TAG , "recv len:"+size);
				}
			  if(size < iDataLen){
				  continue;
			  }
			  if(iDataLen == mInputStream.read(byData, 0, iDataLen)){
				  byte[] tmp = new byte[iDataLen];
				  System.arraycopy(byData, 0, tmp, 0, iDataLen);
				  Log.d(TAG , "   <--"+ Ulitily.byteArrayToHexString(tmp));
				  return iDataLen;
			  }
		  }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG , "recv Excpetion");
			return -2;
		}
		Log.d(TAG , "recv timeout:"+size);
		return -1;
	}

	  
	  public boolean flushUart()
	  {
		  try {
			int size = mInputStream.available();
			if(size>0){
//				mInputStream.skip(size);
				
				byte[] buffer = new byte[size];
				int byteOffset = 0;
				int byteCount = size;
				mInputStream.read(buffer, byteOffset, byteCount );
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return true;
	  }
}
