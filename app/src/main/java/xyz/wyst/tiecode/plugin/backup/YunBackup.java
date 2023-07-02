package xyz.wyst.tiecode.plugin.backup;

import xyz.wyst.tiecode.plugin.App;
import xyz.wyst.tiecode.plugin.action.MySettingPageAction;
import xyz.wyst.tiecode.plugin.util.HttpUtils;
import xyz.wyst.tiecode.plugin.util.StringUtils;
import java.util.Date;
import java.lang.*;
import xyz.wyst.tiecode.plugin.util.LogUtils;
import org.json.*;
import xyz.wyst.tiecode.plugin.backup.YunBackup;
import java.net.URLEncoder;
import xyz.wyst.tiecode.plugin.util.EncryUtils;
import java.io.File;

/*
* Written by WYstudio
*/

public class YunBackup {
    private final static App app = App.mApp;
    private final static String classname = "(YunBackup.java)";
    private static HttpUtils httpHtils = new HttpUtils();

    public static void checkLzyFolderId() {
        wlog("开始检查本地文件夹id是否与云端相等");
        String fol_id_set = MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_FOID, "");
        String fol_id_new = getLzyFolderId(-1, "TieCode");
        if (!fol_id_set.equals("")) {
            if (fol_id_new != null) {
                if (fol_id_new.equals("-1")) {
                    if (newLzyFolder("TieCode")) {
                        String f_id = getLzyFolderId(-1, "TieCode");
                        MySettingPageAction.put(MySettingPageAction.NAME_TEXT_YUN_USER_FOID, f_id);
                        wlog("云端不存在文件夹，已新建。本地已更新id：" + f_id);
                    } else {
                        wlog("云端不存在文件夹，且创建失败");
                    }
                } else {
                    if (fol_id_new.equals(fol_id_set)) {
                        wlog("本地与云端文件夹id相等");
                    } else {
                        MySettingPageAction.put(MySettingPageAction.NAME_TEXT_YUN_USER_FOID, fol_id_new);
                        wlog("本地与云端文件夹id不相等已更改 本地：" + fol_id_set + "云端：" + fol_id_new);
                    }
                }
            } else {
                wlog("无法获取云端文件夹id");
            }
        } else {
            wlog("本地还没有保存文件夹id开始新建");
            LogUtils.writeBackupLog("开始获取文件夹id");
            if (fol_id_new.equals("-1")) {
                LogUtils.writeBackupLog("开始新建文件夹");
                if (YunBackup.newLzyFolder("TieCode")) {
                    String fol_id = YunBackup.getLzyFolderId(-1, "TieCode");
                    LogUtils.writeBackupLog("创建文件夹成功，id:" + fol_id);
                    MySettingPageAction.put(MySettingPageAction.NAME_TEXT_YUN_USER_FOID, fol_id);
                    LogUtils.writeBackupLog("文件夹id获取成功并已写入设置");
                } else {
                    LogUtils.writeBackupLog("新建文件夹失败，请检查cookie");
                    return;
                }
            }
        }
    }

    public static String loginLzy(String user_name, String user_pass) {
        wlog("开始通过账号密码登录蓝奏云");
        if (user_name.equals("") || user_pass.equals("")) {
            wlog("错误，未设置账号密码");
            return "未设置账号密码";
        }

        String time = String.valueOf(System.currentTimeMillis() / 1000);
        JSONObject json_d = new JSONObject();
        try {
            json_d.put("username", user_name);
            json_d.put("userpassword", user_pass);
            json_d.put("time", time);
        } catch (Exception e) {
            LogUtils.printfException(app, e);
            return "json解析时出现错误";
        }
        String data = EncryUtils.encode(json_d.toString());
        String wy_url = "https://api.wystudio.xyz/wy/tiecode_plugin/AutoBackup/login_lzy.php";

        String[] bs = httpHtils.Post(wy_url, data, null);
        String content = bs[0];

        if (content.equals("请求失败") || content == null) {
            wlog("登录蓝奏云返回空数据");
            return "请检查网络是否正常";
        }

        try {
            JSONObject json_f = new JSONObject(content);
            if (json_f.getInt("code") == 200) {
                if (!json_f.getString("cookie").equals("")) {
                    MySettingPageAction.put(MySettingPageAction.NAME_TEXT_YUN_USER_COOKIE, json_f.getString("cookie"));
                    wlog("获取cookie成功！\n" + json_f.getString("cookie"));
                    return "成功";
                } else {
                    wlog("登录cookie获取失败，请重试");
                    return "登录cookie获取失败，请重试";
                }
            } else {
                MySettingPageAction.put(MySettingPageAction.NAME_TEXT_YUN_USER_COOKIE, json_f.getString("cookie"));
                wlog("登录错误：" + json_f.getString("msg"));
                return json_f.getString("msg");
            }
        } catch (Exception e) {
            LogUtils.printfException(app, e);
            return "json解析时出现错误";
        }
    }

    private static void wlog(String str) {
        LogUtils.writeMainLog(classname + ">>>" + str);
    }

    public static String initLzy(String user_cookie) {
        wlog("开始初始化获取蓝奏云文件夹");
        /*
        String res_get = httpHtils.GET("https://pc.woozooo.com/account.php?action=login&ref=/mydisk.php",null);
        String token_y = StringUtils.jiequ_string(res_get,"></script><script language=\"javascript\">var nc_token = [\"","\", (new",false);
        String token = token_y + (new Date()).getDate() + Math.random();
        String[] result = httpHtils.Post("https://pc.woozooo.com/account.php","edf=asss",null);
        String content = result[0];
        String cookie = result[1];
        return content;
        */
        //String cookie2 = "phpdisk_info=ADUENgFmBTFSZwFgWTBaCQZnBD8MXAJkBzFTOwI0UWFZZV5uAGUHOw88A2EKWVQ6ATIFYwhpUzFVNAZgADQKbABnBGUBMQU%2FUmMBZ1llWmIGMgRjDGYCYQc9UzsCYFFrWT5ebgBkBz0PPwNnCmhUBwFgBTIIaVM0VWQGbgA0Cj8ANAQ3AWE%3D;__51laig__=15;__tins__21412745=%7B%22sid%22%3A%201665546444984%2C%20%22vd%22%3A%202%2C%20%22expires%22%3A%201665548678714%7D;folder_id_c=-1;__51cke__=;ylogin=1058713;uag=fa1d6e0fd8ee6d3935f2ac21903c75de;_uab_collina=166549061701716424465424;PHPSESSID=k5vfb92i8shonr5muq85snt9a6gh147v";
        //String cookie3 = "ADUENgFmBTFSZwFgWTBaCQZnBD8MXAJkBzFTOwI0UWFZZV5uAGUHOw88A2EKWVQ6ATIFYwhpUzFVNAZgADQKbABnBGUBMQU%2FUmMBZ1llWmIGMgRjDGYCYQc9UzsCYFFrWT5ebgBkBz0PPwNnCmhUBwFgBTIIaVM0VWQGbgA0Cj8ANAQ3AWE%3D";
        if (user_cookie.equals("")) {
            wlog("cookie未设置，未登录");
            return "cookie未设置，未登录";
        }

        String folder_id = getLzyFolderId(-1, "TieCode");
        if (folder_id == null) {
            wlog("无法获取文件列表，请检查cookie");
            return "无法获取文件列表，请检查cookie";
        } else {
            wlog("文件列表获取成功！");
            if (folder_id.equals("-1")) {
                boolean bs_new = newLzyFolder("TieCode");
                if (bs_new) {
                    wlog("获取已创建文件夹id");
                    String f_id_2 = getLzyFolderId(-1, "TieCode");
                    if (f_id_2 == null) {
                        wlog("无法获取文件列表，请检查登录状态");
                        return "无法获取文件列表，请检查登录状态";
                    }

                    if (f_id_2.equals("-1")) {
                        wlog("已创建但获取失败，请重试！");
                        return "已创建获取失败，请重试！";
                    }

                    MySettingPageAction.put(MySettingPageAction.NAME_TEXT_YUN_USER_FOID, f_id_2);
                    wlog("获取成功id：" + f_id_2);
                    return "成功";
                } else {
                    wlog("创建文件夹失败，请检查登录状态");
                    return "创建文件夹失败，请检查登录状态";
                }
            } else {
                MySettingPageAction.put(MySettingPageAction.NAME_TEXT_YUN_USER_FOID, folder_id);
                wlog("成功");
                return "成功";
            }
        }

    }

    public static String deleteLzyFile(int folder_id, String file_name) {
        String file_id = getLzyFileId(folder_id, file_name);
        if (file_id == null) {
            return "错误，cookie可能已过期";
        }

        if (file_id.equals("-1")) {
            return "没有文件";
        }

        String user_cookie = MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_COOKIE, "");
        if (user_cookie.equals("")) {
            return "错误，cookie未设置";
        }

        String[] t = httpHtils.Post("https://pc.woozooo.com/doupload.php", "task=6&file_id=" + file_id, user_cookie);
        String t2 = StringUtils.decodeUnicode(t[0]);

        try {
            JSONObject json = new JSONObject(t2);
            int zt = json.getInt("zt");
            if (zt == -1) {
                return "错误，cookie可能已过期";
            }
            String info = json.getString("info");
            if (info.equals("已删除")) {
                return "成功";
            } else {
                return "错误，" + info;
            }
        } catch (Exception e) {
            LogUtils.printfException(app, e);
            return "出现错误";
        }

    }

    public static String getLzyFileId(int f_fol_id, String file_name) {
        int a = -1;
        for (int cishu = 1; cishu < 100; cishu++) {
            String t2 = getLzyFileList(f_fol_id, false, cishu);
            if (t2 == null && cishu > 1) {
                return "-1";
            } else if (t2 == null && cishu == 1) {
                return null;
            }

            try {
                JSONObject json = new JSONObject(t2);
                if (json.getInt("zt") != 1) {
                    wlog("请求错误，" + json.getString("info"));
                    return null;
                }
                JSONArray json_text = json.getJSONArray("text");
                wlog("数组长度：" + json_text.length());
                if (json_text.length() == 0) {
                    return null;
                }
                int i;
                for (i = 0; i < json_text.length(); i++) {
                    JSONObject json_text_o = json_text.getJSONObject(i);
                    String name = json_text_o.getString("name");
                    if (name.equals(file_name)) {
                        a = i;
                        break;
                    }
                }

                if (a != -1) {
                    String file_id_2 = json_text.getJSONObject(a).getString("id");
                    wlog("文件id：" + file_id_2);
                    return file_id_2;
                }

            } catch (Exception e) {
                LogUtils.printfException(app, e);
                return null;
            }
        }
        return "-1";
    }

    public static String uploadLzyFile(String file_path_string, HttpUtils.OnUploadListener callback) {
        String user_cookie = MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_COOKIE, "");
        String user_folderid = MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_FOID, "");
        if (user_cookie.equals("")) {
            wlog("cookie未设置");
            return "cookie未设置";
        }

        if (user_folderid.equals("")) {
            wlog("folder_id未获取");
            return "folder_id未获取";
        }

        String t = httpHtils.UPLOAD("https://up.woozooo.com/html5up.php", user_cookie, file_path_string, user_folderid,
                callback);
        String t2 = StringUtils.decodeUnicode(t);
        wlog("文件上传返回数据：\n" + t);
        wlog("文件上传返回数据(decodeUnicode)：\n" + t2);
        try {
            if (t2.equals("") || t2 == null) {
                return "请检查网络后重试！";
            }
            if (t2.startsWith("请求错误", 0)) {
                return t2;
            }
            JSONObject json = new JSONObject(t2);
            int zt = json.getInt("zt");
            if (zt != 1) {
                return "错误，cookie可能已过期";
            }
            String info = json.getString("info");
            if (info.equals("上传成功")) {
                return "成功";
            } else {
                return "错误，" + info;
            }
        } catch (Exception e) {
            LogUtils.printfException(app, e);
            return "解析json时出现错误！";
        }
    }

    public static boolean checkLogin() {

        return true;
    }

    public static String getLzyFileList(int fol_id, boolean sf_folder, int page) {
        String user_cookie = MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_COOKIE, "");
        if (user_cookie.equals("")) {
            return null;
        }
        String t2;
        if (sf_folder) {
            String[] t = httpHtils.Post("https://pc.woozooo.com/doupload.php",
                    "task=47&folder_id=" + fol_id + "&pg=" + page, user_cookie);
            t2 = StringUtils.decodeUnicode(t[0]);
        } else {
            String[] t = httpHtils.Post("https://pc.woozooo.com/doupload.php",
                    "task=5&folder_id=" + fol_id + "&pg=" + page, user_cookie);
            t2 = StringUtils.decodeUnicode(t[0]);
        }
        wlog("获取文件列表http返回内容：\n" + t2);
        if (t2.equals("请求失败")){
            return null;
        }
        return t2;
    }

    public static boolean newLzyFolder(String f_name) {
        String user_cookie = MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_COOKIE, "");
        if (user_cookie.equals("")) {
            return false;
        }
        String[] t = httpHtils.Post("https://pc.woozooo.com/doupload.php",
                "task=2&parent_id=0&folder_name=" + f_name + "&folder_description=" + URLEncoder.encode("由结绳自动备份插件创建"),
                user_cookie);
        String t2 = StringUtils.decodeUnicode(t[0]);

        wlog("创建文件夹http返回内容" + t2);
        try {
            JSONObject json = new JSONObject(t2);
            if (json.getInt("zt") != 1) {
                wlog("请求错误，" + json.getString("info"));
                return false;
            }
            String info = json.getString("info");
            if (info.equals("创建成功")) {
                wlog("创建文件夹" + f_name + "成功");
                return true;
            } else {
                wlog("创建文件夹" + f_name + "失败");
                return false;
            }
        } catch (Exception e) {
            LogUtils.printfException(app, e);
            return false;
        }
    }

    public static String getLzyFolderId(int f_fol_id, String fol_name) {
        wlog("获取文件夹id");
        int a = -1;
        for (int cishu = 1; cishu < 100; cishu++) {

            String t2 = getLzyFileList(f_fol_id, true, cishu);
            if (t2 == null && cishu > 1) {
                return "-1";
            } else if (t2 == null && cishu == 1) {
                return null;
            }

            try {
                JSONObject json = new JSONObject(t2);
                if (json.getInt("zt") != 1) {
                    wlog("请求错误，" + json.getString("info"));
                    return null;
                }
                JSONArray json_text = json.getJSONArray("text");
                wlog("数组长度：" + json_text.length());
                if (json_text.length() == 0) {
                    return null;
                }
                int i;
                for (i = 0; i < json_text.length(); i++) {
                    JSONObject json_text_o = json_text.getJSONObject(i);
                    String name = json_text_o.getString("name");
                    if (name.equals(fol_name)) {
                        a = i;
                        break;
                    }
                }

                if (a != -1) {
                    String folder_id = json_text.getJSONObject(a).getString("fol_id");
                    wlog("文件夹id：" + folder_id);
                    return folder_id;
                }

            } catch (Exception e) {
                LogUtils.printfException(app, e);
                return null;
            }

        }

        return "-1";
    }
}