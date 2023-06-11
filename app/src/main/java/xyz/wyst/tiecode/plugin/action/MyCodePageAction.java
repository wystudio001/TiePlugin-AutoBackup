package xyz.wyst.tiecode.plugin.action;

import com.tiecode.plugin.action.page.code.CodePageAction;
import xyz.wyst.tiecode.plugin.R;
import com.tiecode.develop.component.api.option.TieMenu;
import com.tiecode.develop.component.widget.option.TieMenuItem;
import com.tiecode.develop.component.api.option.TieItem;
import xyz.wyst.tiecode.plugin.App;
import xyz.wyst.tiecode.plugin.backup.LocalBackup;
import xyz.wyst.tiecode.plugin.util.FileUtils;
import com.tiecode.develop.component.api.item.ISettingItemView;
import com.tiecode.develop.component.api.item.ITextSwitchItemView;
import com.tiecode.plugin.action.page.setting.PluginSettingPageAction;
import com.tiecode.plugin.api.project.ProjectContext;
import com.tiecode.develop.component.widget.dialog.BottomInputDialog;
import com.tiecode.develop.component.api.item.ITextArrowItemView;
import com.tiecode.develop.component.widget.item.setting.TextArrowItemView;
import com.tiecode.develop.component.widget.dialog.IBottomDialog;
import com.tiecode.develop.component.widget.dialog.BottomDialog;
import android.widget.Toast;
import android.content.Context;
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
import android.view.ViewGroup.LayoutParams;
import xyz.wyst.tiecode.plugin.action.MySettingPageAction;
import xyz.wyst.tiecode.plugin.util.LogUtils;
import xyz.wyst.tiecode.plugin.util.HttpUtils;
import xyz.wyst.tiecode.plugin.backup.YunBackup;
import xyz.wyst.tiecode.plugin.backup.CommonBackup;
import xyz.wyst.tiecode.plugin.util.ViewUtils;
import com.tiecode.develop.component.module.layout.impl.TieLinearLayout;
import com.tiecode.develop.component.module.text.impl.TieTextView;
import android.widget.LinearLayout;

/*
* Written by WYstudio
*/

//代码编辑器界面
public class MyCodePageAction extends CodePageAction {
    public static File file_save; //设置文件的保存路径
    public static File file_backup;
    public static String project_path;
    public static String project_name;
    public static String file_save_string;
    public boolean plugin_start;
    public boolean backup_start_fugai;
    public boolean backup_tishi;

    private static HttpUtils httpUtils;
    public static Activity activity;

    @Override
    public void onCreate() {
        activity = getActivity();
        project_path = ProjectContext.getCurrentProject().getProjectDir().getAbsolutePath();
        project_name = ProjectContext.getCurrentProject().getName();

        file_save = new File(MySettingPageAction.getString("备份保存路径", "/sdcard/TieCode/backup/"));
        file_save_string = MySettingPageAction.getString("备份保存路径", "/sdcard/TieCode/backup/");
        file_backup = new File(project_path);
        plugin_start = MySettingPageAction.getBoolean("总开关", true);
        backup_tishi = MySettingPageAction.getBoolean("完成后是否弹出提示", true);

        CommonBackup.initBackup();
        if (plugin_start) {
            if (MySettingPageAction.getBoolean(MySettingPageAction.NAME_SWITCH_YUN, false)) {
                if (!HttpUtils.checkConnectNetwork()) {
                    tips("无网络连接，云备份取消");
                    return;
                }
                LogUtils.writeBackupLog("工程名：" + project_name);
                LogUtils.writeBackupLog("云端备份线程开启");
                if (MySettingPageAction.getBoolean(MySettingPageAction.NAME_SWTICH_YUN_USER_WIFI, false)) {
                    if (HttpUtils.checkNetworkIsWifi()) {
                        backup_yun();
                    }
                } else {
                    backup_yun();
                }
            } else {
                backup_zi();
            }
        }
    }

    @Override
    public void onLoadProject() {
    }

    //创建菜单
    @Override
    public void onCreateMenu(TieMenu p1) {
        if (App.isDark()) {
            p1.addItem(
                    new TieMenuItem("自动备份插件", App.mApp.getResources().getDrawable(R.drawable.ic_launcher_dark, null)));
        } else {
            p1.addItem(new TieMenuItem("自动备份插件", App.mApp.getResources().getDrawable(R.drawable.ic_launcher, null)));
        }
    }

