package km.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

/** Created by wuzhengu on 2018/10/29 0029 */
public class NameFragment extends BaseFragment
{
	String name;
	{
		setName(getClass().getSimpleName());
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle bundle){
		super.onViewCreated(view, bundle);
	}
	
	public NameFragment setName(String name){
		this.name=name;
		return this;
	}
	
	public String getName(){
		return name;
	}
}
