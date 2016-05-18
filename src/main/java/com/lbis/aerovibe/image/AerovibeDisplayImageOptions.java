package com.lbis.aerovibe.image;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import com.lbis.aerovibe_android.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class AerovibeDisplayImageOptions {

	static DisplayImageOptions instance;

	public static DisplayImageOptions getInstance(Context context) {

		if (instance == null) {
			// DON'T COPY THIS CODE TO YOUR PROJECT! This is just example of ALL
			// options using.
			instance = new DisplayImageOptions.Builder().showImageOnLoading(R.drawable.ic_launcher) // resource
					.showImageForEmptyUri(R.drawable.ic_launcher) // resource or
																	// drawable
					.showImageOnFail(R.drawable.ic_launcher) // resource or
																// drawable
					.resetViewBeforeLoading(false) // default
					.delayBeforeLoading(1000).cacheInMemory(false) // default
					.cacheOnDisc(false) // default
					// .preProcessor(new BitmapProcessor))
					// .postProcessor(...)
					// .extraForDownloader(...)
					.considerExifParams(false) // default
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2) // default
					.bitmapConfig(Bitmap.Config.ARGB_8888) // default
					// .decodingOptions(new Options())
					.displayer(new RoundedBitmapDisplayer(25)) // default
					.handler(new Handler()) // default
					.build();
		}
		return instance;
	}

}
