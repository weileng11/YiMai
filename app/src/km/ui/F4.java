package km.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.v4.widget.Space;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import java.util.Arrays;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import kameng.yimai.R;
import km.api.*;
import km.base.HomeFragment;
import km.model.CardDetail;
import km.model.M;
import km.quick.QuickAdapter;
import km.quick.QuickClickAdapter;
import km.quick.QuickHolder;
import km.util.LoginUtil;
import km.util.pop.BusCodePop;
import km.view.KmRecyclerView;
import km.view.KmRefreshLayout;
import km.view.KmRefreshListener;

/**
 * @author: ${bruce}
 * @project: YiMai
 * @package: km.ui
 * @description:11122
 * @date: 2018/11/6 0006
 * @time: 上午 11:42
 */
public class F4 extends HomeFragment
{
	@BindView(R.id.f4_refresh)
	KmRefreshLayout vRefresh;
	@BindView(R.id.f4_list)
	RecyclerView vList;
	@BindView(R.id.f4_nodata)
	LinearLayout vNodata;
	@BindView(R.id.f4_create)
	CardView vCreate;
	final SparseArray<List> mDatas=new SparseArray<>();
	String busPic;
	CardDetail mCardDetail;
	
	@Override
	public int getLayout(){
		return R.layout.f4;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle bundle){
		super.onViewCreated(view, bundle);
		vList.setAdapter(new Adapter());
		vRefresh.setOnMultiPurposeListener(new KmRefreshListener()
		{
			@Override
			public void onRefresh(RefreshLayout refreshLayout){
				load();
			}
		});
		//立刻创建
		vCreate.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v){
				getActivity().startActivity(ActWeb.intent(Server.BASE_WEB+"CreateBusiness"));
			}
		});
	}
	
	@Override
	public void onInitLate(){
		super.onInitLate();
		getProxy().showLoading(null);
		load();
	}
	
	void load(){
		String userId=String.valueOf(LoginUtil.getUser().userId);
		Servers.start(this, Server.get().getCardDetail(userId, "collection"),
				new RxListener2<CardDetail>()
				{
					@Override
					public void onNext(M<CardDetail> m, String msg){
						update();
						if(m==null) return;
						if(1==m.status){
							if(null!=m.result){
								vList.setVisibility(View.VISIBLE);
								vNodata.setVisibility(View.GONE);
								mCardDetail=m.result;
							}
						}else{
							vList.setVisibility(View.GONE);
							vNodata.setVisibility(View.VISIBLE);
							toast(m.msg);
						}
					}
				});
	}
	
	void update(){
		getProxy().dismissLoading();
		vRefresh.finishRefresh();
		vRefresh.finishLoadMore();
		vList.getAdapter().notifyDataSetChanged();
	}
	
	class Adapter extends QuickAdapter<Integer>
	{
		public Adapter(){
			super(0, Arrays.asList(1, 2, 3, 4));
		}
		
		@Override
		public int getItemViewType(int p){
			Integer item=getItem(p);
			return item==null? EMPTY_VIEW: item;
		}
		
		@Override
		public QuickHolder onCreateViewHolder(ViewGroup vg, int t){
			try{
				return (H)Class.forName(H.class.getName()+t)
						.getConstructor(F4.class)
						.newInstance(F4.this);
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
	
	class PersonF4Adapter<Item extends CardDetail.Word> extends QuickClickAdapter<Item>
	{
		public PersonF4Adapter(int layout){
			super(layout);
		}
		
		public PersonF4Adapter(int layout, List<Item> items){
			super(layout, items);
		}
		
		@Override
		protected void convert(QuickHolder h, Item item){
			if(item==null) return;
			//h.setImage(R.id.iv_f5_head, busPic, R.mipmap.contacts_logo);
			h.setText(R.id.txv_jl_content, item.companyName);
			h.setText(R.id.txv_jl_time, item.beginTime+"/"+item.endTime+","+item.job);
		}
	}
	
	//点赞
	private void busAddPrise(String catId){
		Servers.start(this, Server.get().doCardPraise(catId), new RxListener<M>()
		{
			@Override
			public void onNext(M m, String msg){
				if(m!=null) if(1==m.status){
					toast(m.msg);
					load();
				}else toast(m.msg);
				else toast(!TextUtils.isEmpty(msg)? msg: "获取失败");
			}
		});
	}
	
	//收藏
	private void busCollent(String catId){
		Servers.start(this, Server.get().doCardCollect(catId), new RxListener<M>()
		{
			@Override
			public void onNext(M m, String msg){
				if(m!=null) if(1==m.status){
					toast(m.msg);
					load();
				}else toast(m.msg);
				else toast(!TextUtils.isEmpty(msg)? msg: "获取失败");
			}
		});
	}
	
	class BusMpAdapter<Item extends CardDetail.GoodsInfo> extends QuickClickAdapter<Item>
	{
		public BusMpAdapter(int layout){
			super(layout);
		}
		
		public BusMpAdapter(int layout, List<Item> items){
			super(layout, items);
		}
		
		@Override
		protected void convert(QuickHolder h, Item item){
			if(item==null) return;
			h.setImage(R.id.item_good_image, item.image, R.mipmap.dft_avatar);
			h.setText(R.id.item_good_name, item.name);
			h.setText(R.id.item_good_price,"¥"+item.priceNow);
		}
	}
	
	@Keep
	class H<T> extends QuickHolder
	{
		QuickAdapter<T> mAdapter;
		
		public H(int layout){
			super(inflate(vList, layout));
		}
		
		void update(int type){
			if(mAdapter!=null) mAdapter.setNewData(getData(type));
		}
		
		List<T> getData(int type){
			List list=mDatas.get(type);
			if(list==null || list.isEmpty()){
				T[] data=getDftData();
				if(data!=null && data.length>0) list=Arrays.asList(data);
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
		CardDetail.CardInfo cardInfo;
		
		public H1(){
			super(R.layout.item_f4_h1);
			ButterKnife.bind(this, itemView);
		}
		
		@Override
		void update(int type){
			if(null!=mCardDetail){
				cardInfo=mCardDetail.cardInfo;
				if(null!=cardInfo){
					busPic=cardInfo.headPortrait;
					setText(R.id.f4_card_name, cardInfo.realName);
					setText(R.id.f4_card_job, cardInfo.job);
					setText(R.id.f4_card_mobile, cardInfo.mobile);
					setText(R.id.f4_card_weixin, cardInfo.weixin);
					setText(R.id.f4_card_company, cardInfo.companyName);
					setImage(R.id.f4_card_head, cardInfo.headPortrait, R.mipmap.dft_avatar);
					setImage(R.id.f4_card_logo, cardInfo.logo, R.mipmap.f4_card_logo);
					setText(R.id.f4_card_hot, "人气:"+cardInfo.hot);
					setText(R.id.f4_card_likes, "被点赞:"+cardInfo.dianzan);
					setText(R.id.f4_card_follows, "被收藏:"+cardInfo.collection);
				}
			}
		}
		
		@OnClick({
				         R.id.ll_mp_jia, R.id.ll_code, R.id.ll_phone, R.id.ll_dh, R.id.btn_mp_video,
				         R.id.txv_mp_bj, R.id.f4_card_likes, R.id.f4_card_follows
		         })
		public void onViewClicked(View view){
			switch(view.getId()){
			case R.id.ll_mp_jia://名片夹
				//toast("jia");
				getActivity().startActivity(ActWeb.intent(Server.BASE_WEB+"cardClip"));
				break;
			case R.id.ll_code: //二维码
				//toast("jia1");
				//getActivity().startActivity(ActWeb.intent(Server.BASE_WEB+"cardClip"));
				new BusCodePop(getActivity(), cardInfo);
				break;
			case R.id.ll_phone: //电话
				//toast("jia3");
				startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+cardInfo.mobile)));
				break;
			case R.id.ll_dh: //导航
				//toast("jia5");
				getActivity().startActivity(
						ActWeb.intent("http://map.baidu.com/?l=&s=s%26wd%3D3213123"));
				break;
			case R.id.btn_mp_video://创建我的录音
				//toast("jia11111111");
				getActivity().startActivity(
						ActWeb.intent(Server.BASE_WEB+"CreateBusiness?id="+cardInfo.id));
				break;
			case R.id.txv_mp_bj://编辑
				//toast("jiaxx");
				getActivity().startActivity(
						ActWeb.intent(Server.BASE_WEB+"CreateBusiness?id="+cardInfo.id));
				break;
			case R.id.f4_card_likes://点赞
				//toast("jiaxx");
				busAddPrise(String.valueOf(cardInfo.id));
				break;
			case R.id.f4_card_follows://收藏
				//toast("jiaxx");
				busCollent(String.valueOf(cardInfo.id));
				break;
			}
		}
	}
	
	@Keep
	class H2 extends H
	{
		@BindView(R.id.rv_mp_jl)
		KmRecyclerView rvMpJl;
		@BindView(R.id.btn_add_jl)
		Button btnAddJl;
		
		public H2(){
			super(R.layout.item_f4_h2);
			ButterKnife.bind(this, itemView);
			rvMpJl.setAdapter(mAdapter=new PersonF4Adapter(R.layout.item_f4_work));
		}
		
		@Override
		void update(int type){
			super.update(type);
			if(null!=mCardDetail){
				if(null!=mCardDetail.word){
					if(mCardDetail.word.size()>0){
						mAdapter.setNewData(mCardDetail.word);
					}
				}
			}
		}
		
		@OnClick(R.id.btn_add_jl)
		public void onViewClicked(){
			//toast("点击经历");
			getActivity().startActivity(ActWeb.intent(Server.BASE_WEB+"CreateWork"));
		}
	}
	
	@Keep
	class H3 extends H
	{
		@BindView(R.id.txv_domain)
		TextView txvDomain;
		
		public H3(){
			super(R.layout.item_f4_h3);
			ButterKnife.bind(this, itemView);
		}
		
		@Override
		void update(int type){
			super.update(type);
			if(null!=mCardDetail){
				CardDetail.CardInfo cardInfo=mCardDetail.cardInfo;
				if(null!=cardInfo){
					//个性签名
					txvDomain.setText(cardInfo.autograph);
				}
			}
		}
		
		@Override
		Object[] getDftData(){
			return null;
		}
	}
	
	@Keep
	class H4 extends H
	{
		
		public H4(){
			super(R.layout.item_f4_h4);
			ButterKnife.bind(this, itemView);
			RecyclerView rv=getView(R.id.f4_h4_shop_new);
			rv.setAdapter(mAdapter=new BusMpAdapter(R.layout.item_bus_shop_type));
		}
		
		@Override
		void update(int type){
			super.update(type);
			if(null!=mCardDetail){
				if(mCardDetail.goodsInfo!=null){
					if(mCardDetail.goodsInfo.size()>0) mAdapter.setNewData(mCardDetail.goodsInfo);
				}
			}
		}
		
		@OnClick({R.id.f4_shop_new, R.id.f4_shop_all})
		public void click(View v){
			boolean forNew=v.getId()==R.id.f4_shop_new;
			setGone(R.id.f4_h4_shop_new, forNew);
			setGone(R.id.h4_shop_all, !forNew);
			setChecked(R.id.f4_shop_new, forNew);
			setChecked(R.id.f4_shop_all, !forNew);
		}
	}
}