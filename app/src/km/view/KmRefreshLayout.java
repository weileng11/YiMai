package km.view;

import android.content.Context;
import android.util.AttributeSet;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;

/** Created by wuzhengu on 2018/10/30 0030 */
public class KmRefreshLayout extends SmartRefreshLayout
{
	public KmRefreshLayout(Context context){
		this(context, null);
	}
	
	public KmRefreshLayout(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public KmRefreshLayout(Context context, AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		setEnableLoadMore(false);
		setEnableAutoLoadMore(false);
	}
}
