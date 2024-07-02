package com.yumu.hexie.common.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class DESUtil {

	public static String encryptByKey(String data, String key) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(desKeySpec.getKey());
       //解密换成解密模式, 2
        cipher.init(Cipher. ENCRYPT_MODE, secretKey, iv);
        return toHexString(cipher.doFinal(data.getBytes("UTF-8")));
    }
	
	public static String toHexString(byte[] b) {
        StringBuffer hexString = new StringBuffer();

        for(int i = 0; i < b.length; ++i) {
            String plainText = Integer.toHexString(255 & b[i]);
            if (plainText.length() < 2) {
                plainText = "0" + plainText;
            }

            hexString.append(plainText);
        }

        return hexString.toString();
    }

	public static void main(String[] args) throws Exception {
		
		String data = "xibuprod";
		String key = "70523a6315f9e192e7ab9d11ea2017e1";
		
		String encrypted = encryptByKey(key, data);
		System.out.println(encrypted);
		
//		data = "12eddqsa!!ooo";
//		encrypted = encryptByKey(data, key);
//		System.out.println(encrypted);
	}
	
}
