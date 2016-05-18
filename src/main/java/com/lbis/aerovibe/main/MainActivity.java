package com.lbis.aerovibe.main;

import java.io.File;

import org.apache.log4j.Logger;

import android.graphics.LightingColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lbis.aerovibe.fragments.AlertsFragment;
import com.lbis.aerovibe.fragments.NearYouFragment;
import com.lbis.aerovibe.fragments.OldNearYouFragment;
import com.lbis.aerovibe.fragments.ProfileFragment;
import com.lbis.aerovibe.fragments.TheWorldFragment;
import com.lbis.aerovibe.fragments.YouFragment;
import com.lbis.aerovibe.image.AerovibeImageLoaderConfiguration;
import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe.views.adapters.MainMenuListAdapter;
import com.lbis.aerovibe.views.components.CircleTransform;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.model.UserDbExecutors;
import com.lbis.mobile.utils.Utils;
import com.squareup.picasso.Picasso;

public class MainActivity extends SherlockFragmentActivity implements OnItemClickListener {

	ListView mainMenuListView;
	DrawerLayout mainDrawerLayout;
	View mainMenuHeaderView;
	MainMenuListAdapter mainMenuListViewAdapter;
	ImageView mainMenuHeaderViewProfileImageView;
	ImageView mainContentBackgroundImageView;
	View imageSpacerView;
	View mainMenuHeaderViewTextView;
	int mainScreenWidth;
	int mainScreenHeight;
	Logger log = Logger.getLogger(getClass().getSimpleName());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Utils.getInstance().designActionBar(this);
		setContentView(R.layout.main_activity);

		mainMenuListView = (ListView) findViewById(R.id.mainMenuListView);
		mainDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
		mainContentBackgroundImageView = (ImageView) findViewById(R.id.mainContentBackgroundImageView);

		mainMenuHeaderView = getLayoutInflater().inflate(R.layout.main_menu_header_view, null);
		mainMenuHeaderViewProfileImageView = (ImageView) mainMenuHeaderView.findViewById(R.id.mainMenuHeaderViewProfileImageView);
		mainContentBackgroundImageView.setImageURI(Uri.parse(Environment.getExternalStorageDirectory() + File.separator + Splash.BLURRED_FILE_NAME));
		mainContentBackgroundImageView.setColorFilter(new LightingColorFilter( 0xFFFFFFFF, 0x000000FF ) );

		mainScreenHeight = Utils.getInstance().getScreenSizes(this)[1];
		mainScreenWidth = Utils.getInstance().getScreenSizes(this)[0];

		mainMenuHeaderView.setLayoutParams(new AbsListView.LayoutParams(mainScreenWidth > mainScreenHeight ? mainScreenWidth / 2 : mainScreenHeight / 2, AbsListView.LayoutParams.MATCH_PARENT));

		AerovibeImageLoaderConfiguration.getInstance(getApplicationContext());

		AnimationDrawable loadingAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.loading);

		int pictureHeight = mainScreenHeight / 6;
		int pictureWidth = mainScreenWidth / 4;
		if (mainScreenHeight <= mainScreenWidth) {
			pictureHeight = mainScreenHeight / 4;
			pictureWidth = mainScreenWidth / 6;
		}

		mainMenuHeaderViewProfileImageView.setLayoutParams(new LinearLayout.LayoutParams(pictureWidth, pictureHeight));

		User user = new UserDbExecutors().getFirstObjectIfExists(getApplicationContext());

		Picasso.with(this).load(user.getUserPicture()).centerCrop().resize(pictureWidth, pictureHeight).placeholder(loadingAnimation).transform(new CircleTransform()).placeholder(loadingAnimation).into(mainMenuHeaderViewProfileImageView);

		imageSpacerView = new View(this);
		imageSpacerView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, Utils.getInstance().getActionBarHeight(this)));

		mainMenuListView.addHeaderView(imageSpacerView);
		mainMenuListView.addHeaderView(mainMenuHeaderView);

		mainMenuListViewAdapter = new MainMenuListAdapter(this, R.layout.main_manu_list_item, Utils.getInstance().getAllMenuNames());
		mainMenuListView.setAdapter(mainMenuListViewAdapter);

		((TextView) mainMenuHeaderView.findViewById(R.id.mainMenuHeaderViewProfileTextView)).setText(user.getUserFirstName() + " " + user.getUserLastName());
		((TextView) mainMenuHeaderView.findViewById(R.id.mainMenuHeaderViewProfileTextView)).setTypeface(Utils.getInstance().getFont(getApplicationContext()));
		mainMenuListView.setOnItemClickListener(this);
		if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
			onItemClick(null, null, 3, 0L);
		}
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		SherlockFragment destinationFragment = null;

		switch (arg2) {
		case 0:
			mainDrawerLayout.closeDrawer(mainMenuListView);
			return;
		case 1:
			mainDrawerLayout.closeDrawer(mainMenuListView);
			return;
		case 2:
			destinationFragment = new YouFragment();
			break;
		case 3:
			destinationFragment = new NearYouFragment();
			break;
		case 4:
			destinationFragment = new TheWorldFragment();
			break;
		case 5:
			destinationFragment = new ProfileFragment();
			break;
		case 6:
			destinationFragment = new AlertsFragment();
			break;
		}

		if (destinationFragment == null) {
			destinationFragment = new OldNearYouFragment();
		}

		Utils.getInstance().replaceFragment(getSupportFragmentManager(), destinationFragment, R.id.main_content);
		mainMenuListView.setItemChecked(arg2, true);
		setTitle(mainMenuListView.getItemAtPosition(arg2).toString());
		mainDrawerLayout.closeDrawer(mainMenuListView);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == android.R.id.home) {

			if (mainDrawerLayout.isDrawerOpen(mainMenuListView)) {
				mainDrawerLayout.closeDrawer(mainMenuListView);
			} else {
				mainDrawerLayout.openDrawer(mainMenuListView);
			}
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() > 1)
			getSupportFragmentManager().popBackStack();
		else
			moveTaskToBack(true);
	}

}
