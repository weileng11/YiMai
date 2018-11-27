package km.quick;

import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.GlideApp;
import com.chad.library.adapter.base.BaseViewHolder;

/** Created by wuzhengu on 2018/10/30 0030 */
public class QuickHolder extends BaseViewHolder
{
	public QuickHolder(View view){
		super(view);
	}
	
	public void setImage(@IdRes int vId, String imgUrl){
		setImage(vId, imgUrl, 0);
	}
	
	public void setImage(@IdRes int vId, String imgUrl, @DrawableRes int dftImg){
		ImageView iv=getView(vId);
		if(TextUtils.isEmpty(imgUrl)){
			iv.setImageResource(dftImg);
			return;
		}
		GlideApp.with(iv.getContext()).load(imgUrl).error(dftImg).into(iv);
	}
	
	public BaseViewHolder setTextSafe(@IdRes int viewId, CharSequence value) {
		TextView view = getView(viewId);
		if(view!=null) view.setText(value);
		return this;
	}
	
	public void setAdapter(int vId, RecyclerView.Adapter adapter){
		RecyclerView rv=getView(vId);
		rv.setAdapter(adapter);
	}
}
