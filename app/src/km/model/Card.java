package km.model;

import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import km.api.Server;

/** Created by wuzhengu on 2018/11/3 0003 */
public class Card
{
	@SerializedName("id")
	public String id;
	@SerializedName("company_name")
	public String companyName;
	@SerializedName("job")
	public String job;
	@SerializedName("real_name")
	public String realName;
	@SerializedName("manage")
	public String manage;
	@SerializedName("mobile")
	public String mobile;
	@SerializedName("weixin")
	public String weixin;
	@SerializedName("province")
	public String province;
	@SerializedName("city")
	public String city;
	@SerializedName("area")
	public String area;
	@SerializedName("address")
	public String address;
	@SerializedName("cate_id")
	public String cateId;
	@SerializedName("head_portrait")
	public String headPic;
	@SerializedName("logo")
	public String logo;
	@SerializedName("qrcode")
	public String qrcode;
	@SerializedName("voice")
	public String voice;
	@SerializedName("goods_id")
	public String goodsId;
	@SerializedName("work_desc")
	public String workDesc;
	@SerializedName("user_id")
	public String userId;
	@SerializedName("add_time")
	public String addTime;
	@SerializedName("status")
	public int status;
	@SerializedName("hot")
	public int hits;
	@SerializedName("collection")
	public int collection;
	@SerializedName("dianzan")
	public int likes;
	@SerializedName("autograph")
	public String autograph;
	@SerializedName("location_x")
	public String locationX;
	@SerializedName("location_y")
	public String locationY;
	@SerializedName("geohash")
	public String geohash;
	@SerializedName("user_info")
	public Object userInfo;
	@SerializedName("collection_count")
	public int collectionCount;
	@SerializedName("cate_name")
	public String cateName;
	@SerializedName("link")
	public String link;
	@SerializedName("set_up_sort")
	public int setUpSort;
	@SerializedName("set_up_time")
	public String setUpTime;
	@SerializedName("set_up_desc")
	public String setUpDesc;
	
	public boolean showDesc;
	
	public static String link(String uid){
		if(uid==null) return "";
		return Server.BASE_WEB+"MyBusinessCard/"+uid;
	}
	
	public String getLink(){
		if(link==null) link=link(userId);
		return link;
	}
	
	public String getHeadPic(){
		String str=null;
		if(userInfo instanceof UserInfo) str=((UserInfo)userInfo).headPic;
		if(TextUtils.isEmpty(str)) str=headPic;
		return str;
	}
	
	public String getNickname(){
		String str=null;
		if(userInfo instanceof UserInfo) str=((UserInfo)userInfo).nickname;
		if(TextUtils.isEmpty(str)) str=realName;
		return str;
	}
	
	public static class Page extends DataPage<List<Card>>
	{
		
	}
	
	public static class UserInfo
	{
		@SerializedName("nickname")
		public String nickname;
		@SerializedName("head_pic")
		public String headPic;
		@SerializedName("invite_code")
		public String inviteCode;
	}
	
	public static class Category
	{
		@SerializedName("id")
		public int id;
		@SerializedName("name")
		public String name;
		@SerializedName("level")
		public int level;
		@SerializedName("parent_id")
		public int parentId;
		@SerializedName("sort")
		public int sort;
		@SerializedName("icon")
		public String icon;
		@SerializedName("children")
		public List<Category> children;
		
		public boolean checked;
	}
}
