package km.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Base64;
import android.view.View;
import android.webkit.*;
import android.widget.Toast;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;
import km.util.DownloadReceiver;

/** Created by wuzhengu on 2018/11/12 0012 */
public class KmWebView extends WebView implements DownloadListener, View.OnLongClickListener
{
	String mDirName;
	
	public String getDirName(){
		return mDirName;
	}
	
	public KmWebView setDirName(String dirName){
		mDirName=dirName;
		return this;
	}
	
	public KmWebView(Context ctxt){
		super(ctxt);
		init();
	}
	
	public KmWebView(Context ctxt, AttributeSet attrs){
		super(ctxt, attrs);
		init();
	}
	
	/**
	 @param defStyleAttr 不可为0
	 */
	public KmWebView(Context ctxt, AttributeSet attrs, int defStyleAttr){
		super(ctxt, attrs, defStyleAttr);
		init();
	}
	
	void init(){
		WebSettings settings=getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		settings.setUseWideViewPort(true);
		if(Build.VERSION.SDK_INT>=21){
			settings.setMixedContentMode(WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE);
		}
		setOnLongClickListener(this);
		setDownloadListener(this);
	}
	
	@Override
	public boolean onLongClick(View v){
		HitTestResult result=getHitTestResult();
		if(result.getType()==HitTestResult.IMAGE_TYPE){
			final String url=result.getExtra();
			if(!TextUtils.isEmpty(url)){
				final boolean isUrl=url.startsWith("http");
				new AlertDialog.Builder(getContext())
						.setMessage("保存图片到下面文件夹？\n"+getImageDirPath())
						.setNegativeButton("取消", null)
						.setPositiveButton("保存", new DialogInterface.OnClickListener()
						{
							@Override
							public void onClick(DialogInterface dialog, int which){
								saveImage(url, isUrl);
							}
						})
						.show();
			}
		}
		return false;
	}
	
	@Override
	public void onDownloadStart(String url, String agent, String disposition, String type, long length){
		String name=URLUtil.guessFileName(url, disposition, type);
		DownloadReceiver.download(getContext(), url, type, name, getTitle(), getDirName());
	}
	
	String getImageDirPath(){
		String dirName=getDirName();
		if(dirName==null) dirName=Environment.DIRECTORY_PICTURES;
		return "sdcard/"+(dirName.isEmpty()? "": (dirName+"/"));
	}
	
	void saveImage(final String url, boolean isUrl){
		final Context ctxt=getContext();
		int p=ContextCompat.checkSelfPermission(ctxt, Manifest.permission.WRITE_EXTERNAL_STORAGE);
		if(p!=PackageManager.PERMISSION_GRANTED){
			Toast.makeText(ctxt, "权限不足，请检查手机设置", Toast.LENGTH_SHORT).show();
			return;
		}
		String title=getTitle();
		if(isUrl){
			DownloadReceiver.download(getContext(), url, "image/*", null, title, getDirName());
			return;
		}
		String ext=null;
		String code=null;
		String header="data:image/";
		if(url.startsWith(header)){
			int index=url.indexOf(';', header.length());
			if(index>0){
				ext=url.substring(header.length(), index);
				header="base64,";
				index=url.indexOf(header, index);
				if(index>0){
					code=url.substring(index+header.length());
					if(code.trim().isEmpty()) code=null;
				}
			}
		}
		if(ext!=null && code!=null){
			File dir=new File(getImageDirPath());
			boolean ok=(dir.exists()? dir.isDirectory(): dir.mkdirs());
			if(!ok){
				Toast.makeText(ctxt, "文件夹（"+dir+"）无效", Toast.LENGTH_SHORT).show();
				return;
			}
			
			byte[] data=Base64.decode(code, Base64.DEFAULT);
			String name=Integer.toString(code.hashCode(), 16);
			if(name==null){
				SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
				name=format.format(System.currentTimeMillis());
			}
			File file=new File(dir, title+name+"."+ext);
			
			FileOutputStream fos=null;
			try{
				fos=new FileOutputStream(file);
				fos.write(data);
			}catch(Exception e){
				e.printStackTrace();
				ok=false;
			}
			if(fos!=null) try{ fos.close(); }catch(Exception e){ e.printStackTrace(); }
			if(!ok){
				Toast.makeText(ctxt, "图片保存失败", Toast.LENGTH_SHORT).show();
				return;
			}
			MediaScannerConnection.scanFile(ctxt, new String[]{file.getAbsolutePath()}, null,
					new MediaScannerConnection.OnScanCompletedListener()
			{
				@Override
				public void onScanCompleted(String path, Uri uri){
					if(uri==null) uri=Uri.parse(path);
					try{
						getContext().startActivity(
								new Intent(Intent.ACTION_VIEW).setDataAndType(uri, "image/*"));
					}catch(Exception e){
						e.printStackTrace();
					}
				}
			});
		}
	}
}
