package xyz.wyst.tiecode.plugin;

import java.io.*;

import android.os.AsyncTask;

import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.io.*;
import java.util.*;
import android.net.*;
import java.text.*;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;
import xyz.wyst.tiecode.plugin.FileUtils;

@SuppressWarnings("all")
public class EasyHttp {

    public static HashMap<String, String> sHeader = new HashMap<String, String>();

    public static void setRequestProperty(String str1, String str2) {
        sHeader.put(str1, str2);
    }

    public static void setHeader(HashMap<String, String> header) {
        sHeader = header;
    }

    public static HashMap<String, String> getHeader() {
        return sHeader;
    }

    public static String get_sync(String url) {
        return new HttpTask_Sync(url, "GET", null, null, null).getOrPost(HttpTask_Sync.TYPE_GET);
    }

    public static String get_sync(String url, String cookie) {
        return new HttpTask_Sync(url, "GET", null, cookie, null).getOrPost(HttpTask_Sync.TYPE_GET);
    }

    public static String get_sync(String url, String cookie, String charset) {
        return new HttpTask_Sync(url, "GET", null, cookie, charset).getOrPost(HttpTask_Sync.TYPE_GET);
    }

    public static String post_sync(String url, String data) {
        return new HttpTask_Sync(url, "POST", data, null, null).getOrPost(HttpTask_Sync.TYPE_POST);
    }

    public static String post_sync(String url, String data, String cookie) {
        return new HttpTask_Sync(url, "POST", data, cookie, null).getOrPost(HttpTask_Sync.TYPE_POST);
    }

    public static String post_sync(String url, String data, String cookie, String charset) {
        return new HttpTask_Sync(url, "POST", data, cookie, charset).getOrPost(HttpTask_Sync.TYPE_POST);
    }

