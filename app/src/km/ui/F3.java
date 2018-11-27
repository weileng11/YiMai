package km.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import java.util.List;
import butterknife.BindView;
import kameng.yimai.R;
import km.api.RxListener;
import km.api.Server;
import km.api.Servers;
import km.base.HomeFragment;
import km.model.Card;
import km.model.M;
import km.other.SimpleTabListener;
import km.quick.QuickAdapter;
import km.quick.QuickClickAdapter;
import km.quick.QuickHolder;
import km.view.KmRefreshLayout;
import km.view.KmRefreshListener;
import rx.Observable;
import rx.Subscriber;

/**
 人脉广场
 <br/>
 Created by wuzhengu on 2018/10/29 0029
 */
public class F3 extends HomeFragment
{
	@BindView(R.id.f3_refresh)
	KmRefreshLayout vRefresh;
	@BindView(R.id.f3_list)
	RecyclerView vList;
	@BindView(R.id.f3_tabs)
	TabLayout vTabs;
	@BindView(R.id.f3_category)
	View vCate;
	int mRequestRun;
	boolean mNearby=false;
	String mCate;
	List<Card.Category> mCates;
	Request mCateRequest;
	F3CategoryDialog mCateDialog;
	Runnable mRunDismiss;
	int mTabPosition;
	int mPage;
	
	@Override
	public int getLayout(){
		return R.layout.f3;
	}
	
	@Override
	public void onInitLate(){
		super.onInitLate();
		vList.setAdapter(new Adapter());
		vCate.setVisibility(View.GONE);
		vTabs.getTabAt(mNearby? 1: 0).select();
		vTabs.addOnTabSelectedListener(new SimpleTabListener()
		{
			@Override
			public void onTabSelected(TabLayout.Tab tab){
				int p=tab.getPosition();
				if(p==2){
					onTabReselected(tab);
					return;
				}
				if(mCateDialog!=null) mCateDialog.cancel();
				if(mTabPosition==p) return;
				mTabPosition=p;
				mNearby=p==1;
				getProxy().showLoading(null);
				load(mPage=0);
			}
			@Override
			public void onTabReselected(TabLayout.Tab tab){
				int p=tab.getPosition();
				if(p!=2) return;
				if(mCateDialog!=null && mCateDialog.isShowing()){
					if(mRunDismiss==null) mRunDismiss=new Runnable()
					{
						@Override
						public void run(){
							mCateDialog.dismiss();
						}
					};
					vTabs.postDelayed(mRunDismiss, 200);
					return;
				}
				if(mCates!=null && mCates.size()>0) showCategory();
				else{
					getProxy().showLoading("加载分类...");
					loadCategory();
				}
			}
		});
		vRefresh.setEnableLoadMore(true);
		vRefresh.setOnMultiPurposeListener(new KmRefreshListener()
		{
			@Override
			public void onRefresh(RefreshLayout layout){
				load(mPage=0);
			}
			@Override
			public void onLoadMore(RefreshLayout layout){
				super.onLoadMore(layout);
				load(mPage+1);
			}
		});
		getProxy().showLoading(null);
		load(mPage=0);
	}
	
	void load(final int page){
		int hot=0;
		int nearby=0;
		String lon=null;
		String lat=null;
		if(mNearby){
			nearby=1;
			lon=String.valueOf(mLongitude);
			lat=String.valueOf(mLatitude);
		}else{
			hot=1;
		}
		new Request<Card.Page>(Server.get().getCards(page, hot, nearby, lon, lat, mCate))
		{
			@Override
			public void onNext(M<Card.Page> m){
				QuickAdapter<Card> adapter=(QuickAdapter)vList.getAdapter();
				List list=null;
				if(m!=null){
					if(m.result!=null){
						mPage=m.result.currentPage;
						list=m.result.data;
						if(page>1) list.addAll(0, adapter.getData());
					}else{
						if(!TextUtils.isEmpty(m.msg)) toast(m.msg);
					}
				}
				adapter.setNewData(list);
			}
		}.start();
	}
	
	void loadCategory(){
		if(mCateRequest!=null) return;
		new Request<List<Card.Category>>(Server.get().getCardCategorys())
		{
			{ mCateRequest=this; }
			@Override
			public void onCompleted(){
				super.onCompleted();
				if(mCateRequest==this) mCateRequest=null;
			}
			
			@Override
			public void onNext(M<List<Card.Category>> m){
				if(m==null) return;
				mCates=m.result==null? null: m.result;
				if(mCates==null || mCates.isEmpty()){
					String msg=m.msg;
					if(TextUtils.isEmpty(msg)) msg="行业分类暂无数据";
					new AlertDialog.Builder(getContext())
							.setMessage(msg)
							.setCancelable(false)
							.setPositiveButton("知道了", null)
							.show();
					return;
				}
				if(mCateRequest==this) showCategory();
			}
		}.start();
	}
	
