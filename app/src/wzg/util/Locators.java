package wzg.util;

import android.content.Context;
import android.text.TextUtils;
import com.baidu.location.*;

/** Created by wuzhengu on 2018/9/14 0014 */
public class Locators
{
	public static final String COOR_TYPE="GCJ02";
	public static final int INTERVAL=10*60*1000;
	public static final boolean USE_GPS=true;
	public static final boolean NEED_ADDRESS=true;
	
	public static String toText(Locator locator){
		return locator.getLongitude()+","+locator.getLatitude();
	}
	
	public static String trim(String name, char... footer){
		if(name==null) return name;
		int last=name.length()-1;
		if(last>0){
			char lastChar=name.charAt(last);
			for(char c: footer) if(lastChar==c) return name.substring(0, last);
		}
		return name;
	}
	
	public static String trimProvince(String name){
		if(TextUtils.isEmpty(name)) return name;
		String[] vip={
				
		};
		for(String str: vip) if(name.equals(str)) return name;
		String[] footer={
				"特别行政区",
				"壮族自治区",
				"回族自治区",
				"维吾尔自治区",
				"自治区",
		};
		for(String str: footer){
			int index=name.lastIndexOf(str);
			if(index>0) return name.substring(0, index);
		}
		return trim(name, '省');
	}
	
	public static String trimCity(String name){
		if(TextUtils.isEmpty(name)) return name;
		String[] vip={
				"克州",
		};
		for(String str: vip) if(name.equals(str)) return name;
		String[] footer={
				"地区",
				"朝鲜族自治州",
				"土家族苗族自治州",
				"土家族苗族自治州",
				"藏族羌族自治州",
				"藏族自治州",
				"彝族自治州",
				"苗族侗族自治州",
				"布依族苗族自治州",
				"布依族苗族自治州",
				"彝族自治州",
				"哈尼族彝族自治州",
				"壮族苗族自治州",
				"傣族自治州",
				"白族自治州",
				"傣族景颇族自治州",
				"傈僳族自治州",
				"藏族自治州",
				"回族自治州",
				"藏族自治州",
				"藏族自治州",
				"藏族自治州",
				"蒙古族藏族自治州",
				"藏族自治州",
				"藏族自治州",
				"藏族自治州",
				"哈萨克自治州",
				"蒙古自治州",
				"回族自治州",
				"蒙古自治州",
				"自治州",
		};
		for(String str: footer){
			int index=name.lastIndexOf(str);
			if(index>0) return name.substring(0, index);
		}
		return trim(name, '市', '州', '盟');
	}
	
	public static Locator create(){
		return new LocatorBaidu();
	}
	
	public interface Callback
	{
		void onLocate(Locator locator);
	}
	
	public interface Locator
	{
		void start(Context ctxt, Callback callback);
		void pause(boolean pause);
		void stop();
		double getLongitude();
		double getLatitude();
		String getCountry();
		String getProvince();
		String getCity();
		String getAddress();
	}
	
	static class LocatorBaidu extends BDAbstractLocationListener implements Locator
	{
		LocationClient client;
		Callback callback;
		BDLocation location;
		
		@Override
		public void onReceiveLocation(BDLocation loc){
			if(loc==null) return;
			this.location=loc;
			if(callback!=null) callback.onLocate(this);
		}
		
		@Override
		public double getLongitude(){
			if(location!=null) return location.getLongitude();
			return 0;
		}
		
		@Override
		public double getLatitude(){
			if(location!=null) return location.getLatitude();
			return 0;
		}
		
		@Override
		public String getCountry(){
			if(location!=null) return location.getCountry();
			return null;
		}
		
		@Override
		public String getProvince(){
			if(location!=null) return location.getProvince();
			return null;
		}
		
		@Override
		public String getCity(){
			if(location!=null) return location.getCity();
			return null;
		}
		
		@Override
		public String getAddress(){
			if(location!=null) return location.getAddrStr();
			return null;
		}
		
		@Override
		public void start(Context ctxt, Callback callback){
			this.callback=callback;
			if(client==null){
				LocationClientOption option=new LocationClientOption();
				//可选，设置定位模式，默认高精度
				//LocationMode.Hight_Accuracy：高精度；
				//LocationMode. Battery_Saving：低功耗；
				//LocationMode. Device_Sensors：仅使用设备；
				option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
				//可选，设置经纬度坐标类型，默认GCJ02
				//GCJ02：国测局坐标；
				//BD09LL：百度经纬度坐标；
				//BD09：百度墨卡托坐标；
				//WGS84：地心坐标系，海外地区定位统一用此类型
				option.setCoorType(COOR_TYPE);
				//可选，设置发起定位请求的间隔，int类型，单位ms
				//如果设置为0，则代表单次定位，即仅定位一次，默认为0
				//如果设置非0，需设置1000ms以上才有效
				option.setScanSpan(INTERVAL);
				//可选，设置是否使用gps，默认false
				//使用高精度和仅用设备两种定位模式的，参数必须设置为true
				option.setOpenGps(USE_GPS);
				//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
				option.setLocationNotify(true);
				//可选，定位SDK内部是一个service，并放到了独立进程。
				//设置是否在stop的时候杀死这个进程，默认（建议）不杀死
				option.setIgnoreKillProcess(true);
				//可选，设置是否收集Crash信息，默认收集，即参数为false
				option.SetIgnoreCacheException(true);
				//可选，V7.2版本新增能力
				//如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，
				//若超出有效期，会先重新扫描Wi-Fi，然后定位
				option.setWifiCacheTimeOut(60*60*1000);
				//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
				option.setEnableSimulateGps(false);
				//可选，是否需要地址信息，默认为不需要，即参数为false
				option.setIsNeedAddress(NEED_ADDRESS);
				client=new LocationClient(ctxt);
				client.setLocOption(option);
			}
			client.registerLocationListener(this);
			client.start();
		}
		
		@Override
		public void pause(boolean pause){
			if(client==null) return;
			if(pause) client.stop();
			else client.start();
		}
		
		@Override
		public void stop(){
			if(client==null) return;
			client.stop();
			client.unRegisterLocationListener(this);
			client=null;
		}
	}
}
