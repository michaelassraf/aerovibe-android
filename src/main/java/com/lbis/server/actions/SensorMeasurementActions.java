package com.lbis.server.actions;

import java.lang.reflect.Type;
import java.util.LinkedList;

import com.google.gson.reflect.TypeToken;
import com.lbis.aerovibe.enums.URLEnums.URLs;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.server.WebRequester;

public class SensorMeasurementActions extends WebRequester<SensorMeasurement> {

	@Override
	public Class<SensorMeasurement> getClassType() {
		return SensorMeasurement.class;
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
		return "Sensor Result Pair";
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
		return URLs.GetLatestMeasurements;
	}

	@Override
	protected Type getMultipleClassType() {
		return new TypeToken<LinkedList<SensorMeasurement>>() {
		}.getType();
	}
}
