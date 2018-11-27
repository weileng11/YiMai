package km.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** Created by wuzhengu on 2018/10/30 0030 */
public class Good extends BaseGood
{
	@SerializedName("is_hot")
	public int isHot;
	@SerializedName("mobile_name")
	public String mobileName;
	@SerializedName("parent_id")
	public int parentId;
	@SerializedName("parent_id_path")
	public String parentIdPath;
	@SerializedName("level")
	public int level;
	@SerializedName("sort_order")
	public int sortOrder;
	@SerializedName("is_show")
	public int isShow;
	@SerializedName("cat_group")
	public int catGroup;
	@SerializedName("commission_rate")
	public int commissionRate;
	@SerializedName(value="sales_sum", alternate={})
	public int hits;
	@SerializedName(value="shop_price", alternate={"price"})
	public float priceNow;
	@SerializedName("market_price")
	public float priceOld;
	@SerializedName("people_number")
	public int people;
	@SerializedName("coupon")
	public float coupon;
	
	public static class Page extends DataPage<List<Good>> implements DataList<Good>
	{
		@Override
		public List<Good> getList(){
			return data;
		}
		
	}
}
