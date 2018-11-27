package kameng.yimai.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.*;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.HttpsURLConnection;

/**
 微信登录
 <br/>
 Created by wuzhengu on 2018/8/20
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler
{
	public static final String APP_ID="wxe8a2cead4dc8cb2b";
	public static final String APP_SECRET="894526eb4afe9f7997fd9a99db759993";
	static IWXAPI mApi;
	
	public static IWXAPI api(Context ctxt){
		if(ctxt==null){
			if(mApi!=null){
				mApi.unregisterApp();
				mApi.detach();
				mApi=null;
			}
			return null;
		}
		if(mApi==null){
			mApi=WXAPIFactory.createWXAPI(ctxt, APP_ID, true);
			mApi.registerApp(APP_ID);
		}
		return mApi;
	}
	
	public static boolean login(Context ctxt, Callback callback){
		String state=callback==null? null: Integer.toHexString(callback.hashCode());
		SendAuth.Req req=new SendAuth.Req();
		req.scope="snsapi_userinfo";
		req.state=state;
		boolean ok=api(ctxt).sendReq(req);
		set(callback, ok);
		return ok;
	}
	
	public static boolean share(Context ctxt,
			String url, String title, String desc, Bitmap bmp, Callback callback){
		WXWebpageObject web=new WXWebpageObject();
		web.webpageUrl=url;
		WXMediaMessage msg=new WXMediaMessage(web);
		msg.title=title;
		msg.description=desc;
		if(bmp!=null){
			int size=100;
			if(bmp.getWidth()>size || bmp.getHeight()>size){
				bmp=Bitmap.createScaledBitmap(bmp, size, size, true);
			}
			msg.thumbData=bmpToBytes(bmp, true);
		}
		SendMessageToWX.Req req=new SendMessageToWX.Req();
		req.message=msg;
		req.transaction="webpage"+System.currentTimeMillis();
		req.scene=SendMessageToWX.Req.WXSceneSession;
		boolean ok=api(ctxt).sendReq(req);
		set(callback, ok);
		return ok;
	}
	
	public static byte[] bmpToBytes(final Bitmap bmp, final boolean recycle){
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		bmp.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		if(recycle) bmp.recycle();
		byte[] data=bos.toByteArray();
		try{ bos.close(); }catch(Exception e){ e.printStackTrace(); }
		return data;
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
			case ConstantsAPI.COMMAND_SENDAUTH:
				callback.call(((SendAuth.Resp)resp).code);//微信登录
				break;
			case ConstantsAPI.COMMAND_SENDMESSAGE_TO_WX:
				callback.call("ok");//微信分享
				break;
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
	
	/**Do remember to call {@link #destroy()} !!!*/
	public static abstract class Callback implements Runnable
	{
		Handler mHandler;
		int mState=0;
		String mCode;
		Auth mAuth;
		User mUser;
		
		public void destroy(){
			mHandler=null;
		}
		
		/**
		 @param code null表示失败，empty表示取消，“ok”或其他表示成功
		 */
		public boolean onCall(String code){ return false; }
		public boolean onCall(Auth auth){ return false; }
		public boolean onCall(User user){ return false; }
		
		void call(String data){
			if(onCall(data)) return;
			if(mHandler==null) mHandler=new Handler();
			mCode=data;
			mState=1;
			new Thread(this).start();
		}
		
		void call(Auth data){
			if(onCall(data)) return;
			if(mHandler==null) return;
			mAuth=data;
			mState=2;
			new Thread(this).start();
		}
		
		void call(User data){
			if(onCall(data)) return;
		}
		
		@Override
		public final void run(){
			try{
				switch(mState){
				case 1:
					final Auth auth=mCode==null? null: getAuth(mCode);
					if(mHandler!=null) mHandler.post(new Runnable(){
						@Override
						public void run(){
							call(auth);
						}
					});
					break;
				case 2:
					final User user=mAuth==null? null: getUser(mAuth.accessToken, mAuth.openid);
					if(mHandler!=null) mHandler.post(new Runnable(){
						@Override
						public void run(){
							call(user);
						}
					});
					break;
				}
			}catch(Exception e){ e.printStackTrace(); }
		}
	}
	
	public static Auth getAuth(String code) throws Exception{
		String path="https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APP_ID+
		            "&secret="+APP_SECRET+"&grant_type=authorization_code&code="+code;
		return get(Auth.class, path);
	}
	
	public static User getUser(String token, String openid) throws Exception{
		String path="https://api.weixin.qq.com/sns/userinfo?access_token="+token+"&openid="+openid;
		return get(User.class, path);
	}
	
	@SuppressWarnings("unchecked")
	@Keep
	public static <Data extends JSONObject> Data get(Class<Data> cls, String url) throws Exception{
		int timeout=10*1000;
		HttpsURLConnection con=(HttpsURLConnection)new URL(url).openConnection();
		con.setConnectTimeout(timeout);
		con.setReadTimeout(timeout);
		BufferedReader r=new BufferedReader(new InputStreamReader(con.getInputStream()));
		StringBuilder builder=new StringBuilder();
		String text;
		while((text=r.readLine())!=null){
			if(builder.length()>0) builder.append("\n");
			builder.append(text);
		}
		text=builder.toString();
		Data data=cls.getDeclaredConstructor(String.class).newInstance(text);
		for(Field f : data.getClass().getDeclaredFields()){
			JsonName name=f.getAnnotation(JsonName.class);
			String key=name!=null? name.value(): f.getName();
			Object value=data.opt(key);
			if(value instanceof JSONArray){
				List<String> list=new ArrayList<>();
				JSONArray ja=(JSONArray)value;
				for(int i=0; i<ja.length(); i++) list.add(String.valueOf(ja.opt(i)));
				value=list;
			}
			if(value!=null) f.set(data, f.getType().cast(value));
		}
		return data;
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@interface JsonName
	{
		String value();
	}
	
	@Keep
	public static class Auth extends JSONObject
	{
		public Auth(String json) throws JSONException{
			super(json);
		}
		
		@JsonName("access_token")
		public String accessToken;
		@JsonName("expires_in")
		public Integer expiresIn;
		@JsonName("openid")
		public String openid;
		@JsonName("refresh_token")
		public String refreshToken;
		@JsonName("scope")
		public String scope;
		@JsonName("unionid")
		public String unionid;
	}
	
	@Keep
	public static class User extends JSONObject
	{
		public User(String json) throws JSONException{
			super(json);
		}
		
		@JsonName("city")
		public String city;
		@JsonName("country")
		public String country;
		@JsonName("headimgurl")
		public String headimgurl;
		@JsonName("language")
		public String language;
		@JsonName("nickname")
		public String nickname;
		@JsonName("openid")
		public String openid;
		@JsonName("privilege")
		public List<String> privilege;
		@JsonName("province")
		public String province;
		@JsonName("sex")
		public Integer sex;
		@JsonName("unionid")
		public String unionid;
		
		@Override
		public String toString(){
			try{
				return toString(4);
			}catch(Exception e){
				e.printStackTrace();
			}
			return super.toString();
		}
	}
}