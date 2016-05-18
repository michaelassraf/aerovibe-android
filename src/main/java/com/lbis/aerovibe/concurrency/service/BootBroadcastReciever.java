package com.lbis.aerovibe.concurrency.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lbis.mobile.utils.Utils;

public class BootBroadcastReciever extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Utils.getInstance().setAlarmIfNotSet(context);
	}
}
