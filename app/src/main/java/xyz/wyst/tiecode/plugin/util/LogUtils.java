package xyz.wyst.tiecode.plugin.util;

import xyz.wyst.tiecode.plugin.App;
import xyz.wyst.tiecode.plugin.util.FileUtils;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.io.File;
import java.io.IOException;
import android.content.Context;
import java.io.PrintWriter;
import java.io.StringWriter;
import android.app.AlertDialog.Builder;
import android.view.View.OnClickListener;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.widget.Toast;
import android.view.View;
import com.tiecode.develop.component.widget.dialog.BottomDialog;
import com.tiecode.develop.component.widget.log.TieLogcatView;
import android.app.Activity;
import xyz.wyst.tiecode.plugin.action.MyCodePageAction;
import com.tiecode.plugin.api.project.ProjectContext;

/*
* Written by WYstudio
*/

public class LogUtils {
    private static File file_log_backup;
    private static File file_log_error;
    private static File file_log_main;
    private static TieLogcatView tielogcatview;

    public static void init(Context context) {
        tielogcatview = new TieLogcatView(context);
        file_log_backup = new File(context.getExternalFilesDir(null).getAbsolutePath() + "/plugins/" + App.packageName
                + "/log_backup.txt");
        file_log_error = new File(
                context.getExternalFilesDir(null).getAbsolutePath() + "/plugins/" + App.packageName + "/log_error.txt");
        file_log_main = new File(
                context.getExternalFilesDir(null).getAbsolutePath() + "/plugins/" + App.packageName + "/log_main.txt");
        deleteMainLog();
        deleteBackupLog();
        deleteErrorLog();
    }

    public static void writeBackupLog(String log) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String date_now = "[" + formatter.format(date) + "]";
        String log_y = date_now + " " + log + '\n';
        if (!file_log_backup.exists()) {
            FileUtils.createFile(file_log_backup);
            try {
                FileUtils.addString(file_log_backup, log_y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileUtils.addString(file_log_backup, log_y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeMainLog(String log) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String date_now = "[" + formatter.format(date) + "]";
        String log_y = date_now + " " + log + '\n';
        if (!file_log_main.exists()) {
            FileUtils.createFile(file_log_main);
            try {
                FileUtils.addString(file_log_main, log_y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileUtils.addString(file_log_main, log_y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeErrorLog(String log) {
        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
        String date_now = "[" + formatter.format(date) + "]";
        String log_y = date_now + " " + log + '\n';
        if (!file_log_error.exists()) {
            FileUtils.createFile(file_log_error);
            try {
                FileUtils.addString(file_log_error, log_y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileUtils.addString(file_log_error, log_y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteErrorLog() {
        if (file_log_error.exists()) {
            FileUtils.deleteFile(file_log_error);
        }
    }

    public static void deleteBackupLog() {
        if (file_log_backup.exists()) {
            FileUtils.deleteFile(file_log_backup);
        }
    }

    public static void deleteMainLog() {
        if (file_log_main.exists()) {
            FileUtils.deleteFile(file_log_main);
        }
    }

    public static void printfException(Context context, Throwable th) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        printWriter.close();
        if (MyCodePageAction.activity != null) {
            MyCodePageAction.activity.runOnUiThread(new Runnable() {
                public void run() {
                    printfMessage(context, "自动备份插件出现异常", "请将以下信息反馈给插件作者\n\n" + stringWriter.toString());
                    writeErrorLog(stringWriter.toString());
                    tielogcatview.sendLog("自动备份插件出现异常\n请将以下信息反馈给插件作者\n\n" + stringWriter.toString());
                }
            });
        }else{
            writeErrorLog(stringWriter.toString());
        }
    }

    public static void printfError(Activity context, String error) {
        context.runOnUiThread(new Runnable() {
            public void run() {
                printfMessage(context, "自动备份插件出现异常", "请将以下信息反馈给插件作者\n\n" + error);
                tielogcatview.sendLog("自动备份插件出现异常\n请将以下信息反馈给插件作者\n\n" + error);
                writeErrorLog(error);
            }
        });
    }

    public static void printfMessage(final Context context, CharSequence charSequence,
            final CharSequence charSequence2) {
        String str = "复制内容";
        try {
            final BottomDialog builder = new BottomDialog(context).builder();
            builder.setTitle(charSequence.toString());
            builder.setContent(charSequence2.toString());
            builder.setPositiveButton(str, new OnClickListener() {
                public void onClick(View view) {
                    ((ClipboardManager) context.getSystemService("clipboard")).setText(charSequence2);
                    Toast.makeText(context, "复制成功", 0).show();
                    builder.dismiss();
                }
            });
            builder.show();
        } catch (Throwable th) {
            try {
                Builder builder2 = new Builder(context);
                builder2.setTitle(charSequence);
                builder2.setMessage(charSequence2);
                builder2.setPositiveButton("复制", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((ClipboardManager) context.getSystemService("clipboard")).setText(charSequence2);
                        Toast.makeText(context, "复制成功", 0).show();
                    }
                });
                builder2.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder2.create().show();
            } catch (Throwable th2) {
                th2.printStackTrace();
            }
        }
    }
}