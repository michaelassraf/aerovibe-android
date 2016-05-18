package com.lbis.database.executors.model;

import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.database.executors.ExecuteMethods;

public class SensorMeasurementDbExecutors extends ExecuteMethods<SensorMeasurement> {

	@Override
	public Class<SensorMeasurement> getHandledObjectType() {
		return SensorMeasurement.class;
	}
}
