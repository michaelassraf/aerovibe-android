package com.lbis.aerovibe.concurrency.callables;

import org.apache.log4j.Logger;

import android.content.Context;

import com.lbis.aerovibe.concurrency.callables.model.DataSetCallableAbs;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.database.executors.model.SensorMeasurementDbExecutors;
import com.lbis.server.actions.SensorMeasurementActions;

public class SensorMeasurementsCallable extends DataSetCallableAbs<SensorMeasurement, SensorMeasurementDbExecutors> {

	public SensorMeasurementsCallable(Context context, Logger log) {
		super(context, log);
		databaseImpl = new SensorMeasurementDbExecutors();
	}

	public Boolean call() throws Exception {
		Thread.currentThread().setName(getClass().getSimpleName());

		if (context == null) {
			log.info("Activity is null will exit now.");
			return false;
		}

		try {
			listOfObjects = new SensorMeasurementActions().getObjectsWithGet(context);
			log.info("Successfully got " + listOfObjects.size() + " sensors measurements from web");
		} catch (Throwable th) {
			log.error("Failed to get sensors measurements from web", th);
			return false;
		}
		return true;

	}

}
