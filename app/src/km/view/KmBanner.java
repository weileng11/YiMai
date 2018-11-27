package km.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.bumptech.glide.GlideApp;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.loader.ImageLoaderInterface;
import kameng.yimai.R;

/** Created by wuzhengu on 2018/11/1 0001 */
public class KmBanner extends Banner implements ImageLoaderInterface<ImageView>
{
	Callback mCallback;
	
	public KmBanner(Context context){
		this(context, null);
	}
	
	public KmBanner(Context context, AttributeSet attrs){
		this(context, attrs, 0);
	}
	
	public KmBanner(Context context, AttributeSet attrs, int defStyle){
		super(context, attrs, defStyle);
		setBannerAnimation(Transformer.Default);
		setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
		setIndicatorGravity(BannerConfig.CENTER);
		setViewPagerIsScroll(true);
		isAutoPlay(true);
		setDelayTime(5000);
		setImageLoader(this);
	}
	
	@Override
	public void displayImage(Context ctxt, Object path, ImageView iv){
		iv.setScaleType(ImageView.ScaleType.FIT_XY);
		if(mCallback!=null) path=mCallback.getPath(path);
		GlideApp.with(ctxt).load(path).error(R.drawable.dft_item).into(iv);
	}
	
	@Override
	public ImageView createImageView(Context ctxt){
		return new ImageView(ctxt);
	}
	
	public KmBanner setCallback(Callback callback){
		mCallback=callback;
		return this;
	}
	
	public interface Callback
	{
		Object getPath(Object path);
	}
}
