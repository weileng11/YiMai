package km.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** Created by wuzhengu on 2018/10/30 0030 */
public class GoodsType extends BaseGood implements DataList<Good>
{
	@SerializedName("banner")
	public String banner;
	@SerializedName("sort_order")
	public int sortOrder;
	@SerializedName(value="goods", alternate={"goods_list"})
	public List<Good> goods;
	
	@Override
	public List<Good> getList(){
		return goods;
	}
}
