package com.lbis.database.executors.model;

import com.lbis.aerovibe.model.Sensor;
import com.lbis.database.executors.ExecuteMethods;

public class SensorDbExecutors extends ExecuteMethods<Sensor> {

	@Override
	public Class<Sensor> getHandledObjectType() {
		return Sensor.class;
	}
}