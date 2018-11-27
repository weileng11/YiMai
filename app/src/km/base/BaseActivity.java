package km.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import km.api.RxActivity;
import km.app.App;

/** Created by wuzhengu on 2018/10/29 0029 */
public class BaseActivity extends RxActivity
{
	ActivityProxy mProxy;
	Unbinder mUnbinder;
	
	public ActivityProxy getProxy(){
		if(mProxy==null) mProxy=new ActivityProxy().set(this, null);
		return mProxy;
	}
	
	public BaseActivity getActivity(){ return this; }
	
	public Context getContext(){ return this; }
	
	@Override
	protected void onDestroy(){
		if(mProxy!=null) mProxy.dismissLoading();
		super.onDestroy();
		App.set(this, false);
	}
	
	@Override
	protected void onCreate(@Nullable Bundle bundle){
		App.set(this, true);
		super.onCreate(bundle);
		int layout=getLayoutId();
		if(layout!=0) setContentView(layout);
	}
	
	protected int getLayoutId(){
		return 0;
	}
	
	public View getView() {
		return findViewById(Window.ID_ANDROID_CONTENT);
	}
	
	@Override
	public void onActivityResult(int req, int res, Intent data){
		if(mProxy!=null && mProxy.onActivityResult(req, res, data)) return;
		super.onActivityResult(req, res, data);
	}
	
	@Override
	public void onRequestPermissionsResult(int req, @NonNull String[] ps, @NonNull int[] res){
		super.onRequestPermissionsResult(req, ps, res);
	}
	
	@Override
	public void setContentView(@LayoutRes int layout){
		setContentView(getLayoutInflater().inflate(layout, null));
	}
	
	@Override
	public void setContentView(View view){
		super.setContentView(view);
		bindView(view);
	}
	
	@Override
	public void setContentView(View view, ViewGroup.LayoutParams params){
		super.setContentView(view, params);
		bindView(view);
	}
	
	public void bindView(View view){
		if(mUnbinder!=null) mUnbinder.unbind();
		mUnbinder=ButterKnife.bind(this, view);
	}
	
	public void toast(CharSequence text){
		toast(getActivity(), text);
	}
	
	public static void toast(Context ctxt, CharSequence text){
		Toast.makeText(ctxt, text, Toast.LENGTH_SHORT).show();
	}
}
