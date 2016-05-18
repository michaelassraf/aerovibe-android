package com.lbis.aerovibe.main;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.internal.nineoldandroids.animation.ObjectAnimator;
import com.actionbarsherlock.view.Window;
import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.model.GraphUser;
import com.lbis.aerovibe.concurrency.DataScheduleTaskRefresher;
import com.lbis.aerovibe.enums.UserEnum;
import com.lbis.aerovibe.enums.UserEnum.UserType;
import com.lbis.aerovibe.management.ExecuteManagementMethods;
import com.lbis.aerovibe.management.SQLiteManagementClient;
import com.lbis.aerovibe.model.Token;
import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.SQLiteClient;
import com.lbis.database.executors.model.SensorDbExecutors;
import com.lbis.database.executors.model.UserDbExecutors;
import com.lbis.mobile.utils.Utils;
import com.lbis.server.actions.UserActions;
import com.lbis.user.fragments.UserLogInDialogFragment;
import com.lbis.user.fragments.UserSignUpDialogFragment;

public class Splash extends SherlockFragmentActivity {

	View main;
	FrameLayout spalshFrameLayout;
	ImageView splashBackgroundImageView;
	TextView splashAerovibeLogoTextView;
	ImageView splashLogoImgeView;
	Animation splashAerovibeLogoTextViewAnimation;
	Button splashFacebookLoginButton;
	Button splashNativeSignUpButton;
	Button splashNativeLogInButton;
	LinearLayout splashButtonsLinearLayout;

	Random random = new Random();
	Logger log = Logger.getLogger(getClass().getSimpleName());
	Bitmap splashCloudWithShadowBitmap;
	int splashScreenHeight;
	int splashScreenWidth;

	String FACEBOOK_EMAIL_PROPERTY = "email";
	String FACEBOOK_BIRTHDAY_PROPERTY = "user_birthday";
	String FACEBOOK_GENDER_PROPERTY = "gender";
	Session session;
	User activeUser;
	ExecutorService splashCloudsExecutorService;
	ExecutorService imagesExecutorService;
	ProgressDialog progressDialog;
	public static String blurredArtPath;

	public static final String BLURRED_FILE_NAME = "blurredArtPath.jpg";
	public static final String POPPED_ART_FILE_NAME = "poppedArtPath.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		try {
			PackageInfo info = getPackageManager().getPackageInfo("com.lbis.aerovibe_android", PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				Log.e("KeyHash:" , Base64.encodeToString(md.digest(), Base64.DEFAULT));
			}
			
			Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
			Debug.getMemoryInfo(memoryInfo);

