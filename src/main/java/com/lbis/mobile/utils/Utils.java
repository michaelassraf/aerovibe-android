package com.lbis.mobile.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.log4j.Level;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Shader.TileMode;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lbis.aerovibe.concurrency.service.ScheduleTasksReceiver;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.model.SensorDbExecutors;
import com.lbis.database.executors.model.SensorMeasurementDbExecutors;
import com.lbis.database.executors.model.UserDbExecutors;
import com.lbis.server.actions.SensorActions;
import com.lbis.server.actions.SensorMeasurementActions;
import com.lbis.server.actions.UserActions;

import de.mindpipe.android.logging.log4j.LogConfigurator;

public class Utils {
	
	private static final String APPLICATION_FONT_NAME = "HighlandGothicFLF.ttf"; 

	private static Utils utils;

	final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(Utils.class.getSimpleName());

	public static Utils getInstance() {
		if (utils == null)
			utils = new Utils();

		return utils;
	}
	
	public String getInitialDateAsString(Context ctx) {
		Calendar initialCalendar = Calendar.getInstance();
		initialCalendar = Calendar.getInstance();
		initialCalendar.set(Calendar.DAY_OF_MONTH, 01);
		initialCalendar.set(Calendar.MONTH, 01);
		initialCalendar.set(Calendar.YEAR, 1980);
		initialCalendar.set(Calendar.HOUR, 00);
		initialCalendar.set(Calendar.MINUTE, 00);
		initialCalendar.set(Calendar.SECOND, 1980);
		return android.text.format.DateFormat.getDateFormat(ctx).format(
				initialCalendar.getTimeInMillis());
	}


	public void designActionBar(SherlockFragmentActivity activity) {
		int titleId = activity.getResources().getIdentifier("action_bar_title", "id", "android");
		TextView yourTextView = (TextView) activity.findViewById(titleId);
		yourTextView.setTypeface(getFont(activity));
		activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		activity.getSupportActionBar().setHomeButtonEnabled(true);
		activity.getSupportActionBar().setBackgroundDrawable(new ColorDrawable(activity.getResources().getColor(R.color.translucent_black))); // This
	}
	
	public Long setChosenDate(Context ctx, TextView dateView, int year,
			int monthOfYear, int dayOfMonth) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
		cal.set(Calendar.MONTH, monthOfYear);
		cal.set(Calendar.YEAR, year);

