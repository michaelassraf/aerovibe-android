package com.lbis.server.actions;

import java.lang.reflect.Type;
import java.util.LinkedList;

import com.google.gson.reflect.TypeToken;
import com.lbis.aerovibe.enums.URLEnums.URLs;
import com.lbis.aerovibe.model.UserLocation;
import com.lbis.server.WebRequester;

public class UserLocationActions extends WebRequester<UserLocation> {

	@Override
	public Class<UserLocation> getClassType() {
		return UserLocation.class;
	}

	@Override
	public URLs createNewRequestUrl() {
		return URLs.AddNewUserLocation;
	}

	@Override
	public URLs getObjectForObjectUrl() {
		return null;
	}

	public URLs getLoginUrl() {
		return null;
	}

	@Override
	public URLs getAllObjectsSinceUrl() {
		return null;
	}

	@Override
	public String getClassName() {
		return "User";
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
		return null;
	}

	@Override
	protected Type getMultipleClassType() {
		return new TypeToken<LinkedList<UserLocation>>() {
		}.getType();
	}
}
