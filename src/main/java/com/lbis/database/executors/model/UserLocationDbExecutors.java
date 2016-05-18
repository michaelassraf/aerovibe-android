package com.lbis.database.executors.model;

import com.lbis.aerovibe.model.UserLocation;
import com.lbis.database.executors.ExecuteMethods;

public class UserLocationDbExecutors extends ExecuteMethods<UserLocation> {

	@Override
	public Class<UserLocation> getHandledObjectType() {
		return UserLocation.class;
	}
}