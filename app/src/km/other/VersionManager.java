package km.other;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import kameng.yimai.BuildConfig;
import kameng.yimai.R;
import km.model.VersionInfo;
import km.util.DownloadReceiver;
import okhttp3.*;

/** Created by wuzhengu on 2018/11/8 0008 */
public class VersionManager extends Fragment
{
	static final long INTERVAL=10*60*1000;
	long mCheckTime;
	OkHttpClient mClient;
	OkCallback mListener;
	
	void check(){
		try{
			if(mClient==null) mClient=
					new OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
							.readTimeout(10, TimeUnit.SECONDS)
							.writeTimeout(10, TimeUnit.SECONDS)
							.build();
			if(mListener==null) mListener=new OkCallback(this);
			String url="http://api.fir.im/apps/latest/"
			           +"5be3a9e7ca87a85c9516321e"
			           +"?api_token="
			           +"7ea8233a710bfb97aaa6f3389686e275";
			mClient.newCall(new Request.Builder()
					.get()
					.url(url)
					.build()).enqueue(mListener);
		}catch(Exception e){ e.printStackTrace(); }
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mListener!=null) mListener.setHost(null);
	}
	
	@Override
	public void onStart(){
		super.onStart();
		long now=System.currentTimeMillis();
		if(now-mCheckTime>INTERVAL){
			mCheckTime=now;
			check();
		}
	}
	
	static class OkCallback implements Callback
	{
		VersionManager mHost;
		
		public OkCallback(VersionManager host){
			setHost(host);
		}
		
		public OkCallback setHost(VersionManager host){
			mHost=host;
			return this;
		}
		
		@Override
		public void onFailure(Call call, IOException e){
			
		}
		
		@Override
		public void onResponse(Call call, Response resp) throws IOException{
			if(mHost==null) return;
			try{
				String json=resp.body().string();
				final VersionInfo info=new Gson().fromJson(json, VersionInfo.class);
				if(info.version>BuildConfig.VERSION_CODE){
					final Context ctxt=mHost==null? null: mHost.getContext();
					if(ctxt==null) return;
					mHost.getActivity().runOnUiThread(new Runnable()
					{
						@Override
						public void run(){
							onUpdata(ctxt, info);
						}
					});
				}
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}
		
		void onUpdata(final Context ctxt, final VersionInfo info){
			final String appname=ctxt.getResources().getString(R.string.appname);
			StringBuilder builder=new StringBuilder();
			builder.append("新版本号：");
			builder.append(info.versionName);
			builder.append(" (");
			builder.append(info.version);
			builder.append(")");
			if(!TextUtils.isEmpty(info.changelog)){
				builder.append("\n");
				builder.append("更新说明：\n");
				builder.append(info.changelog);
			}
			AlertDialog dialog=new AlertDialog.Builder(ctxt)
					.setCancelable(false)
					.setTitle(String.format("%s 新版本", appname))
					.setMessage(builder)
					.setNegativeButton("取消", null)
					.setPositiveButton("下载更新", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which){
							DownloadReceiver.download(ctxt, info.installUrl, null, appname+".apk",
									"版本"+info.version, null);
						}
					})
					.show();
			dialog.getButton(DialogInterface.BUTTON_POSITIVE)
					.setTextColor(ctxt.getResources().getColor(R.color.colorAccent));
		}
	}
}
