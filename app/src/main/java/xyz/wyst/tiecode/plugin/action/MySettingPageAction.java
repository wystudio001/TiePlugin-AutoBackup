package xyz.wyst.tiecode.plugin.action;

import android.content.Context;
import android.view.View.*;
import android.view.*;
import android.content.*;
import android.content.res.*;
import android.net.Uri;
import android.widget.*;
import android.os.*;
import android.app.*;
import java.io.*;
import java.util.*;
import android.webkit.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.content.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import com.tiecode.plugin.action.page.setting.PluginSettingPageAction;
import com.tiecode.develop.component.api.item.ISettingItemView;
import com.tiecode.develop.component.api.item.ITextSwitchItemView;
import com.tiecode.develop.component.widget.item.setting.TextSwitchItemView;
import com.tiecode.develop.component.api.item.ITextArrowItemView;
import com.tiecode.develop.component.widget.item.setting.TextArrowItemView;
import com.tiecode.develop.component.widget.dialog.IBottomInputDialog;
import com.tiecode.develop.component.widget.dialog.BottomInputDialog;
import com.tiecode.develop.component.widget.dialog.IBottomDialog;
import com.tiecode.develop.component.widget.dialog.BottomDialog;
import xyz.wyst.tiecode.plugin.R;
import xyz.wyst.tiecode.plugin.App;
import xyz.wyst.tiecode.plugin.EasyHttp;
import xyz.wyst.tiecode.plugin.util.FileUtils;
import xyz.wyst.tiecode.plugin.util.ViewUtils;
import java.io.File;
import org.json.JSONException;
import xyz.wyst.tiecode.plugin.backup.YunBackup;
import xyz.wyst.tiecode.plugin.util.HttpUtils;
import xyz.wyst.tiecode.plugin.util.StringUtils;
import xyz.wyst.tiecode.plugin.util.LogUtils;
import com.tiecode.develop.component.module.input.impl.TieTextInputField;
import com.tiecode.develop.component.module.text.impl.TieTextView;
import com.tiecode.develop.component.module.layout.impl.TieLinearLayout;
import com.tiecode.develop.component.widget.dialog.BottomListDialog;
import com.tiecode.develop.component.widget.dialog.BottomCheckListDialog;
import android.widget.AdapterView.OnItemClickListener;
import com.tiecode.develop.component.widget.dialog.IBottomCheckListDialog.OnItemCheckedListener;
import org.json.JSONObject;

/*
* Written by WYstudio
*/

//设置界面
public class MySettingPageAction extends PluginSettingPageAction {

    public static File setting_file; //设置文件的保存路径
    public final static String NAME_SWITCH = "总开关"; //开关
    public final static String NAME_SWITCH2 = "完成后是否弹出提示";
    public final static String NAME_SWITCH_FU = "是否覆盖备份";
    public final static String NAME_SWITCH_FU_NUM = "覆盖备份数";
    public final static String NAME_PATH_SAVE = "备份保存路径";
    public final static String NAME_SWITCH_YA = "是否开启压缩备份";
    public final static String NAME_SWITCH_YUN = "是否开启云端备份";
    public final static String NAME_TEXT_YUN_USER = "云盘账号";
    public final static String NAME_TEXT_YUN_USER_PASS = "云盘密码";
    public final static String NAME_TEXT_YUN_USER_COOKIE = "云盘cookie";
    public final static String NAME_SWTICH_YUN_USER_WIFI = "云盘WIFI备份";
    public final static String NAME_TEXT_YUN_USER_FOID = "蓝奏云文件夹id";
    public final static String NAME_TEXT_LOCAL_BAO_NUM = "保留备份数量";

    private static JSONObject obj;
    private static HttpUtils httpUtils;
    private static ITextSwitchItemView item_yun;
    public static Activity activity;

    private static void wlog(String str) {
        LogUtils.writeMainLog("(MySettingPageAction.java)" + ">>>" + str);
    }

