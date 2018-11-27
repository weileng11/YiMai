package com.bumptech.glide;

import android.content.Context;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;
import java.io.File;

/** Created by wuzhengu on 2018/10/30 0010 */
@GlideModule
public class MyGlideModule extends AppGlideModule
{
	public static final int CACHE_SIZE_MEMORY=1024*1024*100;
	public static final int CACHE_SIZE_DISK=1024*1024*1000;
	public static File CACHE_DIR;
	
	public static File getCacheDir(Context ctxt){
		if(CACHE_DIR==null) CACHE_DIR=new File(ctxt.getExternalCacheDir().getParent(), "glide");
		return CACHE_DIR;
	}
	
	@Override
	public void applyOptions(Context ctxt, GlideBuilder builder){
		builder.setDiskCache(new DiskLruCacheFactory(getCacheDir(ctxt).getPath(), CACHE_SIZE_DISK));
		builder.setMemoryCache(new LruResourceCache(CACHE_SIZE_MEMORY));
	}
	
	@Override
	public void registerComponents(Context context, Glide glide, Registry registry){
		
	}
	
	@Override
	public boolean isManifestParsingEnabled(){
		return false;
	}
}
