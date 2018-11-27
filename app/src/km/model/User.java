package km.model;

import com.google.gson.annotations.SerializedName;

/** Created by wuzhengu on 2018/11/1 0001 */
public class User
{
	
	@SerializedName("user_id")
	public int userId;
	@SerializedName("email")
	public String email;
	@SerializedName("password")
	public String password;
	@SerializedName("paypwd")
	public Object paypwd;
	@SerializedName("sex")
	public int sex;
	@SerializedName("birthday")
	public int birthday;
	@SerializedName("user_money")
	public String userMoney;
	@SerializedName("frozen_money")
	public String frozenMoney;
	@SerializedName("distribut_money")
	public String distributMoney;
	@SerializedName("underling_number")
	public int underlingNumber;
	@SerializedName("pay_points")
	public int payPoints;
	@SerializedName("address_id")
	public int addressId;
	@SerializedName("reg_time")
	public int regTime;
	@SerializedName("last_login")
	public int lastLogin;
	@SerializedName("last_ip")
	public String lastIp;
	@SerializedName("qq")
	public String qq;
	@SerializedName("mobile")
	public String mobile;
	@SerializedName("mobile_validated")
	public int mobileValidated;
	@SerializedName("oauth")
	public String oauth;
	@SerializedName("openid")
	public Object openid;
	@SerializedName("unionid")
	public Object unionid;
	@SerializedName("head_pic")
	public String headPic;
	@SerializedName("province")
	public int province;
	@SerializedName("city")
	public int city;
	@SerializedName("district")
	public int district;
	@SerializedName("email_validated")
	public int emailValidated;
	@SerializedName("nickname")
	public String nickname;
	@SerializedName("letter")
	public String letter;
	@SerializedName("level")
	public int level;
	@SerializedName("level_2")
	public int level2;
	@SerializedName("discount")
	public String discount;
	@SerializedName("total_amount")
	public String totalAmount;
	@SerializedName("is_lock")
	public int isLock;
	@SerializedName("is_distribut")
	public int isDistribut;
	@SerializedName("first_leader")
	public int firstLeader;
	@SerializedName("second_leader")
	public int secondLeader;
	@SerializedName("third_leader")
	public int thirdLeader;
	@SerializedName("token")
	public String token;
	@SerializedName("message_mask")
	public int messageMask;
	@SerializedName("push_id")
	public String pushId;
	@SerializedName("distribut_level")
	public int distributLevel;
	@SerializedName("is_vip")
	public int isVip;
	@SerializedName("invite_code")
	public String inviteCode;
	@SerializedName("relation")
	public String relation;
	@SerializedName("sale_number")
	public int saleNumber;
	@SerializedName("parent_id")
	public int parentId;
	@SerializedName("wechat")
	public Object wechat;
	@SerializedName("wechat_qrcode")
	public Object wechatQrcode;
	@SerializedName("set_pass")
	public int setPass;
	@SerializedName("im_id")
	public String imId;
	@SerializedName("im_pwd")
	public String imPwd;
	@SerializedName("is_top")
	public int isTop;
	@SerializedName("paper_number")
	public int paperNumber;
	@SerializedName("paper_number_allowance")
	public int paperNumberAllowance;
	@SerializedName("voice_notice")
	public int voiceNotice;
	@SerializedName("backimg")
	public Object backimg;
	@SerializedName("team_number")
	public int teamNumber;
	@SerializedName("layer")
	public int layer;
	@SerializedName("level_name")
	public String levelName;
	
	public static class Result
	{
		@SerializedName("user")
		public User user;
	}
}
