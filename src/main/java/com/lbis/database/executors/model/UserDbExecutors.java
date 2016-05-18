package com.lbis.database.executors.model;

import com.lbis.aerovibe.model.User;
import com.lbis.database.executors.ExecuteMethods;

public class UserDbExecutors extends ExecuteMethods<User> {

	@Override
	public Class<User> getHandledObjectType() {
		return User.class;
	}

}
