package km.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import kameng.yimai.R;
import km.api.*;
import km.base.BaseTitleActivity;
import km.model.M;
import km.model.PhoneBind;
import km.util.PrefUtil;

/** Created by wuzhengu on 2018/11/5 */
public class ActPhoneBind extends BaseTitleActivity
{
	static final String KEY_CODE_GET_TIME="code_get_time";
	static final int INTERVAL=60*1000;
	@BindView(R.id.phone_bind_num)
	TextView vPhone;
	@BindView(R.id.phone_bind_pwd)
	TextView vPwd;
	@BindView(R.id.phone_bind_code)
	TextView vCode;
	
	@Override
	protected int getLayoutId(){
		return R.layout.act_phone_bind;
	}
	
	@Override
	protected void onCreate(@Nullable Bundle bundle){
		super.onCreate(bundle);
		setTitle("手机号和密码");
	}
	
	@OnClick(R.id.phone_bind_get_code)
	void click_get_code(){
		if(System.currentTimeMillis()-PrefUtil.getLong(KEY_CODE_GET_TIME, 0)<INTERVAL){
			toast("验证码已发送");
			return;
		}
		String phone=vPhone.getText().toString().trim();
		if(phone.isEmpty()){
			toast("请输入手机号");
			return;
		}
		getProxy().showLoading("验证码获取中...");
		Servers.start(this, Server.get().doSendCode(phone), new RxListener<M>()
		{
			@Override
			public void onNext(M m, String msg){
				getProxy().dismissLoading();
				boolean ok=false;
				if(m!=null){
					ok=m.status==1;
					if(!TextUtils.isEmpty(m.msg)) msg=m.msg;
					else if(ok) msg="获取成功";
				}
				toast(!TextUtils.isEmpty(msg)? msg: "获取失败");
				if(ok) PrefUtil.putLong(KEY_CODE_GET_TIME, System.currentTimeMillis());
			}
		});
	}
	
	@OnClick(R.id.phone_bind_start)
	void click_start(){
		String phone=vPhone.getText().toString().trim();
		if(phone.isEmpty()){
			toast("请输入手机号");
			return;
		}
		String pwd=vPwd.getText().toString().trim();
		if(pwd.isEmpty()){
			toast("请输入新密码");
			return;
		}
		String code=vCode.getText().toString().trim();
		if(code.isEmpty()){
			toast("请输入验证码");
			return;
		}
		getProxy().showLoading("提交中...");
		Servers.start(this, Server.get().doBindPhone(phone, code, pwd, pwd), new RxListener2<PhoneBind>()
		{
			@Override
			public void onNext(M<PhoneBind> m, String msg){
				getProxy().dismissLoading();
				boolean ok=false;
				if(m!=null){
					ok=m.status==1;
					String phone=null;
					PhoneBind bind=m.result;
					if(bind!=null) phone=bind.mobile;
					msg=ok? "设置成功": "设置失败";
					if(!TextUtils.isEmpty(phone)) msg=phone+msg;
				}
				toast(msg);
				if(ok) finish();
			}
		});
	}
}
