package km.view;

import android.view.View;
import android.widget.AdapterView;
import java.util.ArrayList;
import java.util.List;
import chihane.jdaddressselector.AddressProvider;
import chihane.jdaddressselector.AddressSelector;
import chihane.jdaddressselector.OnAddressSelectedListener;
import chihane.jdaddressselector.model.*;
import km.api.*;
import km.model.Region;
import km.model.M;

/** Created by wuzhengu on 2018/8/2 0002 */
public abstract class KmCityPicker extends AddressSelector
		implements AddressProvider, OnAddressSelectedListener
{
	public enum Level
	{
		PROVINCE, CITY, COUNTY, STREET,;
		
		public boolean isLowerThan(Level level){
			return this.ordinal()<level.ordinal();
		}
	}
	
	RxActivity mActivity;
	Level mLevel=Level.COUNTY;
	Province mProvince;
	City mCity;
	County mCounty;
	
	public KmCityPicker(RxActivity act){
		super(act);
		mActivity=act;
		super.setAddressProvider(this);
		super.setOnAddressSelectedListener(this);
	}
	
	public void setLevel(Level level){
		if(level==null) level=Level.CITY;
		mLevel=level;
	}
	
	@Override
	public void provideProvinces(AddressReceiver<Province> receiver){
		if(mLevel.isLowerThan(Level.PROVINCE)){
			receiver.send(null);
			return;
		}
		provide(0, receiver, new Converter<Province, Region>()
		{
			@Override
			public Province convert(Region src){
				Province dst=new Province();
				dst.id=src.id;
				dst.name=src.name;
				return dst;
			}
		});
	}
	
	@Override
	public void provideCitiesWith(int id, AddressReceiver<City> receiver){
		if(mLevel.isLowerThan(Level.CITY)){
			receiver.send(null);
			return;
		}
		provide(id, receiver, new Converter<City, Region>()
		{
			@Override
			public City convert(Region src){
				City dst=new City();
				dst.id=src.id;
				dst.name=src.name;
				dst.province_id=src.parentId;
				return dst;
			}
		});
	}
	
	@Override
	public void provideCountiesWith(int id, AddressReceiver<County> receiver){
		if(mLevel.isLowerThan(Level.COUNTY)){
			receiver.send(null);
			return;
		}
		provide(id, receiver, new Converter<County, Region>()
		{
			@Override
			public County convert(Region src){
				County dst=new County();
				dst.id=src.id;
				dst.name=src.name;
				dst.city_id=src.parentId;
				return dst;
			}
		});
	}
	
	@Override
	public void provideStreetsWith(int id, AddressReceiver<Street> receiver){
		if(mLevel.isLowerThan(Level.STREET)){
			receiver.send(null);
			return;
		}
		provide(id, receiver, new Converter<Street, Region>()
		{
			@Override
			public Street convert(Region src){
				Street dst=new Street();
				dst.id=src.id;
				dst.name=src.name;
				dst.county_id=src.parentId;
				return dst;
			}
		});
	}
	
	@Override
	public void onItemClick(AdapterView<?> pa, View view, int p, long id){
		super.onItemClick(pa, view, p, id);
		Object item=pa.getAdapter().getItem(p);
		if(item==null){
		}else if(item instanceof Province){
			if(Level.PROVINCE.isLowerThan(mLevel)){
				onItemClick(mProvince=(Province)item, mCity=null, mCounty=null, null);
			}
		}else if(item instanceof City){
			if(Level.CITY.isLowerThan(mLevel)){
				onItemClick(mProvince, mCity=(City)item, mCounty=null, null);
			}
		}else if(item instanceof County){
			if(Level.COUNTY.isLowerThan(mLevel)){
				onItemClick(mProvince, mCity, mCounty=(County)item, null);
			}
		}
	}
	
	@Override
	public void onAddressSelected(Province province, City city, County county, Street street){}
	
	public void onItemClick(Province province, City city, County county, Street street){}
	
	<T> void provide(int id, final AddressReceiver<T> receiver, final Converter<T, Region> c){
		Servers.start(mActivity, Server.get().getCityList(id),
				new RxListener<M<Region.Result>>()
				{
					@Override
					public void onNext(M<Region.Result> model, Throwable ex){
						List<T> list=new ArrayList<>();
						List<Region> data=null;
						if(model!=null && model.result!=null) data=model.result.list;
						if(data!=null) for(Region bean : data) list.add(c.convert(bean));
						receiver.send(list);
					}
				});
	}
	
	private interface Converter<Dst, Src>
	{
		Dst convert(Src src);
	}
}
