package km.quick;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import com.chad.library.adapter.base.BaseQuickAdapter;
import java.util.List;

/**  Created by wuzhengu on 2018/10/30 0030 */
public abstract class QuickClickAdapter<Item> extends QuickAdapter<Item>
		implements BaseQuickAdapter.OnItemClickListener, BaseQuickAdapter.OnItemChildClickListener
{
	public QuickClickAdapter(@LayoutRes int layout){
		this(layout, null);
	}
	
	public QuickClickAdapter(@Nullable List<Item> items){
		this(0, items);
	}
	
	public QuickClickAdapter(@LayoutRes int layout, @Nullable List<Item> items){
		super(layout, items);
		setOnItemClickListener(this);
		setOnItemChildClickListener(this);
	}
	
	@Override
	public void onItemChildClick(BaseQuickAdapter a, View v, int p){
		onClick(v, p);
	}
	
	@Override
	public void onItemClick(BaseQuickAdapter a, View v, int p){
		onClick(v, p);
	}
	
	public void onClick(View v, int p){
		
	}
}
