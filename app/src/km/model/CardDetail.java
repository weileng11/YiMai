package km.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

/**
 * @author: ${bruce}
 * @project: YiMai
 * @package: km.model
 * @description:
 * @date: 2018/11/5 0005  
 * @time: 下午 5:11
 */
public class CardDetail
{
		@SerializedName("card_info")
		public CardInfo cardInfo;
		@SerializedName("word")
		public List<Word> word;
		@SerializedName("goods_info")
		public List<GoodsInfo> goodsInfo;
		
		public static class CardInfo
		{
			@SerializedName("id")
			public int id;
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
			public String headPortrait;
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
			public int userId;
			@SerializedName("add_time")
			public String addTime;
			@SerializedName("status")
			public int status;
			@SerializedName("hot")
			public int hot;
			@SerializedName("collection")
			public int collection;
			@SerializedName("dianzan")
			public int dianzan;
			@SerializedName("autograph")
			public String autograph;
			@SerializedName("location_x")
			public String locationX;
			@SerializedName("location_y")
			public String locationY;
			@SerializedName("geohash")
			public String geohash;
			@SerializedName("is_collection")
			public int isCollection;
			@SerializedName("is_dianzan")
			public int isDianzan;
			@SerializedName("cate_name")
			public String cateName;
			@SerializedName("province_str")
			public String provinceStr;
			@SerializedName("city_str")
			public String cityStr;
			@SerializedName("area_str")
			public String areaStr;
		}
		
		public static class Word
		{
			@SerializedName("id")
			public int id;
			@SerializedName("company_name")
			public String companyName;
			@SerializedName("job")
			public String job;
			@SerializedName("desc")
			public String desc;
			@SerializedName("begin_time")
			public String beginTime;
			@SerializedName("end_time")
			public String endTime;
			@SerializedName("user_id")
			public int userId;
			@SerializedName("add_time")
			public String addTime;
			@SerializedName("status")
			public int status;
		}
		
		public static class GoodsInfo
		{
			@SerializedName("id")
			public int id;
			@SerializedName("user_id")
			public int userId;
			@SerializedName("goods_name")
			public String name;
			@SerializedName("image")
			public String image;
			@SerializedName("price")
			public String priceNow;
			@SerializedName("sku")
			public String sku;
			@SerializedName("freight")
			public String freight;
			@SerializedName("goods_desc")
			public String goodsDesc;
			@SerializedName("image_detail")
			public String imageDetail;
			@SerializedName("add_time")
			public String addTime;
			@SerializedName("adopt_status")
			public int adoptStatus;
			@SerializedName("shelf_status")
			public int shelfStatus;
			@SerializedName("is_delete")
			public int isDelete;
			@SerializedName("update_time")
			public Object updateTime;
	}
}
