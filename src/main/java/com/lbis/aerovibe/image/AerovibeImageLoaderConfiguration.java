package com.lbis.aerovibe.image;

import java.io.File;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

public class AerovibeImageLoaderConfiguration {

	static ImageLoaderConfiguration instance;

	public static ImageLoaderConfiguration getInstance(Context context) {
		if (instance == null) {
			File cacheDir = StorageUtils.getCacheDirectory(context);
			instance = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(480, 800) // default
					.discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75, null).taskExecutor(Executors.newFixedThreadPool(4)).taskExecutorForCachedImages(Executors.newFixedThreadPool(4)).threadPoolSize(3) // default
					.threadPriority(Thread.NORM_PRIORITY - 1) // default
					.tasksProcessingOrder(QueueProcessingType.FIFO) // default
					.denyCacheImageMultipleSizesInMemory().memoryCache(new LruMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(13) // default
					.discCache(new UnlimitedDiscCache(cacheDir)) // default
					.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100).discCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
					.imageDownloader(new BaseImageDownloader(context)) // default
					.imageDecoder(new BaseImageDecoder(false)) // default
					.defaultDisplayImageOptions(AerovibeDisplayImageOptions.getInstance(context)) // default
					.writeDebugLogs().build();
		}
		ImageLoader.getInstance().init(instance);
		return instance;
	}

}
