package km.app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import java.io.File;

/**Created by wuzhengu on 2018/1/10 */
public class FileProvider extends android.support.v4.content.FileProvider
{
	
	public static String AUTH;
	
	static String getAuth(Context ctxt){
		if(AUTH==null) AUTH="auth:"+ctxt.getPackageName()+".FileProvider";
		return AUTH;
	}
	
	public static Uri getUri(Context ctxt, File file){
		Uri uri=android.support.v4.content.FileProvider.getUriForFile(ctxt, getAuth(ctxt), file);
		return uri;
	}
	
	public static Uri getUri(Context ctxt, String filePath){
		return getUri(ctxt, new File(filePath));
	}
	
	public static Intent setIntent(Context ctxt, File file, Intent intent, String type){
		Uri uri=null;
		try{
			uri=Uri.fromFile(file);
		}catch(Exception ignored){
			uri=getUri(ctxt, file);
			intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		}
		if(type==null) type=intent.getType();
		intent.setDataAndType(uri, type);
		return intent;
	}
	
	public static Intent setIntent(Context ctxt, String filePath, Intent intent, String type){
		return setIntent(ctxt, new File(filePath), intent, type);
	}
	
	public static Intent setIntent(Context ctxt, File file, String action, String type){
		return setIntent(ctxt, file, new Intent(action), type);
	}
	
	public static Intent setIntent(Context ctxt, String filePath, String action, String type){
		return setIntent(ctxt, new File(filePath), new Intent(action), type);
	}
	
	public static Intent setIntent(Context ctxt, File file, Intent intent){
		return setIntent(ctxt, file, intent, null);
	}
	
	public static Intent setIntent(Context ctxt, String filePath, Intent intent){
		return setIntent(ctxt, new File(filePath), intent);
	}
	
	public static Intent setIntent(Context ctxt, File file, String action){
		return setIntent(ctxt, file, action, null);
	}
	
	public static Intent setIntent(Context ctxt, String filePath, String action){
		return setIntent(ctxt, new File(filePath), action);
	}
}
