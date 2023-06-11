package xyz.wyst.tiecode.plugin.util;

import xyz.wyst.tiecode.plugin.App;
import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;
import java.security.cert.*;
import xyz.wyst.tiecode.plugin.util.LogUtils;
import xyz.wyst.tiecode.plugin.util.FileUtils;
import java.text.DateFormat;
import java.math.BigDecimal;
import android.content.Context;
import android.net.*;
import android.telephony. *;
import android.annotation. *;
import javax.net.ssl.*;

/*
* Written by WYstudio
*/

public class HttpUtils {
    private HashMap wy_qqt = new HashMap();
    private boolean wy_sf_cdx = false;

    private static void wlog(String str) {
        LogUtils.writeMainLog("(HttpUtils.java)" + ">>>" + str);
    }

    public String[] Post(String url, Object data, String cookie) {
        /*
        Object[] results = post_tong_nei(url, data, cookie);
        if (results == null) {
            return new String[] { "请求失败2", "请求失败2" };
        } else {
            byte[] content = (byte[]) results[0];
            String content2 = new String(content);
            String cookie2 = (String) results[1];
            return new String[] { content2, cookie2 };
        }
        */

        String[] res = post_2(url, (String) data, cookie);
        if (res == null) {
            return new String[] { "请求失败", "请求失败" };
        } else {
            String content = res[0];
            String cookie2 = res[1];
            return new String[] { content, cookie2 };
        }

    }

    public String UPLOAD(String url, String cookie, String file, String fol_id, OnUploadListener callback) {
        return sendPostUplodFile(url, cookie, file, fol_id, callback);
    }

