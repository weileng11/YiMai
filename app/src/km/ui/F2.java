package km.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import java.util.*;
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
import km.view.KmBanner;
import km.view.KmCityPicker.Level;
import km.view.KmRefreshLayout;
import km.view.KmRefreshListener;
import rx.Observable;
import rx.Subscriber;
import wzg.util.Locators;

/**
 同城服务
 <br/>
 Created by wuzhengu on 2018/10/29 0029
 */
public class F2 extends HomeFragment
{
	static final int TYPE_NEW=1;
	static final int TYPE_HOT=2;
	static final int TYPE_NEARBY=3;
	static final int TYPE_FOLLOW=4;
	
	static final int[] HEADERS={ 1, 2, 3, 4, 5 };
	static final int TYPE_NEWS=6;
	@BindView(R.id.f2_refresh)
	KmRefreshLayout vRefresh;
	@BindView(R.id.f2_list)
	RecyclerView vList;
	int mRequestRun;
	CityService mCityService;
	int mNewsPage=0;
	int mNewsType=TYPE_NEW;
	Request mNewsRequest;
	List<News> mNewsList;
	List mTags;
	String mCity;
	
	@Override
	public HomeFragment setGps(Locators.Locator locator){
		super.setGps(locator);
		mCity=Locators.trimCity(locator.getCity());
		RecyclerView.Adapter adapter=vList==null? null: vList.getAdapter();
		if(adapter!=null) adapter.notifyItemChanged(0);
		return this;
	}
	
	@Override
	public int getLayout(){
		return R.layout.f2;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle bundle){
		super.onViewCreated(view, bundle);
		vList.setAdapter(new Adapter().update());
		vRefresh.setOnMultiPurposeListener(new KmRefreshListener()
		{
			@Override
			public void onRefresh(RefreshLayout layout){
				load();
				loadNews(mNewsPage=0);
			}
			@Override
			public void onLoadMore(RefreshLayout layout){
				super.onLoadMore(layout);
				loadNews(mNewsPage+1);
			}
		});
	}
	
	@Override
	public void onInitLate(){
		super.onInitLate();
		getProxy().showLoading(null);
		load();
		loadNews(mNewsPage=0);
	}
	
	@OnClick(R.id.f2_float)
	void click_float(){
		startActivity(ActWeb.intent(Server.BASE_WEB+"InformationDelivery"));
	}
	
	void load(){
		new Request<CityService>(Server.get().getCityService())
		{
			@Override
			public void onNext(M<CityService> m){
				if(m!=null) mCityService=m.result;
			}
		}.start();
	}
	
	void loadNews(final int page){
		String type=String.valueOf(mNewsType);
		String lon=null;
		String lat=null;
		if(mNewsType==TYPE_NEARBY){
			lon=String.valueOf(mLongitude);
			lat=String.valueOf(mLatitude);
		}
		new Request<News.Page>(Server.get().getNews(page, type, lon, lat, null))
		{
			{ mNewsRequest=this; }
			@Override
			public void onNext(M<News.Page> m){
				if(mNewsRequest!=this) return;
				if(m!=null){
					if(page<=1) mNewsList=null;
					if(m.result!=null){
						mNewsPage=m.result.currentPage;
						List<News> list=m.result.data;
						if(list!=null){
							if(mNewsList==null) mNewsList=list;
							else mNewsList.addAll(list);
						}
					}else{
						if(!TextUtils.isEmpty(m.msg)) toast(m.msg);
					}
				}
			}
		}.start();
	}
	
	class Request<T> extends Subscriber<M<T>>
	{
		Observable<M<T>> observable;
		
		public Request(Observable<M<T>> observable){
			this.observable=observable;
		}
		
		public void start(){
			mRequestRun++;
			Servers.start(F2.this, observable, this);
			return;
		}
		
		@Override
		public void onCompleted(){
			mRequestRun--;
			if(mRequestRun<=0){
				getProxy().dismissLoading();
				vRefresh.finishRefresh();
				((Adapter)vList.getAdapter()).update();
			}
		}
		
		@Override
		public void onError(Throwable ex){
			onCompleted();
		}
		
		@Override
		public void onNext(M<T> m){
			
		}
	}
	
	class Adapter extends QuickAdapter<Object>
	{
		
		public Adapter(){
			super(0);
		}
		
		Adapter update(){
			List<Object> list=getData();
			list.clear();
			for(int i: HEADERS) list.add(i);
			if(mNewsList!=null) list.addAll(mNewsList);
			setNewData(list);
			return this;
		}
		
		@Override
		public int getItemViewType(int p){
			Object item=getItem(p);
			return item instanceof Integer? ((Integer)item): TYPE_NEWS;
		}
		
