package km.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import kameng.yimai.R;

/** Created by wuzhengu on 2018/10/30 0030 */
public class KmRefreshHeader extends ClassicsHeader
{
	static{
		REFRESH_HEADER_PULLDOWN="下拉刷新";
		REFRESH_HEADER_RELEASE="松开刷新";
		REFRESH_HEADER_REFRESHING="正在刷新";
	}
	public KmRefreshHeader(Context context){
		this(context, null);
	}
	
	public KmRefreshHeader(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public KmRefreshHeader(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		setEnableLastTime(false);
		setArrowResource(R.mipmap.pull_loading00);
		setProgressDrawable(new AnimationDrawable()
		{{
			setOneShot(false);
			int interval=120;
			addFrame(getResources().getDrawable(R.mipmap.pull_loading01), interval);
			addFrame(getResources().getDrawable(R.mipmap.pull_loading02), interval);
			addFrame(getResources().getDrawable(R.mipmap.pull_loading03), interval);
			addFrame(getResources().getDrawable(R.mipmap.pull_loading04), interval);
			addFrame(getResources().getDrawable(R.mipmap.pull_loading05), interval*2);
		}});
	}
}
