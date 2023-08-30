package com.yumu.hexie.common.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import com.alibaba.fastjson.JSONObject;
import com.yumu.hexie.integration.wechat.constant.ConstantWd;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;

public class RSAUtil {

    private static final String ALGORITHM_NAME = "RSA";
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

    /**
     * 签名
     * @param content
     * @param privateKey
     * @param input_charset
     * @return
     * @throws Exception
     */
	public static String signByPrivate(String content, String privateKey, String input_charset) throws Exception {
		if (privateKey == null) {
			throw new Exception("加密私钥为空, 请设置");
		}
		PrivateKey privateKeyInfo = getPrivateKey(privateKey);
		return signByPrivate(content, privateKeyInfo, input_charset);
	}

    /**
     * 得到私钥
     * @param key 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String key) throws Exception {
        byte[] keyBytes = buildPKCS8Key(key);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        return keyFactory.generatePrivate(keySpec);
    }
    
    private static byte[] buildPKCS8Key(String privateKey) throws IOException {
        if (privateKey.contains("-----BEGIN PRIVATE KEY-----")) {
            return Base64.decodeBase64(privateKey.replaceAll("-----\\w+ PRIVATE KEY-----", "").getBytes());
        } else if (privateKey.contains("-----BEGIN RSA PRIVATE KEY-----")) {
            final byte[] innerKey = Base64.decodeBase64(privateKey.replaceAll("-----\\w+ RSA PRIVATE KEY-----", "").getBytes());
            final byte[] result = new byte[innerKey.length + 26];
            System.arraycopy(Base64.decodeBase64("MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKY=".getBytes()), 0, result, 0, 26);
            System.arraycopy(BigInteger.valueOf(result.length - 4).toByteArray(), 0, result, 2, 2);
            System.arraycopy(BigInteger.valueOf(innerKey.length).toByteArray(), 0, result, 24, 2);
            System.arraycopy(innerKey, 0, result, 26, innerKey.length);
            return result;
        } else {
            return Base64.decodeBase64(privateKey.getBytes());
        }
    }
    
    public static String signByPrivate(String content, PrivateKey privateKey, String input_charset) throws Exception {
		if (privateKey == null) {
			throw new Exception("加密私钥为空, 请设置");
		}
		java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
		signature.initSign(privateKey);
		signature.update(content.getBytes(input_charset));
		return new String(Base64.encodeBase64(signature.sign()));
	}
    
    /**
     * 读取证书信息
     * @param filePath
     * @param charSet
     * @return
     * @throws Exception
     */
    public static String readFile(String filePath, String charSet) throws Exception {
        FileInputStream fileInputStream = new FileInputStream(filePath);
        try {
            FileChannel fileChannel = fileInputStream.getChannel();
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) fileChannel.size());
            fileChannel.read(byteBuffer);
            byteBuffer.flip();
            return new String(byteBuffer.array(), charSet);
        } finally {
            fileInputStream.close();
        }
    }
    
    public static boolean verifyByKeyPath(String content, String sign, String publicKeyPath, String input_charset) {
        try {
            return verify(content, sign, getKey(publicKeyPath), input_charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public static String getKey(String string) throws Exception {
        String content = readFile(string, "UTF8");
        return content.replaceAll("\\-{5}[\\w\\s]+\\-{5}[\\r\\n|\\n]", "");
    }
    
    /**
     * RSA验签名检查
     * @param content 待签名数据
     * @param sign 签名值
     * @param publicKey 支付宝公钥
     * @param input_charset 编码格式
     * @return 布尔值
     */
    public static boolean verify(String content, String sign, String publicKey, String input_charset) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            byte[] encodedKey =  Base64.decodeBase64(publicKey.getBytes());
            PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));
            return verify(content, sign, pubKey, input_charset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
	public static boolean verify(String content,String sign,PublicKey publicKey,String inputCharset){
		try {
            Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
            signature.initVerify(publicKey);
            signature.update(content.getBytes(inputCharset));
            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * RSA加密，获取公钥
     * @param publicKey base64加密的公钥字符串
     */
    public static PublicKey getPublicKey(String publicKey) throws Exception {
        //使用decodeBase64进行破译编码，并返回一个byte字节数组
        byte[] decodedKey = Base64.decodeBase64(publicKey.getBytes());
        //使用X509标准作为密钥规范管理的编码格式,按照 X509 标准对其进行编码的密钥。复制数组的内容，以防随后的修改。
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decodedKey);
        //创建一个KeyFactory对象。
        //密钥工厂用于将 密钥 （ Key类型的不透明密码密钥）转换为 密钥规范 （底层密钥资料的透明表示）

        //返回一个KeyFactory对象，用于转换指定算法的公钥/私钥。
        //返回封装指定Provider对象的KeyFactorySpi实现的新KeyFactory对象。 请注意，指定的Provider对象不必在提供程序列表中注册。
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        //根据提供的密钥规范（密钥材料）生成公钥对象。
        return keyFactory.generatePublic(keySpec);
    }

    /**
     * RSA加密
     * @param data      待加密数据
     * @param publicKey 公钥
     */
    public static String encrypt(String data, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        byte[] tempBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        // 加密后的字符串
        return new String(Base64.encodeBase64(tempBytes));
    }

    public static String encrypt1(String data, String publicKey) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.ENCRYPT_MODE, getPrivateKey(publicKey));
        byte[] tempBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        // 加密后的字符串
        return new String(Base64.encodeBase64(tempBytes));
    }

    /**
     * RSA解密
     *
     * @param data       待解密数据
     * @param privateKey 私钥
     */
    public static String decrypt(String data, String privateKey) throws Exception {
        // 生成私钥
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME);
        cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
        // 密文解码
        byte[] secretTextDecoded = Base64.decodeBase64(data.getBytes(StandardCharsets.UTF_8));
        byte[] tempBytes = cipher.doFinal(secretTextDecoded);
        // 解密后的内容
        return new String(tempBytes, StandardCharsets.UTF_8);
    }

    /**
     * 验签
     *
     * @param srcData   原始字符串
     * @param publicKey 公钥
     * @param sign      签名
     */
    public static boolean verify(String srcData, PublicKey publicKey, String sign) throws Exception {
        byte[] keyBytes = publicKey.getEncoded();
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        PublicKey key = keyFactory.generatePublic(keySpec);
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);

        signature.initVerify(key);
        signature.update(srcData.getBytes());
        //signature.verify签署或验证所有更新字节的签名
        return signature.verify(Base64.decodeBase64(sign.getBytes()));
    }

    public static String sign(String data, PrivateKey privateKey) throws Exception {
        byte[] keyBytes = privateKey.getEncoded();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM_NAME);
        PrivateKey key = keyFactory.generatePrivate(keySpec);
        Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
        signature.initSign(key);
        signature.update(data.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.encodeBase64(signature.sign()), StandardCharsets.UTF_8);
    }
}
