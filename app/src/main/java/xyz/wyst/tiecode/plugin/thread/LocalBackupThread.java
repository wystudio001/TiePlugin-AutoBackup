package xyz.wyst.tiecode.plugin.thread;

import xyz.wyst.tiecode.plugin.App;
import xyz.wyst.tiecode.plugin.util.FileUtils;
import xyz.wyst.tiecode.plugin.backup.LocalBackup;
import xyz.wyst.tiecode.plugin.action.MySettingPageAction;
import com.tiecode.plugin.api.project.ProjectContext;
import android.widget.*;
import android.os.*;
import android.app.*;
import android.view.*;
import android.view.View.*;
import android.webkit.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.content.*;
import java.io.*;
import org.json.JSONException;
import xyz.wyst.tiecode.plugin.util.LogUtils;
import org.json.JSONObject;

/*
* Written by WYstudio
*/

public class LocalBackupThread extends Thread {
    private Thread t;
    private String threadName;
    private boolean bs;
    private Activity activity;
    private File file_backup;
    private File file_save;
    private String project_name;
    private int backup_num;
    private int backup_num_new;
    private String file_save_string;
    

    public LocalBackupThread(String name, Activity act, File f_b, File f_s, String pro_name, String file_save_s) {
        threadName = name;
        activity = act;
        file_backup = f_b;
        file_save = f_s;
        project_name = pro_name;
        file_save_string = file_save_s;
    }

