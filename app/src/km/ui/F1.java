package km.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kameng.yimai.R;
import km.api.Server;
import km.api.Servers;
import km.base.ActivityProxy;
import km.base.HomeFragment;
import km.model.*;
import km.other.BannerHelper;
import km.other.GoodsAdapter;
import km.quick.QuickAdapter;
import km.quick.QuickClickAdapter;
import km.quick.QuickHolder;
import km.view.*;
import rx.Observable;
import rx.Subscriber;
import wzg.util.Locators;

/**
 首页
 <br/>
 Created by wuzhengu on 2018/10/29 0029
 */
public class F1 extends HomeFragment
{
	@BindView(R.id.f1_refresh)
	KmRefreshLayout vRefresh;
	@BindView(R.id.f1_list)
	RecyclerView vList;
	@BindView(R.id.f1_city)
	TextView vCity;
	int mRequestRun;
	final SparseArray<List> mDatas=new SparseArray<>();
	GoodsType mCollageGoods;
	List mTags;
	int mPage;
	String mCity;
	
	@Override
	public HomeFragment setGps(Locators.Locator locator){
		super.setGps(locator);
		mCity=Locators.trimCity(locator.getCity());
		if(vCity!=null) vCity.setText(mCity);
		return this;
	}
	
	@Override
	public int getLayout(){
		return R.layout.f1;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle bundle){
		super.onViewCreated(view, bundle);
		if(mCity!=null) vCity.setText(mCity);
		vList.setAdapter(new Adapter());
		vRefresh.setEnableLoadMore(false);
		vRefresh.setEnableAutoLoadMore(false);
		vRefresh.setOnMultiPurposeListener(new KmRefreshListener()
		{
			@Override
			public void onRefresh(RefreshLayout layout){
				load();
			}
			
			@Override
			public void onLoadMore(RefreshLayout layout){
				super.onLoadMore(layout);
				loadMore(mPage+1);
			}
		});
	}
	
	@Override
	public void onInitLate(){
		super.onInitLate();
		getProxy().showLoading(null);
		load();
	}
	
	@OnClick(R.id.f1_city)
	void click_city(final TextView v){
		//跳转并回调
		getProxy().startActivity(new ActivityProxy.Starter()
		{
			@Override
			public Intent getIntent(){
				return ActCityPicker.intent(KmCityPicker.Level.CITY);
			}
			@Override
			protected void onResult(boolean ok, Intent data){
				ActCityPicker.Address address=ActCityPicker.parseResult(data);
				if(address!=null){
					String city=Locators.trimCity(address.cityName);
					if(city==null) city=Locators.trimProvince(address.provinceName);
					v.setText(city);
				}
			}
		});
	}
	
	@OnClick(R.id.f1_search)
	void click_search(TextView v){
		startActivity(ActWeb.intent(Server.BASE_WEB+""));
	}
	
	@OnClick(R.id.f1_news)
	void click_news(){
		startActivity(ActWeb.intent(Server.BASE_WEB+"orderMessage"));
	}
	
	void load(){
		final List<Request> mRequests=new ArrayList<>();
		mRequests.add(new Request<List<BaseGood>>(0, Server.get().getTags())
		{
			@Override
			public void onNext(M<List<BaseGood>> m){
				if(m!=null) mTags=m.result;
			}
		});
		mRequests.add(new Request<>(1, Server.get().getBanners()));
		mRequests.add(new Request<>(2, Server.get().getGoodsType()).setMax(10));
		mRequests.add(new Request<>(3, Server.get().getVipGo()).setMax(4));
		mRequests.add(new Request<GoodsType>(4, Server.get().getCollage())
		{
			@Override
			public void onNext(M<GoodsType> model){
				if(model!=null) mCollageGoods=model.result;
				super.onNext(model);
			}
		});
		mRequests.add(new Request<>(5, Server.get().getHots()));
		mRequests.add(new Request<>(5, Server.get().getHots()));
		for(Request r: mRequests) r.start();
		loadMore(1);
	}
	
	void loadMore(final int p){
		new Request<Good.Page>(6, Server.get().getGoods(p, null, null))
		{
			@Override
			public void onNext(M<Good.Page> m){
				if(m==null) return;
				List list=null;
				Good.Page page=m.result;
				if(page!=null){
					mPage=page.currentPage;
					list=page.getList();
					if(p>1){
						List old=mDatas.get(type);
						if(old!=null && list!=null){
							old.addAll(list);
							list=old;
						}
					}
				}
				if(list!=null && list.size()>0) mDatas.put(type, list);
			}
		}.setMax(0).start();
	}
	
