package km.model;

import com.google.gson.annotations.SerializedName;

/** Created by wuzhengu on 2018/11/3 0003 */
public class DataPage<Data>
{
	@SerializedName("data")
	public Data data;
	@SerializedName("total")
	public int total;
	@SerializedName("per_page")
	public int perPage;
	@SerializedName("current_page")
	public int currentPage;
	@SerializedName("last_page")
	public int lastPage;
}
