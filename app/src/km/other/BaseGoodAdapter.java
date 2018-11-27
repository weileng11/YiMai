package km.other;

import android.view.View;
import java.util.List;
import kameng.yimai.R;
import km.model.BaseGood;
import km.quick.QuickClickAdapter;
import km.quick.QuickHolder;

/** Created by wuzhengu on 2018/11/1 0002 */
public class BaseGoodAdapter<Item extends BaseGood> extends QuickClickAdapter<Item>
{
	public BaseGoodAdapter(int layout){
		super(layout);
	}
	
	public BaseGoodAdapter(int layout, List<Item> items){
		super(layout, items);
	}
	
	@Override
	protected void convert(QuickHolder h, Item item){
		if(item==null) return;
		h.setText(R.id.item_good_name, item.getName());
		h.setImage(R.id.item_good_image, item.getImage(),
				item.imageRes!=0? item.imageRes: R.drawable.dft_item);
	}
	
	@Override
	public void onClick(View v, int p){
		
	}
}