	void update(){
		getProxy().dismissLoading();
		vRefresh.finishRefresh();
		vRefresh.finishLoadMore();
		vList.getAdapter().notifyDataSetChanged();
	}
	
	class Request<T> extends Subscriber<M<T>>
	{
		Observable<M<T>> observable;
		boolean mRunning;
		int type;
		int max;
		
		public Request(int type, Observable<M<T>> observable){
			this.observable=observable;
			this.type=type;
		}
		
		public Request setMax(int max){
			this.max=max;
			return this;
		}
		
		public void start(){
			if(mRunning) return;
			mRunning=true;
			mRequestRun++;
			Servers.start(F1.this, observable, this);
			return;
		}
		
		@Override
		public void onCompleted(){
			mRunning=false;
			mRequestRun--;
			if(mRequestRun<=0) update();
		}
		
		@Override
		public void onError(Throwable ex){
			onCompleted();
		}
		
		@Override
		public void onNext(M<T> m){
			if(type>0){
				List list=null;
				if(m!=null){
					if(m.result instanceof List) list=(List)m.result;
					else if(m.result instanceof DataList) list=((DataList)m.result).getList();
				}
				if(list!=null && list.size()>0){ //10 max=5  5
					if(max>0) for(int i=list.size()-max; i>0; i--) list.remove(list.size()-1);
					mDatas.put(type, list);
				}
			}
		}
	}
	
	void clickGood(BaseGood good){
		GoodsAdapter.clickGood(getContext(), good);
	}
	
	void clickTag(String tip){
		((TextView)findViewById(R.id.f1_search)).setText(tip);
		startActivity(ActWeb.intent(Server.BASE_WEB+"searchGoods?keyword="+tip));
	}
	
	class Adapter extends QuickAdapter<Integer>
	{
		
		public Adapter(){
			super(0, Arrays.asList(1, 2, 3, 4, 5, 6));
		}
		
		@Override
		public int getItemViewType(int p){
			Integer item=getItem(p);
			return item==null? EMPTY_VIEW: item;
		}
		
		@Override
		public QuickHolder onCreateViewHolder(ViewGroup vg, int t){
			try{
				return (H)Class.forName(H.class.getName()+t).getConstructor(F1.class).newInstance(F1.this);
			}catch(Exception e){
				e.printStackTrace();
			}
			return new QuickHolder(new Space(getContext()));
		}
		
		@Override
		protected void convert(QuickHolder h, Integer item){
			((H)h).update(item);
		}
	}
	
	@Keep
	class H<T> extends QuickHolder
	{
		QuickAdapter<T> mAdapter;
		List<?> dftData;
		
		public H(int layout){
			super(inflate(vList, layout));
		}
		
		void update(int type){
			if(mAdapter!=null) mAdapter.setNewData(getData(type));
		}
		
		List<T> getData(int type){
			List list=mDatas.get(type);
			if(list==null || list.isEmpty()){
				if(dftData!=null) list=dftData;
				else{
					T[] data=getDftData();
					if(data!=null && data.length>0) list=Arrays.asList(data);
					dftData=list;
				}
			}
			return list;
		}
		
		T[] getDftData(){
			return null;
		}
	}
	
	@Keep
	class H1 extends H
	{
	
		KmBanner vBanner;
		List<Good> mData;
		
		public H1(){
			super(R.layout.item_f1_h1);
			vBanner=getView(R.id.f1_h1_banner);
			new BannerHelper(vBanner){
				@Override
				public List getData(){
					return mData;
				}
			};
			RecyclerView rv=getView(R.id.f1_h1_tags);
			rv.setAdapter(mAdapter=new QuickClickAdapter<Object>(R.layout.item_tag)
			{
				
				@Override
				protected void convert(QuickHolder h, Object item){
					String name;
					if(item instanceof BaseGood) name=((BaseGood)item).getName();
					else name=String.valueOf(item);
					h.setText(R.id.tag_name, name);
				}
				@Override
				public void onClick(View v, int p){
					Object item=getItem(p);
					String name=null;
					if(item instanceof BaseGood){
						name=((BaseGood)item).getName();
						String url=((BaseGood)item).getLink();
						if(!TextUtils.isEmpty(url)){
							startActivity(ActWeb.intent(url));
							return;
						}
					}
					else if(item!=null) name=item.toString();
					if(TextUtils.isEmpty(name)) return;
					clickTag(name);
				}
			});
		}
		
		@Override
		void update(int type){
			mAdapter.setNewData(mTags);
			vBanner.setImages(mData=getData(type)).start();
		}
		
		@Override
		Object[] getDftData(){
			return new Object[]{
					"",
					"",
					"",
			};
		}
	}
	
