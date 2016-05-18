package com.lbis.server.actions;

import java.lang.reflect.Type;
import java.util.LinkedList;

import com.google.gson.reflect.TypeToken;
import com.lbis.aerovibe.enums.URLEnums.URLs;
import com.lbis.aerovibe.model.UserMeasurement;
import com.lbis.server.WebRequester;

public class UserMeasurementActions extends WebRequester<UserMeasurement> {

	@Override
	public Class<UserMeasurement> getClassType() {
		return UserMeasurement.class;
	}

	@Override
	public URLs createNewRequestUrl() {
		return null;
	}

	@Override
	public URLs getObjectForObjectUrl() {
		return URLs.GetLatestMeasurements;
	}

	@Override
	public URLs getAllObjectsSinceUrl() {
		return null;
	}

	@Override
	public String getClassName() {
		return "User Measurement";
	}

	@Override
	public URLs getSearchObjectsUrl() {
		return null;
	}

	@Override
	public String getInitializeSincePrefix() {
		return "";
	}

	@Override
	public String getPullSincePrefix() {
		return "";
	}

	@Override
	public URLs getObjectsForObjectUrl() {
		return URLs.GetLastWeek;
	}

	@Override
	protected Type getMultipleClassType() {
		return new TypeToken<LinkedList<UserMeasurement>>() {
		}.getType();
	}
}
