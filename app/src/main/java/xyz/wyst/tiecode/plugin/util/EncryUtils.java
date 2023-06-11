package xyz.wyst.tiecode.plugin.util;

import xyz.wyst.tiecode.plugin.App;
import java.lang.*;
import xyz.wyst.tiecode.plugin.util.Base64Utils;
import android.content.Context;
import java.util.*;
import android.content.pm.*;
import java.security.MessageDigest;
import xyz.wyst.tiecode.plugin.util.LogUtils;
import java.io.*;
import java.util.jar.*;
import java.security.cert.Certificate;
import java.util.Base64;

/*
* Written by WYstudio
*/

public class EncryUtils {
    public static String encode(String str) {
        String s1 = Base64Utils.encode(str, "UTF-8");
        String s2 = wy_add(s1, "v3A", 10);
        String s3 = Base64Utils.encode(s2, "UTF-8");
        String s4 = Base64Utils.encode(s3, "UTF-8");
        String s5 = Base64Utils.encode(s4, "UTF-8");
        String s6 = wy_add(s5, "A2WYVI3G", 20);
        return s6;
    }

    private static String wy_add(String str1, String str2, int location) {
        StringBuffer sb = new StringBuffer(str1);
        sb.insert(location, str2);
        return sb.toString();
    }

    public static boolean checkInit() {
        String path = getPath(App.sysGetPluginPath() + "xyz.wyst.tiecode.plugin/");
        String MD5 = "";
        try {
            MD5 = hexDigest(getSignaturesFromApk(path));
        } catch (IOException e) {

        }
        LogUtils.writeMainLog(MD5);
        if (MD5.equals(Base64Utils.decode("M2RmYzBlYzU4NzRmNzllNmFiZmEyYTNkYWQ3MWU2YzU=","UTF-8"))) {
            return true;
        }else{
            return false;
        }
    }

    private static String getPath(String file_path) {
        String result = "";
        String s_hou = ".tpk";
        for (File f : new File(file_path).listFiles()) {
            if (f.getPath().substring(f.getPath().length() - s_hou.length()).equals(s_hou) && !f.isDirectory()) {
                result = f.getPath();
            }
        }
        return result;
    }

    public static String getMD5(Context context) {
        StringBuffer md5StringBuffer = new StringBuffer();
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_SIGNATURES);
            byte[] bytes = packageInfo.signatures[0].toByteArray();
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(bytes);
            byte[] digest = messageDigest.digest();
            for (int i = 0; i < digest.length; i++) {
                String hexString = Integer.toHexString(digest[i] & 0xff);

                if (hexString.length() == 1)
                    md5StringBuffer.append("0");

                md5StringBuffer.append(hexString);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5StringBuffer.toString();
    }

    public static String hexDigest(byte[] bytes) {
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        byte[] md5Bytes = md5.digest(bytes);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
    
    * 从APK中读取签名
    
    *
    
    * @param file
    
    * @return
    
    * @throws IOException
    
    */

    private static byte[] getSignaturesFromApk(String strFile) throws IOException {
        File file1 = new File(strFile);
        JarFile jarFile = new JarFile(file1);
        try {
            JarEntry je = jarFile.getJarEntry("AndroidManifest.xml");
            byte[] readBuffer = new byte[8192];
            Certificate[] certs = loadCertificates(jarFile, je, readBuffer);
            if (certs != null) {
                for (Certificate c : certs) {
                    return c.getEncoded();
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    /**
    
    * 加载签名
    
    *
    
    * @param jarFile
    
    * @param je
    
    * @param readBuffer
    
    * @return
    
    */

    private static Certificate[] loadCertificates(JarFile jarFile, JarEntry je, byte[] readBuffer) {
        try {
            InputStream is = jarFile.getInputStream(je);
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {

            }
            is.close();
            return je != null ? je.getCertificates() : null;
        } catch (IOException e) {

        }
        return null;
    }

}