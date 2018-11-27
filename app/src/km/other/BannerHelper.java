package km.other;

import com.youth.banner.listener.OnBannerListener;
import java.util.List;
import km.model.BaseGood;
import km.view.KmBanner;

/** Created by wuzhengu on 2018/11/2 0002 */
public class BannerHelper implements KmBanner.Callback, OnBannerListener
{
	KmBanner mBanner;
	
	public BannerHelper(KmBanner banner){
		mBanner=banner;
		banner.setCallback(this);
		banner.setOnBannerListener(this);
	}
	
	@Override
	public Object getPath(Object path){
		if(path instanceof BaseGood) return ((BaseGood)path).getImage();
		return path;
	}
	
	@Override
	public void OnBannerClick(int p){
		List list=getData();
		Object obj=list==null? null: list.get(p);
		if(obj instanceof BaseGood) GoodsAdapter.clickGood(mBanner.getContext(), (BaseGood)obj);
	}
	
	public List getData(){
		return null;
	}
}
