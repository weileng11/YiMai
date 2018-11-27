package km.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.Space;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
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
import km.model.*;
import km.other.BaseGoodAdapter;
import km.quick.QuickAdapter;
import km.quick.QuickClickAdapter;
import km.quick.QuickHolder;
import km.view.KmRefreshLayout;
import km.view.KmRefreshListener;
import km.view.PriceView;
import vip.VipActivity;

/** Created by wuzhengu on 2018/10/29 0029 */
public class F5 extends HomeFragment
{
	@BindView(R.id.f5_refresh)
	KmRefreshLayout vRefresh;
	@BindView(R.id.f5_list)
	RecyclerView vList;
	final SparseArray<List> mDatas=new SparseArray<>();
	User mUser;
	UserCenter mCenter;
	CityMaster mMaster;
	boolean mInviteShown;
	
	public int getLayout(){
		return R.layout.f5;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle bundle){
		super.onViewCreated(view, bundle);
		vRefresh.setEnableRefresh(true);
		vList.setAdapter(new Adapter());
		vRefresh.setOnMultiPurposeListener(new KmRefreshListener()
		{
			@Override
			public void onRefresh(RefreshLayout refreshLayout){
				load();
			}
		});
	}
	
	@Override
	public void onInitLate(){
		super.onInitLate();
		getProxy().showLoading(null);
		load();
	}
	
	@OnClick(R.id.f5_float)
	void click_float(){
		//startActivity(ActWeb.intent("http://wpa.qq.com/msgrd?v=3&uin=2060569668&site=qq"));
		ActWeb.startQQ(getContext(), "2060569668");
	}
	
	void load(){
		Servers.start(this, Server.get().getUserInfo(), new RxListener2<User.Result>()
		{
			@Override
			public void onNext(M<User.Result> m, String msg){
				if(m!=null){
					mUser=m.result==null? null: m.result.user;
					if(mUser!=null){
						if(mUser.parentId==0 && mUser.level2==0) showInvite();
					}else{
						if(!TextUtils.isEmpty(m.msg)) msg=m.msg;
					}
				}
				if(mUser==null && !TextUtils.isEmpty(msg)) toast(msg);
				loadUserCenter();
			}
		});
	}
	
	void loadUserCenter(){
		Servers.start(this, Server.get().getUserCenter(), new RxListener2<UserCenter>()
		{
			@Override
			public void onNext(M<UserCenter> m, String msg){
				if(m!=null) mCenter=m.result;
				loadCityMaster();
			}
		});
	}
	
	void loadCityMaster(){
		Servers.start(this, Server.get().getCityMaster(), new RxListener2<CityMaster>()
		{
			@Override
			public void onNext(M<CityMaster> m, String msg){
				vRefresh.finishRefresh();
				getProxy().dismissLoading();
				if(m!=null){
					mMaster=m.result;
					if(mMaster!=null) mMaster.ok=m.status==1;
				}
				vList.getAdapter().notifyDataSetChanged();
			}
		});
	}
	
	void showInvite(){
		if(mInviteShown) return;
		mInviteShown=true;
		new InviteDialog(getContext()).show();
	}
	
	class InviteDialog extends Dialog implements View.OnClickListener
	{
		View vStart;
		
		public InviteDialog(@NonNull Context context){
			super(context);
			setCancelable(false);
		}
		
		@Override
		protected void onCreate(Bundle bundle){
			super.onCreate(bundle);
			getWindow().setBackgroundDrawable(new ColorDrawable(0));
			setContentView(R.layout.dialog_invite);
			(vStart=findViewById(R.id.invite_start)).setOnClickListener(this);
			findViewById(R.id.invite_close).setOnClickListener(this);
		}
		
		@Override
		public void onClick(View v){
			if(v==vStart){
				startInvite();
				return;
			}
			dismiss();
			mInviteShown=false;
		}
		
		void startInvite(){
			TextView tv=(TextView)findViewById(R.id.invite_input);
			String code=tv.getText().toString().trim();
			if(code.isEmpty()) return;
			vStart.setEnabled(false);
			Servers.start(F5.this, Server.get().doInvite(code), new RxListener<M>()
			{
				@Override
				public void onNext(M m, String msg){
					vStart.setEnabled(true);
					boolean ok=false;
					if(m!=null){
						ok=m.status==1;
						if(!ok && !TextUtils.isEmpty(m.msg)) msg=m.msg;
					}
					if(ok){
						toast("邀请码设置成功");
						dismiss();
					}else{
						toast(!TextUtils.isEmpty(msg)? msg: "邀请码设置失败");
					}
				}
			});
		}
	}
	
