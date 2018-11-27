package km.model;

import android.support.annotation.Keep;
import android.text.TextUtils;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import java.util.Map;
import km.api.Server;

/** Created by wuzhengu on 2018/11/2 0002 */
public class CityService
{
	@SerializedName("count")
	public Count count;
	@SerializedName("tips")
	public List<BaseGood> tips;
	@SerializedName("classify")
	public Map<String, Type> types;
	@SerializedName("banner")
	public List<BaseGood> banners;
	@SerializedName("second_banner")
	public List<BaseGood> banners2;
	@SerializedName("best_new")
	public List<News> news;
	
	@Keep
	public static class Type extends BaseGood
	{
		@SerializedName(value="icon")
		public String image;
		@SerializedName(value="cate_id")
		public String cateId;
		
		@Override
		public String getImage(){
			return image;
		}
		
		@Override
		public String getLink(){
			if(TextUtils.isEmpty(link)) link=Server.BASE_WEB+"navDetail/"+cateId;
			return link;
		}
	}
	
	public static class Count
	{
		@SerializedName("card_count")
		public int card;
		@SerializedName("message_count")
		public int message;
		@SerializedName("message_pv_count")
		public int messagePv;
	}
	
	public static class News
	{
		@SerializedName("content")
		public String content;
		@SerializedName("add_time")
		public String addTime;
		@SerializedName("id")
		public int id;
		@SerializedName("link")
		public String link;
		
	}
}