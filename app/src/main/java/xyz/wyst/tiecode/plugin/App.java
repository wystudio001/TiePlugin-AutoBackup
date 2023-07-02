package xyz.wyst.tiecode.plugin;

import com.tiecode.plugin.app.PluginApp;
import android.content.Context;
import com.tiecode.plugin.action.ActionController;
import xyz.wyst.tiecode.plugin.action.MySettingPageAction;
import xyz.wyst.tiecode.plugin.util.FileUtils;
import xyz.wyst.tiecode.plugin.controller.MyActionController;
import com.tiecode.develop.util.firstparty.android.SettingUtils;
import android.widget.*;
import android.os.*;
import android.app.*;
import android.view.*;
import android.view.View.*;
import android.webkit.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.content.*;
import xyz.wyst.tiecode.plugin.util.LogUtils;
import xyz.wyst.tiecode.plugin.util.HttpUtils;
import xyz.wyst.tiecode.plugin.backup.YunBackup;
import com.tiecode.develop.util.constant.SystemPath;
import xyz.wyst.tiecode.plugin.util.EncryUtils;
import com.tiecode.plugin.PluginConfig;
import xyz.wyst.tiecode.plugin.util.Base64Utils;
import java.io.*;

/*
* Written by WYstudio
*/

public class App extends PluginApp {

    public final static String packageName = "xyz.wyst.tiecode.plugin"; //插件的包名
    public static App mApp; //插件的上下文
    public static MyActionController mActionController; //动作控制器
    public final static String app_version = "2.3";
    private final static String message = "5b2T5YmN6Ieq5Yqo5aSH5Lu95o+S5Lu26Z2e5q2j54mI77yB6K+35LuO5a6Y5pa55rig6YGT5LiL6L29";

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
    }

    //初始化插件
    @Override
    public void onInitPlugin(Context p1) {
        LogUtils.init(p1);
        wlog("结绳启动，开始初始化插件");
        if(!EncryUtils.checkInit()){
            tips(Base64Utils.decode(message, "UTF-8"));
            wlog(Base64Utils.decode(message, "UTF-8"));
            return;
        }
        MySettingPageAction.init(p1); //初始化设置
        mActionController = new MyActionController();
        setActionController(mActionController);
        wlog("插件初始化成功");
        wlog(sysGetBackupPath());
        wlog(sysGetProjectPath());
        wlog(sysGetPluginPath());
    }

    //插件被安装
    @Override
    public void onInstall(Context p1) {
    }

    //插件被卸载
    @Override
    public void onUninstall(Context p1) {
    }

    public static boolean isDark() {
        return SettingUtils.isDarkMode();
    }

    public void tips(String str) {
        Toast.makeText(mApp, str, 3000).show();
    }
    
    public static void wlog(String str) {
        LogUtils.writeMainLog("(App.java)" + ">>>" + str);
    }
    
    public static String sysGetBackupPath(){
        return SystemPath.BACKUPS + "/";
    }
    
    public static String sysGetProjectPath(){
        return SystemPath.PROJECTS + "/";
    }
    
    public static String sysGetPluginPath(){
        return SystemPath.PLUGINS + "/";
    }
}