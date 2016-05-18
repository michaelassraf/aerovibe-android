package com.lbis.user.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lbis.aerovibe.main.Splash;
import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe.views.components.DialogUserFragmentBase;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.model.UserDbExecutors;
import com.lbis.mobile.utils.Utils;
import com.lbis.server.actions.UserActions;

public class UserLogInDialogFragment extends DialogUserFragmentBase {

	EditText userLoginEmail;
	EditText userLoginPassword;
	Button userLoginResetButton;
	Button userLoginCommitButton;
	View main;
	User user;

	static UserLogInDialogFragment userLogInDialogFragment;

	public static UserLogInDialogFragment getDialogInstance() {
		return new UserLogInDialogFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		setRetainInstance(true);
		main = inflater.inflate(R.layout.user_log_in_fragment, null);
		userLoginEmail = (EditText) main.findViewById(R.id.login_user_email);
		userLoginPassword = (EditText) main.findViewById(R.id.login_user_password);
		userLoginResetButton = (Button) main.findViewById(R.id.login_user_reset);
		userLoginCommitButton = (Button) main.findViewById(R.id.login_user_login);
		userLoginResetButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initializeAllViews();
			}
		});

		userLoginCommitButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				user.setUserEmail(userLoginEmail.getText().toString());
				user.setUserPassword(userLoginPassword.getText().toString());
//				String partialValidation = user.partialValidation(true);
//				if (partialValidation != null) {
//					Toast.makeText(getActivity(), (getDialog().getOwnerActivity()).show(partialValidation);
//					return;
//				}
				new ProcessUserLogInRequest(getDialog().getOwnerActivity(), user).execute();
			}
		});

		initializeAllViews();
		return main;
	}

	private void initializeAllViews() {
		userLoginEmail.setText("");
		userLoginPassword.setText("");
	}

	class ProcessUserLogInRequest extends AsyncTask<Void, Void, Void> {

		Activity activity;
		User returnedUser;
		User recievedUser;

		ProcessUserLogInRequest(Activity activity, User recievedUser) {
			this.activity = activity;
			this.recievedUser = recievedUser;

		}

		@Override
		protected void onPreExecute() {
			progressDialog = Utils.getInstance().showProgressDialog(activity);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... arg0) {
			try {
				returnedUser = new UserActions().logIn(recievedUser, activity);
				if (returnedUser.getUserEmail() != null && returnedUser.getUserId() == null) {
					log.info("Password is incorrect");
					return null;
				}
				new UserDbExecutors().put(activity, returnedUser);
			} catch (Throwable th) {
				log.error("Problem occur while trying to create an account", th);
				activity.runOnUiThread(new Runnable() {
					public void run() {
//						Toast.makeText(getActivity(), (activity).show("There is an issue with our server. Please try again later.");
					}
				});
				returnedUser = new User();
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			progressDialog.dismiss();
			if (returnedUser != null && returnedUser.getUserId() != null && returnedUser.getUserId().equals(-1L))
				return;
			if (returnedUser != null) {
				if (returnedUser.getUserId() == null) {
					activity.runOnUiThread(new Runnable() {
						public void run() {
//							Toast.makeText(getActivity(), (activity).show("Credintails are incorrect.\nPlease try again.");
						}
					});
					log.error("No User Id recivied");
					return;
				}
//				Toast.makeText(getActivity(), (activity).show(returnedUser.welcome());
				Thread thread = new Thread() {
					public void run() {
						Thread.currentThread().setName("App Launcher Thread from Log in");
						activity.finish();
						log.info("Got user object from server ! Lauching up !!");
						Intent myIntent = new Intent(activity, Splash.class);
						startActivity(myIntent);
						new UserDbExecutors().put(activity, returnedUser);
					}
				};
				thread.start();
			} else {
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(getActivity(), "We have a problem process your request.\nPlease try again later.", 1).show();
					}
				});
				log.error("Error occured while trying to get user from server. Returned object was null.");
			}
			super.onPostExecute(result);
		}
	}

}
