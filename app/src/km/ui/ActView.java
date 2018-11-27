package km.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import km.base.BaseActivity;

/** Created by wuzhengu on 2018/11/10 0010 */
public class ActView extends BaseActivity
{
	@Override
	protected void onCreate(@Nullable Bundle bundle){
		super.onCreate(bundle);
		Intent it=getIntent();
		final Uri uri=it.getData();
		final String type=String.valueOf(it.getType());
		Log.i("____", type+"\n"+uri);
		switch(type){
		case "application/apk":
			Intent intent=new Intent(Intent.ACTION_VIEW);
			intent.setData(uri);
			intent.setType("application/vnd.android.package-archive");
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
			break;
		}
	}
}
