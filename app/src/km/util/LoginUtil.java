package km.util;

import android.text.TextUtils;
import com.google.gson.Gson;
import km.model.User;

public class LoginUtil
{
	private static final String KEY_TOKEN="token";
	private static final String KEY_USER="user";
	private static User mUser;
	private static String mToken;
	private static Gson gson=new Gson();
	
	public synchronized static void setUser(User user){
		if(user==null) return;
		PrefUtil.putString(KEY_USER, gson.toJson(mUser=user));
	}
	
	public synchronized static User getUser(){
		if(mUser==null) try{
			mUser=gson.fromJson(PrefUtil.getString(KEY_USER, null), User.class);
		}catch(Throwable e){
			e.printStackTrace();
			PrefUtil.remove(KEY_USER);
		}
		if(mUser==null) mUser=new User();
		return mUser;
	}
	
	public synchronized static void setToken(String token){
		if(token==null) return;
		PrefUtil.putString(KEY_TOKEN, mToken=token);
	}
	
	public static String getToken(){
		if(mToken==null) mToken=PrefUtil.getString(KEY_TOKEN, "");
		return mToken;
	}
	
	public synchronized static boolean hasLogin(){
		return !TextUtils.isEmpty(getToken());
	}
	
	public static void logout(){
		mToken=null;
		PrefUtil.remove(KEY_TOKEN);
		mUser=null;
		PrefUtil.remove(KEY_USER);
	}
}
