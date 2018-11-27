package km.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import java.util.List;
import kameng.yimai.R;
import km.base.BaseActivity;
import km.base.HomeFragment;
import km.other.VersionManager;
import wzg.util.Locators;

/**
 主页
 <br/>
 Created by wuzhengu on 2018/10/30 0030
 */
public class ActHome extends BaseActivity
{
	Locators.Locator mLocator;
	TipHandler mHandler=new TipHandler();
	long mTime;
	static boolean started;
	
	public static void start(Context ctxt){
		if(started) return;
		started=true;
		ctxt.startActivity(new Intent(ctxt, ActHome.class));
	}
	
	class TipHandler extends Handler implements Runnable
	{
		@Override
		public void run(){
			toast("双击即可退出");
		}
		
		TipHandler cancel(){
			removeCallbacks(this);
			return this;
		}
		
		boolean post(long delay){
			return postDelayed(this, delay);
		}
	}
	
	@Override
	public void onBackPressed(){
		mHandler.cancel();
		long now=System.currentTimeMillis();
		if(now-mTime>1000){
			mTime=now;
			mHandler.post(250);
			return;
		}
		finish();
	}
	
	@Override
	public void finish(){
		started=false;
		super.finish();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		mLocator.stop();
	}
	
	@Override
	public int getLayoutId(){
		return R.layout.act_home;
	}
	
	@Override
	protected void onCreate(final Bundle bundle){
		super.onCreate(bundle);
		mLocator=Locators.create();
		getSupportFragmentManager().beginTransaction()
				.add(new VersionManager(), VersionManager.class.getSimpleName()).commit();
		final RadioGroup group=(RadioGroup)findViewById(R.id.home_tabs);
		final ViewPager pager=(ViewPager)findViewById(R.id.home_pager);
		pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager())
		{
			@Override
			public void setPrimaryItem(ViewGroup vg, int p, Object o){
				super.setPrimaryItem(vg, p, o);
			}
			@Override
			public int getCount(){
				return 5;
			}
			@Override
			public Fragment getItem(int p){
				HomeFragment fragment=getFragment(p);
				if(fragment!=null) fragment.setGps(mLocator);
				return fragment;
			}
			HomeFragment getFragment(int p){
				switch(p){
				case 0: return new F1();
				case 1: return new F2();
				case 2: return new F4();
				case 3: return new F3();
				case 4: return new F5();
				}
				return null;
			}
		});
		pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener()
		{
			{ onPageSelected(0); }
			@Override
			public void onPageSelected(int p){
				group.check(group.getChildAt(p).getId());
			}
		});
		group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(RadioGroup group, int id){
				for(int i=0, j=group.getChildCount(); i<j; i++) {
					RadioButton button=(RadioButton)group.getChildAt(i);
					if(id==button.getId()){
						if(button.isChecked()) pager.setCurrentItem(i, false);
						break;
					}
				}
			}
		});
		boolean ok=checkLocate();
		if(!ok && Build.VERSION.SDK_INT>=23) requestPermissions(P_LOCATE, P_LOCATE.hashCode());
	}
	
	@Override
	public void onRequestPermissionsResult(int req, @NonNull String[] ps, @NonNull int[] res){
		if(req==P_LOCATE.hashCode()){
			if(!checkLocate()) getProxy().alert("定位权限不足，请检查手机设置");
			return;
		}
		super.onRequestPermissionsResult(req, ps, res);
	}
	
	boolean checkLocate(){
		boolean ok=true;
		for(String p: P_LOCATE){
			ok=checkCallingOrSelfPermission(p)==PackageManager.PERMISSION_GRANTED;
			if(!ok) break;
		}
		if(!ok) return false;
		mLocator.start(this, new Locators.Callback()
		{
			@Override
			public void onLocate(Locators.Locator locator){
				List<Fragment> fragments=getSupportFragmentManager().getFragments();
				if(fragments!=null) for(Fragment f: fragments) if(f instanceof HomeFragment){
					((HomeFragment)f).setGps(mLocator);
				}
			}
		});
		return true;
	}
	
	static final String[] P_LOCATE={
			Manifest.permission.ACCESS_FINE_LOCATION,
			Manifest.permission.WRITE_EXTERNAL_STORAGE,
	};
}
