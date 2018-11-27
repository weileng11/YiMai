package km.model;

import com.google.gson.annotations.SerializedName;

/**
 Created by wuzhengu on 2018/11/12 0012 */
public class CityMaster
{
	@SerializedName("user_number")
	public int userNumber;
	@SerializedName("vip_number")
	public int vipNumber;
	@SerializedName("income")
	public float income;
	@SerializedName("head_pic")
	public String headPic;
	@SerializedName("nickname")
	public String nickname;
	@SerializedName("region_name")
	public String regionName;
	
	public boolean ok;
}
