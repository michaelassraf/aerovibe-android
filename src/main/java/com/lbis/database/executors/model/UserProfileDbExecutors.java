package com.lbis.database.executors.model;

import com.lbis.aerovibe.model.UserProfile;
import com.lbis.database.executors.ExecuteMethods;

public class UserProfileDbExecutors extends ExecuteMethods<UserProfile> {

	@Override
	public Class<UserProfile> getHandledObjectType() {
		return UserProfile.class;
	}
}