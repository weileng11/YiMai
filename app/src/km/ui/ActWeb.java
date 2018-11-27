package km.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import android.widget.ProgressBar;
import org.json.JSONObject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kameng.yimai.BuildConfig;
import kameng.yimai.R;
import kameng.yimai.wxapi.WXPayEntryActivity;
import kameng.yimai.wxapi.WxPayReq;
import km.api.Server;
import km.base.ActivityProxy;
import km.base.BaseTitleActivity;
import km.util.LoginUtil;
import km.view.KmWebView;

/**
 网页
 <br/>
 Created by wuzhengu on 2018/11/1 0001
 */
public class ActWeb extends BaseTitleActivity
{
	public static final String KEY_URL="web_url";
	String mUrl;
	WebView mWeb;
	ProgressBar vProgress;
	
	public static Intent intent(String url){
		Intent intent=new Intent();
		intent.putExtra(KEY_URL, url);
		intent.setClassName(BuildConfig.APPLICATION_ID, ActWeb.class.getName());
		return intent;
	}
	
	@Override
	protected void onDestroy(){
		mWeb.destroy();
		super.onDestroy();
	}
	
	@Override
	protected int getLayoutId(){
		return R.layout.act_web;
	}
	
	@Override
	protected void onCreate(@Nullable Bundle bundle){
		super.onCreate(bundle);
		mUrl=getIntent().getStringExtra(KEY_URL);
		vProgress=(ProgressBar)findViewById(R.id.web_progress);
		((ViewGroup)findViewById(R.id.web_view)).addView(mWeb=new KmWebView(this));
		mWeb.setWebChromeClient(new WebChromeClient()
		{
			@Override
			public void onProgressChanged(WebView view, int p){
				vProgress.setProgress(p);
				vProgress.setVisibility(p<100? View.VISIBLE: View.GONE);
			}
			
			@SuppressWarnings("unused")
			//@Override
			public void openFileChooser(final ValueCallback<Uri> callback, String type, String capture){
				String[] types={type};
				pickFile(new ValueCallback<Uri[]>()
				{
					@Override
					public void onReceiveValue(Uri[] value){
						callback.onReceiveValue(value!=null && value.length>0? value[0]: null);
					}
				}, types, null);
			}
			
			@Override
			public boolean onShowFileChooser(WebView web,
					ValueCallback<Uri[]> callback, final FileChooserParams params){
				if(Build.VERSION.SDK_INT>=21){
					pickFile(callback, params.getAcceptTypes(), params.createIntent());
					return true;
				}
				return super.onShowFileChooser(web, callback, params);
			}
			
			void pickFile(final ValueCallback<Uri[]> callback, String[] types, Intent intent){
				if(intent==null){
					intent=new Intent(Intent.ACTION_PICK);
					List<String> list=new ArrayList<>();
					if(types!=null) for(String t: types) if(!TextUtils.isEmpty(t)) list.add(t);
					if(list.size()==1) intent.setType(list.get(0));
					else{
						intent.setType("*/*");
						intent.putExtra(Intent.EXTRA_MIME_TYPES, list.toArray());
					}
				}
				boolean ok=getProxy().startActivity(intent, new ActivityProxy.Starter()
				{
					@Override
					protected void onResult(boolean ok, Intent data)
					{
						Uri uri=null;
						if(ok) uri=data.getData();
						callback.onReceiveValue(uri==null? null: new Uri[]{uri});
					}
				});
				if(!ok) callback.onReceiveValue(null);
			}
		});
		mWeb.setWebViewClient(new WebViewClient()
		{
			@Override
			public boolean shouldOverrideUrlLoading(WebView web, String url){
				boolean isHttp=url.startsWith("http://") || url.startsWith("https://");
				if(isHttp){
					if(url.startsWith("http://wpa.qq.com/")){
						Matcher m=Pattern.compile("[?&]uin=\\d+").matcher(url);
						if(m.find()){
							boolean ok=startQQ(getContext(), m.group().substring(5));
							if(ok) return true;
						}
					}
				}
				if(!isHttp){
					try{
						startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
					}catch(Exception e){
						e.printStackTrace();
						web.loadUrl(url);
					}
					return true;
				}
				if(url.startsWith("https://wx.tenpay.com")){
					Map<String, String> headers=new HashMap<>();
					headers.put("Referer", Server.BASE_WEB);
					web.loadUrl(url, headers);
					return true;
				}
				WebView.HitTestResult result=web.getHitTestResult();
				if(result==null){
					web.loadUrl(url);
					return true;
				}
				return super.shouldOverrideUrlLoading(web, url);
			}
			
			@Override
			public void onReceivedError(WebView view, int code, String desc, String url){
				super.onReceivedError(view, code, desc, url);
			}
			
			@Override
			public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error){
				handler.proceed();
			}
		});
		mWeb.addJavascriptInterface(this, "js_obj");
		String url=mUrl;
		if(url.indexOf('?')<0) url+='?';
		url+="&token="+LoginUtil.getToken();
		mWeb.loadUrl(url);
		setLeftText("关闭");
		setRightText("返回");
	}
	
	@Override
	protected void onClickLeft(){
		finish();
	}
	
	@Override
	protected void onClickRight(){
		mWeb.goBack();
	}
	
	@Override
	public void onBackPressed(){
		if(mWeb.canGoBack()) mWeb.goBack();
		else new AlertDialog.Builder(getContext())
				.setMessage("关闭当前页面？")
				.setCancelable(false)
				.setNegativeButton("取消", null)
				.setPositiveButton("关闭", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which){
						finish();
					}
				})
				.show();
	}
	
	@JavascriptInterface
	public void shareWeb(String json){
		if(true){
			toast("即将开放");
			return;
		}
		try{
			JSONObject jo=new JSONObject(json);
			String title=jo.getString("title");
			String desc=jo.getString("desc");
			String url=jo.getString("url");
			String imgUrl=jo.getString("share_img");
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	@JavascriptInterface
	public void logout(){
		LoginUtil.logout();
		ActLogin.start(this);
		finish();
	}
	
	@JavascriptInterface
	public void wxPay(String json){
		try{
			JSONObject jo=new JSONObject(json);
			WxPayReq req=new WxPayReq();
			req.nonceStr=jo.optString("noncestr");
			req.partnerId=jo.optString("partnerid");
			req.prepayId=jo.optString("prepayid");
			req.timeStamp=jo.optString("timestamp");
			req.sign=jo.optString("sign");
			WXPayEntryActivity.pay(getContext(), req, new WXPayEntryActivity.Callback()
			{
				@Override
				public boolean onCall(String code){
					return true;
				}
			});
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static boolean startQQ(Context ctxt, String qNumber){
		try{
			ctxt.startActivity(new Intent(Intent.ACTION_VIEW,
					Uri.parse("mqqwpa://im/chat?chat_type=wpa&uin="+qNumber)));
			return true;
		}catch(Exception e){ e.printStackTrace(); }
		return false;
	}
}