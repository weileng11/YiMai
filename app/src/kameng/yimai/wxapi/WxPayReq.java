package kameng.yimai.wxapi;

import com.tencent.mm.opensdk.modelpay.PayReq;

/** Created by wuzhengu on 2018/11/12 0012 */
public class WxPayReq extends PayReq
{
	public WxPayReq(){
		appId=WXEntryActivity.APP_ID;
		packageValue="Sign=WXPay";
	}
}
