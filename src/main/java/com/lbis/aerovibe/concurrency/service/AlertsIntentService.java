package com.lbis.aerovibe.concurrency.service;

import org.apache.log4j.Logger;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.lbis.aerovibe.model.UserLocation;
import com.lbis.mobile.utils.Utils;

public class AlertsIntentService extends IntentService {

	public AlertsIntentService() {
		super("AlertsIntentService");
	}

	public AlertsIntentService(String name) {
		super("AlertsIntentService");
	}

	Logger log = Logger.getLogger(AlertsIntentService.class.getSimpleName());
	UserLocation userLocation;
	static Double userLocationLongitude;
	static Double userLocationLatitude;
	PowerManager powerManager;
	PowerManager.WakeLock wakeLock;

	@Override
	protected void onHandleIntent(Intent workIntent) {

		Utils.getInstance().configureLog4J();
		powerManager = (PowerManager) getApplicationContext().getSystemService(Context.POWER_SERVICE);
		wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getCanonicalName());
		wakeLock.acquire();

		log.info("Starting alerts schedule task.");
		Thread.currentThread().setName(getClass().getSimpleName());

		try {

			log.info("Successfully perfoemed alerts schedule task.");
		} catch (Throwable th) {
			log.error("Successfully perfoemed alerts schedule task.", th);
		}
		wakeLock.release();
	}
}