			String memMessage = String.format(
			    "Memory: Pss=%.2f MB, Private=%.2f MB, Shared=%.2f MB",
			    memoryInfo.getTotalPss() / 1024.0,
			    memoryInfo.getTotalPrivateDirty() / 1024.0,
			    memoryInfo.getTotalSharedDirty() / 1024.0);
			Log.e("------- ", memMessage);
		} catch (Throwable th) {
			log.error("ERROR  !!!", th);
		}
		
		initializeApp();

		main = (FrameLayout) getLayoutInflater().inflate(R.layout.splash, null);
		spalshFrameLayout = (FrameLayout) main.findViewById(R.id.spalshFrameLayout);
		splashBackgroundImageView = (ImageView) main.findViewById(R.id.splashBackgroundImageView);
		splashButtonsLinearLayout = (LinearLayout) main.findViewById(R.id.splashButtonsLinearLayout);
		splashFacebookLoginButton = (Button) main.findViewById(R.id.splashFacebookLoginButton);
		splashNativeSignUpButton = (Button) main.findViewById(R.id.splashNativeSignUpButton);
		splashNativeLogInButton = (Button) main.findViewById(R.id.splashNativeLogInButton);

		splashFacebookLoginButton.setVisibility(View.INVISIBLE);
		splashNativeSignUpButton.setVisibility(View.INVISIBLE);
		splashNativeLogInButton.setVisibility(View.INVISIBLE);

		splashCloudsExecutorService = Executors.newFixedThreadPool(40);
		splashCloudWithShadowBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cloud_w_shadow);
		splashScreenHeight = Utils.getInstance().getScreenSizes(getApplicationContext())[1];
		splashScreenWidth = Utils.getInstance().getScreenSizes(getApplicationContext())[0];

		splashLogoImgeView = new ImageView(this);
		splashLogoImgeView.setImageResource(R.drawable.ic_launcher);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.CENTER;
		splashLogoImgeView.setLayoutParams(params);
		spalshFrameLayout.addView(splashLogoImgeView);

		FrameLayout.LayoutParams paramsLogoText = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		paramsLogoText.gravity = Gravity.TOP | Gravity.CENTER;
		paramsLogoText.topMargin = splashScreenHeight / 4;

		splashAerovibeLogoTextViewAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_around_center_point);

		splashAerovibeLogoTextView = new TextView(this);
		splashAerovibeLogoTextView.setText("Aerovibe");
		splashAerovibeLogoTextView.setTypeface(Utils.getInstance().getFont(this));
		splashAerovibeLogoTextView.setTextSize(splashScreenHeight / 20);
		splashAerovibeLogoTextView.setLayoutParams(paramsLogoText);

		spalshFrameLayout.addView(splashAerovibeLogoTextView);
		splashAerovibeLogoTextView.startAnimation(splashAerovibeLogoTextViewAnimation);

		imagesExecutorService = Executors.newFixedThreadPool(1);
		imagesExecutorService.execute(new Runnable() {

			public void run() {
				try {
					Bitmap poppedArtBitmap = Utils.getInstance().setImagePopArtGradientEffect(getApplicationContext(), Utils.getInstance().getRandomSplashImage());
					final String poppedArtPath = Utils.getInstance().writeBitmapToDisk(poppedArtBitmap, POPPED_ART_FILE_NAME);
					runOnUiThread(new Runnable() {
						public void run() {
							splashBackgroundImageView.setImageURI(Uri.parse(poppedArtPath));
						}
					});
					Bitmap bluredBitmap = Utils.getInstance().setImageBlurEffect(getApplicationContext(), poppedArtBitmap, random.nextInt(24));
					Utils.getInstance().writeBitmapToDisk(bluredBitmap, BLURRED_FILE_NAME);
				} catch (Throwable th) {
					log.error("Failed to create images in splash screen.", th);
				}
			}
		});

		Token token = new ExecuteManagementMethods().getTokenAndUserId(getApplicationContext());
		if (token == null || token.getTokenUserId() == null || token.getTokenUserId() == null) {
			showUserButtons();
			setContentView(main);
			return;
		}

		splashButtonsLinearLayout.setVisibility(View.INVISIBLE);

		new Thread(new Runnable() {
			public void run() {
				if (isNetworkAvailable())
					DataScheduleTaskRefresher.getInstance(getApplicationContext()).refreshAllLists();
				else
					Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
				if (getApplication() != null) {
					runOnUiThread(new Runnable() {
						public void run() {
							launchMain(getApplicationContext());
						}
					});
				}
			}
		}).start();

		setContentView(main);

	}

	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	void launchMain(Context context) {
		Intent mainActivityIntent = new Intent(context, MainActivity.class);
		startActivity(mainActivityIntent);
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	SherlockFragmentActivity sherlockFragmentActivity = this;

	void showUserButtons() {

		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, splashScreenHeight / 3);
		params.gravity = Gravity.BOTTOM | Gravity.CENTER;
		splashButtonsLinearLayout.setLayoutParams(params);

		splashFacebookLoginButton.setVisibility(View.VISIBLE);
		splashFacebookLoginButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				signUpWithFacebook(sherlockFragmentActivity);
			}
		});

		splashNativeSignUpButton.setVisibility(View.VISIBLE);
		splashNativeSignUpButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				signUp(sherlockFragmentActivity);
			}
		});

		splashNativeLogInButton.setVisibility(View.VISIBLE);
		splashNativeLogInButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				logIn(sherlockFragmentActivity);
			}
		});
	}

	private void signUp(SherlockFragmentActivity sherlockFragmentActivity) {
		UserSignUpDialogFragment userSignUpDialogFragment = UserSignUpDialogFragment.getDialogInstance();
		Utils.getInstance().showDialog(sherlockFragmentActivity, null, userSignUpDialogFragment.getClass().getCanonicalName(), userSignUpDialogFragment);
	}

	private void logIn(SherlockFragmentActivity sherlockFragmentActivity) {
		UserLogInDialogFragment userLogInDialogFragment = UserLogInDialogFragment.getDialogInstance();
		Utils.getInstance().showDialog(sherlockFragmentActivity, null, userLogInDialogFragment.getClass().getCanonicalName(), userLogInDialogFragment);
	}

	void startCloudsAnimation() {
		for (int i = 0; i < 40; i++) {
			final int finalInt = i;
			splashCloudsExecutorService.execute(new Thread() {
				public void run() {

					final ImageView currentImageView = new ImageView(getApplicationContext());
					final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
					if (finalInt % 2 == 0) {
						params.topMargin = random.nextInt(splashScreenHeight);
						params.gravity = Gravity.TOP | Gravity.LEFT;
					} else {
						params.bottomMargin = random.nextInt(splashScreenHeight);
						params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
					}
					int width = random.nextInt((int) (splashScreenWidth / 4));
					int height = random.nextInt((int) (splashScreenHeight / 4));
					height = height <= 0 ? 20 : height;
					width = width <= 0 ? 20 : width;
					params.height = height;
					params.width = width;
					currentImageView.setAlpha((float) (random.nextFloat() * 0.5));
					currentImageView.setImageBitmap(splashCloudWithShadowBitmap);
					final ObjectAnimator imageAnimation;
					int margin = random.nextInt(splashScreenWidth);
					if (finalInt % 2 == 0) {
						imageAnimation = ObjectAnimator.ofFloat(currentImageView, "x", -params.leftMargin, splashScreenWidth, -margin, splashScreenWidth, -margin);
					} else {
						imageAnimation = ObjectAnimator.ofFloat(currentImageView, "x", splashScreenWidth, -margin, splashScreenWidth, -margin, splashScreenWidth);
					}
					imageAnimation.setRepeatCount(Animation.INFINITE);
					if (finalInt % 3 == 0) {
						imageAnimation.setDuration(10000);
					}
					if (finalInt % 3 == 1) {
						imageAnimation.setDuration(15000);
					} else {
						imageAnimation.setDuration(20000);
					}
					try {
						sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (getApplicationContext() != null) {
						runOnUiThread(new Runnable() {
							public void run() {
								imageAnimation.start();
								spalshFrameLayout.addView(currentImageView, params);
							}
						});
					}

				};
			});

		}
		splashCloudsExecutorService.shutdown();
	}

	private void loadDummyData(Context context) {
		log.info("Going to insert some dummy info to all tables.");

		if (Utils.getInstance().loadDummySensorsData(context)) {
			log.info("Successfully loaded dummy sensors data");
		} else
			log.info("Failed to load dummy sensors data");

		if (Utils.getInstance().loadDummySensorMeasurementsData(context)) {
			log.info("Successfully loaded dummy sensor measurements data");
		} else
			log.info("Failed to load dummy sensor measurements data");

	}

	void initializeApp() {
		Utils.getInstance().configureLog4J();
		SQLiteClient.getConnetion(getApplicationContext());
		SQLiteManagementClient.getConnetion(getApplicationContext());
		if (new SensorDbExecutors().getFirstObjectIfExists(getApplicationContext()) == null) {
			loadDummyData(getApplicationContext());
		}
		Utils.getInstance().setAlarmIfNotSet(this);
	}

	void signUpWithFacebook(final Activity activity) {
		session = new Session(activity.getApplicationContext());
		Session.OpenRequest openRequest = new Session.OpenRequest(activity);
		List<String> permissions = new ArrayList<String>();

		permissions.add(FACEBOOK_EMAIL_PROPERTY);
		permissions.add(FACEBOOK_BIRTHDAY_PROPERTY);
		openRequest.setPermissions(permissions);
		session.openForRead(openRequest);
		session.addCallback(new Session.StatusCallback() {

			public void call(Session session, SessionState sessionState, Exception ex) {
				progressDialog = Utils.getInstance().showProgressDialog(activity);
				if (session != null && session.isOpened()) {
					Request.newMeRequest(session, new Request.GraphUserCallback() {
						public void onCompleted(GraphUser user, Response response) {
							FacebookRequestError error = response.getError();
							if (error != null) {
								log.error("Error while trying to connect facebook - " + error.toString());
								finish();
							}
							if (user != null) {

								log.info("Data retrieved from Facebook " + user.toString());

								String genderFB = user.getProperty(FACEBOOK_GENDER_PROPERTY).toString();

								UserEnum.Sex enumGender = UserEnum.Sex.Male;

								if (genderFB.startsWith("f"))
									enumGender = UserEnum.Sex.Female;

								activeUser = new User();
								activeUser.setUserPicture(Utils.getInstance().getFacebookProfileURL(user.getId()));
								activeUser.setUserBirthday(Utils.getInstance().parseFacbookDate(user.getBirthday()));
								activeUser.setUserSex(enumGender);
								activeUser.setUserEmail(user.getProperty(FACEBOOK_EMAIL_PROPERTY).toString());
								activeUser.setUserPassword(user.getId().toString());
								activeUser.setUserJoinDate(System.currentTimeMillis());
								activeUser.setUserLastName(user.getLastName());
								activeUser.setUserFirstName(user.getFirstName());
								activeUser.setUserType(UserType.Facebook);

								log.info("User data parsed from Facebook " + activeUser.toString());
								new SignUpUser().execute(activeUser);
								if (progressDialog != null)
									progressDialog.dismiss();
							}
						}

					}).executeAsync();
				} else {
					if (progressDialog != null) {
						progressDialog.dismiss();
					}

				}
			}
		});
	}

	class SignUpUser extends AsyncTask<User, Void, User> {

		@Override
		protected void onPreExecute() {
			progressDialog = Utils.getInstance().showProgressDialog(sherlockFragmentActivity);
			super.onPreExecute();
		}

		@Override
		protected User doInBackground(User... user) {
			User signedUpUser = null;
			try {
				signedUpUser = new UserActions().signUp(user[0], getApplicationContext());
			} catch (Throwable th) {
				log.error("Can't get user for sign up from server", th);
			}
			return signedUpUser;
		}

		@Override
		protected void onPostExecute(User user) {
			if (user != null) {
				new UserDbExecutors().put(getApplicationContext(), user);
				DataScheduleTaskRefresher.getInstance(getApplicationContext()).refreshAllLists();
				launchMain(getApplicationContext());
			}
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (session != null) {
			session.onActivityResult(this, requestCode, resultCode, data);
		}
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}
}