    //添加设置项目
    public static void put(String name, Object data) {
        try {
            obj.put(name, data);
            save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //读取设置项目boolean
    public static boolean getBoolean(String name, boolean _default) {
        try {
            obj = new JSONObject(FileUtils.readString(setting_file));
            if (obj.has(name)) {
                return obj.getBoolean(name);
            } else {
                //没有这个值，把它添加到设置文件里，然后保存
                obj.put(name, _default);
                save();
                return _default;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return _default;
        }
    }

    //读取设置项目String
    public static String getString(String name, String _default) {
        try {
            obj = new JSONObject(FileUtils.readString(setting_file));
            if (obj.has(name)) {
                return obj.getString(name);
            } else {
                //没有这个值，把它添加到设置文件里，然后保存
                obj.put(name, _default);
                save();
                return _default;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return _default;
        }
    }

    //读取整数型
    public static int getInt(String name, int _default) {
        try {
            obj = new JSONObject(FileUtils.readString(setting_file));
            if (obj.has(name)) {
                return obj.getInt(name);
            } else {
                //没有这个值，把它添加到设置文件里，然后保存
                obj.put(name, _default);
                save();
                return _default;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return _default;
        }
    }

    //保存设置
    public static void save() {
        if (!setting_file.exists()) {
            FileUtils.createFile(setting_file);
        }
        try {
            FileUtils.writeString(setting_file, obj.toString(4));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //初始化
    public static void init(Context context) {
        wlog("开始初始化");
        setting_file = new File(
                context.getExternalFilesDir(null).getAbsolutePath() + "/plugins/" + App.packageName + "/setting.json");
        File file_old_setting = new File("/sdcard/TieCode/plugin_backup/setting.json");
        if (!setting_file.exists()) {
            FileUtils.copy(file_old_setting, setting_file);
        }

        try {
            obj = new JSONObject();
            if (setting_file.exists()) {
                obj = new JSONObject(FileUtils.readString(setting_file));
                if (!obj.has(NAME_SWITCH)) {
                    obj.put(NAME_SWITCH, true);
                }

                if (!obj.has(NAME_SWITCH2)) {
                    obj.put(NAME_SWITCH2, true);
                }

                if (!obj.has(NAME_SWITCH_FU)) {
                    obj.put(NAME_SWITCH_FU, true);
                }

                if (!obj.has(NAME_PATH_SAVE)) {
                    obj.put(NAME_PATH_SAVE, "/sdcard/TieCode/backup/");
                }

                if (!obj.has(NAME_SWITCH_YA)) {
                    obj.put(NAME_SWITCH_YA, false);
                }
                /*
                if(!obj.has(NAME_SWITCH_FU_NUM)){
                    obj.put(NAME_SWITCH_FU_NUM, 0);
                }
                */
                save();

            } else {
                obj.put(NAME_SWITCH, true); //默认值
                obj.put(NAME_SWITCH2, true);
                obj.put(NAME_SWITCH_FU, true);
                obj.put(NAME_SWITCH_YA, false);
                //obj.put(NAME_SWITCH_FU_NUM, 0);
                obj.put(NAME_PATH_SAVE, "/sdcard/TieCode/backup/");
                save();

            }

        } catch (Throwable e) {
            e.printStackTrace();

        }

        checkUpdata_init();
        wlog("初始化成功");
    }

    public static void tips(String str) {
        Toast.makeText(App.mApp, str, 1500).show();
    }

    public void showDialog_yun() {
        BottomDialog builder = new BottomDialog(getActivity()).builder();
        TieLinearLayout linearLayout = new TieLinearLayout(getActivity());
        TieTextView text1 = new TieTextView(getActivity());
        TieTextView text1_1 = new TieTextView(getActivity());
        TieTextInputField edit1 = new TieTextInputField(getActivity());
        TieTextInputField edit2 = new TieTextInputField(getActivity());
        TieTextInputField edit3 = new TieTextInputField(getActivity());
        TieTextView text2 = new TieTextView(getActivity());

        linearLayout.setLayoutParams(new LayoutParams(-1, -1));
        linearLayout.setOrientation(1);
        linearLayout.addView(text1);
        linearLayout.addView(text1_1);
        linearLayout.addView(edit1);
        linearLayout.addView(edit2);
        //linearLayout.addView(edit3);
        linearLayout.addView(text2);

        builder.setTitle("云盘备份设置");
        builder.setContent(linearLayout);
        builder.setPositiveButton("立即登录", new OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edit1.getText().toString();
                String user_pass = edit2.getText().toString();
                String user_cookie = edit3.getText().toString();

                if (user.equals("") || user_pass.equals("")) {
                    tips("请填写完整！");
                } else {
                    //put(NAME_TEXT_YUN_USER_COOKIE, user_cookie);
                    put(NAME_TEXT_YUN_USER, user);
                    put(NAME_TEXT_YUN_USER_PASS, user_pass);
                    put(NAME_SWITCH_YUN, false);
                    //item_yun.setButtonChecked(false);
                    ViewUtils.showProgressDialog(getActivity(), "请稍等", "正在登录到蓝奏云......");
                    new Thread(new Runnable() {
                        public void run() {
                            String bs = YunBackup.loginLzy(user, user_pass);
                            if (bs.equals("成功")) {
                                String bs2 = YunBackup.initLzy(getString(NAME_TEXT_YUN_USER_COOKIE, ""));
                                if (bs2.equals("成功")) {
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            tips("登录成功！");
                                            ViewUtils.dismissProgressDialog();
                                            builder.dismiss();
                                            item_yun.setItemEnabled(true);
                                        }
                                    });
                                } else {
                                    getActivity().runOnUiThread(new Runnable() {
                                        public void run() {
                                            tips(bs2);
                                            ViewUtils.dismissProgressDialog();
                                        }
                                    });
                                }

                            } else {
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        tips(bs);
                                        ViewUtils.dismissProgressDialog();
                                    }
                                });
                            }
                        }
                    }).start();

                }
            }
        });
        builder.show();
        text1.setText("当前仅支持蓝奏云备份");
        text1.setWidth(-1);
        text1.setGravity(Gravity.CENTER_HORIZONTAL);
        text1.setTextSize(18f);
        text1.setTextColor(Color.RED);
        text1_1.setText(" 请在下方填写您的账号密码");
        //text1_1.setText(" 请在下方填入已登录的蓝奏云cookie\n cookie可以通过抓包浏览器获取，或者通过js代码直接获取。具体方法可百度搜索！\n");
        //text1_1.setTextSize(18f);
        text1.setWidth(-1);
        //text1.setTextSize(18f);
        edit1.setHint("输入你的账号");
        edit1.setTitle("账号");
        edit1.setText(getString(NAME_TEXT_YUN_USER, ""));
        edit2.setHint("输入你的密码");
        edit2.setTitle("密码");
        edit2.setText(getString(NAME_TEXT_YUN_USER_PASS, ""));
        text2.setText(
                "\n 将备份到 /云盘根目录/TieCode/ \n 您的账号密码保存在本地！不会有任何人知道！\n\n 注意：上传时间取决于您的网速，如果您的工程较大，上传到云盘可能需要较长时间，请耐心等待！");
        text2.setWidth(-1);
        //text2.setTextSize(18f);

    }

    @Override
    public void onCreate() {
        ISettingItemView siv1 = addSettingItem("普通设置");
        ISettingItemView siv_local = addSettingItem("本地备份设置");
        ISettingItemView siv_yun = addSettingItem("云端备份设置");
        ISettingItemView siv4 = addSettingItem("帮助");
        ISettingItemView siv2 = addSettingItem("关于");
        ITextSwitchItemView item1 = new TextSwitchItemView(getActivity());
        ITextArrowItemView item2 = new TextArrowItemView(getActivity());
        ITextSwitchItemView item3 = new TextSwitchItemView(getActivity());
        ITextArrowItemView item4 = new TextArrowItemView(getActivity());
        ITextArrowItemView item5 = new TextArrowItemView(getActivity());
        ITextArrowItemView item6 = new TextArrowItemView(getActivity());
        ITextSwitchItemView item7 = new TextSwitchItemView(getActivity());
        ITextArrowItemView item_bao_num = new TextArrowItemView(getActivity());
        ITextSwitchItemView item_ya = new TextSwitchItemView(getActivity());
        item_yun = new TextSwitchItemView(getActivity());
        ITextArrowItemView item_yun_se = new TextArrowItemView(getActivity());
        ITextSwitchItemView item_yun_wifi = new TextSwitchItemView(getActivity());
        ITextArrowItemView item_help = new TextArrowItemView(getActivity());

        item1.setTitle("开启自动备份");
        item1.setDescription("如果关闭将不再自动备份");
        item1.setButtonChecked(getBoolean(NAME_SWITCH, true));
        item1.setButtonListener(new ITextSwitchItemView.OnCheckedChangeListener() {
            @Override
            public void onChange(ITextSwitchItemView switchButton, boolean z) {
                put(NAME_SWITCH, z); //保存设置
            }
        });
        siv1.addSwitchItem(item1);

        item3.setTitle("开启提示");
        item3.setDescription("备份成功后是否弹出提示");
        item3.setButtonChecked(getBoolean(NAME_SWITCH2, true));
        item3.setButtonListener(new ITextSwitchItemView.OnCheckedChangeListener() {
            @Override
            public void onChange(ITextSwitchItemView switchButton, boolean z) {
                put(NAME_SWITCH2, z); //保存设置
            }
        });
        siv1.addSwitchItem(item3);

        item4.setTitle("备份保存路径");
        item4.setDescription(getString(NAME_PATH_SAVE, "/sdcard/TieCode/backup/"));
        item4.setDefaultText("");
        item4.setClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomInputDialog dialog_input = new BottomInputDialog(getActivity()).builder();
                dialog_input.setTitle("备份保存路径");
                dialog_input.setInputTitle("请输入路径。最后请加上/");
                dialog_input.setText(getString(NAME_PATH_SAVE, "/sdcard/TieCode/backup/"));
                dialog_input.setPositiveButton("立即修改", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String text = dialog_input.getText();
                        if (text.endsWith("/")) {
                            put(NAME_PATH_SAVE, dialog_input.getText());
                            item4.setDescription(dialog_input.getText());
                            tips("修改路径成功");
                            dialog_input.dismiss();
                        } else {
                            tips("结尾请加上/");
                        }

                    }
                }).show();
            }
        });
        siv1.addArrowItem(item4);

        item7.setTitle("覆盖备份");
        item7.setDescription("如果关闭将保留以往的备份！");
        item7.setButtonChecked(getBoolean(NAME_SWITCH_FU, true));
        item7.setButtonListener(new ITextSwitchItemView.OnCheckedChangeListener() {
            @Override
            public void onChange(ITextSwitchItemView switchButton, boolean z) {
                put(NAME_SWITCH_FU, z); //保存设置

                if (!z) {
                    getInt(NAME_TEXT_LOCAL_BAO_NUM,5);
                }
            }
        });
        siv_local.addSwitchItem(item7);
        
        item_bao_num.setTitle("保留备份数");
        item_bao_num.setDescription("设置保留备份的数量");
        item_bao_num.setDefaultText(getInt(NAME_TEXT_LOCAL_BAO_NUM,5) + "");
        item_bao_num.setClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomListDialog builder = new BottomListDialog(getActivity()).builder();
                builder.setTitle("选择数量");
                builder.setContentItems(new String[]{"2","3","4","5","6","7","8","9"},new OnItemClickListener(){
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                        switch (i){
                            case 0:
                                put(NAME_TEXT_LOCAL_BAO_NUM,2);
                                break;
                            case 1:
                                put(NAME_TEXT_LOCAL_BAO_NUM,3);
                                break;
                            case 2:
                                put(NAME_TEXT_LOCAL_BAO_NUM,4);
                                break;
                            case 3:
                                put(NAME_TEXT_LOCAL_BAO_NUM,5);
                                break;
                            case 4:
                                put(NAME_TEXT_LOCAL_BAO_NUM,6);
                                break;
                            case 5:
                                put(NAME_TEXT_LOCAL_BAO_NUM,7);
                                break;
                            case 6:
                                put(NAME_TEXT_LOCAL_BAO_NUM,8);
                                break;
                            case 7:
                                put(NAME_TEXT_LOCAL_BAO_NUM,9);
                                break;
                        }
                        item_bao_num.setDefaultText(getInt(NAME_TEXT_LOCAL_BAO_NUM,5) + "");
                        tips("设置成功");
                        builder.dismiss();
                    } 
                });
                builder.show();
            }
        });
        siv_local.addArrowItem(item_bao_num);

        item_ya.setTitle("压缩备份");
        item_ya.setDescription("开启后将备份成压缩文件");
        item_ya.setButtonChecked(getBoolean(NAME_SWITCH_YA, false));
        item_ya.setButtonListener(new ITextSwitchItemView.OnCheckedChangeListener() {
            @Override
            public void onChange(ITextSwitchItemView switchButton, boolean z) {
                put(NAME_SWITCH_YA, z); //保存设置

                /*
                if (z) {
                    tips("压缩备份为测试功能，可能会有BUG！");
                }
                */
            }
        });
        siv_local.addSwitchItem(item_ya);

        item5.setTitle("恢复默认设置");
        item5.setDescription("将恢复插件的默认设置");
        item5.setDefaultText("");
        item5.setClick(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BottomDialog dialog_recover = new BottomDialog(getActivity()).builder();
                dialog_recover.setTitle("恢复默认设置");
                dialog_recover.setContent("是否确定要恢复默认设置？");
                dialog_recover.setPositiveButton("确定", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        put(NAME_SWITCH, true);
                        put(NAME_SWITCH2, true);
                        put(NAME_SWITCH_FU, true);
                        put(NAME_SWITCH_YA, false);
                        put(NAME_PATH_SAVE, "/sdcard/TieCode/backup/");
                        item1.setButtonChecked(getBoolean(NAME_SWITCH, true));
                        item3.setButtonChecked(getBoolean(NAME_SWITCH2, true));
                        item7.setButtonChecked(getBoolean(NAME_SWITCH_FU, true));
                        item_ya.setButtonChecked(getBoolean(NAME_SWITCH_YA, false));
                        item4.setDescription(getString(NAME_PATH_SAVE, "/sdcard/TieCode/backup/"));
                        tips("恢复默认设置成功");
                        dialog_recover.dismiss();
                    }
                }).show();
            }
        });
        siv_local.addArrowItem(item5);

        item_yun.setTitle("开启云端自动备份");
        item_yun.setDescription("开启云端后不再进行本地备份\n此开关不影响手动云备份");
        item_yun.setButtonChecked(getBoolean(NAME_SWITCH_YUN, false));
        if(getString(NAME_TEXT_YUN_USER_COOKIE, "").equals("") || getString(NAME_TEXT_YUN_USER_FOID, "").equals("")){
            item_yun.setItemEnabled(false);
        }
        item_yun.setButtonListener(new ITextSwitchItemView.OnCheckedChangeListener() {
            @Override
            public void onChange(ITextSwitchItemView switchButton, boolean z) {
                if (z) {
                    //getString(NAME_TEXT_YUN_USER, "") == "" || getString(NAME_TEXT_YUN_USER_PASS, "") == "") 
                    if (getString(NAME_TEXT_YUN_USER_COOKIE, "").equals("")
                            || getString(NAME_TEXT_YUN_USER_FOID, "").equals("")) {
                        tips("请先配置云盘相关设置！");
                    } else {
                        put(NAME_SWITCH_YUN, true);
                    }

                } else {
                    put(NAME_SWITCH_YUN, false);
                }
            }
        });
        siv_yun.addSwitchItem(item_yun);

        item_yun_se.setTitle("云盘设置");
        item_yun_se.setDescription("设置云盘的相关配置");
        item_yun_se.setDefaultText("");
        item_yun_se.setClick(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialog_yun();
            }
        });
        siv_yun.addArrowItem(item_yun_se);

        item_yun_wifi.setTitle("是否只在WIFI下备份");
        item_yun_wifi.setDescription("开启后将只在WIFI环境下进行备份");
        item_yun_wifi.setButtonChecked(getBoolean(NAME_SWTICH_YUN_USER_WIFI, false));
        item_yun_wifi.setButtonListener(new ITextSwitchItemView.OnCheckedChangeListener() {
            @Override
            public void onChange(ITextSwitchItemView switchButton, boolean z) {
                put(NAME_SWTICH_YUN_USER_WIFI, z);
            }
        });
        siv_yun.addSwitchItem(item_yun_wifi);

        item_help.setTitle("使用方法");
        item_help.setDescription("点我查看使用方法\n>>>必看<<<");
        item_help.setDefaultText("");
        item_help.setClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final BottomDialog builder = new BottomDialog(getActivity()).builder();
                builder.setTitle("使用方法").setContent(getHelp()).setNegativeButton("关闭", new OnClickListener() {
                    public void onClick(View view) {
                        builder.dismiss();
                    }
                }).show();
            }
        });
        siv4.addArrowItem(item_help);

        item2.setTitle("作者");
        item2.setDescription("QQ：1519258319");
        item2.setDefaultText("WYstudio");
        item2.setClick(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setData(Uri.parse(
                        "mqqapi://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin=1519258319"));
                try {
                    getActivity().startActivity(intent);

                    //成功
                } catch (Exception e) {
                    tips(e.toString());
                    //有问题不存在
                }
            }
        });
        siv2.addArrowItem(item2);

        item6.setTitle("版本");
        item6.setDescription(App.app_version);
        item6.setDefaultText("检查更新");
        item6.setClick(new OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpdata();
            }
        });
        siv2.addArrowItem(item6);

    }

    public String getHelp() {
        try {
            InputStream open = App.mApp.getAssets().open("help");
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bArr = new byte[512];
            while (true) {
                int read = open.read(bArr);
                if (read == -1) {
                    byteArrayOutputStream.flush();
                    byteArrayOutputStream.close();
                    open.close();
                    return new String(byteArrayOutputStream.toByteArray());
                }
                byteArrayOutputStream.write(bArr, 0, read);
            }
        } catch (Exception unused) {
            return "";
        }
    }

    public final static String url_updata = "https://api.wystudio.xyz/wy/tiecode_plugin/AutoBackup/updata.txt";
    public static JSONObject json_up;
    public static String NewVersion;
    public static String NewContent;
    public static String NewUrl;

    public void checkUpdata() {
        EasyHttp.get(url_updata, new EasyHttp.OnRequestListener() {
            @Override
            public void onCompleted(String code, String text, byte[] content, String cookie) {
                //tips(text);
                try {
                    json_up = new JSONObject(text);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                try {
                    NewVersion = json_up.getString("NewVersion");
                    NewContent = json_up.getString("NewContent");
                    NewUrl = json_up.getString("NewUrl");
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                if (App.app_version.equals(NewVersion)) {
                    tips("当前为最新版本");
                } else {

                    BottomDialog dialog_updata = new BottomDialog(getActivity());
                    dialog_updata.builder();
                    dialog_updata.setTitle("有新版本了 " + NewVersion);
                    dialog_updata.setContent(NewContent + "\n\n若安装失败，请先卸载旧版插件再安装新版！");
                    dialog_updata.setPositiveButton("立即更新", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            File file_to = new File("/sdcard/TieCode/plugin_backup/setting.json");
                            FileUtils.copy(setting_file, file_to);
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse(NewUrl);
                            intent.setData(content_url);
                            startActivity(intent);
                            tips("请手动下载");
                            dialog_updata.hide();
                        }
                    });
                    dialog_updata.show();
                }
            }

            @Override
            public void onFailed(String code, String text, byte[] content) {
            }

            @Override
            public void onProgressChanged(int value) {
            }
        });
    }

    public static void checkUpdata_init() {
        wlog("检查插件更新");
        EasyHttp.get(url_updata, new EasyHttp.OnRequestListener() {
            @Override
            public void onCompleted(String code, String text, byte[] content, String cookie) {
                //String text = StringUtils.decodeUnicode(httpUtils.GET(url_updata, null));
                try {
                    json_up = new JSONObject(text);
                } catch (Throwable e) {
                    e.printStackTrace();
                    LogUtils.printfException(App.mApp, e);
                }

                try {
                    NewVersion = json_up.getString("NewVersion");
                    NewContent = json_up.getString("NewContent");
                    NewUrl = json_up.getString("NewUrl");
                } catch (Throwable e) {
                    e.printStackTrace();
                    LogUtils.printfException(App.mApp, e);
                }

                if (!App.app_version.equals(NewVersion)) {
                    wlog("需要更新，最新版本" + NewVersion);
                    tips("自动备份插件需要更新！");
                } else {
                    wlog("已是最新版本");
                }
            }

            @Override
            public void onFailed(String code, String text, byte[] content) {
            }

            @Override
            public void onProgressChanged(int value) {
            }
        });
    }
}