	void showCategory(){
		final int size=mCates.size();
		String[] items=new String[size];
		final boolean[] checks=new boolean[size];
		for(int i=0; i<size; i++){
			Card.Category item=mCates.get(i);
			items[i]=item.name;
			checks[i]=item.checked;
		}
		
		vRefresh.setEnabled(false);
		if(mCateDialog!=null){
			mCateDialog.show();
			return;
		}
		mCateDialog=new F3CategoryDialog(getContext())
				.setView(vCate)
				.setTitle("提示：请选择行业分类(可多选)")
				.setMultiChoiceItems(items, checks, new DialogInterface.OnMultiChoiceClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int i, boolean checked){
						checks[i]=checked;
					}
				})
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int button){
						dialog.dismiss();
						StringBuilder sb=null;
						for(int i=0; i<size; i++){
							Card.Category item=mCates.get(i);
							item.checked=checks[i];
							if(!item.checked) continue;
							if(sb==null) sb=new StringBuilder();
							else if(sb.length()>0) sb.append(",");
							sb.append(item.id);
						}
						String result=sb==null? null: sb.toString();
						if(mCate==null? result==null: mCate.equals(result)) return;
						mCate=result;
						getProxy().showLoading(null);
						load(mPage=0);
					}
				})
				.setDismissListener(new DialogInterface.OnDismissListener()
				{
					@Override
					public void onDismiss(DialogInterface dialog){
						vTabs.getTabAt(mTabPosition).select();
						vRefresh.setEnabled(true);
					}
				})
				.show();
	}
	
	void applyFriend(Card item){
		final String uid=item.userId;
		if(TextUtils.isEmpty(uid)){
			toast("暂时无法添加");
			return;
		}
		String name=item.getNickname();
		if(TextUtils.isEmpty(name)) name=uid;
		new AlertDialog.Builder(getContext())
				.setMessage(String.format("确定申请添加“%s”为好友？", name))
				.setNegativeButton("取消", null)
				.setPositiveButton("确定", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int button){
						getProxy().showLoading("添加好友...");
						Servers.start(F3.this, Server.get().doFriendApply(uid, null),
								new RxListener<M>()
								{
									@Override
									public void onNext(M m, String msg){
										getProxy().dismissLoading();
										if(m!=null && !TextUtils.isEmpty(m.msg)) msg=m.msg;
										toast(msg);
									}
								});
					}
				})
				.show();
	}
	
	class Request<T> extends Subscriber<M<T>>
	{
		Observable<M<T>> observable;
		
		public Request(Observable<M<T>> observable){
			this.observable=observable;
		}
		
		public void start(){
			mRequestRun++;
			Servers.start(F3.this, observable, this);
		}
		
		@Override
		public void onCompleted(){
			mRequestRun--;
			if(mRequestRun<=0){
				getProxy().dismissLoading();
				vRefresh.finishRefresh();
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
	
	class Adapter extends QuickClickAdapter<Card>
	{
		SparseArray<View> mViews=new SparseArray<>();
		
		public Adapter(){
			super(R.layout.item_f3_card);
		}
		
		@Override
		protected void convert(QuickHolder h, Card item, int p){
			h.setImage(R.id.item_card_head, item.getHeadPic(), R.mipmap.dft_avatar);
			h.setText(R.id.item_card_name, item.getNickname());
			h.setText(R.id.item_card_category, item.cateName);
			h.setText(R.id.item_card_business, item.manage);
			h.setText(R.id.item_card_hits, String.valueOf(item.hits));
			h.setText(R.id.item_card_likes, String.valueOf(item.likes));
			h.setGone(R.id.item_card_stick, item.setUpSort>0);
			TextView tv=h.getView(R.id.item_card_hi);
			tv.setText(item.setUpDesc);
			tv.setVisibility(item.showDesc? View.VISIBLE: View.GONE);
			mViews.put(p, tv);
		}
		
		@Override
		public QuickHolder onCreateViewHolder(ViewGroup vg, int type){
			QuickHolder h=super.onCreateViewHolder(vg, type);
			h.addOnClickListener(R.id.item_card_contact);
			h.addOnClickListener(R.id.item_card_stick);
			h.addOnClickListener(R.id.item_card_hi);
			return h;
		}
		
		@Override
		public void onClick(View v, int p){
			Card item=getItem(p);
			switch(v.getId()){
			case R.id.item_card_contact:
				applyFriend(item);
				return;
			case R.id.item_card_stick:
				if(!item.showDesc){
					//隐藏其他
					List<Card> list=getData();
					for(int i=0; i<list.size(); i++) if(list.get(i).showDesc){
						list.get(i).showDesc=false;
						mViews.get(i).setVisibility(View.GONE);
					}
					item.showDesc=true;
					mViews.get(p).setVisibility(View.VISIBLE);
					return;
				}
			case R.id.item_card_hi:
				item.showDesc=false;
				mViews.get(p).setVisibility(View.GONE);
				return;
			}
			String link=item.getLink();
			if(!TextUtils.isEmpty(link)) startActivity(ActWeb.intent(link));
		}
	}
	
}
