package com.lbis.aerovibe.views.components;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe_android.R;
import com.lbis.mobile.utils.Utils;

public class DialogUserFragmentBase extends DialogFragment {

	protected ProgressDialog progressDialog;
	protected Logger log = Logger.getLogger(DialogUserFragmentBase.class.getSimpleName());

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progressDialog = new ProgressDialog(getActivity());
		setStyle(DialogFragment.STYLE_NO_TITLE, R.style.Theme_Fragmanet_Dialog);
		setCancelable(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		getDialog().getWindow().setWindowAnimations(R.style.dialog_fragment_animation_fade);
		int screenWidth = Utils.getInstance().getScreenSizes(getActivity())[0];
		int screenHeight = Utils.getInstance().getScreenSizes(getActivity())[1];
		getDialog().getWindow().setLayout(screenWidth - (screenWidth / 4), screenHeight - (screenHeight / 3));
		getDialog().setCanceledOnTouchOutside(true);
	}

	protected String validateUser(User user, boolean isLogIn) {

		if (!isLogIn) {
			if (user.getUserFirstName() == null) {
				return "Please fill first name";
			}

			if (user.getUserLastName() == null) {
				return "Please fill last name";
			}

			if (user.getUserBirthday() == null) {
				return "Please fill birth day";
			}

			if (user.getUserSex() == null) {
				return "Please choose geneder";
			}

			if (user.getUserPassword() == null) {
				return "Please fill password";
			}
		}
		
		if (!validate(user.getUserPassword(), User.passwordRegEx)) {
			return isLogIn ? "Password is incorrect" : "Password must contain number, lower and upper case letters";
		}

		if (!validate(user.getUserEmail(), User.emailRegEx)) {
			return "Email is not valid";
		}

		return null;
	}

	public boolean validate(String value, String regEx) {
		Pattern pattern = Pattern.compile(regEx);
		Matcher matcher = pattern.matcher(value);
		return matcher.matches();

	}
}