	class Adapter extends QuickAdapter<Integer>
	{
		public Adapter(){
			super(0, Arrays.asList(1, 2, 2, 3, 4));
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
						.getConstructor(F5.class)
						.newInstance(F5.this);
			}catch(Exception e){
				e.printStackTrace();
			}
			return new QuickHolder(new Space(getContext()));
		}
		
		@Override
		public void onBindViewHolder(QuickHolder h, int p){
			if(h instanceof H2) ((H2)h).isMy=(p==2);
			super.onBindViewHolder(h, p);
		}
		
		@Override
		protected void convert(QuickHolder h, Integer item){
			if(h instanceof H) ((H)h).update(item);
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
	
	static SparseArray<String> LINKS;
	
	@Keep
	class H1 extends H implements View.OnClickListener
	{
		
		public H1(){
			super(R.layout.item_f5_h1);
			if(LINKS==null) LINKS=new SparseArray<String>()
			{{
				String url;
				put(R.id.f5_user_sign, url=Server.BASE_WEB+"user/signIn");
				put(R.id.f5_user_setting, url=Server.BASE_WEB+"user/setting");
				put(R.id.f5_user_head, url);
				put(R.id.f5_user_profit_total, url=Server.BASE_WEB+"user/accountManagement");
				put(R.id.f5_user_profit_invite, url);
				put(R.id.f5_user_profit_today, url);
				put(R.id.f5_user_master, url="");
			}};
			for(int i=0; i<LINKS.size(); i++) setOnClickListener(LINKS.keyAt(i), this);
		}
		
		@Override
		public void onClick(View v){
			String link=LINKS.get(v.getId());
			if(link==null) return;
			if(link.isEmpty()){
				toast("即将开放");
				return;
			}
			startActivity(ActWeb.intent(link));
		}
		
		@Override
		void update(int type){
			UserCenter.Profit profit=mCenter==null? null: mCenter.profit;
			if(profit==null){
				setText(R.id.f5_user_profit_total_num, "0");
				setText(R.id.f5_user_profit_invite_num, "0");
				setText(R.id.f5_user_profit_today_num, "0");
			}else{
				setText(R.id.f5_user_profit_total_num, PriceView.format(2, false, profit.total));
				setText(R.id.f5_user_profit_invite_num, PriceView.format(2, false, profit.invite));
				setText(R.id.f5_user_profit_today_num, PriceView.format(2, false, profit.today));
			}
			setText(R.id.f5_user_name, mUser==null? "暂无名称": mUser.nickname);
			setText(R.id.f5_user_invite, "会员码:"+(mUser==null? "暂无": mUser.inviteCode));
			setImage(R.id.f5_user_head, mUser==null? null: mUser.headPic, R.mipmap.dft_avatar);
			int levelRes=0;
			if(mUser!=null) switch(mUser.level2){
			case 0:
				levelRes=R.mipmap.level_00;
				break;
			case 20:
				levelRes=R.mipmap.level_01;
				break;
			case 30:
				switch(mUser.level){
				case 1:
				case 20:
					levelRes=R.mipmap.level_03;
					break;
				case 30:
					levelRes=R.mipmap.level_04;
					break;
				case 40:
					levelRes=R.mipmap.level_05;
					break;
				case 50:
					levelRes=R.mipmap.level_06;
					break;
				}
			}
			setImageResource(R.id.f5_user_level, levelRes);
			String masterName="前往开通城主";
			String masterIcon="";
			boolean masterOk=mMaster!=null && mMaster.ok;
			if(masterOk){
				masterName=mMaster.regionName;
				masterIcon=mMaster.headPic;
			}
			setText(R.id.f5_user_master_name, masterName);
			setImage(R.id.f5_user_master_icon, masterIcon, R.mipmap.f5_master);
			String link=Server.BASE_WEB+(masterOk? "user/cityMasterInfo": "user/cityMaster");
			LINKS.put(R.id.f5_user_master, link);
		}
	}
	
	@Keep
	class H2 extends H
	{
		boolean isMy;
		
		public H2(){
			super(R.layout.item_f5_h2);
			RecyclerView rv=getView(R.id.f5_h2_list);
			rv.setAdapter(mAdapter=new BaseGoodAdapter<BaseGood>(R.layout.item_tool)
			{@Override
			public void onClick(View v, int p){
				BaseGood item=getItem(p);
				if(item==null) return;
				if(item.imageRes==R.mipmap.tool_a_05){
					if(mUser!=null) if(mUser.level2>0 || "13480375487".equals(mUser.mobile)){
						getProxy().startActivity(VipActivity.class);
						return;
					}
					toast("请先开通会员");
				}
				String link=item.getLink();
				if(!TextUtils.isEmpty(link)) startActivity(ActWeb.intent(link));
				else toast("敬请期待");
			}
			});
		}
		
		@Override
		void update(int type){
			super.update(type);
			setText(R.id.f5_tools_title, isMy? "我的工具": "必备工具");
		}
		
		@Override
		Object[] getDftData(){
			BaseGood[] items;
			if(!isMy) items=new BaseGood[]{
					new BaseGood(){{ imageRes=R.mipmap.tool_a_01; name="积分/会员卡"; link="user/bonusCoupon/CashCoupon"; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_a_02; name="语音课堂"; link=null; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_a_03; name="排行榜"; link=null; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_a_04; name="任务广场"; link="user/taskAll"; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_a_05; name="VIP视频"; link="user/bonusCoupon/CashCoupon"; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_a_06; name="商家管理"; link=null; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_a_07; name="拼团管理"; link=null; }},
			};
			else items=new BaseGood[]{
					new BaseGood(){{ imageRes=R.mipmap.tool_b_01; name="我的订单"; link="MyPurchase"; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_b_02; name="团队管理"; link="user/team"; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_b_03; name="我的发布"; link="user/myRelease"; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_b_04; name="购物积分"; link="user/currency"; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_b_05; name="我的拼团"; link=null; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_b_06; name="地址管理"; link="user/addressManagement"; }},
					new BaseGood(){{ imageRes=R.mipmap.tool_b_07; name="推广海报"; link="user/poster"; }},
			};
			for(BaseGood item: items) if(item.link!=null) item.link=Server.BASE_WEB+item.link;
			return items;
		}
	}
	
	@Keep
	class H3 extends H
	{
		public H3(){
			super(R.layout.item_f5_h3);
			ButterKnife.bind(this, itemView);
			RecyclerView rv=getView(R.id.f5_h3_list);
			rv.setAdapter(mAdapter=new QuickClickAdapter<BaseGood>(R.layout.item_count){
				@Override
				protected void convert(QuickHolder h, BaseGood item){
					h.setText(R.id.item_good_price, String.valueOf(item.id));
					h.setText(R.id.item_good_name, String.valueOf(item.name));
				}
				@Override
				public void onClick(View v, int p){
					BaseGood item=getItem(p);
					if(item==null) return;
					String link=item.getLink();
					if(!TextUtils.isEmpty(link)) startActivity(ActWeb.intent(link));
				}
			});
		}
		
		@Override
		List getData(int type){
			List<BaseGood> list=super.getData(type);
			UserCenter.Card card=mCenter==null? null: mCenter.card;
			if(card!=null){
				list.get(0).id=card.num1;
				list.get(1).id=card.num2;
				list.get(2).id=card.num3;
				list.get(3).id=card.num4;
			}else for(BaseGood item : list) item.id=0;
			return list;
		}
		
		@Override
		Object[] getDftData(){
			BaseGood[] items={
					new BaseGood(){{ id=0; name="名片夹"; link="cardClip"; }},
					new BaseGood(){{ id=0; name="谁看过我"; link="user/vistorList?type=2"; }},
					new BaseGood(){{ id=0; name="我看过谁"; link="user/vistorList?type=1"; }},
					new BaseGood(){{ id=0; name="谁赞过我 "; link="user/vistorList?type=3"; }},
			};
			for(BaseGood good: items) if(good.link!=null) good.link=Server.BASE_WEB+good.link;
			return items;
		}
		
		@OnClick(R.id.f5_card_update)
		public void click_update(){
			startActivity(ActWeb.intent(Server.BASE_WEB+"MyBusinessCard/"+mUser.userId));
		}
	}
	
	@Keep
	class H4 extends H
	{
		public H4(){
			super(R.layout.item_f5_shop);
			ButterKnife.bind(this, itemView);
			RecyclerView rv=getView(R.id.f5_shop_list);
			rv.setAdapter(mAdapter=new QuickAdapter<Good>(R.layout.item_shop_type){
				@Override
				protected void convert(QuickHolder h, Good item){
					h.setImage(R.id.item_good_image, item.image, R.mipmap.dft_avatar);
					h.setText(R.id.item_good_name, "¥"+item.priceNow);
				}
			});
		}
		
		@Override
		void update(int type){
			super.update(type);
			boolean hasData=mCenter!=null && mCenter.goods.data.size()>0;
			setGone(R.id.f5_shop_no_data, !hasData);
			setGone(R.id.f5_shop_list, hasData);
		}
		
		@OnClick(R.id.f5_shop_more)
		void click_more(){
			toast("即将开放");
		}
		
		@OnClick(R.id.f5_shop_create)
		void click_create(){
			startActivity(ActWeb.intent(Server.BASE_WEB+"ShelfGoods"));
		}
	}
}