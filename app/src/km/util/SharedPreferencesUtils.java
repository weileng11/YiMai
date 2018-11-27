package km.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.support.v4.content.SharedPreferencesCompat;
import android.util.Log;
import java.io.*;

public final class SharedPreferencesUtils<T>{
	private final static String name = "config";
	private final static int mode = Context.MODE_PRIVATE;
	
	/**
	 * 保存SharedPreference（boolean）类型
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveBoolean(Context context,String key,boolean value){
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		Editor edit = sp.edit();
		edit.putBoolean(key, value);
		edit.commit();
	}

	/**
	 * 保存SharedPreference（Int）类型
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveInt(Context context,String key,int value){
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		Editor edit = sp.edit();
		edit.putInt(key, value);
		edit.commit();
	}

	/**
	 * 保存SharedPreference（String）类型
	 * @param context
	 * @param key
	 * @param value
	 */
	public static void saveString(Context context,String key,String value){
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}
	
	
	/**
	 * 获取SharedPreference（String）类型
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static boolean getBoolean(Context context,String key,boolean defValue){
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		return sp.getBoolean(key, defValue);
	}

	/**
	 * 获取SharedPreference（Int）类型
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static int getInt(Context context,String key,int defValue){
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		return sp.getInt(key, defValue);
	}

	/**
	 * 获取SharedPreference（String）类型
	 * @param context
	 * @param key
	 * @param defValue
	 * @return
	 */
	public static String getString(Context context,String key,String defValue){
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		return sp.getString(key, defValue);
	}

	/**
	 * 序列化对象
	 *
	 * @param person
	 * @return
	 * @throws IOException
	 */
	public String serialize(T person) throws IOException{
		long startTime = System.currentTimeMillis();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(
				byteArrayOutputStream);
		objectOutputStream.writeObject(person);
		String serStr = byteArrayOutputStream.toString("ISO-8859-1");
		serStr = java.net.URLEncoder.encode(serStr, "UTF-8");
		objectOutputStream.close();
		byteArrayOutputStream.close();
		Log.d("serial", "serialize str ="+serStr);
		long  endTime = System.currentTimeMillis();
		Log.d("serial", "序列化耗时为:"+(endTime-startTime));
		return serStr;
	}


	/**
	 * 反序列化对象
	 *
	 * @param str
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public  T deSerialization(String str) throws IOException, ClassNotFoundException{
		long startTime = System.currentTimeMillis();
		String redStr = java.net.URLDecoder.decode(str, "UTF-8");
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(
				redStr.getBytes("ISO-8859-1"));
		ObjectInputStream objectInputStream = new ObjectInputStream(
				byteArrayInputStream);
		T person = (T) objectInputStream.readObject();
		objectInputStream.close();
		byteArrayInputStream.close();
		long endTime = System.currentTimeMillis();
		Log.d("serial", "反序列化耗时为:"+(endTime-startTime));
		return person;
	}

	public static void saveObject(Context mcontext,String strObject) {
		SharedPreferences sp =mcontext.getSharedPreferences("person", 0);
		Editor edit = sp.edit();
		edit.putString("person", strObject);
		edit.commit();
	}

	public static String getObject(Activity mcontext) {
		SharedPreferences sp = mcontext.getSharedPreferences("person", 0);
		return sp.getString("person", null);
	}

	/**
	 * 清空数据
	 */
	public static void clearSpData(Context context){
		SharedPreferences sp = context.getSharedPreferences(name, mode);
		Editor editor = sp.edit();
		editor.clear();
		SharedPreferencesCompat.EditorCompat.getInstance().apply(editor);
	}


	//只针对于账号和密码
	public static void savePhonePwdString(Context context,String key,String value){
		SharedPreferences sp = context.getSharedPreferences("phone_pwd", mode);
		Editor edit = sp.edit();
		edit.putString(key, value);
		edit.commit();
	}

	//只针对于账号和密码
	public static String getPhonePwdString(Context context,String key,String defValue){
		SharedPreferences sp = context.getSharedPreferences("phone_pwd", mode);
		return sp.getString(key, defValue);
	}
}
