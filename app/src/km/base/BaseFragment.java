package km.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import km.api.RxFragment;

/** Created by wuzhengu on 2018/10/29 0029 */
public class BaseFragment extends RxFragment
{
	ActivityProxy mProxy;
	View mView;
	boolean mReuse=true;
	boolean mCreated;
	boolean mInited;
	Unbinder mUnbinder;
	
	public BaseFragment setReuse(boolean reuse){
		mReuse=reuse;
		return this;
	}
	
	public ActivityProxy getProxy(){
		if(mProxy==null) mProxy=new ActivityProxy().set(null, this);
		return mProxy;
	}
	
	@Override
	public void onDestroy(){
		if(mProxy!=null) mProxy.dismissLoading();
		super.onDestroy();
	}
	
	@Override
	public void onCreate(@Nullable Bundle bundle){
		super.onCreate(bundle);
	}
	
	@Override
	public void onActivityResult(int req, int res, Intent data){
		if(mProxy!=null && mProxy.onActivityResult(req, res, data)) return;
		super.onActivityResult(req, res, data);
	}
	
	public int getLayout(){
		return 0;
	}
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle bundle){
		if(mReuse && mView!=null) return mView;
		int layout=getLayout();
		if(layout!=0) return inf.inflate(layout, vg, false);
		return null;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle bundle){
		super.onViewCreated(view, bundle);
		mCreated=true;
		mView=view;
		bindView(getView());
		checkAndInitLazily();
	}
	
	@Override
	public void setUserVisibleHint(boolean visible){
		super.setUserVisibleHint(visible);
		checkAndInitLazily();
	}
	
	void checkAndInitLazily(){
		if(mCreated && !mInited && getUserVisibleHint()) {
			mInited=true;
			onInitLate();
		}
	}
	
	public void onInitLate(){
		
	}
	
	@Override
	public void onDestroyView(){
		if(!mReuse) mInited=false;
		mCreated=false;
		super.onDestroyView();
	}
	
	public void bindView(View view){
		if(mUnbinder!=null) mUnbinder.unbind();
		mUnbinder=ButterKnife.bind(this, view);
	}
	
	public <V extends View> V findViewById(int vId){
		return (V)getView().findViewById(vId);
	}
	
	public void toast(CharSequence text){
		BaseActivity.toast(getActivity(), text);
	}
	
	public static <V extends View> V inflate(ViewGroup vg, int layout) {
		return (V)LayoutInflater.from(vg.getContext()).inflate(layout, vg, false);
	}
}
