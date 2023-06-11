package xyz.wyst.tiecode.plugin.backup;

import xyz.wyst.tiecode.plugin.App;
import xyz.wyst.tiecode.plugin.action.MySettingPageAction;
import java.io.*;
import xyz.wyst.tiecode.plugin.util.FileUtils;

/*
* Written by WYstudio
*/

public class CommonBackup {
    public static void initBackup() {
        File file_old_setting = new File("/sdcard/TieCode/plugin_backup/setting.json");
        FileUtils.copy(MySettingPageAction.setting_file, file_old_setting);
    }
}