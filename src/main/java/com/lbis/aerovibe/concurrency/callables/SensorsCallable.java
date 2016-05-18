package com.lbis.aerovibe.concurrency.callables;

import org.apache.log4j.Logger;

import android.content.Context;

import com.lbis.aerovibe.concurrency.callables.model.DataSetCallableAbs;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.database.executors.model.SensorDbExecutors;
import com.lbis.server.actions.SensorActions;

public class SensorsCallable extends DataSetCallableAbs<Sensor, SensorDbExecutors> {

	public SensorsCallable(Context context, Logger log) {
		super(context, log);
		databaseImpl = new SensorDbExecutors();
	}

	public Boolean call() throws Exception {

		Thread.currentThread().setName(getClass().getSimpleName());

		if (context == null) {
			log.info("Activity is null will exit now.");
			return false;
		}

		try {
			listOfObjects = new SensorActions().getObjectsWithGet(context);
			log.info("Successfully got " + listOfObjects.size() + " sensors from web");
		} catch (Throwable th) {
			log.error("Failed to get sensors from web", th);
			return false;
		}
		return true;
	}

}
