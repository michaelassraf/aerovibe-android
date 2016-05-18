package com.lbis.aerovibe.fragments;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lbis.aerovibe.concurrency.DataScheduleTaskRefresher;
import com.lbis.aerovibe.enums.PollutantsEnums;
import com.lbis.aerovibe.model.SensorMeasurementValue;
import com.lbis.aerovibe.model.UserMeasurement;
import com.lbis.aerovibe.views.SingleChartLinearLayout;
import com.lbis.aerovibe_android.R;
import com.lbis.mobile.utils.Utils;

public class YouFragment extends SherlockFragment {
	View main;
	LinearLayout youFragmentLinearLayout;
	View youFragmentSpacerView;
	LinkedList<UserMeasurement> userMeasurements = new LinkedList<UserMeasurement>();
	HashMap<PollutantsEnums, LinkedList<SensorMeasurementValue>> userMeasurementsChunked = new HashMap<PollutantsEnums, LinkedList<SensorMeasurementValue>>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		getSherlockActivity().getSupportActionBar().setCustomView(null);
		getSherlockActivity().getSupportActionBar().setTitle("You - Coming soon...");
		main = inflater.inflate(R.layout.you_fragment, container, false);
		youFragmentSpacerView = new View(getActivity());
		youFragmentSpacerView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, Utils.getInstance().getActionBarHeight((SherlockFragmentActivity) getActivity())));
		youFragmentLinearLayout = (LinearLayout) main.findViewById(R.id.youFragmentLinearLayout);
		youFragmentLinearLayout.addView(youFragmentSpacerView);

		userMeasurementsChunked = new HashMap<PollutantsEnums, LinkedList<SensorMeasurementValue>>();
		userMeasurementsChunked.putAll(DataScheduleTaskRefresher.getInstance(getActivity()).getFreshUserMeasurementsChunkedData());

		userMeasurements = new LinkedList<UserMeasurement>();
		userMeasurements.addAll(DataScheduleTaskRefresher.getInstance(getActivity()).getFreshUserMeasurementsData());

		for (Entry<PollutantsEnums, LinkedList<SensorMeasurementValue>> entry : userMeasurementsChunked.entrySet()) {
			youFragmentLinearLayout.addView(new SingleChartLinearLayout(getActivity(), entry.getValue(), "Chart for " + entry.getKey().getPollutantsEnumUnitsTitle()));
		}

		return main;
	}
}
