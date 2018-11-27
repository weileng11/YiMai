package km.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener;

/** Created by wuzhengu on 2018/11/13 0013 */
public class KmRefreshListener extends SimpleMultiPurposeListener
{
	boolean alertAutoLoadMore=true;
	
	public boolean getAlertAutoLoadMore(){
		return alertAutoLoadMore;
	}
	
	@Override
	public void onLoadMore(final RefreshLayout layout){
		if(getAlertAutoLoadMore() && !layout.isEnableAutoLoadMore()){
			alertAutoLoadMore=false;
			new AlertDialog.Builder(layout.getLayout().getContext())
					.setMessage("需要自动加载更多数据吗？")
					.setCancelable(false)
					.setNegativeButton("不用", null)
					.setPositiveButton("是的", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which){
							layout.setEnableAutoLoadMore(true);
						}
					})
					.show();
		}
	}
}
