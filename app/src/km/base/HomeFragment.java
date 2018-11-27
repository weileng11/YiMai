package km.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.Loader;
import android.view.*;
import android.widget.TextView;
import wzg.util.Locators;

/** Created by wuzhengu on 2018/11/8 */
public class HomeFragment extends NameFragment
{
	public double mLongitude;
	public double mLatitude;
	
	@Override
	public View onCreateView(LayoutInflater inf, ViewGroup vg, Bundle bundle){
		View view=super.onCreateView(inf, vg, bundle);
		if(view!=null) return view;
		return new TextView(getContext())
		{{
			setText(getName());
			setGravity(Gravity.CENTER);
			setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
		}};
	}
	
	public HomeFragment setGps(Locators.Locator locator){
		mLongitude=locator.getLongitude();
		mLatitude=locator.getLatitude();
		return this;
	}
}
