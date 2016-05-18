package com.lbis.server.actions;

import java.lang.reflect.Type;
import java.util.LinkedList;

import com.google.gson.reflect.TypeToken;
import com.lbis.aerovibe.enums.URLEnums.URLs;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.server.WebRequester;

public class SensorActions extends WebRequester<Sensor> {

	@Override
	public Class<Sensor> getClassType() {
		return Sensor.class;
	}

	@Override
	public URLs createNewRequestUrl() {
		return null;
	}

	@Override
	public URLs getObjectForObjectUrl() {
		return URLs.GetActiveSensors;
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
		return URLs.GetActiveSensors;
	}

	@Override
	protected Type getMultipleClassType() {
		return new TypeToken<LinkedList<Sensor>>() {
		}.getType();
	}
}
