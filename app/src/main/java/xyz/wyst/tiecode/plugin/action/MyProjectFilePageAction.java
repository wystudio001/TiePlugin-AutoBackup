package xyz.wyst.tiecode.plugin.action;

import com.tiecode.plugin.action.page.code.ProjectFilePageAction;

/*
* Written by WYstudio
*/

public class MyProjectFilePageAction extends ProjectFilePageAction {

	@Override
    public void onCreate() {
		//registerURIOpenAction(new MyURIOpenAction(this), ".java");
	}

}