    public static String sendPostUplodFile(String wy_url, String wy_cookie, String file_path, String folder_id,
            OnUploadListener callback) {
        DataOutputStream out = null;
        BufferedReader in = null;
        String result = "";
        int len;
        try {
            //URL realUrl = new URL("http://file.kgfuns.com/fileserver/upload?BusiessId=23");
            URL realUrl = new URL(wy_url);
            //打开和URL之间的连接
            HttpURLConnection conn;
            if (wy_url.startsWith("https://")) {
                conn = (HttpsURLConnection) realUrl.openConnection();
                setSsl();
            } else {
                conn = (HttpURLConnection) realUrl.openConnection();
            }
            //构建请求头
            String BOUNDARY = "----WebKitFormBoundary07I8UIuBx6LN2KyY";
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "UTF-8");
            if (wy_cookie != null) {
                conn.setRequestProperty("Cookie", wy_cookie);
            }
            //conn.setRequestProperty("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36");
            //conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
            //发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setFollowRedirects(true);
            conn.setUseCaches(false); //不允许使用缓存
            conn.connect();

            //设置输出流
            out = new DataOutputStream(conn.getOutputStream());

            //添加参数file
            //File file = new File(in11);
            File file = new File(file_path);
            StringBuffer sb = new StringBuffer();
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            //媒体类型上传的类型
            //sb.append("Content-Disposition: form-data; name=\"media\";filename=\"").append(fileName).append(typeName);
            //（Instream）流文件上传的时候要指定filename的值
            sb.append("Content-Disposition: form-data;name=\"task\"");
            sb.append("\r\n\n");
            sb.append("1");
            sb.append("\r\n");
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"ve\"");
            sb.append("\r\n\n");
            sb.append("2");
            sb.append("\r\n");
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"id\"");
            sb.append("\r\n\n");
            sb.append("WU_FILE_0");
            sb.append("\r\n");
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"name\"");
            sb.append("\r\n\n");
            sb.append(file.getName());
            sb.append("\r\n");
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"type\"");
            sb.append("\r\n\n");
            sb.append("application/octet-stream");
            sb.append("\r\n");
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"lastModifiedDate\"");
            sb.append("\r\n\n");
            sb.append("Thu Jul 14 2022 13:24:54 GMT+0800 (CST)");
            sb.append("\r\n");
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"size\"");
            sb.append("\r\n\n");
            sb.append(file.length());
            sb.append("\r\n");
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"folder_id_bb_n\"");
            sb.append("\r\n\n");
            sb.append(folder_id);
            sb.append("\r\n");
            sb.append("--");
            sb.append(BOUNDARY);
            sb.append("\r\n");
            sb.append("Content-Disposition: form-data;name=\"upload_file\";filename=\"" + file.getName() + "\"");
            sb.append("\r\n");
            sb.append("Content-Type: application/octet-stream");
            sb.append("\r\n");
            sb.append("\r\n\n");
            out.write(sb.toString().getBytes());

            //DataInputStream in1 = new DataInputStream(new FileInputStream(file));
            InputStream in1 = new FileInputStream(file);
            //DataInputStream in1 = new DataInputStream(file);
            wlog("文件上传操作开始输入文件流");
            int bytes = 0;
            byte[] bufferOut = new byte[1024];

            //out.write(FileUtils.readStream(in1));

            long max = file.length();
            long progress = 0;
            while ((bytes = in1.read(bufferOut)) != -1) {
                out.write(bufferOut, 0, bytes);
                progress += bytes;
                double d = (new BigDecimal(progress / (double) max).setScale(2, BigDecimal.ROUND_HALF_UP))
                        .doubleValue();
                double d1 = d * 100;
                if (d1 % 10 == 0) {
                    wlog("文件上传进度：" + d1 + "%");
                }
                callback.onProgressChanged(d1, "文件上传中...");
            }
            out.write("\r\n".getBytes());
            in1.close();
            byte[] end_data = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(end_data);
            //flush输出流的缓冲
            out.flush();
            callback.onProgressChanged(0, "正在等待服务器响应\n(网络越卡或工程越大，这里等待时间越长)");
            wlog("文件上传操作文件流输入完毕");
            int res = conn.getResponseCode();
            if (res >= 200 && res < 400) {
                wlog("文件上传操作获取返回数据流");
                //FileUtils.deleteFile(new File(file_path));
                //FileUtils.deleteFile(new File(file_path));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                InputStream resultStream = conn.getInputStream();
                len = -1;
                byte[] buffer = new byte[1024 * 8];
                long progress2 = 0;
                while ((len = resultStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                    progress2 += len;
                    double o_d = (new BigDecimal(progress2 / 10240).setScale(2, BigDecimal.ROUND_HALF_UP)).doubleValue();
                    double o_d1 = o_d * 100;
                    callback.onProgressChanged(o_d1,"获取返回数据");
                }
                resultStream.close();
                bos.flush();
                bos.close();
                wlog("文件上传操作返回数据流获取完毕");
                result = new String(bos.toByteArray());
            }else{
                result = "请求错误 http代码：" + res;
            }

            //定义BufferedReader输入流来读取URL的响应
            /*
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            */
            //            HashMap hashMap = JSON.parseObject(result, HashMap.class);
            //            JSONObject jsonObject=new JSONObject(JSON.parseObject(result, HashMap.class));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.printfException(App.mApp, e);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                LogUtils.printfException(App.mApp, ex);
            }
        }
        return result;
    }

    public interface OnUploadListener {
        public void onProgressChanged(double value, String value2);
    }

    public String GET(String url, String cookie) {
        Object[] results = get_tong_nei(url, cookie);
        if (results == null) {
            return "请求失败";
        } else {
            byte[] content = (byte[]) results[0];
            String content2 = new String(content);
            String cookie2 = (String) results[1];
            return content2;
        }
    }

    private Object[] post_tong_nei(String url, Object data, String cookie) {
        return HttpSend(url, "POST", null, data, cookie, 6000, "UTF-8");
    }

    private Object[] get_tong_nei(String url, String cookie) {
        return HttpSend(url, "GET", null, null, cookie, 6000, "UTF-8");
    }

    private String[] post_2(String wy_url, String wy_data, String wy_cookie) {
        try {
            URL url = new URL(wy_url);
            // 将url 以 open方法返回的urlConnection 连接强转为HttpURLConnection连接
            // (标识一个url所引用的远程对象连接)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置连接输出流为true,默认false (post 请求是以流的方式隐式的传递参数)
            connection.setDoOutput(true);
            // 设置连接输入流为true
            connection.setDoInput(true);

            connection.setRequestMethod("POST");

            connection.setUseCaches(false);
            connection.setConnectTimeout(6000);
            connection.setReadTimeout(20000);

            connection.setInstanceFollowRedirects(true);
            // application/x-javascript text/xml->xml数据
            // application/x-javascript->json对象
            // application/x-www-form-urlencoded->表单数据
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            if (wy_cookie != null) {
                connection.setRequestProperty("Cookie", wy_cookie);
            }

            connection.connect();
            DataOutputStream dataout = new DataOutputStream(connection.getOutputStream());
            //String parm = URLEncoder.encode(wy_data, "utf-8");
            String parm = wy_data;
            dataout.writeBytes(parm);
            dataout.flush();

            BufferedReader bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();

            Map<String, List<String>> hs = connection.getHeaderFields();
            int responseCode = connection.getResponseCode();
            if (responseCode >= 200 && responseCode < 400) {
                List<String> cs = hs.get("Set-Cookie");
                StringBuffer cok = new StringBuffer();
                if (cs != null) {
                    for (String s : cs) {
                        cok.append(s + ";");
                    }
                }

                // 循环读取流,若不到结尾处
                while ((line = bf.readLine()) != null) {
                    //sb.append(bf.readLine());
                    sb.append(line);
                }
                String[] result1 = { sb.toString(), cok.toString() };
                return result1;
            } else {
                return new String[] { "请求失败", "请求失败" };
            }
            // bf.close(); // 重要且易忽略步骤 (关闭流,切记!)
            // connection.disconnect(); // 销毁连接
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.printfException(App.mApp, e);
            String[] result2 = { "请求失败", "请求失败" };
            return result2;
        }
        /*
        String[] result2 = { "请求失败2", "请求失败2" };
        return result2;
        */
    }

    private Object[] HttpSend(String wy_httpurl, String wy_requir_type, String wy_xiaxing_path, Object wy_data,
            String wy_cookie, int wy_time_c, String wy_bianma) {
        try {
            URL url = new URL(wy_httpurl);
            HttpURLConnection conn;
            //https设置ssl
            if (wy_httpurl.startsWith("https://")) {
                conn = (HttpsURLConnection) url.openConnection();
                setSsl();
            } else {
                conn = (HttpURLConnection) url.openConnection();
            }
            conn.setConnectTimeout(wy_time_c);
            conn.setFollowRedirects(true);
            conn.setDoInput(true);
            //设置编码
            conn.setRequestProperty("Accept-Charset", wy_bianma);
            //设置cookie
            if (wy_cookie != null) {
                conn.setRequestProperty("Cookie", wy_cookie);
            }
            //设置请求类型(GET/POST/DELETE/PUT)
            conn.setRequestMethod(wy_requir_type);
            //设置请求头
            if (wy_qqt != null) {
                Set<Map.Entry<String, String>> entries = wy_qqt.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    conn.setRequestProperty(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                }
            }
            //POST发送的数据
            byte[] data = null;
            if (wy_data != null) {
                data = formatData(wy_data, wy_bianma);
                if (data != null) {
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-length", "" + data.length);
                }
            }
            conn.connect();
            //如果下行路径不为空且请求类型为GET，则是下载
            if ("GET".equals(wy_requir_type) && wy_xiaxing_path != null) {
                long length = conn.getContentLengthLong();
                File f = new File(wy_xiaxing_path);
                if (!f.getParentFile().exists()) {
                    f.getParentFile().mkdirs();
                }
                FileOutputStream os = new FileOutputStream(f);
                InputStream is = conn.getInputStream();
                copyFile(is, os, length);
                Map<String, List<String>> hs = conn.getHeaderFields();
                List<String> cs = hs.get("Set-Cookie");
                StringBuffer cok = new StringBuffer();
                if (cs != null) {
                    for (String s : cs) {
                        cok.append(s + ";");
                    }
                }
                String returnCookie = cok.toString();
                return new Object[] { returnCookie };
            }
            int responseCode = conn.getResponseCode();
            //判断重定向
            if (wy_sf_cdx && (responseCode == HttpURLConnection.HTTP_MOVED_TEMP
                    || responseCode == HttpURLConnection.HTTP_MOVED_PERM
                    || responseCode == HttpURLConnection.HTTP_SEE_OTHER)) {
                conn.disconnect();
                String newUrl = conn.getHeaderField("Location");
                if (newUrl.startsWith("https://")) {
                    conn = (HttpsURLConnection) url.openConnection();
                    setSsl();
                } else {
                    conn = (HttpURLConnection) url.openConnection();
                }
                conn.setConnectTimeout(wy_time_c);
                conn.setFollowRedirects(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Accept-Charset", wy_bianma);
                if (wy_cookie != null)
                    conn.setRequestProperty("Cookie", wy_cookie);
                conn.setRequestMethod(wy_requir_type);
                if (wy_qqt != null) {
                    Set<Map.Entry<String, String>> entries = wy_qqt.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        conn.setRequestProperty(String.valueOf(entry.getKey()), String.valueOf(entry.getValue()));
                    }
                }
                conn.connect();
                responseCode = conn.getResponseCode();
            }
            //如果欲发送数据不为空，则进行发送
            if (data != null) {
                OutputStream os = conn.getOutputStream();
                os.write(data);
                os.flush();
            }
            //获取返回结果
            Map<String, List<String>> hs = conn.getHeaderFields();

            if (responseCode >= 200 && responseCode < 400) {
                List<String> cs = hs.get("Set-Cookie");
                StringBuffer cok = new StringBuffer();
                if (cs != null) {
                    for (String s : cs) {
                        cok.append(s + ";");
                    }
                }
                ByteArrayOutputStream boas = new ByteArrayOutputStream();
                byte[] tmp = new byte[1024];
                int len;
                InputStream is = conn.getInputStream();
                while ((len = is.read(tmp)) != -1) {
                    boas.write(tmp, 0, len);
                }
                byte[] result = boas.toByteArray();
                boas.close();
                is.close();
                String cookie = cok.toString();
                return new Object[] { result, cookie };
            } else {
                return new Object[] { "http码:" + responseCode, "http码:" + responseCode };
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.printfException(App.mApp, e);
            return new Object[] { "请求失败2", "请求失败2" };
        }
    }

    private boolean copyFile(InputStream in, OutputStream out, long length) {
        try {
            int readLength = 0;
            int byteread = 0;
            byte[] buffer = new byte[1024 * 1024];
            while ((byteread = in.read(buffer)) != -1) {
                readLength += byteread;
                double value = ((readLength / (length * 1.0)) * 100);
                out.write(buffer, 0, byteread);
            }
            //in.close
            //out.close
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    private static byte[] formatData(Object obj, String charset) throws UnsupportedEncodingException, IOException {
        byte[] bs = null;
        if (obj instanceof String)
            bs = ((String) obj).getBytes(charset);
        else if (obj.getClass().getComponentType() == byte.class)
            bs = (byte[]) obj;
        else if (obj instanceof File)
            bs = readAll(new FileInputStream((File) obj));
        else
            bs = String.valueOf(obj).getBytes(charset);
        return bs;
    }

    private static byte[] readAll(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream(4096);
        byte[] buffer = new byte[2 ^ 32];
        int n = 0;
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
        }
        byte[] ret = output.toByteArray();
        output.close();
        return ret;
    }

    private static void setSsl() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[] { new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            } }, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static boolean checkConnectNetwork() {
        if(getNetworkType(App.mApp).equals("无网络")){
            return false;
        }
        return true;
    }
    
    public static boolean checkNetworkIsWifi(){
        if(getNetworkType(App.mApp).equals("WIFI")){
            return true;
        }
        return false;
    }
    
    public static String getNetworkType(Context context){
       ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
       if (null == connManager) {
          return "无网络";
       }
       @SuppressLint("MissingPermission") NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
       if (activeNetInfo == null || !activeNetInfo.isAvailable()) {
          return "无网络";
       }
       @SuppressLint("MissingPermission") NetworkInfo wifiInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
       if (null != wifiInfo) {
          NetworkInfo.State state = wifiInfo.getState();
          if (null != state) {
             if (state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING) {
                return "WIFI";
             }
          }
       }
       TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
       int networkType = telephonyManager.getNetworkType();
       switch (networkType) {
          case TelephonyManager.NETWORK_TYPE_GPRS:
          case TelephonyManager.NETWORK_TYPE_CDMA:
          case TelephonyManager.NETWORK_TYPE_GSM:
          case TelephonyManager.NETWORK_TYPE_EDGE:
          case TelephonyManager.NETWORK_TYPE_1xRTT:
          case TelephonyManager.NETWORK_TYPE_IDEN:
          return "2G";
          case TelephonyManager.NETWORK_TYPE_EVDO_A:
          case TelephonyManager.NETWORK_TYPE_UMTS:
          case TelephonyManager.NETWORK_TYPE_EVDO_0:
          case TelephonyManager.NETWORK_TYPE_HSDPA:
          case TelephonyManager.NETWORK_TYPE_HSUPA:
          case TelephonyManager.NETWORK_TYPE_HSPA:
          case TelephonyManager.NETWORK_TYPE_EVDO_B:
          case TelephonyManager.NETWORK_TYPE_EHRPD:
          case TelephonyManager.NETWORK_TYPE_HSPAP:
          case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
          return "3G";
          case TelephonyManager.NETWORK_TYPE_LTE:
          case TelephonyManager.NETWORK_TYPE_IWLAN:
          return "4G";
          case TelephonyManager.NETWORK_TYPE_NR:
          return "5G";
          default:
          return "MOBILE";
       }
    }
}