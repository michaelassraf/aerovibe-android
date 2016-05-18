package com.lbis.aerovibe.fragments;

import org.apache.log4j.Logger;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lbis.aerovibe.aqi.calc.AQICalculator;
import com.lbis.aerovibe.enums.AQILevels;
import com.lbis.aerovibe.model.User;
import com.lbis.aerovibe.views.SeekArc;
import com.lbis.aerovibe.views.SeekArc.OnSeekArcChangeListener;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.model.UserDbExecutors;
import com.lbis.mobile.utils.Utils;

public class ProfileFragment extends SherlockFragment {

	RelativeLayout main;

	LinearLayout profleTitleLinearLayout;
	LinearLayout profileFragmentSeekArcLinearLayout;
	LinearLayout profileAQITextLevelLinearLayout;

	TextView profleAQITextLevelTextView;
	TextView profileLinearLayoutPorgressTextView;
	TextView profileLinearLayoutPorgressTitleTextView;
	TextView profleTitleDescriptionTextView;
	TextView profleTitleQuestionTextView;

	SeekArc profileLinearLayoutSeekArc;

	User loggedInUser;

	Logger log = Logger.getLogger(getClass().getSimpleName());

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		main = (RelativeLayout) inflater.inflate(R.layout.profile_fragment, null);

		getSherlockActivity().getSupportActionBar().setCustomView(null);
		getSherlockActivity().getSupportActionBar().setTitle("Profile");

		profleTitleLinearLayout = (LinearLayout) main.findViewById(R.id.profleTitleLinearLayout);
		profileFragmentSeekArcLinearLayout = (LinearLayout) main.findViewById(R.id.profileFragmentSeekArcLinearLayout);
		profileAQITextLevelLinearLayout = (LinearLayout) main.findViewById(R.id.profileAQITextLevelLinearLayout);

		profileLinearLayoutSeekArc = (SeekArc) main.findViewById(R.id.profileLinearLayoutSeekArc);

		profleAQITextLevelTextView = (TextView) main.findViewById(R.id.profleAQITextLevelTextView);
		profileLinearLayoutPorgressTextView = (TextView) main.findViewById(R.id.profileLinearLayoutPorgressTextView);
		profileLinearLayoutPorgressTitleTextView = (TextView) main.findViewById(R.id.profileLinearLayoutPorgressTitleTextView);
		profleTitleDescriptionTextView = (TextView) main.findViewById(R.id.profleTitleDescriptionTextView);
		profleTitleQuestionTextView = (TextView) main.findViewById(R.id.profleTitleQuestionTextView);

		profleAQITextLevelTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		profileLinearLayoutPorgressTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		profileLinearLayoutPorgressTitleTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		profleTitleDescriptionTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		profleTitleQuestionTextView.setTypeface(Utils.getInstance().getFont(getActivity()), Typeface.BOLD);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin = Utils.getInstance().getActionBarHeight((SherlockFragmentActivity) getActivity()) + 10;
		profleTitleLinearLayout.setLayoutParams(params);

		loggedInUser = new UserDbExecutors().getFirstObjectIfExists(getActivity());

		profileLinearLayoutSeekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {

			public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
				setProgressForSeekArc(progress);
			}

			public void onStartTrackingTouch(SeekArc seekArc) {
			}

			public void onStopTrackingTouch(SeekArc seekArc) {

			}

		});
		profileLinearLayoutSeekArc.setProgress(loggedInUser.getUserAQIProfileLevel());
		setProgressForSeekArc(loggedInUser.getUserAQIProfileLevel());
		profileLinearLayoutSeekArc.performClick();
		return main;
	}

	void setProgressForSeekArc(int progress) {
		loggedInUser.setUserAQIProfileLevel(progress);
		profileLinearLayoutPorgressTextView.setText(String.valueOf(progress));
		AQILevels aQILevel = AQICalculator.getInstance().getAQILevelForAQILevelNumber(progress);
		((GradientDrawable) profileAQITextLevelLinearLayout.getBackground()).setColor(Color.parseColor(aQILevel.getaQILevelsColor()));
		((GradientDrawable) profileAQITextLevelLinearLayout.getBackground()).setAlpha(50);
		profleAQITextLevelTextView.setText(aQILevel.getaQILevelsName());
	}

	@Override
	public void onPause() {
		new UserDbExecutors().put(getActivity(), loggedInUser);
		Utils.getInstance().updateUserFromDBOnServerAsync(getActivity(), log);
		super.onPause();
	}
}
