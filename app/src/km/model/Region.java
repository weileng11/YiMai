package km.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/** Created by wuzhengu on 2018/10/31 0031 */
public class Region
{
	@SerializedName("name")
	public String name;
	@SerializedName("id")
	public int id;
	@SerializedName("parent_id")
	public int parentId;
	@SerializedName("level")
	public int level;
	@SerializedName("initials")
	public String initials;
	@SerializedName("num")
	public int num;
	
	public static class Result
	{
		@SerializedName("region_list")
		public List<Region> list;
	}
}
