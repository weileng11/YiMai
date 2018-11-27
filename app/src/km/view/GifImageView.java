package km.view;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.bumptech.glide.GlideApp;

/** Created by wuzhengu on 2018/10/31 0031 */
public class GifImageView extends ImageView
{
	
	public GifImageView(Context context){
		this(context, null);
	}
	
	public GifImageView(Context context, @Nullable AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public GifImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr){
		super(context, attrs, defStyleAttr);
		if(attrs!=null){
			String xmlns="http://schemas.android.com/apk/res/android";
			int resId=attrs.getAttributeResourceValue(xmlns, "src", 0);
			if(resId!=0) setImageResource(resId);
			boolean hasType=false;
			for(int i=0, j=attrs.getAttributeCount(); i<j; i++){
				hasType="scaleType".equals(attrs.getAttributeName(i));
				if(hasType) break;
			}
			if(!hasType) setScaleType(ScaleType.FIT_XY);
		}
	}
	
	@Override
	public void setImageResource(@DrawableRes int resId){
		if(isInEditMode()){
			super.setImageResource(resId);
			return;
		}
		GlideApp.with(getContext()).load(resId).into(this);
	}
}