		dateView.setText(Utils.getInstance().getDateAsString(ctx,
				cal.getTimeInMillis()));
		return cal.getTimeInMillis();
	}
	
	public String getDateAsString(Context ctx, long date) {
		return android.text.format.DateFormat.getDateFormat(ctx).format(date);
	}

	public ProgressDialog showProgressDialog(Context context) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setMessage("Please wait...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		return progressDialog;
	}

	public String getFacebookProfileURL(String userId) {
		StringBuilder sb = new StringBuilder().append("https://graph.facebook.com/").append(userId).append("/picture?type=large");
		return sb.toString();
	}

	public int safeLongToInt(long l) {
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			throw new IllegalArgumentException(l + " cannot be cast to int without changing its value.");
		}
		return (int) l;
	}

	public Integer getActionBarHeight(SherlockFragmentActivity activity) {
		TypedValue tv = new TypedValue();
		if (activity.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
			return TypedValue.complexToDimensionPixelSize(tv.data, activity.getResources().getDisplayMetrics()) + 10;
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	public int[] getScreenSizes(Context context) {
		int screen[] = new int[2];
		if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.HONEYCOMB_MR2) {
			WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			screen[0] = display.getWidth();
			screen[1] = display.getHeight();
		}

		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB_MR2) {
			Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			screen[0] = size.x;
			screen[1] = size.y;
		}
		return screen;
	}

	public String configureLog4J() {
		try {
			File logsDirectory = new File(Environment.getExternalStorageDirectory() + File.separator + "Aerovibe");
			logsDirectory.mkdirs();
			LogConfigurator logConfigurator = new LogConfigurator();
			logConfigurator.setFileName(logsDirectory.getAbsolutePath() + File.separator + "aerovibe.log");
			logConfigurator.setRootLevel(Level.ALL);
			logConfigurator.setMaxFileSize( 60 * 1024 * 1024);
			logConfigurator.setUseLogCatAppender(true);
//			logConfigurator.setUseLogCatAppender(false);
			logConfigurator.configure();
			return logConfigurator.getFileName();
		} catch (Throwable th) {
			Logger.getLogger("Log4J config").log(java.util.logging.Level.SEVERE, "Can't configure Log4J", th);
			return "Can't configure Log4J";
		}
	}

	public void replaceFragment(FragmentManager fragmentManager, Fragment fragment, int destViewId) {
		System.gc();
		FragmentTransaction ft = fragmentManager.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
		ft.replace(destViewId, fragment, fragment.getClass().getName());
		ft.addToBackStack(fragment.toString());
		ft.commitAllowingStateLoss();
	}

	public Long parseFacbookDate(String date) {
		try {
			DateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.ENGLISH);
			Date result = df.parse(date);
			Calendar cal = Calendar.getInstance();
			cal.setTime(result);
			return cal.getTimeInMillis() / 1000;
		} catch (Exception ex) {
			log.error("Can't parse fucking facebook date", ex);
		}
		return 0L;

	}

	public Typeface getFont(Context context) {
		return Typeface.createFromAsset(context.getAssets(), APPLICATION_FONT_NAME);
	}

	public ArrayList<String> getAllMenuNames() {
		ArrayList<String> mainMenuListItmes = new ArrayList<String>();
		mainMenuListItmes.add("You - WIP");
		mainMenuListItmes.add("Near you");
		mainMenuListItmes.add("The world");
		mainMenuListItmes.add("Profile - WIP");
		mainMenuListItmes.add("Alerts - WIP");
		return mainMenuListItmes;
	}

	public boolean loadDummySensorsData(Context context) {
		InputStream inputStream = null;
		boolean isSuccessed = false;
		try {
			AssetManager assetManager = context.getAssets();
			inputStream = assetManager.open("sensors.txt");
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, "UTF-8");
			String theString = writer.toString();
			LinkedList<Sensor> sensors = new SensorActions().createObjectsFromJson(theString);
			new SensorDbExecutors().putAll(context, sensors);
			isSuccessed = true;
		} catch (Throwable th) {
			log.error("Can't load dummy content to sensors table.", th);
			isSuccessed = false;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable th) {
					log.error("Can't close sensors dummy data input streem.", th);
				}
			}
		}
		return isSuccessed;
	}

	public boolean loadDummySensorMeasurementsData(Context context) {
		InputStream inputStream = null;
		boolean isSuccessed = false;
		try {
			AssetManager assetManager = context.getAssets();
			inputStream = assetManager.open("sensorMeasurements.txt");
			StringWriter writer = new StringWriter();
			IOUtils.copy(inputStream, writer, "UTF-8");
			String theString = writer.toString();
			LinkedList<SensorMeasurement> sensors = new SensorMeasurementActions().createObjectsFromJson(theString);
			new SensorMeasurementDbExecutors().putAll(context, sensors);
			isSuccessed = true;
		} catch (Throwable th) {
			log.error("Can't load dummy content to sensors measurements table.", th);
			isSuccessed = false;
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (Throwable th) {
					log.error("Can't close sensors measurements dummy data input streem.", th);
				}
			}
		}
		return isSuccessed;
	}

	public void showDialog(SherlockFragmentActivity activity, String previousFragmentTag, String prospetctedFragmentTag, DialogFragment fragment) {
		FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
		Fragment prev = activity.getSupportFragmentManager().findFragmentByTag(previousFragmentTag);
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(previousFragmentTag);
		fragment.show(ft, prospetctedFragmentTag);
	}

	public int getRandomSplashImage() {
		Random generator = new Random(System.currentTimeMillis());
		int i = generator.nextInt(4);
		return Enums.Picture.values()[i].getDrawableInt();
	}

	public String convertMinutesToHour(int rawMinutes) {
		int hours = rawMinutes / 60; // since both are ints, you get an int
		int minutes = rawMinutes % 60;
		return hours + ":" + minutes;
	}
	
	public void launchImageChoser(Activity activity, String title,
			int callbackNum) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		if (android.os.Build.VERSION.SDK_INT > 10) {
			intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
		}
		activity.startActivityForResult(Intent.createChooser(intent, title),
				callbackNum);
	}

	public void setAlarmIfNotSet(Context context) {
		if (PendingIntent.getBroadcast(context, 0, new Intent(context, ScheduleTasksReceiver.class), PendingIntent.FLAG_NO_CREATE) == null) {
			Calendar updateTime = Calendar.getInstance();
			Intent downloader = new Intent(context, ScheduleTasksReceiver.class);
			PendingIntent recurringDownload = PendingIntent.getBroadcast(context, 0, downloader, PendingIntent.FLAG_CANCEL_CURRENT);
			AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_HALF_HOUR, recurringDownload);
			log.info("Re schedule user location service !");
		} else {
			log.info("No need to re schedule user location service !");
		}
	}

	public void updateUserFromDBOnServer(Context context, org.apache.log4j.Logger logger) {
		try {
			new UserDbExecutors().put(context, new UserActions().getObjectForObject(new UserDbExecutors().getFirstObjectIfExists(context), context));
		} catch (Throwable th) {
			logger.error("Failed to update user on the server.", th);
		}
	}

	public void updateUserFromDBOnServerAsync(final Context context, final org.apache.log4j.Logger logger) {

		Runnable updateUserFromDBOnServer = new Runnable() {
			public void run() {
				updateUserFromDBOnServer(context, logger);
			}
		};

		new Thread(updateUserFromDBOnServer).start();
	}

	public Bitmap setImageBlurEffect(Context ctx, Bitmap image, float blurRadius) {
		Bitmap outputBitmap = Bitmap.createBitmap(image);
		RenderScript rs = RenderScript.create(ctx);
		ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		Allocation tmpIn = Allocation.createFromBitmap(rs, image);
		Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
		theIntrinsic.setRadius(blurRadius);
		theIntrinsic.setInput(tmpIn);
		theIntrinsic.forEach(tmpOut);
		tmpOut.copyTo(outputBitmap);
		return outputBitmap;
	}

	public Bitmap setImagePopArtGradientEffect(Context context, int yourDrawableResource) {
		int[] colors = new int[] { Color.parseColor("#FFD900"), Color.parseColor("#FF5300"), Color.parseColor("#FF0D00"), Color.parseColor("#AD009F"), Color.parseColor("#1924B1") };
		float[] colorPositions = new float[] { 0.2f, 0.4f, 0.6f, 0.8f, 1.0f };

		final Resources res = context.getResources();
		Drawable drawable = res.getDrawable(yourDrawableResource);

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);
		LinearGradient grad = new LinearGradient(0, 0, 0, canvas.getHeight(), colors, colorPositions, TileMode.CLAMP);
		Paint p = new Paint();
		p.setStyle(Style.FILL);
		p.setAlpha(110); // adjust alpha for overlay intensity
		p.setShader(grad);
		canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), p);
		return bitmap;
	}

	public String writeBitmapToDisk(Bitmap bitmap, String fileName) throws Throwable {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
		File file = new File(Environment.getExternalStorageDirectory() + File.separator + fileName);
		file.createNewFile();
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		fileOutputStream.write(byteArrayOutputStream.toByteArray());
		fileOutputStream.close();
		return file.getAbsolutePath();
	}

	public Bitmap getBitmapFromURL(String strURL) {
		try {
			URL url = new URL(strURL);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoInput(true);
			connection.connect();
			InputStream input = connection.getInputStream();
			Bitmap myBitmap = BitmapFactory.decodeStream(input);
			return myBitmap;
		} catch (IOException e) {
			log.error("Error when trying to download ", e);
			return null;
		}
	}

}
