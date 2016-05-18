package com.lbis.aerovibe.concurrency.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ScheduleTasksReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent userLocationIntentServiceIntent = new Intent(context, UserLocationIntentService.class);
		Intent alertsIntentServiceIntent = new Intent(context, AlertsIntentService.class);
		context.startService(userLocationIntentServiceIntent);
		context.startService(alertsIntentServiceIntent);
	}

}
