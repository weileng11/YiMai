package km.api;

import java.util.List;
import km.model.*;
import retrofit2.http.*;
import rx.Observable;

/**
 * @author: ${bruce}
 * @description: 网络请求参数类
 * @date: 2018/11/21 0021
 * @time: 下午 2:16
 */
public class Server
{
	public static final String TEST_TOKEN="";  //token
	public static final String DEVICE_TYPE="android"; //类型
	public static final String BASE_WEB="https://m.totobye.cn/"; //web地址
	public static final String BASE_URL="https://api.iudeng.cn/"; //网络请求地址
	
	private static Api api=null;
	
	public static synchronized Api get(){
		if(api==null) api=Servers.getRetrofit(BASE_URL).create(Api.class);
		return api;
	}
	
	public interface Api
	{
		/**
		 登陆
		 */
		@FormUrlEncoded
		@POST("api/User/do_login")
		Observable<M<User>> doLogin(
				@Field("username") String username,
				@Field("password") String password
		);
		
		@FormUrlEncoded
		@POST("api/User/quick_login")
		Observable<M<User.Result>> doQuickLogin(
				@Field("code") String code,
				@Field("flag") String flag
		);
		
		/**
		 用户信息
		 */
		@GET("api/User/get_user_info")
		Observable<M<User.Result>> getUserInfo();
		
		/** 城市列表 */
		@GET("api/User/get_region")
		Observable<M<Region.Result>> getCityList(
				@Query("id") int id
		);
		
		@GET("api/Goods/hot_tips")
		Observable<M<List<BaseGood>>> getTags();
		
		@GET("api/ad/index_ad")
		Observable<M<List<Good>>> getBanners();
		
		@GET("api/Goods/categoryList")
		Observable<M<List<GoodsType>>> getGoodsType();
		
		@GET("api/Goods/indexZero")
		Observable<M<Good.Page>> getVipGo();
		
		@GET("api/Goods/collage")
		Observable<M<GoodsType>> getCollage();
		
		@GET("api/Goods/cateHot")
		Observable<M<List<GoodsType>>> getHots();
		
		@GET("api/Goods/index")
		Observable<M<Good.Page>> getGoods(
				@Query("page") int page,
				@Query("cat_id") String id,
				@Query("keyword") String keyword
		);
		
		@GET("api/business_card/index_data")
		Observable<M<CityService>> getCityService();
		
		@GET("api/business_card/massage_list")
		Observable<M<News.Page>> getNews(
				@Query("page") int page,
				@Query("type") String type,
				@Query("location_x") String longitude,
				@Query("location_y") String latitude,
				@Query("cate_id") String cateId
		);
		
		@GET("api/business_card/card_list")
		Observable<M<Card.Page>> getCards(
				@Query("page") int page,
				@Query("is_hot") int hot,
				@Query("is_nearby") int nearby,
				@Query("location_x") String longitude,
				@Query("location_y") String latitude,
				@Query("cate_id") String cateId
		);
		
		@GET("api/business_card/get_classify")
		Observable<M<List<Card.Category>>> getCardCategorys();
		
		@FormUrlEncoded
		@POST("api/Friend/apply")
		Observable<M> doFriendApply(
				@Field("friend_uid") String uid,
				@Field("msg") String msg
		);
		
		@FormUrlEncoded
		@POST("api/User/send_validate_code")
		Observable<M> doSendCode(
				@Field("mobile") String mobile
		);
		
		@FormUrlEncoded
		@POST("api/UserCentre/binding_mobile")
		Observable<M<PhoneBind>> doBindPhone(
				@Field("mobile") String phone,
				@Field("code") String code,
				@Field("pwd") String pwd,
				@Field("pwd_2") String pwd2
		);
		/**
		  个人中心
		 */
		@GET("api/UserCentre/index")
		Observable<M<UserCenter>> getUserCenter();
		/**
		 城主信息
		 */
		@GET("api/Territory/info")
		Observable<M<CityMaster>> getCityMaster();
		
		/**
		 我的名片
		 */
		@GET("api/business_card/card_detail")
		Observable<M<CardDetail>> getCardDetail(
				@Query("user_id") String uid,
				@Query("type") String type
		);
		
		/**
		  收藏
		 */
		@GET("api/business_card/collection_card")
		Observable<M> doCardCollect(
				@Query("card_id") String cardId
		);
		/**
		 点赞
		 */
		@GET("api/business_card/add_praise")
		Observable<M> doCardPraise(
				@Query("card_id") String cardId
		);
		
		/**
		 设置邀请码
		 */
		@GET("api/UserCentre/binding")
		Observable<M> doInvite(
				@Query("invite_code") String code
		);
	}
}
