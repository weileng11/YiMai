package km.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;
import km.api.Server;

/** Created by wuzhengu on 2018/11/2 0002 */
public class News
{
	@SerializedName("id")
	public String id;
	@SerializedName("user_id")
	public String userId;
	@SerializedName("cate_id")
	public int cateId;
	@SerializedName("image")
	public String image;
	@SerializedName("content")
	public String content;
	@SerializedName("province")
	public int province;
	@SerializedName("city")
	public int city;
	@SerializedName("area")
	public int area;
	@SerializedName("address")
	public String address;
	@SerializedName("mobile")
	public String mobile;
	@SerializedName("goods_id")
	public String goodsId;
	@SerializedName("add_time")
	public String addTime;
	@SerializedName("update_time")
	public Object updateTime;
	@SerializedName("status")
	public int status;
	@SerializedName("pv")
	public int pv;
	@SerializedName("share")
	public int share;
	@SerializedName("dianzan")
	public int likes;
	@SerializedName("location_x")
	public String longitude;
	@SerializedName("location_y")
	public String latitude;
	@SerializedName("geohash")
	public String geohash;
	@SerializedName("set_up_sort")
	public int setUpSort;
	@SerializedName("set_up_time")
	public String setUpTime;
	@SerializedName("nickname")
	public String nickname;
	@SerializedName("head_pic")
	public String headPic;
	@SerializedName("comment_count")
	public int comments;
	@SerializedName("time_text")
	public String timeText;
	@SerializedName("collection")
	public int collection;
	@SerializedName("range")
	public String range;
	@SerializedName("image_arr")
	public List<String> images;
	@SerializedName("link")
	public String link;
	
	public String getLink(){
		if(link==null) link=Server.BASE_WEB+"infoDetail/"+id;
		return link;
	}
	
	public static class Page extends DataPage<List<News>>
	{
		
	}
}