    //菜单被单击
    @Override
    public void onMenuClick(TieItem p1) {
        if (p1.getTitle() == "自动备份插件") {
            showDialog_menu();
        }
    }

    @Override
    public void onCreateToolMenu(TieMenu p1) {
        //p1.addItem(new TieMenuItem("111", App.mApp.getResources().getDrawable(R.drawable.ic_launcher_dark, null)));
    }

    @Override
    public void onCreateProjectMenu(TieMenu p1) {
        if (App.isDark()) {
            p1.addItem(new TieMenuItem("手动云备份", App.mApp.getResources().getDrawable(R.drawable.cloud_dark, null)));
        } else {
            p1.addItem(new TieMenuItem("手动云备份", App.mApp.getResources().getDrawable(R.drawable.cloud, null)));
        }
    }

    @Override
    public void onProjectMenuClick(TieItem p1) {
        if (p1.getTitle() == "手动云备份") {
            backup_yun_shou();
        }
    }

    public void showDialog_menu() {
        BottomDialog builder = new BottomDialog(getActivity()).builder();
        TieLinearLayout linearLayout = new TieLinearLayout(getActivity());
        TieTextView text1 = new TieTextView(getActivity());
        TextArrowItemView itext1 = new TextArrowItemView(getActivity());
        TextArrowItemView itext2 = new TextArrowItemView(getActivity());
        TextArrowItemView itext3 = new TextArrowItemView(getActivity());
        TextArrowItemView itext4 = new TextArrowItemView(getActivity());
        linearLayout.setLayoutParams(new LayoutParams(-1, -1));
        linearLayout.setOrientation(1);
        linearLayout.addView(text1);
        linearLayout.addView(itext1);
        linearLayout.addView(itext2);
        linearLayout.addView(itext3);
        linearLayout.addView(itext4);
        builder.setTitle("自动备份插件");
        builder.setContent(linearLayout);
        builder.show();
        itext1.setDefaultText("");
        itext2.setDefaultText("");
        itext3.setDefaultText("");
        itext4.setDefaultText("");
        itext1.setTitle("自动备份功能");
        itext2.setTitle("是否覆盖备份");
        itext3.setTitle("是否压缩备份");
        itext4.setTitle("是否云端备份");
        text1.setText("  当前工程：" + project_name + "\n  工程路径：" + project_path + "\n");

        if (MySettingPageAction.getBoolean(MySettingPageAction.NAME_SWITCH, true)) {
            itext1.setDescription("已开启");
        } else {
            itext1.setDescription("已关闭");
        }

        if (MySettingPageAction.getBoolean("是否覆盖备份", true)) {
            itext2.setDescription("已开启\n当前处于覆盖备份");
        } else {
            itext2.setDescription("已关闭\n当前处于保留备份");
        }

        if (MySettingPageAction.getBoolean("是否开启压缩备份", false)) {
            itext3.setDescription("已开启\n当前处于压缩备份");
        } else {
            itext3.setDescription("已关闭\n当前处于文件备份");
        }

        if (MySettingPageAction.getBoolean("是否开启云端备份", false)) {
            itext4.setDescription("已开启\n当前处于云端备份");
        } else {
            itext4.setDescription("已关闭\n当前处于本地备份");
        }
    }

    public void backup_zi() {
        LocalBackup.start(getActivity(), file_backup, file_save, project_name, file_save_string);
    }

    public void tips(String str) {
        Toast.makeText(getActivity(), str, 1500).show();
    }