    public static HttpTask1 get(String url, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "GET", null, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask1 get(String url, String cookie, OnRequestListener callback) {
        HttpTask1 task = cookie.matches("[\\w\\-\\.:]+") && Charset.isSupported(cookie)
                ? new HttpTask1(url, "GET", null, cookie, callback) : new HttpTask1(url, "GET", cookie, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask1 get(String url, String cookie, String charset, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "GET", cookie, charset, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask3 upload(String url, String path, OnRequestListener callback) {
        HttpTask3 task = new HttpTask3(url, path, null, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
        return task;
    }

    public static HttpTask3 upload(String url, String path, String cookie, OnRequestListener callback) {
        HttpTask3 task = cookie.matches("[\\w\\-\\.:]+") && Charset.isSupported(cookie)
                ? new HttpTask3(url, path, cookie, null, callback) : new HttpTask3(url, path, cookie, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
        return task;
    }

    public static HttpTask3 upload(String url, String path, String cookie, String charset, OnRequestListener callback) {
        HttpTask3 task = new HttpTask3(url, path, cookie, charset, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
        return task;
    }

    public static HttpTask2 upload2(String url, Object[] data, OnRequestListener callback) {
        HttpTask2 task = new HttpTask2(url, data, null, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask2 upload2(String url, Object[] data, String cookie, OnRequestListener callback) {
        HttpTask2 task = cookie.matches("[\\w\\-\\.:]+") && Charset.isSupported(cookie)
                ? new HttpTask2(url, data, cookie, null, callback) : new HttpTask2(url, data, cookie, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask2 upload2(String url, Object[] data, String cookie, String charset,
            OnRequestListener callback) {
        HttpTask2 task = new HttpTask2(url, data, cookie, charset, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask1 download(String url, String path, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "GET", null, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
        return task;
    }

    public static HttpTask1 download(String url, String path, String cookie, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "GET", cookie, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, path);
        return task;
    }

    public static HttpTask1 delete(String url, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "DELETE", null, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask1 delete(String url, String cookie, OnRequestListener callback) {
        HttpTask1 task = cookie.matches("[\\w\\-\\.:]+") && Charset.isSupported(cookie)
                ? new HttpTask1(url, "DELETE", null, cookie, callback)
                : new HttpTask1(url, "DELETE", cookie, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask1 delete(String url, String cookie, String charset, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "DELETE", cookie, charset, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return task;
    }

    public static HttpTask1 post(String url, String data, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "POST", null, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask1 post(String url, String data, String cookie, OnRequestListener callback) {
        HttpTask1 task = cookie.matches("[\\w\\-\\.:]+") && Charset.isSupported(cookie)
                ? new HttpTask1(url, "POST", null, cookie, callback)
                : new HttpTask1(url, "POST", cookie, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask1 post(String url, String data, String cookie, String charset, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "POST", cookie, charset, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask1 put(String url, String data, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "PUT", null, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask1 put(String url, String data, String cookie, OnRequestListener callback) {
        HttpTask1 task = cookie.matches("[\\w\\-\\.:]+") && Charset.isSupported(cookie)
                ? new HttpTask1(url, "PUT", null, cookie, callback) : new HttpTask1(url, "PUT", cookie, null, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    public static HttpTask1 put(String url, String data, String cookie, String charset, OnRequestListener callback) {
        HttpTask1 task = new HttpTask1(url, "PUT", cookie, charset, callback);
        task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, data);
        return task;
    }

    static class HttpTask1 extends AsyncTask {
        private String mUrl;
        private OnRequestListener mCallback;
        private byte[] mData;
        private String mCharset;
        private String mCookie;
        private String mMethod;
        private String returnCode = null;
        private String text = null;
        private byte[] returnContent = null;
        private String returnCookie = null;
        private int resultCode = 0;
        private long mFileLength;
        private long mTotalLength;

        public HttpTask1(String url, String method, String cookie, String charset, OnRequestListener callback) {
            mUrl = url;
            mMethod = method;
            mCookie = cookie;
            mCharset = charset;
            mCallback = callback;
        }

        @Override
        protected Object doInBackground(Object[] p1) {
            try {
                URL url = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (mUrl.startsWith("https://")) {
                    conn = (HttpsURLConnection) url.openConnection();
                    EasyHttp.setSsl();
                }
                conn.setConnectTimeout(120000);
                conn.setFollowRedirects(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");
                if (mCharset == null)
                    mCharset = "UTF-8";
                conn.setRequestProperty("Accept-Charset", mCharset);

                if (mCookie != null)
                    conn.setRequestProperty("Cookie", mCookie);

                if (mMethod != null)
                    conn.setRequestMethod(mMethod);

                if (!"GET".equals(mMethod) && p1.length != 0) {
                    mData = formatData(p1);
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-length", "" + mData.length);
                }
                if (sHeader != null) {
                    Set<Map.Entry<String, String>> entries = sHeader.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                conn.connect();

                //download
                if ("GET".equals(mMethod) && p1.length != 0) {
                    mFileLength = conn.getContentLengthLong();
                    File f = new File((String) p1[0]);
                    if (!f.getParentFile().exists())
                        f.getParentFile().mkdirs();
                    FileOutputStream os = new FileOutputStream(f);
                    InputStream is = conn.getInputStream();
                    copyFile(is, os);
                    Map<String, List<String>> hs = conn.getHeaderFields();
                    List<String> cs = hs.get("Set-Cookie");
                    StringBuffer cok = new StringBuffer();
                    if (cs != null)
                        for (String s : cs) {
                            cok.append(s + ";");
                        }
                    returnCookie = cok.toString();
                    returnCode = conn.getResponseCode() + "";
                    resultCode = 1;
                    return new Object[] { returnCode };
                }

                //post upload
                if (p1.length != 0) {
                    OutputStream os = conn.getOutputStream();
                    os.write(mData);
                }

                int code = conn.getResponseCode();
                Map<String, List<String>> hs = conn.getHeaderFields();
                if (code >= 200 && code < 400) {
                    //String encoding=conn.getContentEncoding();
                    List<String> cs = hs.get("Set-Cookie");
                    StringBuffer cok = new StringBuffer();
                    if (cs != null) {
                        for (String s : cs) {
                            cok.append(s + ";");
                        }
                    }
                    ByteArrayOutputStream boas = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int len = 0;
                    byte[] result = null;
                    InputStream is = conn.getInputStream();
                    while ((len = is.read(data)) != -1) {
                        boas.write(data, 0, len);
                    }
                    returnContent = boas.toByteArray();
                    text = boas.toString();
                    boas.close();
                    is.close();
                    returnCode = code + "";
                    returnCookie = cok.toString();
                    resultCode = 1;
                    return new Object[] { returnCode, returnContent, returnCookie };
                } else {
                    returnCode = code + "";
                    text = conn.getResponseMessage();
                    returnContent = text.getBytes();
                    //returnCookie=cok.toString();
                    resultCode = 1;
                    return new Object[] { returnCode, text, returnContent, null };
                }
            } catch (Exception e) {
                e.printStackTrace();
                returnCode = "-1";
                text = e.getMessage();
                returnContent = text.getBytes();
                resultCode = 0;
                System.out.println(e);
                return new Object[] { returnCode, text, returnContent };
            }

        }

        private byte[] formatData(Object[] p1) throws UnsupportedEncodingException, IOException {
            // TODO: Implement this method
            byte[] bs = null;
            if (p1.length == 1) {
                Object obj = p1[0];
                if (obj instanceof String)
                    bs = ((String) obj).getBytes(mCharset);
                else if (obj.getClass().getComponentType() == byte.class)
                    bs = (byte[]) obj;
                else if (obj instanceof File)
                    bs = readAll(new FileInputStream((File) obj));
                else if (obj instanceof File)
                    bs = formatData((Map) obj);
            }
            return bs;
        }

        private byte[] formatData(Map obj) throws UnsupportedEncodingException {
            // TODO: Implement this method
            StringBuilder buf = new StringBuilder();
            return buf.toString().getBytes(mCharset);
        }

        public boolean cancel() {
            // TODO: Implement this method
            return super.cancel(true);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            // TODO: Implement this method
            super.onProgressUpdate(values);
            mCallback.onProgressChanged((int) values[0]);
        }

        @Override
        protected void onPostExecute(Object result) {
            // TODO: Implement this method
            super.onPostExecute(result);
            if (resultCode == 1)
                mCallback.onCompleted(returnCode, text, returnContent, returnCookie);
            else
                mCallback.onFailed(returnCode, text, returnContent);
        }

        private byte[] readAll(InputStream input) throws IOException {
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

        private boolean copyFile(InputStream in, OutputStream out) {
            try {
                int byteread = 0;
                byte[] buffer = new byte[1024 * 1024];
                while ((byteread = in.read(buffer)) != -1) {
                    mTotalLength += byteread;
                    int value = (int) ((mTotalLength / (float) mFileLength) * 100);
                    publishProgress(value);
                    out.write(buffer, 0, byteread);
                }
                //in.close
                //out.close
            } catch (Exception e) {
                return false;
            }
            return true;
        }
    }

    static class HttpTask2 extends AsyncTask {
        private String mUrl;
        private String mCode;
        private String mCharset;
        private String mCookie;
        private String text;
        private byte[] mContent;
        private Object[] mData;
        private OnRequestListener mCallback;
        private long progress;
        int result_Code = 1;

        public HttpTask2(String url, Object[] data, String cookie, String charset, OnRequestListener callback) {
            mUrl = url;
            mCookie = cookie;
            mCharset = charset;
            mCallback = callback;
            mData = data;
        }

        @Override
        protected Object doInBackground(Object[] p1) {
            try {
                URL url = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (mUrl.startsWith("https://")) {
                    conn = (HttpsURLConnection) url.openConnection();
                    EasyHttp.setSsl();
                }
                conn.setConnectTimeout(10000);
                conn.setFollowRedirects(true);
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setChunkedStreamingMode(0);
                conn.setAllowUserInteraction(false);
                if (mCharset == null)
                    mCharset = "UTF-8";
                conn.setRequestProperty("Accept-Charset", mCharset);

                if (mCookie != null)
                    conn.setRequestProperty("Cookie", mCookie);
                if (sHeader != null) {
                    Set<Map.Entry<String, String>> entries = sHeader.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }

                OutputStream out = new DataOutputStream(conn.getOutputStream());
                //取File参数位置
                int fileIndex = 0;
                for (int i = 1; i < mData.length; i++)
                    if (mData[i] instanceof File)
                        fileIndex = i;

                //写入前缀
                if (fileIndex > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < fileIndex; i++)
                        sb.append(mData[i]);

                    out.write(sb.toString().getBytes());
                }
                //输入文件
                File file = (File) mData[fileIndex];
                FileInputStream fis = new FileInputStream(file);
                DataInputStream in = new DataInputStream(fis);
                byte[] bufferOut = new byte[1024];
                int count = 0;
                while ((count = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, count);
                    progress += count;
                    double d = (new BigDecimal(progress / (double) file.length()).setScale(2, BigDecimal.ROUND_HALF_UP))
                            .doubleValue();
                    double d1 = d * 100;
                    publishProgress((int) d1);
                }
                in.close();
                //写入后缀
                if (mData.length > fileIndex + 1) {
                    StringBuilder sb2 = new StringBuilder();
                    for (int i = fileIndex + 1; i < mData.length; i++)
                        sb2.append(mData[i]);

                    out.write(sb2.toString().getBytes());
                }
                out.flush();
                out.close();

                int code = conn.getResponseCode();
                Map<String, List<String>> hs = conn.getHeaderFields();
                if (code >= 200 && code < 400) {
                    List<String> cs = hs.get("Set-Cookie");
                    StringBuffer cok = new StringBuffer();
                    if (cs != null)
                        for (String s : cs) {
                            cok.append(s + ";");
                        }

                    ByteArrayOutputStream boas = new ByteArrayOutputStream();
                    byte[] data = new byte[1024];
                    int len = 0;
                    byte[] result = null;
                    InputStream is = conn.getInputStream();
                    while ((len = is.read(data)) != -1) {
                        boas.write(data, 0, len);
                    }
                    mContent = boas.toByteArray();
                    text = boas.toString();
                    is.close();
                    mCode = code + "";
                    mCookie = cok.toString();
                    result_Code = 1;
                    return new Object[] { mCode, text, mContent, mCookie };
                } else {
                    mCode = code + "";
                    text = conn.getResponseMessage();
                    mContent = text.getBytes();
                    result_Code = 1;
                    return new Object[] { mCode, text, mContent, null };
                }
            } catch (Exception e) {
                result_Code = 0;
                text = e.getMessage();
                mContent = text.getBytes();
                System.out.println(e);
            }
            return new Object[] { text, mContent, mCookie };

        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            // TODO: Implement this method
            super.onProgressUpdate(values);
            mCallback.onProgressChanged((int) values[0]);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (isCancelled())
                return;
            if (result_Code == 1)
                mCallback.onCompleted(mCode, text, mContent, mCookie);
            else
                mCallback.onFailed(mCode, text, mContent);
        }

    }

    static class HttpTask3 extends AsyncTask {
        private String mUrl;
        private String mCode;
        private String mCharset;
        private String mCookie;
        private String text;
        private byte[] mContent;
        private String mPath;
        private OnRequestListener mCallback;
        private long progress;
        int result_Code = 1;

        public HttpTask3(String url, String path, String cookie, String charset, OnRequestListener callback) {
            mUrl = url;
            mCookie = cookie;
            mCharset = charset;
            mCallback = callback;
            mPath = path;
        }

        @Override
        protected Object doInBackground(Object[] p1) {
            String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成 String PREFIX = "--" , LINE_END = "\r\n";
            String PREFIX = "--", LINE_END = "\r\n";
            String CONTENT_TYPE = "multipart/form-data"; //内容类型
            try {
                URL url = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (mUrl.startsWith("https://")) {
                    conn = (HttpsURLConnection) url.openConnection();
                    EasyHttp.setSsl();
                }
                conn.setReadTimeout(20000);
                conn.setConnectTimeout(20000);
                conn.setDoInput(true); //允许输入流
                conn.setDoOutput(true); //允许输出流
                conn.setUseCaches(false); //不允许使用缓存
                conn.setRequestMethod("POST"); //请求方式
                if (mCharset == null)
                    mCharset = "UTF-8";
                conn.setRequestProperty("Accept-Charset", mCharset);

                if (mCookie != null)
                    conn.setRequestProperty("Cookie", mCookie);
                conn.setRequestProperty("connection", "keep-alive");
                conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                if (sHeader != null) {
                    Set<Map.Entry<String, String>> entries = sHeader.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                OutputStream outputSteam = conn.getOutputStream();
                DataOutputStream dos = new DataOutputStream(outputSteam);
                StringBuffer sb = new StringBuffer();
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINE_END);
                /**
                 * 这里重点注意：
                 * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
                 * filename是文件的名字，包含后缀名的 比如:abc.png
                 */
                sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + getFileName(mPath) + "\""
                        + LINE_END);
                sb.append("Content-Type: application/octet-stream; charset=" + mCharset + LINE_END);
                sb.append(LINE_END);
                dos.write(sb.toString().getBytes());
                InputStream is = new FileInputStream(mPath);
                byte[] bytes = new byte[1024];
                int len;
                while ((len = is.read(bytes)) != -1) {
                    dos.write(bytes, 0, len);
                    progress += len;
                    double d = (new BigDecimal(progress / (double) new File(mPath).length()).setScale(2,
                            BigDecimal.ROUND_HALF_UP)).doubleValue();
                    double d1 = d * 100;
                    publishProgress((int) d1);
                }
                is.close();
                dos.write(LINE_END.getBytes());
                byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                dos.write(end_data);
                dos.flush();
                /**
                 * 获取响应码 200=成功
                 * 当响应成功，获取响应的流
                 */
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                InputStream resultStream = conn.getInputStream();
                len = -1;
                byte[] buffer = new byte[1024 * 8];
                while ((len = resultStream.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                resultStream.close();
                bos.flush();
                bos.close();
                mContent = bos.toByteArray();
                text = bos.toString();
                int res = conn.getResponseCode();
                mCode = String.valueOf(res);
                result_Code = 1;
                if (res == 200) {
                    return mContent;
                } else {
                    return null;
                }
            } catch (Exception e) {
                result_Code = 0;
                mCode = "0";
                text = e.getMessage();
                mContent = text.getBytes();
                e.printStackTrace();
            }
            return new Object[] { text, mContent, mCookie };
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            // TODO: Implement this method
            super.onProgressUpdate(values);
            mCallback.onProgressChanged((int) values[0]);
        }

        @Override
        protected void onPostExecute(Object result) {
            super.onPostExecute(result);
            if (isCancelled())
                return;
            if (result_Code == 1)
                mCallback.onCompleted(mCode, text, mContent, mCookie);
            else
                mCallback.onFailed(mCode, text, mContent);
        }

    }

    public interface OnRequestListener {
        public void onCompleted(String code, String text, byte[] content, String cookie);

        public void onFailed(String code, String text, byte[] content);

        public void onProgressChanged(int value);
    }

    public static void setSsl() {
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

    static class HttpTask_Sync {
        private String mUrl;
        private byte[] mData;
        private String dataString;
        private String mCharset;
        private String mCookie;
        private String mMethod;
        private String returnCode = null;
        private String returnContent = null;
        private String returnCookie = null;
        public static final int TYPE_GET = 0;
        public static final int TYPE_POST = 1;

        public HttpTask_Sync(String url, String method, String data, String cookie, String charset) {
            mUrl = url;
            mMethod = method;
            mCookie = cookie;
            mCharset = charset;
            dataString = data;
        }

        public String getOrPost(int type) {
            try {
                URL url = new URL(mUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (mUrl.startsWith("https://")) {
                    conn = (HttpsURLConnection) url.openConnection();
                    EasyHttp.setSsl();
                }
                conn.setConnectTimeout(120000);
                conn.setFollowRedirects(true);
                conn.setDoInput(true);
                conn.setRequestProperty("Accept-Language", "zh-cn,zh;q=0.5");
                if (mCharset == null)
                    mCharset = "UTF-8";
                conn.setRequestProperty("Accept-Charset", mCharset);

                if (mCookie != null)
                    conn.setRequestProperty("Cookie", mCookie);

                if (mMethod != null)
                    conn.setRequestMethod(mMethod);

                if (!"GET".equals(mMethod) && dataString != null) {
                    mData = formatData(new String[] { dataString });
                    conn.setDoOutput(true);
                    conn.setRequestProperty("Content-length", "" + mData.length);
                }

                if (sHeader != null) {
                    Set<Map.Entry<String, String>> entries = sHeader.entrySet();
                    for (Map.Entry<String, String> entry : entries) {
                        conn.setRequestProperty(entry.getKey(), entry.getValue());
                    }
                }
                conn.connect();

                if (type == TYPE_POST) {
                    OutputStream os = conn.getOutputStream();
                    os.write(mData);
                }

                int code = conn.getResponseCode();
                Map<String, List<String>> hs = conn.getHeaderFields();
                if (code >= 200 && code < 400) {
                    List<String> cs = hs.get("Set-Cookie");
                    StringBuffer cok = new StringBuffer();
                    if (cs != null)
                        for (String s : cs) {
                            cok.append(s + ";");
                        }

                    InputStream is = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is, mCharset));
                    StringBuffer buf = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null)
                        buf.append(line + '\n');
                    is.close();
                    returnCode = code + "";
                    returnContent = new String(buf);
                    returnCookie = cok.toString();
                    return returnContent;
                } else {
                    returnCode = code + "";
                    returnContent = new String(conn.getResponseMessage());
                    return returnContent;
                }
            } catch (Exception e) {
                e.printStackTrace();
                returnCode = "-1";
                returnContent = e.getMessage();
                return returnContent;
            }
        }

        private byte[] formatData(Object[] p1) throws UnsupportedEncodingException, IOException {
            // TODO: Implement this method
            byte[] bs = null;
            if (p1.length == 1) {
                Object obj = p1[0];
                if (obj instanceof String)
                    bs = ((String) obj).getBytes(mCharset);
                else if (obj.getClass().getComponentType() == byte.class)
                    bs = (byte[]) obj;
                else if (obj instanceof File)
                    bs = readAll(new FileInputStream((File) obj));
                else if (obj instanceof File)
                    bs = formatData((Map) obj);
            }
            return bs;
        }

        private byte[] formatData(Map obj) throws UnsupportedEncodingException {
            // TODO: Implement this method
            StringBuilder buf = new StringBuilder();
            return buf.toString().getBytes(mCharset);
        }

        private byte[] readAll(InputStream input) throws IOException {
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
    }
    
    
    public static String getFileName(String path) {
        return FileUtils.getFileName(path);
    }
}