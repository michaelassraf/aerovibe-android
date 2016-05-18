package com.lbis.aerovibe.concurrency.service;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.lbis.aerovibe.aqi.calc.AQICalculator;
import com.lbis.aerovibe.concurrency.DataScheduleTaskRefresher;
import com.lbis.aerovibe.location.LocationRetriever;
import com.lbis.aerovibe.location.LocationRetriever.LocationResult;
import com.lbis.aerovibe.main.Splash;
import com.lbis.aerovibe.management.ExecuteManagementMethods;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe.model.UserLocation;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.model.UserDbExecutors;
import com.lbis.database.executors.model.UserLocationDbExecutors;
import com.lbis.mobile.utils.Utils;
import com.lbis.server.actions.UserLocationActions;

public class UserLocationIntentService extends IntentService {

	public UserLocationIntentService() {
		super("UserLocationIntentService");
	}

	public UserLocationIntentService(String name) {
		super("UserLocationIntentService");
	}

	Logger log = Logger.getLogger(UserLocationIntentService.class.getSimpleName());
	UserLocation userLocation;
	static Double userLocationLongitude;
	static Double userLocationLatitude;
	PowerManager powerManager;
	PowerManager.WakeLock wakeLock;

	@SuppressLint("Wakelock")
	@Override
	protected void onHandleIntent(Intent workIntent) {

		Utils.getInstance().configureLog4J();
		powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
		wakeLock.acquire();

		log.info("Starting user location schedule task.");
		Thread.currentThread().setName(getClass().getSimpleName());

		try {
			final LocationResult locationResult = new LocationResult() {
				@Override
				public void gotLocation(Location location) {
					if (location == null)
						return;
					userLocationLongitude = location.getLongitude();
					userLocationLatitude = location.getLatitude();
					log.info("Successfully got location !");
				}
			};

			LocationRetriever locationRetriever = new LocationRetriever();
			locationRetriever.getLocation(getApplicationContext(), locationResult);
			Thread.sleep(10 * 1000);

			if (userLocationLongitude == null || userLocationLatitude == null) {
				userLocation = new UserLocationDbExecutors().get(new ExecuteManagementMethods().getTokenAndUserId(getApplicationContext()).getTokenUserId(), getApplicationContext());
			} else {
				userLocation = new UserLocation(userLocationLatitude, userLocationLongitude, new ExecuteManagementMethods().getTokenAndUserId(getApplicationContext()).getTokenUserId(), null);
				new UserLocationDbExecutors().put(getApplicationContext(), userLocation);
			}

			if (userLocation == null || userLocation.getUserLocationUserId() == null || userLocation.getUserLocationLatitude() == null || userLocation.getUserLocationLongitude() == null) {
				userLocation = new UserLocation(null, null, new ExecuteManagementMethods().getTokenAndUserId(getApplicationContext()).getTokenUserId(), null);
			}

			DataScheduleTaskRefresher.getInstance(getApplicationContext()).refreshAllLists();
			UserLocation userLocationRecieved = new UserLocationActions().createNewRequest(userLocation, getApplicationContext());
			Sensor closestSensor = DataScheduleTaskRefresher.getInstance(getApplicationContext()).getFreshSensorsData().get(userLocationRecieved.getUserLocationClosestSensorId());
			SensorMeasurement closestSensorMeasurement = DataScheduleTaskRefresher.getInstance(getApplicationContext()).getFreshSensorMeasurementData().get(userLocationRecieved.getUserLocationClosestSensorId());
			log.info("Closest sensor is " + closestSensor.getSensorCity() + " AQI level is " + closestSensorMeasurement.getSensorMeasurementAQILevel());
			User loggedInUser = new UserDbExecutors().getFirstObjectIfExists(getApplicationContext());
			log.info("User AQI profile level is " + loggedInUser.getUserAQIProfileLevel());
			if (closestSensorMeasurement.getSensorMeasurementAQILevel() < loggedInUser.getUserAQIProfileLevel()) {
				log.info("AQI is too low won't generate notification.");
				return;
			}

			Bitmap notificationPicutre = Utils.getInstance().getBitmapFromURL(closestSensor.getSensorAddressImage());
			NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
			notiStyle.setBigContentTitle(closestSensor.getSensorCity());
			notiStyle.setSummaryText(closestSensor.getSensorCountry());
			notiStyle.bigPicture(notificationPicutre);
			Intent resultIntent = new Intent(this, Splash.class);
			resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
			stackBuilder.addParentStack(Splash.class);
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
			Uri path = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.wind_sound);
			Notification myNotification = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher).setSound(path).setAutoCancel(true).setLargeIcon(notificationPicutre).setContentIntent(resultPendingIntent).setContentTitle("Current AQI is " + closestSensorMeasurement.getSensorMeasurementAQILevel()).setContentText("Current AQI is " + closestSensorMeasurement.getSensorMeasurementAQILevel()).setLights(Color.parseColor(AQICalculator.getInstance().getAQILevelForAQILevelNumber(closestSensorMeasurement.getSensorMeasurementAQILevel()).getaQILevelsColor()), 2000, 5000).setNumber(closestSensorMeasurement.getSensorMeasurementAQILevel()).setStyle(notiStyle).build();
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			mNotificationManager.notify(300, myNotification);

			log.info("Successfully sent user location request.");
		} catch (Throwable th) {
			log.error("Failed to send user location request.", th);
		} finally {
			wakeLock.release();
		}
	}
}