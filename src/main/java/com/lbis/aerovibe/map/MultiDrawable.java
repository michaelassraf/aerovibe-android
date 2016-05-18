package com.lbis.aerovibe.map;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import com.lbis.aerovibe.concurrency.AsyncTaskMap;

/**
 * Draws up to four other drawables.
 */
public class MultiDrawable extends Drawable {

	private final List<String> stringDrawbles;
	private List<Drawable> mDrawables;
	Activity activity;
	Logger log = Logger.getLogger(getClass().getSimpleName());

	public MultiDrawable(List<String> profilePhotos, Activity activity) {
		stringDrawbles = profilePhotos;
		this.activity = activity;
		downloadAllDrawables(stringDrawbles);
	}

	@SuppressWarnings("unchecked")
	private void downloadAllDrawables(List<String> stringDrawbles) {
		mDrawables = new LinkedList<Drawable>();
		AsyncTaskMap.getAsyncHashMap().cancelAll();
		DownloadAllPictures task = new DownloadAllPictures();
		AsyncTaskMap.getAsyncHashMap().add(task);
		try {
			task.execute(stringDrawbles);
			mDrawables = task.get();
		} catch (Throwable th) {
			log.error("Can't download photos", th);
		}
	}

	class DownloadAllPictures extends AsyncTask<List<String>, Void, ArrayList<Drawable>> {

		@Override
		protected ArrayList<Drawable> doInBackground(@SuppressWarnings("unchecked") List<String>... args) {
			ArrayList<Drawable> resultList = new ArrayList<Drawable>();
			for (String picture : args[0]) {
				try {
					URL url = new URL(picture);
					Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
					Drawable d = new BitmapDrawable(activity.getResources(), bmp);
					resultList.add(d);
				} catch (Throwable th) {
					log.error("Can't load image", th);
				}
			}
			return resultList;
		}
	}

	@Override
	public void draw(Canvas canvas) {
		if (mDrawables.size() == 1) {
			mDrawables.get(0).draw(canvas);
			return;
		}
		int width = getBounds().width();
		int height = getBounds().height();

		canvas.save();
		canvas.clipRect(0, 0, width, height);

		if (mDrawables.size() == 2 || mDrawables.size() == 3) {
			// Paint left half
			canvas.save();
			canvas.clipRect(0, 0, width / 2, height);
			canvas.translate(-width / 4, 0);
			mDrawables.get(0).draw(canvas);
			canvas.restore();
		}
		if (mDrawables.size() == 2) {
			// Paint right half
			canvas.save();
			canvas.clipRect(width / 2, 0, width, height);
			canvas.translate(width / 4, 0);
			mDrawables.get(1).draw(canvas);
			canvas.restore();
		} else {
			// Paint top right
			canvas.save();
			canvas.scale(.5f, .5f);
			canvas.translate(width, 0);
			mDrawables.get(1).draw(canvas);

			// Paint bottom right
			canvas.translate(0, height);
			mDrawables.get(2).draw(canvas);
			canvas.restore();
		}

		if (mDrawables.size() >= 4) {
			// Paint top left
			canvas.save();
			canvas.scale(.5f, .5f);
			mDrawables.get(0).draw(canvas);

			// Paint bottom left
			canvas.translate(0, height);
			mDrawables.get(3).draw(canvas);
			canvas.restore();
		}

		canvas.restore();
	}

	@Override
	public void setAlpha(int i) {

	}

	@Override
	public void setColorFilter(ColorFilter colorFilter) {

	}

	@Override
	public int getOpacity() {
		return 0;
	}
}
