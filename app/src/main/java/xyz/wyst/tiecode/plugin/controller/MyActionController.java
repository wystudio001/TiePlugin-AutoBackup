package xyz.wyst.tiecode.plugin.controller;

import xyz.wyst.tiecode.plugin.action.MyCodeBodyPageAction;
import xyz.wyst.tiecode.plugin.action.MyCodePageAction;
import xyz.wyst.tiecode.plugin.action.MyProjectFilePageAction;
import xyz.wyst.tiecode.plugin.action.MySettingPageAction;
import com.tiecode.plugin.action.ActionController;
import com.tiecode.plugin.action.page.code.CodeBodyPageAction;
import com.tiecode.plugin.action.page.code.CodePageAction;
import com.tiecode.plugin.action.page.code.ProjectFilePageAction;
import com.tiecode.plugin.action.page.setting.PluginSettingPageAction;

/*
* Written by WYstudio
*/

public class MyActionController extends ActionController {

	public MyActionController() {
		//插件设置界面
		addAction(PluginSettingPageAction.class, new MySettingPageAction());
		//代码编辑器界面
		addAction(CodePageAction.class, new MyCodePageAction());
		//代码主体
		addAction(CodeBodyPageAction.class, new MyCodeBodyPageAction());
		//打开文件操作
		addAction(ProjectFilePageAction.class, new MyProjectFilePageAction());
	}

}