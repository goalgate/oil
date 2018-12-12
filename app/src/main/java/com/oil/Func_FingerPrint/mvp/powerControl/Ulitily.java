package com.oil.Func_FingerPrint.mvp.powerControl;

/**
 * @author xumin
 * 
 */
public class Ulitily {

	public static int add(int a, int b) {
		return a+b;
	}
	
	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];

		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static byte[] hexStringToByteArrayWithLen(String s) {
		String s1 = s.replaceAll(" ", "");

		if (s1.length() % 2 != 0) {
		}
		int len = s1.length() / 2;

		byte[] data = new byte[len + 2];

		for (int i = 0; i < len; i++) {
			data[i + 2] = (byte) ((Character.digit(s1.charAt(i * 2), 16) << 4) + Character
					.digit(s1.charAt(i * 2 + 1), 16));
		}
		data[0] = (byte) (len >> 8);
		data[1] = (byte) (len);
		return data;
	}

	final protected static char[] hexArray = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String byteArrayToHexString(byte[] bytes) {
		if(bytes == null) return null;
		char[] hexChars = new char[bytes.length * 2];
		int v;
		for (int j = 0; j < bytes.length; j++) {
			v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String byteToHexString(byte bytes) {
		char[] hexChars = new char[2];
		int v;
		v = bytes & 0xFF;
		hexChars[0] = hexArray[v >>> 4];
		hexChars[1] = hexArray[v & 0x0F];
		return new String(hexChars);
	}

    public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "gbk");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}
	public static String byteArrayWithLenToHexString(byte[] bytes) {

		int len = bytes[0] * 256 + bytes[1];
		char[] hexChars = new char[len * 2];
		int v;
		for (int j = 0; j < len; j++) {
			v = bytes[j + 2] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static String byte2String(byte[] bytearray) {
	    String result = "";
	    char temp;  

	    int length = bytearray.length;  
	    for (int i = 0; i < length; i++) {  
	        temp = (char) bytearray[i];  
	        result += temp;  
	    }  
	    return result;  
	}
	public static String byte2string(byte[] data, int len) {
		int i;
		StringBuffer strbuf = new StringBuffer();
		char[] buff = new char[len];
		for (i = 0; i < len; i++)
			buff[i] = (char) data[i];
		return strbuf.append(buff).toString();
	}

	public int bytes2int(byte[] res) {
		int targets = (res[0] & 0xff) | ((res[1] << 8) & 0xff00)
				| ((res[2] << 24) >>> 8) | (res[3] << 24);
		return targets;
	}

	public byte[] int2bytes(int res) {
		byte[] targets = new byte[4];
		targets[0] = (byte) (res & 0xff);
		targets[1] = (byte) ((res >> 8) & 0xff);
		targets[2] = (byte) ((res >> 16) & 0xff);
		targets[3] = (byte) ((res >>> 24));
		return targets;
	}
	
}
