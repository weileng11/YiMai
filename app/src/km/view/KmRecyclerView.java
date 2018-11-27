package km.view;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class KmRecyclerView extends android.support.v7.widget.RecyclerView
{
	private float mDownX;
	private float mDownY;
	/** 第一个可见的item的位置 */
	private int mFirstVisible;
	/** 最后一个可见的item的位置 */
	private int mLastVisible;
	/** 第一个的位置 */
	private int[] mFirstPositions;
	/** 最后一个的位置 */
	private int[] mLastPositions;
	private boolean mAtTop;
	private boolean mAtBottom;
	
	public KmRecyclerView(Context context){
		this(context, null);
	}
	
	public KmRecyclerView(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public KmRecyclerView(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
		LayoutManager manager=getLayoutManager();
		if(manager instanceof GridLayoutManager){
			mLastVisible=((GridLayoutManager)manager).findLastVisibleItemPosition();
			mFirstVisible=((GridLayoutManager)manager).findFirstVisibleItemPosition();
		}else if(manager instanceof LinearLayoutManager){
			mLastVisible=((LinearLayoutManager)manager).findLastVisibleItemPosition();
			mFirstVisible=((LinearLayoutManager)manager).findFirstVisibleItemPosition();
		}else if(manager instanceof StaggeredGridLayoutManager){
			StaggeredGridLayoutManager sm=(StaggeredGridLayoutManager)manager;
			if(mLastPositions==null){
				mLastPositions=new int[sm.getSpanCount()];
				mFirstPositions=new int[sm.getSpanCount()];
			}
			sm.findLastVisibleItemPositions(mLastPositions);
			sm.findFirstVisibleItemPositions(mFirstPositions);
			mLastVisible=findMax(mLastPositions);
			mFirstVisible=findMin(mFirstPositions);
		}else{
			throw new RuntimeException(
					"Unsupported LayoutManager used. " +
					"Valid ones are LinearLayoutManager, "+
					"GridLayoutManager and StaggeredGridLayoutManager."
			);
		}
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			mDownX=ev.getX();
			mDownY=ev.getY();
			//如果滑动到了最底部，就允许继续向上滑动加载下一页，否者不允许
			getParent().requestDisallowInterceptTouchEvent(true);
			break;
		case MotionEvent.ACTION_MOVE:
			float dx=ev.getX()-mDownX;
			float dy=ev.getY()-mDownY;
			boolean allowParentTouchEvent;
			if(Math.abs(dy)>Math.abs(dx)){
				if(dy>0){
					mAtTop=mFirstVisible==0 && getChildAt(0).getTop()>=0;
					//位于顶部时下拉，让父View消费事件
					allowParentTouchEvent=mAtTop;
				}else{
					mAtBottom=manager.getChildCount()>0 && mLastVisible>=manager.getItemCount()-1 &&
					          getChildAt(getChildCount()-1).getBottom()<=getHeight();
					//位于底部时上拉，让父View消费事件
					allowParentTouchEvent=mAtBottom;
				}
			}else{
				//水平方向滑动
				allowParentTouchEvent=true;
			}
			getParent().requestDisallowInterceptTouchEvent(!allowParentTouchEvent);
		}
		return super.dispatchTouchEvent(ev);
	}
	
	public boolean isTop(){
		return mAtTop;
	}
	
	public boolean isBottom(){
		return mAtBottom;
	}
	
	public void goTop(){
		scrollToPosition(0);
	}
	
	static int findMax(int[] list){
		int max=list[0];
		for(int value : list) if(max<value) max=value;
		return max;
	}
	
	static int findMin(int[] list){
		int min=list[0];
		for(int value : list) if(value<min) min=value;
		return min;
	}
}
