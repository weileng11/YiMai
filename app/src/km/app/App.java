package km.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Process;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/** Created by wuzhengu on 2018/10/30. */
public class App extends vip.VipApp
{
	static final List<Activity> list=new LinkedList<>();
	
	public static void set(Activity ctxt, boolean add){
		list.remove(ctxt);
		if(add) list.add(0, ctxt);
	}
	
	public static Context get(){
		return list.isEmpty()? null: list.get(0);
	}
	
	public static void finish(){
		for(Activity act: list) if(act!=null && !act.isFinishing()) act.finish();
	}
	
	@Override
	protected void attachBaseContext(Context base){
		super.attachBaseContext(base);
		MultiDex.install(this);
	}
	
	@Override
	public void onCreate(){
		String name=getProcessName(this, Process.myPid());
		if(!getPackageName().equals(name)) return;
		setExceptionHandler(this);
		super.onCreate();
		setVmPolicy();
	}
	
	static void setVmPolicy(){
		if(Build.VERSION.SDK_INT<24) return;
		StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
		//builder.detectFileUriExposure();
		StrictMode.setVmPolicy(builder.build());
	}
	
	public static String getProcessName(Context ctxt, int pid){
		ActivityManager am=(ActivityManager)ctxt.getSystemService(ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> list=am.getRunningAppProcesses();
		if(list!=null) for(ActivityManager.RunningAppProcessInfo info: list){
			if(info.pid==pid) return info.processName;
		}
		return null;
	}
	
	public static float dp(Context ctxt, float i){
		DisplayMetrics dm=ctxt.getResources().getDisplayMetrics();
		float dp=Math.min(dm.widthPixels, dm.heightPixels)/360f;
		return i*dp;
	}
	
	static void setExceptionHandler(final Context ctxt){
		final Thread.UncaughtExceptionHandler dft=Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler()
		{
			final SimpleDateFormat sdf=
					new SimpleDateFormat("yyyyMMdd_HHmmss_SSS", Locale.getDefault());
			@Override
			public void uncaughtException(Thread thread, Throwable t){
				try{
					File dir=new File(ctxt.getExternalCacheDir().getParent(),  "crash");
					boolean ok=dir.exists()? dir.isDirectory(): dir.mkdirs();
					if(ok){
						String time=sdf.format(System.currentTimeMillis());
						StringBuilder sb=new StringBuilder();
						sb.append(time);
						sb.append("\n");
						sb.append("\n");
						sb.append(thread);
						sb.append("\n");
						sb.append("\n");
						sb.append(t.getClass().getSimpleName());
						sb.append(":");
						sb.append(t.getMessage());
						for(StackTraceElement s: t.getStackTrace()){
							sb.append("\n");
							sb.append(s.toString());
						}
						byte[] data=sb.toString().getBytes("utf-8");
						OutputStream os=new FileOutputStream(new File(dir, time+".txt"));
						os.write(data);
						os.close();
					}
				}catch(Throwable ex){
					ex.printStackTrace();
				}
				dft.uncaughtException(thread, t);
			}
		});
	}
}
