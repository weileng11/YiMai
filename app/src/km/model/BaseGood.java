package km.model;

import com.google.gson.annotations.SerializedName;

/** Created by wuzhengu on 2018/11/1 0001 */
public class BaseGood
{
	@SerializedName("id")
	public int id;
	@SerializedName(value="name", alternate={"goods_name"})
	public String name;
	@SerializedName(value="image", alternate={"original_img", "ad_code"})
	public String image;
	@SerializedName(value="link", alternate={"ad_link", "url"})
	public String link;
	public int imageRes;
	
	public int getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getImage(){
		return image;
	}
	
	public String getLink(){
		if(link==null) link="";
		return link;
	}
}
