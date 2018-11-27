package km.util;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;
import km.app.App;

public class PrefUtil
{
	public static final String PREF_NAME="config";
	public static final String KEY_USERNAME="login_username";
	public static final String KEY_PASSWORD="login_password";
	public static final String KEY_PASSWORD_REMEMBER="login_password_remember";
	
	private static SharedPreferences getPref(){
		return getPref(App.get());
	}
	
	private static SharedPreferences getPref(Context ctxt){
		return ctxt.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}
	
	public static void putString(String key, String value){
		getPref().edit().putString(key, value).apply();
	}
	
	public static String getString(String key, String defValue){
		return getPref().getString(key, defValue);
	}
	
	public static void putBoolean(String key, Boolean value){
		getPref().edit().putBoolean(key, value).apply();
	}
	
	public static boolean getBoolean(String key, Boolean defValue){
		return getPref().getBoolean(key, defValue);
	}
	
	public static void putInt(String key, int value){
		getPref().edit().putInt(key, value).apply();
	}
	
	public static int getInt(String key, int defValue){
		return getPref().getInt(key, defValue);
	}
	
	public static void putFloat(String fileName, String key, float value){
		getPref().edit().putFloat(key, value).apply();
	}
	
	public static float getFloat(String key, Float defValue){
		return getPref().getFloat(key, defValue);
	}
	
	public static void putLong(String key, long value){
		getPref().edit().putLong(key, value).apply();
	}
	
	public static long getLong(String key, long defValue){
		return getPref().getLong(key, defValue);
	}
	
	static String keyForSize(String key){
		return key+"_size";
	}
	
	public static List<String> getStringList(String key){
		List<String> list=new ArrayList<>();
		int size=getInt(keyForSize(key), 0);
		if(size>0){
			SharedPreferences pref=getPref();
			for(int i=0; i<size; i++) list.add(pref.getString(key+i, null));
		}
		return list;
	}
	
	public static void putStringList(String key, List<String> list){
		if(key==null || key.isEmpty()) return;
		removeStringList(key);
		int size=list==null? 0: list.size();
		if(size>0){
			SharedPreferences.Editor editor=getPref().edit();
			editor.putInt(keyForSize(key), size);
			for(int i=0; i<size; i++) editor.putString(key+i, list.get(i));
			editor.apply();
		}
	}
	
	public static void removeStringList(String key){
		String key2=keyForSize(key);
		int size=getInt(key2, 0);
		if(size>0){
			SharedPreferences.Editor editor=getPref().edit();
			editor.remove(key2);
			for(int i=0; i<size; i++) editor.remove(key+i);
			editor.apply();
		}
	}
	
	public static void remove(String key){
		getPref().edit().remove(key).apply();
	}
}
