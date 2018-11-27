package vip;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.webkit.ConsoleMessage;
import com.uzmap.pkg.openapi.IncPackage;
import com.uzmap.pkg.openapi.WebViewProvider;
import com.uzmap.pkg.uzcore.UZAppActivity;
import com.uzmap.pkg.uzcore.uzmodule.UZModuleContext;
import kameng.yimai.vip.R;

/** Created by wuzhengu on 2018/11/14 0014 */
public class VipActivity extends UZAppActivity
{
	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
		getWindow().setBackgroundDrawableResource(R.drawable.uz_splash_bg);
	}
	
	protected final boolean isFromNativeSDK(){
		return true;
	}
	
	public void onConsoleMessage(ConsoleMessage var1){
	}
	
	protected boolean onHtml5AccessRequest(WebViewProvider var1, UZModuleContext var2){
		return false;
	}
	
	protected final void onPageFinished(WebViewProvider var1, String var2){
		
	}
	
	protected final void onPageStarted(WebViewProvider var1, String var2, Bitmap var3){
		
	}
	
	protected final void onProgressChanged(WebViewProvider var1, int var2){
		
	}
	
	protected final void onReceivedTitle(WebViewProvider var1, String var2){
		
	}
	
	protected boolean onSmartUpdateFinish(IncPackage var1){
		return false;
	}
	
	protected boolean shouldForbiddenAccess(String var1, String var2, String var3){
		return false;
	}
	
	protected final boolean shouldOverrideUrlLoading(WebViewProvider var1, String url){
		return false;
	}
}
