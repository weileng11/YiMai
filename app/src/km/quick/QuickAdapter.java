package km.quick;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;
import com.chad.library.adapter.base.BaseQuickAdapter;
import java.util.List;

/**  Created by wuzhengu on 2018/10/30 0030 */
public abstract class QuickAdapter<Item> extends BaseQuickAdapter<Item, QuickHolder>
{
	
	Boolean mVertical;
	
	public QuickAdapter(@LayoutRes int layout){
		this(layout, null);
	}
	
	public QuickAdapter(@Nullable List<Item> items){
		this(0, items);
	}
	
	public QuickAdapter(@LayoutRes int layout, @Nullable List<Item> items){
		super(layout, items);
	}
	
	public QuickAdapter setVertical(Boolean vertical){
		mVertical=vertical;
		return this;
	}
	
	public Boolean isVertical(){
		return mVertical;
	}
	
	@Override
	public void onAttachedToRecyclerView(RecyclerView rv){
		int o=0;
		RecyclerView.LayoutManager m=rv.getLayoutManager();
		if(m instanceof LinearLayoutManager){
			o=((LinearLayoutManager)m).getOrientation();
		}else if(m instanceof StaggeredGridLayoutManager){
			o=((StaggeredGridLayoutManager)m).getOrientation();
		}
		mVertical=o==RecyclerView.VERTICAL;
	}
	
	@Override
	public QuickHolder onCreateViewHolder(ViewGroup parent, int viewType){
		QuickHolder h=super.onCreateViewHolder(parent, viewType);
		if(mVertical!=null) h.itemView.getLayoutParams().width=mVertical? -1: -2;
		return h;
	}
	
	@Override
	public void onBindViewHolder(QuickHolder h, int p){
		super.onBindViewHolder(h, p);
	}
	
	@Override
	protected void convert(QuickHolder h, Item item){
		convert(h, item, h.getAdapterPosition());
	}
	
	protected void convert(QuickHolder h, Item item, int p){}
}
