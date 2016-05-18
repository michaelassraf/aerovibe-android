package com.lbis.server.actions;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.LinkedList;

import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;

import com.google.gson.reflect.TypeToken;
import com.lbis.aerovibe.enums.URLEnums.URLs;
import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.database.executors.model.UserDbExecutors;
import com.lbis.server.WebRequester;

public class UserActions extends WebRequester<User> {

	@Override
	public Class<User> getClassType() {
		return User.class;
	}

	public User logIn(User user, Context context) throws UnsupportedEncodingException, Exception {
		return getObjectForObject(user, context, getLoginUrl());
	}

	public User signUp(User user, Context context) throws UnsupportedEncodingException, Exception {
		return createNewRequest(user, context);
	}

	public User getUserDetails(String userId, DefaultHttpClient connection, Context context) throws UnsupportedEncodingException, Exception {
		User user = new UserDbExecutors().get(userId, context);
		if (user != null && AerovibeUtils.getInstance().checkIfIsValidUser(user))
			return user;
		user = getObjectForObject(new User(userId), context);
		new UserDbExecutors().put(context, user);
		return user;
	}

	@Override
	public URLs createNewRequestUrl() {
		return URLs.NativeSignup;
	}

	@Override
	public URLs getObjectForObjectUrl() {
		return URLs.UpdateUser;
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
		return new TypeToken<LinkedList<User>>() {
		}.getType();
	}
}
