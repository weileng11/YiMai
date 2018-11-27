package km.view;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import kameng.yimai.R;

/** Created by wuzhengu on 2018/10/30 0030 */
public class KmRefreshFooter extends ClassicsFooter
{
	static {
		REFRESH_FOOTER_PULLUP = "上拉加载";
		REFRESH_FOOTER_RELEASE = "松开加载";
		REFRESH_FOOTER_LOADING = "正在加载";
	}
	public KmRefreshFooter(Context context){
		this(context, null);
	}
	
	public KmRefreshFooter(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public KmRefreshFooter(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		setFinishDuration(0);
		setArrowResource(R.mipmap.pull_loading01);
		setProgressDrawable(new AnimationDrawable()
		{{
			setOneShot(false);
			int interval=120;
			addFrame(getResources().getDrawable(R.mipmap.pull_loading01), interval);
			addFrame(getResources().getDrawable(R.mipmap.pull_loading02), interval);
			addFrame(getResources().getDrawable(R.mipmap.pull_loading03), interval);
			addFrame(getResources().getDrawable(R.mipmap.pull_loading04), interval);
			addFrame(getResources().getDrawable(R.mipmap.pull_loading05), interval*1);
		}});
	}
	
	@Override
	public void onReleased(RefreshLayout layout, int height, int extendHeight){
		if(mNoMoreData) return;
		mProgressView.setVisibility(VISIBLE);
		if(mProgressDrawable != null) mProgressDrawable.start();
		else{
			Drawable drawable = mProgressView.getDrawable();
			if (drawable instanceof Animatable) ((Animatable)drawable).start();
			else mProgressView.animate().rotation(36000).setDuration(100000);
		}
	}
}
