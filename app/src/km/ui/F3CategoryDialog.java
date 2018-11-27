package km.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.*;
import android.widget.TextView;
import java.util.Arrays;
import kameng.yimai.R;
import km.quick.QuickClickAdapter;
import km.quick.QuickHolder;

/** Created by wuzhengu on 2018/11/13 0013 */
class F3CategoryDialog implements View.OnClickListener, DialogInterface
{
	Context mContext;
	View mView;
	View vContent;
	TextView vTitle;
	TextView vButton;
	CharSequence mTitleText;
	CharSequence mButtonText;
	OnClickListener mClickListener;
	OnDismissListener mDismissListener;
	OnMultiChoiceClickListener mChoiceListener;
	String[] mItems;
	boolean[] mChecks;
	boolean mCreated=false;
	boolean mCancelable=true;
	boolean mShowing=false;
	
	public F3CategoryDialog setCancelable(boolean cancelable){
		mCancelable=cancelable;
		return this;
	}
	
	public boolean isShowing(){
		return mShowing;
	}
	
	public View getView(){
		return mView;
	}
	
	public F3CategoryDialog setView(View view){
		mView=view;
		return this;
	}
	
	public F3CategoryDialog setTitle(CharSequence text){
		mTitleText=text;
		if(vTitle!=null) vTitle.setText(text);
		return this;
	}
	
	public F3CategoryDialog setDismissListener(OnDismissListener listener){
		mDismissListener=listener;
		return this;
	}
	
	public F3CategoryDialog setPositiveButton(CharSequence text, OnClickListener listener) {
		mButtonText=text;
		mClickListener=listener;
		if(vButton!=null) vButton.setText(text);
		return this;
	}
	
	public F3CategoryDialog setMultiChoiceItems(
			String[] items, boolean[] checks, OnMultiChoiceClickListener listener){
		mItems=items;
		mChecks=checks;
		mChoiceListener=listener;
		return this;
	}
	
	public F3CategoryDialog(@NonNull Context context){
		mContext=context;
	}
	
	protected void onCreate(){
		if(mView==null) return;
		vContent=mView.findViewById(R.id.f3_category_content);
		vTitle=(TextView)mView.findViewById(R.id.f3_category_title);
		vButton=(TextView)mView.findViewById(R.id.f3_category_button);
		vButton.setOnClickListener(this);
		mView.findViewById(R.id.f3_category).setOnClickListener(this);
		mView.findViewById(R.id.f3_category_content).setOnClickListener(this);
		final RecyclerView rv=(RecyclerView)mView.findViewById(R.id.f3_category_list);
		
		if(mTitleText!=null) vTitle.setText(mTitleText);
		if(mButtonText!=null) vButton.setText(mButtonText);
		
		rv.setAdapter(new QuickClickAdapter<String>(R.layout.item_category, Arrays.asList(mItems))
		{
			@Override
			protected void convert(QuickHolder h, String item, int p){
				h.itemView.setSelected(p%2==0);
				h.setChecked(R.id.item_category_name, mChecks[p]);
				h.setText(R.id.item_category_name, item);
			}
			
			@Override
			public void onClick(View v, int p){
				boolean checked=!mChecks[p];
				mChecks[p]=checked;
				QuickHolder h=(QuickHolder)rv.findContainingViewHolder(v);
				h.setChecked(R.id.item_category_name, checked);
				if(mChoiceListener!=null) mChoiceListener.onClick(F3CategoryDialog.this, p, checked);
			}
		});
	}
	
	@Override
	public void onClick(View v){
		switch(v.getId()){
		case R.id.f3_category:
			if(mCancelable) dismiss();
			return;
		case R.id.f3_category_content:
			return;
		case R.id.f3_category_button:
			if(mClickListener!=null) mClickListener.onClick(this, 0);
			return;
		}
	}
	
	@Override
	public void cancel(){
		setShow(false, false);
	}
	
	public void dismiss(){
		setShow(false, true);
		if(mDismissListener!=null) mDismissListener.onDismiss(this);
	}
	
	public F3CategoryDialog show(){
		setShow(true, true);
		return this;
	}
	
	void setShow(boolean show, boolean anim){
		if(mShowing!=show && mView!=null){
			mShowing=show;
			if(show && !mCreated){
				mCreated=true;
				onCreate();
			}
			if(vContent.getAnimation()!=null) vContent.getAnimation().cancel();
			if(show) mView.setVisibility(View.VISIBLE);
			if(!anim){
				if(!show) mView.setVisibility(View.GONE);
				return;
			}
			if(show) vContent.startAnimation(new TranslateAnimation(1, 0, 1, 0, 1, -1, 1, 0)
			{{
				setInterpolator(new DecelerateInterpolator());
				setDuration(200);
			}});
			else vContent.startAnimation(new TranslateAnimation(1, 0, 1, 0, 1, 0, 1, -1)
			{{
				setInterpolator(new AccelerateInterpolator());
				setDuration(200);
				setAnimationListener(new AnimationListener()
				{
					@Override
					public void onAnimationStart(Animation animation){}
					
					@Override
					public void onAnimationEnd(Animation animation){
						mView.setVisibility(View.GONE);
					}
					
					@Override
					public void onAnimationRepeat(Animation animation){}
				});
			}});
		}
	}
}