    public void backup_yun() {
        new Thread(new Runnable() {
            public void run() {
                if (MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_COOKIE, "").equals("")) {
                    LogUtils.writeBackupLog("cookie未设置！无法备份");
                    return;
                }

                YunBackup.checkLzyFolderId();
                YunBackup.deleteLzyFile(
                        Integer.parseInt(
                                MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_FOID, "")),
                        project_name + ".zip");
                if (ProjectContext.getCurrentProject().backup()) {
                    LogUtils.writeBackupLog("创建备份文件成功");

                    String file_path = App.sysGetBackupPath() + project_name + ".tsp";
                    String file_path2 = "/sdcard/TieCode/backup_cloud/" + project_name + ".zip";
                    //File file_p = new File(file_path);
                    //file_p.renameTo(new File(file_path2));
                    if (!FileUtils.renameFile(file_path, file_path2)) {
                        LogUtils.writeBackupLog("文件移动失败！！");
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                tips("文件更名失败，请重试！");
                            }
                        });
                        return;
                    }
                    LogUtils.writeBackupLog("开始上传文件");
                    String bs = YunBackup.uploadLzyFile(file_path2, new HttpUtils.OnUploadListener() {
                        @Override
                        public void onProgressChanged(double value, String value2) {
                        }
                    });
                    if (bs.equals("成功")) {
                        LogUtils.writeBackupLog("文件上传成功");
                        if (backup_tishi) {
                            getActivity().runOnUiThread(new Runnable() {

                                public void run() {
                                    tips("自动云备份成功");
                                }
                            });
                        }
                        LogUtils.writeBackupLog("自动云备份成功！");
                    } else {
                        LogUtils.writeBackupLog("文件上传失败，详情：" + bs);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                tips(bs);
                            }
                        });
                    }
                    FileUtils.deleteFile(new File(file_path2));
                } else {
                    LogUtils.writeBackupLog("备份文件创建失败！！");
                    return;
                }
            }
        }).start();
    }

    public void backup_yun_shou() {
        if (!HttpUtils.checkConnectNetwork()) {
            tips("无网络连接，云备份取消");
            return;
        }
        ViewUtils.showProgressDialog(getActivity(), "请稍等", "正在备份中......\n当前操作：初始化备份\n进度：0%");
        new Thread(new Runnable() {
            public void run() {
                if (MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_COOKIE, "").equals("")) {
                    LogUtils.writeBackupLog("手动备份-cookie未设置！无法备份");
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ViewUtils.dismissProgressDialog();
                            tips("云盘未登录！");
                        }
                    });
                    return;
                }
                LogUtils.writeBackupLog("手动备份-当前工程：" + project_name);
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ViewUtils.updataProgressDialog("正在备份中......\n当前操作：检查蓝奏云相关配置\n(若卡在这一步，请检查网络是否正常后并重试！)\n进度：0%");
                    }
                });
                YunBackup.checkLzyFolderId();
                YunBackup.deleteLzyFile(
                        Integer.parseInt(
                                MySettingPageAction.getString(MySettingPageAction.NAME_TEXT_YUN_USER_FOID, "")),
                        project_name + ".zip");
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        ViewUtils.updataProgressDialog("正在备份中......\n当前操作：创建备份文件\n(若工程较大，时间可能较长)\n进度：0%");
                    }
                });
                if (ProjectContext.getCurrentProject().backup()) {
                    LogUtils.writeBackupLog("手动备份-创建备份文件成功");
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ViewUtils.updataProgressDialog("正在备份中......\n当前操作：正在移动备份文件\n进度：0%");
                        }
                    });
                    String file_path = App.sysGetBackupPath() + project_name + ".tsp";
                    String file_path2 = "/sdcard/TieCode/backup_cloud/" + project_name + ".zip";
                    //File file_p = new File(file_path);
                    //file_p.renameTo(new File(file_path2));
                    if (!FileUtils.renameFile(file_path, file_path2)) {
                        LogUtils.writeBackupLog("手动备份-文件移动失败！！");
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ViewUtils.dismissProgressDialog();
                                tips("文件更名失败，请重试！");
                            }
                        });
                        return;
                    }
                    LogUtils.writeBackupLog("手动备份-开始上传文件");
                    String bs = YunBackup.uploadLzyFile(file_path2, new HttpUtils.OnUploadListener() {
                        @Override
                        public void onProgressChanged(double value, String value2) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    ViewUtils.updataProgressDialog(
                                            "正在备份中......\n当前操作：" + value2 + "\n进度：" + value + "%");
                                }
                            });
                        }
                    });
                    if (bs.equals("成功")) {
                        LogUtils.writeBackupLog("手动备份-文件上传成功");
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ViewUtils.dismissProgressDialog();
                                tips("手动云备份成功");
                            }
                        });

                        LogUtils.writeBackupLog("手动云备份成功！");
                    } else {
                        LogUtils.writeBackupLog("手动备份-文件上传失败，详情：" + bs);
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                ViewUtils.dismissProgressDialog();
                                tips(bs);
                            }
                        });
                    }
                    FileUtils.deleteFile(new File(file_path2));
                } else {
                    LogUtils.writeBackupLog("手动备份-备份文件创建失败！！");
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            ViewUtils.dismissProgressDialog();
                            tips("备份文件创建失败，请重试！");
                        }
                    });
                    return;
                }
            }
        }).start();
    }

}