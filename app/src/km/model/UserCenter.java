package km.model;

import com.google.gson.annotations.SerializedName;

public class UserCenter
{
	@SerializedName("card")
	public Card card;
	@SerializedName("profit")
	public Profit profit;
	@SerializedName("goods")
	public Good.Page goods;
	
	public static class Card
	{
		@SerializedName("num_1")
		public int num1;
		@SerializedName("num_2")
		public int num2;
		@SerializedName("num_3")
		public int num3;
		@SerializedName("num_4")
		public int num4;
	}
	
	public static class Profit
	{
		@SerializedName("all_profit")
		public float total;
		@SerializedName("tuiguang_profit")
		public float invite;
		@SerializedName("day_profit")
		public float today;
	}
}
