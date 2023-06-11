package xyz.wyst.tiecode.plugin.util;

import xyz.wyst.tiecode.plugin.App;
import java.lang.*;
import java.io.*;
import java.security.*;
import java.util.Base64;
import javax.crypto.Cipher;
import java.util.Random;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import android.app.Activity;
import android.content.Context;

public class Base64Utils {
    private static String base64Table = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
    private static String add = "=";

    /**
     * base64编码
     */
    public static String encode(String str, String charsetName) {
        StringBuilder base64Str = new StringBuilder();
        byte[] bytesStr;
        try {
            bytesStr = str.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        
        // 编码为二进制字符串
        String bytesBinary = binary(bytesStr, 2);
        // 24位为一组，不够后面补0,计算出需要补充的个数
        int addCount = 0;
        while (bytesBinary.length() % 24 != 0) {
            bytesBinary += "0";
            addCount++;
        }
        for (int i = 0; i <= bytesBinary.length() - 6; i += 6) {
            // 二进制转十进制
            int index = Integer.parseInt(bytesBinary.substring(i, i + 6), 2);
            // 如果是有补位的6个0组成，则转换为
            if (index == 0 && i >= bytesBinary.length() - addCount) {
                base64Str.append(add);
            } else {
                base64Str.append(base64Table.charAt(index));
            }
        }
        return base64Str.toString();
    }

    /**
     * base64解码
     */
    public static String decode(String base64str, String charsetName) {
        String base64Binarys = "";
        for (int i = 0; i < base64str.length(); i++) {
            char s = base64str.charAt(i);
            if (s != '=') {
                // 十进制转二进制
                String binary = Integer.toBinaryString(base64Table.indexOf(s));
                // 不够六位进行补位
                while (binary.length() != 6) {
                    binary = "0" + binary;
                }
                base64Binarys += binary;
            }
        }
        // 长度应该是8的倍数，去除后面多余的0
        base64Binarys = base64Binarys.substring(0, base64Binarys.length() - base64Binarys.length() % 8);
        byte[] bytesStr = new byte[base64Binarys.length() / 8];
        for (int bytesIndex = 0; bytesIndex < base64Binarys.length() / 8; bytesIndex++) {
            // 八位截取一次，转化为一个字节
            bytesStr[bytesIndex] = (byte) Integer.parseInt(base64Binarys.substring(bytesIndex * 8, bytesIndex * 8 + 8),
                    2);
        }
        try {
            return new String(bytesStr, charsetName);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字节数组转自定义进制字符串
     */
    public static String binary(byte[] bytes, int radix) {
        // 转化为二进制字符串   1代表正数,如果第一个字节以0开头，转化后会省略，所以要重新补位
        String strBytes = new BigInteger(1, bytes).toString(radix);
        while (strBytes.length() % 8 != 0) {
            strBytes = "0" + strBytes;
        }
        return strBytes;
    }

}

class RSASecurity {
    /** 指定加密算法为RSA */
    private final String ALGORITHM = "RSA";
    /** 密钥长度，用来初始化 */
    private final int KEY_SIZE = 1024;
    /** 指定公钥存放文件 */
    private String PUBLIC_KEY_FILE;
    /** 指定私钥存放文件 */
    private String PRIVATE_KEY_FILE;

    public RSASecurity(Activity 窗口) {
        PUBLIC_KEY_FILE = 窗口.getDir("S5droid", Context.MODE_PRIVATE).getAbsolutePath() + "PublicKey";
        PRIVATE_KEY_FILE = 窗口.getDir("S5droid", Context.MODE_PRIVATE).getAbsolutePath() + "PrivateKey";
    }

    /**
     * 生成密钥对
     * @throws Exception
     */
    public void generateKeyPair() throws Exception {

        /** RSA算法要求有一个可信任的随机数源 */
        SecureRandom secureRandom = new SecureRandom();

        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);

        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        keyPairGenerator.initialize(KEY_SIZE, secureRandom);
        keyPairGenerator.initialize(KEY_SIZE);

        /** 生成密匙对*/
        KeyPair keyPair = keyPairGenerator.generateKeyPair();

        /** 得到公钥*/
        Key publicKey = keyPair.getPublic();

        /** 得到私钥*/
        Key privateKey = keyPair.getPrivate();

        ObjectOutputStream oos1 = null;
        ObjectOutputStream oos2 = null;
        try {
            /** 用对象流将生成的密钥写入文件 */
            oos1 = new ObjectOutputStream(new FileOutputStream(PUBLIC_KEY_FILE));
            oos2 = new ObjectOutputStream(new FileOutputStream(PRIVATE_KEY_FILE));
            oos1.writeObject(publicKey);
            oos2.writeObject(privateKey);
        } catch (Exception e) {
            throw e;
        } finally {
            /** 清空缓存，关闭文件输出流 */
            oos1.close();
            oos2.close();
        }
    }

    /**
     * 加密方法
     * @param source 源数据
     * @return
     * @throws Exception
     */
    public byte[] encrypt(String source) throws Exception {
        generateKeyPair();
        Key publicKey;
        ObjectInputStream ois = null;
        try {

            /** 将文件中的公钥对象读出 */
            ois = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            publicKey = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            ois.close();
        }

        /** 得到Cipher对象来实现对源数据的RSA加密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] b = source.getBytes();
        /** 执行加密操作*/
        byte[] b1 = cipher.doFinal(b);
        return b1;
    }

    /**
     * 解密算法
     * @param cryptograph    密文
     * @return
     * @throws Exception
     */
    public byte[] decrypt(byte[] cryptograph) throws Exception {
        Key privateKey;
        ObjectInputStream ois = null;
        try {
            /** 将文件中的私钥对象读出 */
            ois = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            privateKey = (Key) ois.readObject();
        } catch (Exception e) {
            throw e;
        } finally {
            ois.close();
        }

        /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        /** 执行解密操作*/
        byte[] b = cipher.doFinal(cryptograph);
        return b;
    }
}