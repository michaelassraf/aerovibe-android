package com.lbis.aerovibe.concurrency.callables;

import org.apache.log4j.Logger;

import android.content.Context;

import com.lbis.aerovibe.concurrency.callables.model.DataSetCallableAbs;
import com.lbis.aerovibe.management.ExecuteManagementMethods;
import com.lbis.aerovibe.model.UserLocation;
import com.lbis.aerovibe.model.UserMeasurement;
import com.lbis.database.executors.model.UserMeasurementDbExecutors;
import com.lbis.server.actions.UserMeasurementActions;

public class UserMeasurementsCallable extends DataSetCallableAbs<UserMeasurement, UserMeasurementDbExecutors> {


	public UserMeasurementsCallable(Context context, Logger log) {
		super(context, log);
		databaseImpl = new UserMeasurementDbExecutors();
	}

	public Boolean call() throws Exception {
		Thread.currentThread().setName(getClass().getSimpleName());

		if (context == null) {
			log.info("Activity is null will exit now.");
			return false;
		}

		try {
			UserMeasurement userMeasurement = new UserMeasurement(new UserLocation(null, null, new ExecuteManagementMethods().getTokenAndUserId(context).getTokenUserId(), null));
			listOfObjects = new UserMeasurementActions().getObjectsForObject(userMeasurement, context);
			log.info("Successfully got " + listOfObjects.size() + " user measurements from web");
		} catch (Throwable th) {
			log.error("Failed to get user measurements from web", th);
			return false;
		}
		return true;
	}


}
