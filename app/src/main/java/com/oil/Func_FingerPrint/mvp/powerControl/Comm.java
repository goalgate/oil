package com.oil.Func_FingerPrint.mvp.powerControl;

public interface Comm {

	
	public int open();
	public int close();
	public int write(byte[] byData, int iDataLen, int timeOut);
	public int read(byte[] byData, int iDataLen, int timeOut);
}
