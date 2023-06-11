package xyz.wyst.tiecode.plugin.widget;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tiecode.develop.component.api.multiple.Window;
import java.io.File;
import java.net.URI;

/*
* Written by WYstudio
*/

public class MyEditor extends LinearLayout implements Window {

	public URI uri; //打开的文件
	public Activity context; //上下文

	public TextView tv;

	public MyEditor(Activity context) {
		super(context);
		this.context = context;
		this.tv = new TextView(context);
		setGravity(Gravity.CENTER);
		addView(tv);
	}

	@Override
	public String getName() {
		return new File(uri.getPath()).getName();
	}

	@Override
	public URI getOpenedURI() {
		return uri;
	}

	@Override
	public View getView() {
		return this;
	}

	@Override
	public void onClose() {
	}

	@Override
	public void openURI(URI p1) {
		this.uri = p1;
		//打开文件
		tv.setText(uri.getPath());
	}

	@Override
	public void updateURI(URI p1) {
		this.uri = p1;
		//uri被更新了
		tv.setText(uri.getPath());
	}

}