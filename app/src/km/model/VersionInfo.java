package km.model;

import com.google.gson.annotations.SerializedName;

/** Created by wuzhengu on 2018/11/8 0008 */
public class VersionInfo
{
	@SerializedName("name")
	public String name;
	@SerializedName("version")
	public int version;
	@SerializedName("changelog")
	public String changelog;
	@SerializedName("updated_at")
	public int updatedAt;
	@SerializedName("versionShort")
	public String versionName;
	@SerializedName("build")
	public String build;
	@SerializedName(value="install_url", alternate={"installUrl"})
	public String installUrl;
	@SerializedName("direct_install_url")
	public String directInstallUrl;
	@SerializedName("update_url")
	public String updateUrl;
	@SerializedName("binary")
	public Binary binary;
	
	public static class Binary
	{
		@SerializedName("fsize")
		public int fsize;
	}
}