	@Keep
	class H2 extends H
	{
		public H2(){
			super(R.layout.item_f1_h2);
			RecyclerView rv=getView(R.id.f1_h2_list);
			rv.setAdapter(mAdapter=new GoodsAdapter(R.layout.item_goods_type));
		}
		
		@Override
		List getData(int type){
			List list=super.getData(type);
			if(list!=null) for(Object item: list) if(item instanceof BaseGood){
				BaseGood good=(BaseGood)item;
				if(good.imageRes==0 && TextUtils.isEmpty(good.getImage())) good.imageRes=R.mipmap.goods_type_more;
			}
			return list;
		}
		
		@Override
		Object[] getDftData(){
			return new GoodsType[]{
					new GoodsType(){{ name="家居生活"; imageRes=R.mipmap.goods_type_01; }},
					new GoodsType(){{ name="美妆美容"; imageRes=R.mipmap.goods_type_02; }},
					new GoodsType(){{ name="特色食品"; imageRes=R.mipmap.goods_type_03; }},
					new GoodsType(){{ name="水果生鲜"; imageRes=R.mipmap.goods_type_04; }},
					new GoodsType(){{ name="休闲零食"; imageRes=R.mipmap.goods_type_05; }},
					new GoodsType(){{ name="茶饮酒水"; imageRes=R.mipmap.goods_type_06; }},
					new GoodsType(){{ name="牛奶乳品"; imageRes=R.mipmap.goods_type_07; }},
					new GoodsType(){{ name="养生补品"; imageRes=R.mipmap.goods_type_08; }},
					new GoodsType(){{ name="生活用品"; imageRes=R.mipmap.goods_type_09; }},
					new GoodsType(){{ name="更多分类"; imageRes=R.mipmap.goods_type_more; }},
			};
		}
	}
	
	@Keep
	class H3 extends H
	{
		View[] mViews;
		Good[] mData;
		
		public H3(){
			super(R.layout.item_f1_h3);
			ButterKnife.bind(this, itemView);
			RecyclerView rv=(RecyclerView)getView(R.id.f1_h3_box);
			rv.setAdapter(mAdapter=new GoodsAdapter(R.layout.item_good));
		}
		
		@OnClick(R.id.f1_h3_vip_go)
		void click_vip_go(){
			startActivity(ActWeb.intent(Server.BASE_WEB+"zeroPurchase"));
		}
	}
	
	@Keep
	class H4 extends H implements View.OnClickListener
	{
		public H4(){
			super(R.layout.item_f1_h4);
			RecyclerView rv=getView(R.id.f1_h4_list);
			rv.setAdapter(mAdapter=new GoodsAdapter<Good>(R.layout.item_good_x)
			{
				@Override
				protected void convert(QuickHolder h, Good item){
					super.convert(h, item);
					h.setText(R.id.item_good_people, String.valueOf(item.people));
				}
			});
			getView(R.id.f1_h4_more).setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v){
			toast("即将开放");
		}
		
		@Override
		void update(int type){
			String image=mCollageGoods==null? null: mCollageGoods.getImage();
			setImage(R.id.f1_h4_banner, image, R.mipmap.f1_goods_tuan);
			super.update(type);
		}
	}
	
	@Keep
	class H5 extends H
	{
		GoodsType mData;
		
		public H5(){
			super(R.layout.item_f1_h5);
			ButterKnife.bind(this, itemView);
			RecyclerView rv=getView(R.id.f1_h6_list);
			rv.setAdapter(mAdapter=new GoodsAdapter(R.layout.item_good_h));
		}
		
		@Override
		void update(int type){
			List list=getData(type);
			if(list==null || list.isEmpty()) return;
			mData=(GoodsType)list.get(0);
			if(mData==null) list=null;
			else{
				setImage(R.id.f1_h5_image, mData.banner);
				list=mData.getList();
			}
			if(list==null || list.isEmpty()) list=Arrays.asList(getDftData());
			mAdapter.setNewData(list);
		}
		
		@OnClick(R.id.f1_h5_image)
		void click_image(){
			clickGood(mData);
		}
		
		@Override
		Object[] getDftData(){
			return new Good[6];
		}
	}
	
	@Keep
	class H6 extends H
	{
		public H6(){
			super(R.layout.item_f1_h6);
			RecyclerView rv=getView(R.id.f1_h6_list);
			rv.setAdapter(mAdapter=new GoodsAdapter(R.layout.item_good_f));
			vRefresh.setEnableLoadMore(true);
		}
		
		@Override
		Object[] getDftData(){
			return new Good[4];
		}
	}
	
}
