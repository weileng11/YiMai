package kameng.yimai.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;

/**
 微信支付
 <br/>
 Created by wuzhengu on 2018/11/12
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler
{
	
	public static IWXAPI api(Context ctxt){
		return WXEntryActivity.api(ctxt);
	}
	
	public static boolean pay(Context ctxt, WxPayReq req, Callback callback){
		boolean ok=api(ctxt).sendReq(req);
		set(callback, ok);
		return ok;
	}
	
	public static boolean pay(Context ctxt, String nonceStr,
			String partnerId, String prepayId, String sign, String timeStamp, Callback callback){
		WxPayReq req=new WxPayReq();
		req.nonceStr=nonceStr;
		req.partnerId=partnerId;
		req.prepayId=prepayId;
		req.sign=sign;
		req.timeStamp=timeStamp;
		return pay(ctxt, req, callback);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		api(null);
	}
	
	@Override
	protected void onCreate(@Nullable Bundle bundle){
		super.onCreate(bundle);
		api(this).handleIntent(getIntent(), this);
	}
	
	@Override
	protected void onNewIntent(Intent intent){
		super.onNewIntent(intent);
		api(this).handleIntent(getIntent(), this);
	}
	
	@Override
	public void onReq(BaseReq req){
		
	}
	
	@Override
	public void onResp(BaseResp resp){
		finish();
		Callback callback=mCallback;
		mCallback=null;
		if(callback==null) return;
		if(resp.errCode==BaseResp.ErrCode.ERR_OK){
			switch(resp.getType()){
			case ConstantsAPI.COMMAND_PAY_BY_WX:
				callback.call("ok");//微信支付
				break;
			}
			return;
		}
		switch(resp.errCode){
		case BaseResp.ErrCode.ERR_USER_CANCEL:
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			callback.call("");//用户取消
			return;
		}
		callback.call((String)null);
	}
	
	static void set(Callback callback, boolean ok){
		if(mCallback!=null){
			mCallback.call("");
			mCallback=null;
		}
		if(ok) mCallback=callback;
		else if(callback!=null) callback.call((String)null);
	}
	
	static Callback mCallback;
	
	public static abstract class Callback
	{
		public boolean onCall(String code){ return true; }
		
		void call(String code){
			onCall(code);
		}
	}
}