		@Override
		public QuickHolder onCreateViewHolder(ViewGroup vg, int t){
			try{
				return (H)Class.forName(H.class.getName()+t).getConstructor(F2.class).newInstance(F2.this);
			}catch(Exception e){
				e.printStackTrace();
			}
			return new QuickHolder(new Space(getContext()));
		}
		
		@Override
		protected void convert(QuickHolder h, Object item){
			((H)h).update(item);
		}
	}
	
	@Keep
	class H extends QuickHolder
	{
		
		public H(int layout){
			super(inflate(vList, layout));
		}
		
		void update(Object item){}
	}
	
	@Keep
	class H1 extends H
	{
		@BindView(R.id.f2_h1_search)
		TextView vSearch;
		@BindView(R.id.f2_h1_banner)
		KmBanner vBanner;
		List mData;
		QuickAdapter mAdapter;
		
		public H1(){
			super(R.layout.item_f2_h1);
			ButterKnife.bind(this, itemView);
			setVisible(R.id.f2_h1_news, false);
			setAdapter(R.id.f2_h1_tags, mAdapter=new QuickClickAdapter<Object>(R.layout.item_tag)
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
					vSearch.setText(name);
					startActivity(ActWeb.intent(Server.BASE_WEB+"navDetail?word="+name));
				}
			});
			new BannerHelper(vBanner)
			{
				@Override
				public List getData(){
					return mData;
				}
			};
		}
		
		@Override
		void update(Object item){
			if(mCity!=null) setText(R.id.f2_h1_city, mCity);
			List tips=null;
			mData=null;
			if(mCityService!=null){
				tips=mCityService.tips;
				mData=mCityService.banners;
			}
			if(mData==null) mData=Collections.emptyList();
			vBanner.setImages(mData).start();
			mAdapter.setNewData(tips);
		}
		
		@OnClick(R.id.f2_h1_city)
		void click_city(){
			getProxy().startActivity(new ActivityProxy.Starter()
			{
				@Override
				public Intent getIntent(){
					return ActCityPicker.intent(Level.CITY);
				}
				@Override
				protected void onResult(boolean ok, Intent data){
					ActCityPicker.Address address=ActCityPicker.parseResult(data);
					if(address!=null){
						String city=Locators.trimCity(address.cityName);
						if(city==null) city=Locators.trimProvince(address.provinceName);
						setText(R.id.f2_h1_city, city);
					}
				}
			});
		}
		
		@OnClick(R.id.f2_h1_search)
		void click_search(){
			startActivity(ActWeb.intent(Server.BASE_WEB+"sameCity"));
		}
		
		@OnClick(R.id.f2_h1_news)
		void click_news(){
			startActivity(ActWeb.intent(Server.BASE_WEB+"orderMessage"));
		}
	}
	
	@Keep
	class H2 extends H
	{
		QuickAdapter<BaseGood> mAdapter;
		
		public H2(){
			super(R.layout.item_f2_h2);
			RecyclerView rv=getView(R.id.f2_h2_list);
			rv.setAdapter(mAdapter=new GoodsAdapter<>(R.layout.item_goods_type));
		}
		
		@Override
		void update(Object item){
			List<BaseGood> list=null;
			CityService service=mCityService;
			if(service!=null && service.types!=null && service.types.size()>0){
				list=new ArrayList<BaseGood>(service.types.values());
			}
			if(list==null || list.isEmpty()) list=Arrays.asList(getDftData());
			mAdapter.setNewData(list);
		}
		
		BaseGood[] getDftData(){
			return new GoodsType[]{
					new GoodsType(){{ name="项目推广"; imageRes=R.mipmap.city_service_01; }},
					new GoodsType(){{ name="家政服务"; imageRes=R.mipmap.city_service_02; }},
					new GoodsType(){{ name="开锁换锁"; imageRes=R.mipmap.city_service_03; }},
					new GoodsType(){{ name="房屋出售"; imageRes=R.mipmap.city_service_04; }},
					new GoodsType(){{ name="家电维修"; imageRes=R.mipmap.city_service_05; }},
					new GoodsType(){{ name="优惠券";   imageRes=R.mipmap.city_service_06; }},
					new GoodsType(){{ name="生意转让"; imageRes=R.mipmap.city_service_07; }},
					new GoodsType(){{ name="房屋出租"; imageRes=R.mipmap.city_service_08; }},
					new GoodsType(){{ name="活动促销"; imageRes=R.mipmap.city_service_09; }},
					new GoodsType(){{ name="更多分类"; imageRes=R.mipmap.city_service_10; }},
			};
		}
	}
	
	@Keep
	class H3 extends H
	{
		List mData;
		KmBanner vBanner;
		
		public H3(){
			super(R.layout.item_f2_h3);
			vBanner=getView(R.id.f2_h3_banner);
			new BannerHelper(vBanner)
			{
				@Override
				public List getData(){
					return mData;
				}
			};
		}
		
		@Override
		void update(Object item){
			mData=mCityService==null? null: mCityService.banners2;
			if(mData==null) mData=Collections.emptyList();
			vBanner.setImages(mData).start();
		}
	}
	
	@Keep
	class H4 extends H implements View.OnClickListener, Runnable
	{
		TextView vNews;
		List mData;
		int mIndex;
		
		public H4(){
			super(R.layout.item_f2_h4);
			vNews=getView(R.id.f2_h4_news);
			vNews.setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v){
			String msg=vNews.getText().toString();
			if(msg.isEmpty()) msg=vNews.getHint().toString();
			if(!msg.isEmpty()) toast(msg);
			Object obj=mData!=null && mIndex>=0 && mIndex<mData.size()? mData.get(mIndex): null;
			if(obj instanceof CityService.News){
				String link=((CityService.News)obj).link;
				if(!TextUtils.isEmpty(link)) startActivity(ActWeb.intent(link));
			}
		}
		
		@Override
		public void run(){
			vNews.removeCallbacks(this);
			vNews.postDelayed(this, 3000);
			mIndex++;
			if(mIndex>=mData.size()) mIndex=0;
			Object msg=mData.get(mIndex);
			if(msg instanceof CityService.News) msg=((CityService.News)msg).content;
			vNews.setText(String.valueOf(msg));
		}
		
		@Override
		void update(Object item){
			mData=mCityService==null? null: mCityService.news;
			if(mData==null || mData.isEmpty()) mData=Arrays.asList(getDftData());
			mIndex=-1;
			vNews.post(this);
		}
		
		Object[] getDftData(){
			return new Object[]{
					"1",
					"2",
					"3",
			};
		}
	}
	
	@Keep
	class H5 extends H implements TabLayout.OnTabSelectedListener
	{
		public H5(){
			super(R.layout.item_f2_h5);
			TabLayout tab=getView(R.id.f2_h5_tabs);
			tab.addOnTabSelectedListener(this);
		}
		
		@Override
		public void onTabSelected(TabLayout.Tab tab){
			switch(tab.getPosition()){
			case 0:
				mNewsType=TYPE_NEW;
				break;
			case 1:
				mNewsType=TYPE_HOT;
				break;
			case 2:
				mNewsType=TYPE_NEARBY;
				break;
			case 3:
				mNewsType=TYPE_FOLLOW;
				break;
			}
			getProxy().showLoading(String.format("加载“%s”...", tab.getText()));
			loadNews(mNewsPage=0);
		}
		
		@Override
		public void onTabUnselected(TabLayout.Tab tab){
			
		}
		
		@Override
		public void onTabReselected(TabLayout.Tab tab){
		}
	}
	
	@Keep
	class H6 extends H implements View.OnClickListener
	{
		News mData;
		
		public H6(){
			super(R.layout.item_f2_news);
			ButterKnife.bind(this, itemView);
			itemView.setOnClickListener(this);
			vRefresh.setEnableLoadMore(true);
		}
		
		@Override
		void update(Object item){
			mData=(News)item;
			setImage(R.id.item_news_head, mData.headPic, R.mipmap.dft_avatar);
			setText(R.id.item_news_name, mData.nickname);
			setGone(R.id.item_news_stick, mData.setUpSort>0);
			setText(R.id.item_news_content, mData.content);
			setText(R.id.item_news_date, mData.timeText);
			setText(R.id.item_news_address, mData.address);
			setText(R.id.item_news_likes, String.valueOf(mData.likes));
			setText(R.id.item_news_comments, String.valueOf(mData.comments));
			RecyclerView rv=getView(R.id.item_news_images);
			List list=mData.images;
			if(list!=null) for(int i=list.size()-9; i>0; i--) list.remove(list.size()-1);
			rv.setAdapter(new QuickClickAdapter<Object>(R.layout.item_image, list)
			{
				@Override
				protected void convert(QuickHolder h, Object item){
					h.setImage(R.id.item_image, String.valueOf(item), R.mipmap.dft_item);
					h.addOnClickListener(R.id.item_image);
				}
			});
		}
		
		@Override
		public void onClick(View view){
			String link=mData.getLink();
			if(!TextUtils.isEmpty(link)) startActivity(ActWeb.intent(link));
		}
		
		@OnClick(R.id.item_news_user)
		void click_news_more(){
			String link=Card.link(mData.userId);
			if(!TextUtils.isEmpty(link)) startActivity(ActWeb.intent(link));
		}
		
		@OnClick(R.id.item_news_contact)
		void click_news_contact(){
			String phone=mData.mobile;
			if(!TextUtils.isEmpty(phone)) try{
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phone)));
				return;
			}catch(Exception e){
				e.printStackTrace();
			}
			toast("暂无联系方式");
			
		}
	}
}
