package xyz.wyst.tiecode.plugin.action;

import com.tiecode.plugin.api.project.action.URIOpenAction;
import java.net.URI;

/*
* Written by WYstudio
*/

public class MyURIOpenAction implements URIOpenAction {

	public MyProjectFilePageAction f;

	public MyURIOpenAction(MyProjectFilePageAction f) {
		this.f = f;
	}

	@Override
	public String getName() {
		return "测试打开文件";
	}

	@Override
	public void onOpen(URI p1) {
		if (p1 != null) {
			try {
				URI uri = new URI("open", null, p1.getPath(), "load=false", p1.getFragment());
				f.openURI(uri);
			} catch(Throwable e) {
				throw new RuntimeException(e);
			}
		}
	}

}