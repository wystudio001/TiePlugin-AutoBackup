package xyz.wyst.tiecode.plugin.util;

import android.app.AlertDialog.Builder;
import android.content. *;
import com.tiecode.develop.component.widget.dialog.LoadingDialog;
import java.io. *;

/*
* Written by WYstudio
*/

public class ViewUtils {
	 //private static ProgressDialog pd;
     private static LoadingDialog ld;
     
     public static void showProgressDialog(Context context, CharSequence charSequence, CharSequence charSequence2) {
         /*
        if (pd == null) {
            ProgressDialog progressDialog = new ProgressDialog(context);
            pd = progressDialog;
            progressDialog.setCancelable(false);
        }
        if (charSequence != null) {
            pd.setTitle(charSequence.toString());
        } else {
            pd.setTitle("提示");
        }
        pd.setMessage(charSequence2);
        pd.show();
        */
        if(ld == null){
            LoadingDialog load = new LoadingDialog(context);
            load.setCancelable(false);
            ld = load;
        }
        if (charSequence != null) {
            ld.setTitle(charSequence.toString());
        } else {
            ld.setTitle("提示");
        }
        ld.setTipText(charSequence2.toString());
        ld.show();
    }
    
    public static void dismissProgressDialog() {
        /*
        ProgressDialog progressDialog = pd;
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        pd = null;
        */
        LoadingDialog load = ld;
        if (load != null) {
            load.dismiss();
        }
        ld = null;
    }
    
    public static void updataProgressDialog(CharSequence content){
        /*
        ProgressDialog progressDialog = pd;
        if (progressDialog != null) {
            progressDialog.setMessage(content);
        }
        */
        LoadingDialog load = ld;
        if (load != null) {
            load.setTipText(content.toString());
        }
    }
}