    public void run() {
        //tips(备份线程运行);
        LogUtils.deleteBackupLog();
        LogUtils.writeBackupLog("工程名：" + project_name);
        LogUtils.writeBackupLog("备份线程运行");
        //File file_setting = new File(MySettingPageAction.setting_file);
        File file_old_setting = new File("/sdcard/TieCode/plugin_backup/setting.json");

        FileUtils.copy(MySettingPageAction.setting_file, file_old_setting);

        LogUtils.writeBackupLog("备份路径：" + file_save_string);
        File file_nomedia = new File(MySettingPageAction.getString("备份保存路径", "/sdcard/TieCode/backup/") + ".nomedia");
        if (!file_nomedia.exists()) {
            FileUtils.createFile(file_nomedia);
            LogUtils.writeBackupLog("成功创建.nomedia文件");
        }

        if (!MySettingPageAction.getBoolean("是否覆盖备份", true)) {
            LogUtils.writeBackupLog("开始 保留备份");
            file_save_string = MySettingPageAction.getString("备份保存路径", "/sdcard/TieCode/backup/");

            backup_num = MySettingPageAction.getInt("备份数_" + project_name, 0);
            backup_num_new = getBackupDirNum(file_save_string, project_name + "_");

            if (backup_num == backup_num_new) {
                MySettingPageAction.put("备份数_" + project_name, backup_num + 1);
                backup_num = MySettingPageAction.getInt("备份数_" + project_name, 0);
            } else {
                LogUtils.writeBackupLog("备份数不相等 已修改 原" + Integer.toString(backup_num) + " 新" + Integer.toString(backup_num_new));
                backup_num = backup_num_new;
                MySettingPageAction.put("备份数_" + project_name, backup_num + 1);
                backup_num = MySettingPageAction.getInt("备份数_" + project_name, 0);
            }

            if (backup_num > 5) {
                LogUtils.writeBackupLog("保留备份 备份数大于5 开始替换");
                deleteDir_th(new File(file_save_string + project_name + "_1"));
                updataDirname_th(file_save_string + project_name + "_2", file_save_string + project_name + "_1");
                updataDirname_th(file_save_string + project_name + "_3", file_save_string + project_name + "_2");
                updataDirname_th(file_save_string + project_name + "_4", file_save_string + project_name + "_3");
                updataDirname_th(file_save_string + project_name + "_5", file_save_string + project_name + "_4");
                LogUtils.writeBackupLog("替换完毕");
                MySettingPageAction.put("备份数_" + project_name, 5);

                file_save = new File(file_save_string + project_name + "_5");
                if (MySettingPageAction.getBoolean("是否开启压缩备份", false)) {
                    LogUtils.writeBackupLog("开始压缩备份");
                    if (ProjectContext.getCurrentProject().backup()) {
                        try {
                            FileUtils.moveTo(
                                    new File(App.sysGetBackupPath() + project_name + ".tsp"),
                                    new File(file_save_string + project_name + "_5/" + project_name + ".tsp"));
                        } catch (IOException e) {
                            //e
                            LogUtils.printfError(activity,e.toString());
                        }
                        bs = true;
                        LogUtils.writeBackupLog("压缩备份成功");
                    } else {
                        bs = false;
                        LogUtils.writeBackupLog("压缩备份失败");
                    }
                } else {
                    bs = FileUtils.copyFolder(file_backup, file_save);
                    LogUtils.writeBackupLog("非压缩备份");
                }
            } else {
                LogUtils.writeBackupLog("保留备份 备份数小于五");
                file_save = new File(file_save_string + project_name + "_" + backup_num + "/");
                if (file_save.exists()) {

                }

                if (MySettingPageAction.getBoolean("是否开启压缩备份", false)) {
                    LogUtils.writeBackupLog("开始压缩备份");
                    if (ProjectContext.getCurrentProject().backup()) {
                        try {
                            FileUtils.moveTo(
                                    new File(App.sysGetBackupPath()
                                            + project_name + ".tsp"),
                                    new File(file_save_string + project_name + "_" + backup_num + "/" + project_name
                                            + ".tsp"));
                        } catch (IOException e) {
                            //e
                            LogUtils.printfError(activity,e.toString());
                        }
                        bs = true;
                        LogUtils.writeBackupLog("压缩备份成功");
                    } else {
                        bs = false;
                        LogUtils.writeBackupLog("压缩备份失败");
                    }
                } else {
                    bs = FileUtils.copyFolder(file_backup, file_save);
                    LogUtils.writeBackupLog("非压缩备份");
                }
            }
        } else {
            LogUtils.writeBackupLog("开始 覆盖备份");
            if (MySettingPageAction.getBoolean("是否开启压缩备份", false)) {
                LogUtils.writeBackupLog("开始压缩备份");
                if (ProjectContext.getCurrentProject().backup()) {
                    try {
                        FileUtils.moveTo(
                                new File(App.sysGetBackupPath()
                                        + project_name + ".tsp"),
                                new File(file_save_string + project_name + "/" + project_name + ".tsp"));
                    } catch (IOException e) {
                        //e
                        LogUtils.printfError(activity,e.toString());
                    }
                    bs = true;
                    LogUtils.writeBackupLog("压缩备份成功");
                } else {
                    bs = false;
                    LogUtils.writeBackupLog("压缩备份失败");
                }
            } else {
                bs = FileUtils.copyFolder(file_backup, file_save);
                LogUtils.writeBackupLog("非压缩备份");
            }
        }

        if (bs) {
            LogUtils.writeBackupLog("自动备份成功");
            LocalBackup.hui(true);
        } else {
            LogUtils.writeBackupLog("自动备份失败");
            LocalBackup.hui(false);
        }
    }

    public void start() {
        LogUtils.writeBackupLog("备份线程启动");
        if (t == null) {
            t = new Thread(this, threadName);
            t.start();
        }
    }

    //重命名文件夹
    public static boolean updataDirname_th(String file_name_yuan, String file_name_after) {
        File oldfile = new File(file_name_yuan);
        if (!oldfile.exists()) {
            return false;
        }
        File newfile = new File(file_name_after);
        if (newfile.exists()) {
            return false;
        }
        if (oldfile.renameTo(newfile)) {
            return true;
        }
        return false;
    }

    //删除文件夹
    public static boolean deleteDir_th(File file) {
        boolean success = true;
        if (file.exists()) {
            if (file.isDirectory()) {
                for (File subFile : file.listFiles()) {
                    if (!success) {
                        return false;
                    }
                    success = success && deleteDir_th(subFile);
                }
            }
            if (success)
                success = success && file.delete();
        }
        return success;
    }

    public static int getBackupDirNum(String path, String keyword) {
        int num = 0;
        for (File f : new File(path).listFiles()) {
            if (f.getName().indexOf(keyword) >= 0) {
                num = num + 1;
            }
        }
        return num;
    }

    
}