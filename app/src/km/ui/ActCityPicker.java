package km.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import chihane.jdaddressselector.model.*;
import kameng.yimai.BuildConfig;
import kameng.yimai.R;
import km.base.BaseTitleActivity;
import km.view.KmCityPicker;

/**
 城市选择
 <br/>
 Created by wuzhengu on 2018/10/30 0030
 */
public class ActCityPicker extends BaseTitleActivity
{
	public static final String KEY_ADDRESS_NAME="city_picker_address";
	public static final String KEY_ADDRESS_ID="city_picker_id";
	public static final String KEY_LEVEL="city_picker_level";
	final String[] mAddressName=new String[4];
	final int[] mAddressId=new int[4];
	
	/**
	 @param level
	 {@link KmCityPicker.Level#PROVINCE PROVINCE },
	 {@link KmCityPicker.Level#CITY CITY },
	 {@link KmCityPicker.Level#COUNTY COUNTY },
	 {@link KmCityPicker.Level#STREET STREET }
	 */
	public static Intent intent(KmCityPicker.Level level){
		Intent intent=new Intent();
		intent.putExtra(KEY_LEVEL, level);
		intent.setClassName(BuildConfig.APPLICATION_ID, ActCityPicker.class.getName());
		return intent;
	}
	
	@Override
	public int getLayoutId(){
		return R.layout.act_city_picker;
	}
	
	@Override
	protected void onCreate(Bundle bundle){
		super.onCreate(bundle);
		setTitle("地区选择");
		setRightText("确定");
		final KmCityPicker.Level level=
				(KmCityPicker.Level)getIntent().getSerializableExtra(KEY_LEVEL);
		((ViewGroup)findViewById(R.id.city_picker)).addView(new KmCityPicker(this)
		{
			{
				setLevel(level);
			}
			@Override
			public void onAddressSelected(Province pro, City city, County county, Street street){
				onItemClick(pro, city, county, street);
				onClickRight();
			}
			
			@Override
			public void onItemClick(Province pro, City city, County county, Street street){
				mAddressId[0]=pro==null? 0: pro.id;
				mAddressId[1]=city==null? 0: city.id;
				mAddressId[2]=county==null? 0: county.id;
				mAddressId[3]=street==null? 0: street.id;
				mAddressName[0]=pro==null? null: pro.name;
				mAddressName[1]=city==null? null: city.name;
				mAddressName[2]=county==null? null: county.name;
				mAddressName[3]=street==null? null: street.name;
			}
		}.getView());
	}
	
	@Override
	protected void onClickRight(){
		setResult(RESULT_OK, new Intent()
				.putExtra(KEY_ADDRESS_NAME, mAddressName)
				.putExtra(KEY_ADDRESS_ID, mAddressId)
		);
		finish();
	}
	
	public static Address parseResult(Intent data){
		int[] addressId=null;
		String[] addressName=null;
		if(data!=null){
			addressId=data.getIntArrayExtra(KEY_ADDRESS_ID);
			addressName=data.getStringArrayExtra(KEY_ADDRESS_NAME);
		}
		if(addressId!=null && addressId.length==4 && addressName!=null && addressName.length==4){
			Address address=new Address();
			address.provinceName=addressName[0];
			address.cityName=addressName[1];
			address.countyName=addressName[2];
			address.streetName=addressName[3];
			address.provinceId=addressId[0];
			address.cityId=addressId[1];
			address.countyId=addressId[2];
			address.streetId=addressId[3];
			return address;
		}
		return null;
	}
	
	public static class Address
	{
		public String provinceName;
		public String cityName;
		public String countyName;
		public String streetName;
		public int provinceId;
		public int cityId;
		public int countyId;
		public int streetId;
	}
}
