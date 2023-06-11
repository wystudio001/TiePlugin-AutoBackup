package xyz.wyst.tiecode.plugin.backup;

import xyz.wyst.tiecode.plugin.App;
import xyz.wyst.tiecode.plugin.util.FileUtils;
import xyz.wyst.tiecode.plugin.action.MySettingPageAction;
import xyz.wyst.tiecode.plugin.thread.LocalBackupThread;
import android.widget. *;
import android.os. *;
import android.app. *;
import android.view. *;
import android.view.View. *;
import android.webkit. *;
import android.graphics. *;
import android.graphics.drawable.*;
import android.content. *;
import java.io. *;

/*
* Written by WYstudio
*/

public class LocalBackup {

   public static Activity act;
   public static boolean plugin_start_complete;
   
   public static void start(Activity activity, File file_backup, File file_save, String project_name, String file_save_string) {
      act = activity;
      plugin_start_complete = MySettingPageAction.getBoolean("完成后是否弹出提示", true);
      LocalBackupThread T1 = new LocalBackupThread("Thread-1", activity, file_backup, file_save, project_name, file_save_string);
      T1.start();
   }   
   
   public static void hui(boolean sf) {
       if(plugin_start_complete) {
           if(sf) {
                Looper.prepare();
                Toast.makeText(act, "自动备份成功", 1500).show();
                Looper.loop();
           }else{
                Looper.prepare();
                Toast.makeText(act, "自动备份失败", 1500).show();
                Looper.loop();
           }
       }
   }
   
   public static void tips(String str) {
        Toast.makeText(act, str, 1500).show();
   }
}