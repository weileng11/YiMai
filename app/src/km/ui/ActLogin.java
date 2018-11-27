package km.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import butterknife.BindView;
import butterknife.OnClick;
import kameng.yimai.R;
import kameng.yimai.wxapi.WXEntryActivity;
import km.api.*;
import km.app.App;
import km.base.BaseActivity;
import km.model.M;
import km.model.User;
import km.util.LoginUtil;
import km.util.PrefUtil;

/**
 登录
 <br/>
 Created by wuzhengu on 2018/8/16
 */
public class ActLogin extends BaseActivity
{
	@BindView(R.id.login_input_phone)
	EditText vPhone;
	@BindView(R.id.login_input_pwd)
	EditText vPwd;
	@BindView(R.id.login_remember_pwd)
	CheckBox vRemember;
	RxListener<M<User>> mListener;
	WXEntryActivity.Callback mWxCallback;
	
	static boolean started;
	
	public static void start(Context ctxt){
		if(started) return;
		started=true;
		ctxt.startActivity(new Intent(ctxt, ActLogin.class));
	}
	
	@Override
	public void finish(){
		started=false;
		super.finish();
	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
		App.finish();
	}
	
	@Override
	protected void onDestroy(){
		if(mWxCallback!=null) mWxCallback.destroy();
		super.onDestroy();
	}
	
	@Override
	protected int getLayoutId(){
		return R.layout.act_login;
	}
	
	@Override
	protected void onCreate(@Nullable Bundle bundle){
		super.onCreate(bundle);
		vPhone.setText(PrefUtil.getString(PrefUtil.KEY_USERNAME, null));
		vPwd.setText(PrefUtil.getString(PrefUtil.KEY_PASSWORD, null));
		if(false){
			vPhone.setText("13828821554");
			vPwd.setText("111111");
		}
		if(vPhone.length()>0){
			vPwd.requestFocus();
			vPwd.setSelection(vPwd.length());
		}
		vRemember.setChecked(PrefUtil.getBoolean(PrefUtil.KEY_PASSWORD_REMEMBER, true));
		vRemember.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton button, boolean checked){
				PrefUtil.putBoolean(PrefUtil.KEY_PASSWORD_REMEMBER, checked);
			}
		});
	}
	
	@OnClick(R.id.login_find_pwd)
	void click_find_pwd(){
		startActivity(ActWeb.intent(Server.BASE_WEB+"findpwd"));
	}
	
	@OnClick(R.id.login_start)
	void click_login_start(){
		final String phone=vPhone.getText().toString().trim();
		if(phone.length()<1){
			toast("手机号无效");
			return;
		}
		final String pwd=vPwd.getText().toString().trim();
		if(pwd.length()<1){
			toast("密码无效");
			return;
		}
		getProxy().showLoading("登录中...");
		final boolean remember=vRemember.isChecked();
		if(remember) PrefUtil.putString(PrefUtil.KEY_USERNAME, phone);
		Servers.start(getActivity(), Server.get().doLogin(phone, pwd), new RxListener2<User>()
		{
			
			{ mListener=this; }
			@Override
			public void onNext2(User user, String msg){
				if(mListener!=this) return;
				onLogin(user, msg);
				if(remember) PrefUtil.putString(PrefUtil.KEY_PASSWORD, pwd);
			}
		});
	}
	
	@OnClick(R.id.login_wx)
	void click_login_wx(){
		getProxy().showLoading("微信登录...");
		WXEntryActivity.login(this, new WXEntryActivity.Callback()
		{
			@Override
			public boolean onCall(String code){
				if(TextUtils.isEmpty(code)){
					getProxy().dismissLoading();
					toast("微信登录失败");
				}else Servers.start(getActivity(), Server.get().doQuickLogin(code, "1"),
						new RxListener2<User.Result>()
						{
							@Override
							public void onNext2(User.Result result, String msg){
								onLogin(result==null? null: result.user, msg);
							}
						});
				return true;
			}
			@Override
			public boolean onCall(WXEntryActivity.User user){
				getProxy().dismissLoading();
				new AlertDialog.Builder(getContext())
						.setTitle("微信登录结果")
						.setMessage(String.valueOf(user))
						.show();
				return true;
			}
		});
	}
	
	void onLogin(User user, String msg){
		getProxy().dismissLoading();
		String token=null;
		if(user!=null) token=user.token;
		if(TextUtils.isEmpty(token)){
			toast(msg!=null? msg: "登录失败");
			return;
		}
		LoginUtil.setToken(token);
		LoginUtil.setUser(user);
		final boolean hasPhone=!TextUtils.isEmpty(user.mobile);
		if(hasPhone) PrefUtil.putString(PrefUtil.KEY_USERNAME, user.mobile);
		msg="欢迎登录，正在为您跳转...";
		if(!TextUtils.isEmpty(user.nickname)) msg=user.nickname+"，"+msg;
		getProxy().showLoading(msg);
		ActHome.start(this);
		if(!hasPhone) getProxy().startActivity(ActPhoneBind.class);
		finish();
	}
}