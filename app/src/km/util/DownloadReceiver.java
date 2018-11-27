package km.util;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.webkit.URLUtil;
import android.widget.Toast;
import java.util.Collection;
import java.util.LinkedList;

/** Created by wuzhengu on 2018/11/11 0011 */
public class DownloadReceiver extends BroadcastReceiver
{
	
	public static void download(Context ctxt, String url){
		ctxt.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}
	
	public static void download(Context ctxt,
			String url, String type, String name, String desc, String dir){
		if(false){
			download(ctxt, url);
			return;
		}
		
		int p=ContextCompat.checkSelfPermission(ctxt, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(p!=PackageManager.PERMISSION_GRANTED){
			Toast.makeText(ctxt, "权限不足，请检查手机设置", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(name==null) name=URLUtil.guessFileName(url, null, type);
		else if(type==null || "application/octet-stream".equals(type)){
			int index=name.lastIndexOf('.')+1;
			if(index>0 && index<name.length()){
				String ext=name.substring(index).toLowerCase();
				switch(ext){
				case "apk":
					type="application/vnd.android.package-archive";
					break;
				}
			}
		}
		
		if(dir==null && type!=null){
			if(type.startsWith("image/")) dir=Environment.DIRECTORY_PICTURES;
		}
		if(dir==null) dir=Environment.DIRECTORY_DOWNLOADS;
		if(desc==null) desc="sdcard/"+dir+"/"+name;
		// 指定下载地址
		DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
		// 允许媒体扫描，根据下载的文件类型被加入相册、音乐等媒体库
		request.allowScanningByMediaScanner();
		// 设置通知的显示类型，下载进行时和完成后显示通知
		request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		// 设置通知栏的标题，如果不设置，默认使用文件名
		request.setTitle(name);
		// 设置通知栏的描述
		request.setDescription(desc);
		// 允许在计费流量下下载
		request.setAllowedOverMetered(true);
		// 允许该记录在下载管理界面可见
		request.setVisibleInDownloadsUi(true);
		// 允许漫游时下载
		request.setAllowedOverRoaming(true);
		// 允许下载的网路类型
		//request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		// 设置下载文件类型
		request.setMimeType(type);
		// 设置下载文件保存的路径和文件名
		request.setDestinationInExternalPublicDir(dir, name);
		// 另外可选一下方法，自定义下载路径
		final DownloadManager m=(DownloadManager)ctxt.getSystemService(Context.DOWNLOAD_SERVICE);
		// 添加一个下载任务
		long downloadId=m.enqueue(request);
		add(downloadId);
	}
	
	public static void add(long id){
		synchronized(IDS){
			if(IDS.contains(id)) return;
			IDS.add(id);
		}
	}
	
	static final Collection<Long> IDS=new LinkedList<>();
	
	@Override
	public void onReceive(Context ctxt, Intent intent){
		long id=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
		if(id==-1) return;
		synchronized(IDS){
			if(!IDS.contains(id)) return;
			IDS.remove(id);
		}
		DownloadManager m=(DownloadManager)ctxt.getSystemService(Context.DOWNLOAD_SERVICE);
		Uri uri=m.getUriForDownloadedFile(id);
		if(uri==null) return;
		String type=m.getMimeTypeForDownloadedFile(id);
		try{
			Intent intent2=new Intent(Intent.ACTION_VIEW);
			intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent2.setDataAndType(uri, type);
			ctxt.startActivity(intent2);
		}catch(Exception e){ e.printStackTrace(); }
	}
	
	
}
