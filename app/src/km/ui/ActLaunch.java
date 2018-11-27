package km.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.TextView;
import kameng.yimai.BuildConfig;
import kameng.yimai.R;
import km.api.Server;
import km.base.BaseActivity;
import km.util.LoginUtil;

/**
 启动页
 <br/>
 Created by wuzhengu on 2018/10/30 0030
 */
public class ActLaunch extends BaseActivity
{
	static boolean started;
	
	@Override
	protected void onCreate(@Nullable Bundle bundle){
		super.onCreate(bundle);
		if(started){
			jump();
			return;
		}
		started=true;
		setContentView(R.layout.act_launch);
		TextView tv=(TextView)findViewById(R.id.launch_text);
		tv.setText(BuildConfig.VERSION_NAME+" ("+BuildConfig.VERSION_CODE+")");
		getWindow().getDecorView().postDelayed(new Runnable(){
			@Override
			public void run(){
				jump();
			}
		}, BuildConfig.DEBUG? 0: 1000);
	}
	
	void jump(){
		if(isFinishing()) return;
		String token=LoginUtil.getToken();
		if(TextUtils.isEmpty(token)) token=Server.TEST_TOKEN;
		if(TextUtils.isEmpty(token)) ActLogin.start(this); else ActHome.start(this);
		finish();
	}
}
