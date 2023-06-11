package xyz.wyst.tiecode.plugin.action;

import com.tiecode.develop.component.api.multiple.CreateWindowAction;
import com.tiecode.plugin.action.page.code.CodeBodyPageAction;
import java.net.URI;
import com.tiecode.develop.component.api.multiple.Window;
import xyz.wyst.tiecode.plugin.widget.MyEditor;

/*
* Written by WYstudio
*/

public class MyCodeBodyPageAction extends CodeBodyPageAction {

	@Override
	public void onCreate() {
		//registerSchemeAction("open", this);
	}

    /*
	@Override
	public Window create(URI p1) {
		return new MyEditor(getActivity());
	}
    */
	
}