package com.lbis.aerovibe.fragments;

import org.apache.log4j.Logger;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe.views.SeekArc;
import com.lbis.aerovibe.views.SeekArc.OnSeekArcChangeListener;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.model.UserDbExecutors;
import com.lbis.mobile.utils.Utils;

public class AlertsFragment extends SherlockFragment {

	RelativeLayout main;
	LinearLayout alertsFragmentToggleLinearLayout;
	ToggleButton alertsToggleLinearLayoutToggleButton;
	SeekArc alertsLinearLayoutSeekArc;

	TextView alertsToggleLinearLayoutTitleTitleTextView;
	TextView alertsToggleLinearLayoutTitlePorgressTextView;
	TextView alertsLinearLayoutPorgressTextView;
	TextView alertsLinearLayoutPorgressTitleTextView;
	TextView alertsToggleLinearLayoutDescriptionTextView;
	TextView alertsToggleLinearLayoutQeustionTextView;
	TextView alertsToggleLinearLayoutNotifyPreviewTextView;

	LinearLayout alertsFragmentSeekArcLinearLayout;
	LinearLayout alertsToggleLinearLayoutNotifyPreviewLinearLayout;

	User loggedInUser;

	Logger log = Logger.getLogger(getClass().getSimpleName());

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		main = (RelativeLayout) inflater.inflate(R.layout.alerts_fragment, null);

		getSherlockActivity().getSupportActionBar().setCustomView(null);
		getSherlockActivity().getSupportActionBar().setTitle("Alerts - Coming soon...");

		alertsFragmentToggleLinearLayout = (LinearLayout) main.findViewById(R.id.alertsFragmentToggleLinearLayout);
		alertsToggleLinearLayoutToggleButton = (ToggleButton) main.findViewById(R.id.alertsToggleLinearLayoutToggleButton);
		alertsFragmentSeekArcLinearLayout = (LinearLayout) main.findViewById(R.id.alertsFragmentSeekArcLinearLayout);
		alertsToggleLinearLayoutNotifyPreviewLinearLayout = (LinearLayout) main.findViewById(R.id.alertsToggleLinearLayoutNotifyPreviewLinearLayout);
		alertsLinearLayoutSeekArc = (SeekArc) main.findViewById(R.id.alertsLinearLayoutSeekArc);

		alertsToggleLinearLayoutTitleTitleTextView = (TextView) main.findViewById(R.id.alertsToggleLinearLayoutTitleTitleTextView);
		alertsToggleLinearLayoutTitlePorgressTextView = (TextView) main.findViewById(R.id.alertsToggleLinearLayoutTitlePorgressTextView);
		alertsLinearLayoutPorgressTextView = (TextView) main.findViewById(R.id.alertsLinearLayoutPorgressTextView);
		alertsLinearLayoutPorgressTitleTextView = (TextView) main.findViewById(R.id.alertsLinearLayoutPorgressTitleTextView);
		alertsToggleLinearLayoutDescriptionTextView = (TextView) main.findViewById(R.id.alertsToggleLinearLayoutDescriptionTextView);
		alertsToggleLinearLayoutQeustionTextView = (TextView) main.findViewById(R.id.alertsToggleLinearLayoutQeustionTextView);
		alertsToggleLinearLayoutNotifyPreviewTextView = (TextView) main.findViewById(R.id.alertsToggleLinearLayoutNotifyPreviewTextView);

		alertsToggleLinearLayoutTitleTitleTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		alertsToggleLinearLayoutTitlePorgressTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		alertsLinearLayoutPorgressTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		alertsLinearLayoutPorgressTitleTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		alertsToggleLinearLayoutToggleButton.setTypeface(Utils.getInstance().getFont(getActivity()));
		alertsToggleLinearLayoutDescriptionTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		alertsToggleLinearLayoutQeustionTextView.setTypeface(Utils.getInstance().getFont(getActivity()), Typeface.BOLD);
		alertsToggleLinearLayoutNotifyPreviewTextView.setTypeface(Utils.getInstance().getFont(getActivity()));

		alertsToggleLinearLayoutToggleButton.setChecked(true);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin = Utils.getInstance().getActionBarHeight((SherlockFragmentActivity) getActivity()) + 10;
		alertsFragmentToggleLinearLayout.setLayoutParams(params);

		alertsToggleLinearLayoutNotifyPreviewLinearLayout.setVisibility(View.INVISIBLE);

		loggedInUser = new UserDbExecutors().getFirstObjectIfExists(getActivity());

		alertsLinearLayoutSeekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {

			public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
				setProgressForSeekArc(progress);
			}

			public void onStartTrackingTouch(SeekArc seekArc) {
			}

			public void onStopTrackingTouch(SeekArc seekArc) {
			}
		});

		alertsToggleLinearLayoutToggleButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				alertsFragmentSeekArcLinearLayout.setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
				alertsToggleLinearLayoutNotifyPreviewTextView.setText("Never");
				loggedInUser.setUserNoNotificationsADay(0);
			}
		});

		alertsLinearLayoutSeekArc.setProgress(loggedInUser.getUserNoNotificationsADay());
		setProgressForSeekArc(loggedInUser.getUserNoNotificationsADay());

		return main;
	}

	void setProgressForSeekArc(int progress) {
		alertsLinearLayoutPorgressTextView.setText(String.valueOf(progress));
		if (progress <= 0) {
			alertsToggleLinearLayoutNotifyPreviewLinearLayout.setVisibility(View.INVISIBLE);
			return;
		}
		loggedInUser.setUserNoNotificationsADay(progress);
		alertsToggleLinearLayoutNotifyPreviewLinearLayout.setVisibility(View.VISIBLE);
		alertsToggleLinearLayoutNotifyPreviewTextView.setText("We will check the air every " + Utils.getInstance().convertMinutesToHour(86400 / progress) + " minutes.");
	}

	@Override
	public void onPause() {
		new UserDbExecutors().put(getActivity(), loggedInUser);
		Utils.getInstance().updateUserFromDBOnServerAsync(getActivity(), log);
		super.onPause();
	}
}
