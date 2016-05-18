package com.lbis.database.executors.model;

import com.lbis.aerovibe.model.UserMeasurement;
import com.lbis.database.executors.ExecuteMethods;

public class UserMeasurementDbExecutors extends ExecuteMethods<UserMeasurement> {

	@Override
	public Class<UserMeasurement> getHandledObjectType() {
		return UserMeasurement.class;
	}
}
