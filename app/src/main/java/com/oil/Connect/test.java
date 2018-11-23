package com.oil.Connect;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class test {

	/**
	 * @param args
	 */
	
	public boolean testIpPort(String ip,int port) {
		// TODO Auto-generated method stub

		Socket connect = new Socket();  
		        
		 boolean res =false;
		        try {  
		            connect.connect(new InetSocketAddress(ip,port),5000);  
		              
		           res= connect.isConnected();
		            
		            //System.out.println("" + res);  
		        } catch (IOException e) { 
		        	//System.out.println("false");  
		            //e.printStackTrace();  
		        	res =false;
		        }finally{  
		            try {  
		                connect.close();  
		            } catch (IOException e) {  
		                e.printStackTrace();  
		            }  
		        }  
		     return res;

	}
	
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		//String s="1E:23:05:18:5F:e5";
//		//System.out.println(macToId(s));
//
//		/*
//		String s="yy.xyj";
//		System.out.println(s.substring(0,s.indexOf(".xyj")));
//
//		System.out.println(toStrLen(20,4));
//		String fn="lyq0001.itp";
//		String ss=fn.substring(0,fn.toLowerCase().indexOf(".itp"));
//		System.out.println(ss.substring(ss.length()-4));
//		*/
//		if (testIpPort("www.baidu.com", 80)) {
//			if (testIpPort("61.144.19.121", 88)) {
//				System.out.println("连接服务器成功");
//			} else {
//				System.out.println("连接服务器失败");
//			}
//		} else {
//			System.out.println("连接公网失败");
//		}
//
//	}
	
	public static String toStrLen(int ix,int len)
	{
		String s=ix+"";
		for(int i=s.length();i<len;i++)
		{
			s="0"+s;
		}
		return s;
	}
	
	private static String macToId(String mac)
	{
		String s="";
		if(mac==null){return "";}
		String[] ss=mac.split(":");
		if(ss.length<6){return "";}
		try
		{
			for(int i=0;i<6;i++)
			{
				int b = Integer.parseInt(ss[i].trim(), 16);				
				s+=formatStr(String.valueOf( b),3);
				if(i==1||i==3)
				{
					s+="-";
				}
			}
			
		}catch(Exception ex){}
		return s;
	}
	
	public static String formatStr(String str, int len) {
		String s = "";
		if (str.length() == len) {
			s = str;
		} else if (str.length() < len) {
			for (int i = str.length(); i < len; i++) {
				s = '0' + s;
			}
			s = s + str;
		} else if (str.length() > len) {
			s = str.substring(str.length() - len);

		}

		return s;

	}
	

}
