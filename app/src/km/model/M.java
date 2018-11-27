package km.model;

import com.google.gson.annotations.SerializedName;

/** Created by wuzhengu on 2018/10/30 0030 */
public class M<Result>
{
	@SerializedName("msg")
	public String msg;
	@SerializedName("result")
	public Result result;
	@SerializedName("status")
	public int status;
}
