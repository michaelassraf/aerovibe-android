package com.lbis.aerovibe.fragments;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lbis.aerovibe.aqi.calc.AQICalculator;
import com.lbis.aerovibe.concurrency.DataScheduleTaskRefresher;
import com.lbis.aerovibe.location.LocationRetriever;
import com.lbis.aerovibe.location.LocationRetriever.LocationResult;
import com.lbis.aerovibe.management.ExecuteManagementMethods;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.model.UserLocation;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.views.AnimatedListView;
import com.lbis.aerovibe.views.AnimatedListViewObjectMapper;
import com.lbis.aerovibe.views.SensorView;
import com.lbis.aerovibe.views.adapters.AnimatedListViewAdapter;
import com.lbis.aerovibe.views.components.BlurTransform;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.model.UserLocationDbExecutors;
import com.lbis.mobile.utils.Utils;
import com.squareup.picasso.Picasso;

public class OldNearYouFragment extends SherlockFragment {

	View main;
	View listViewSpacerView;

	int screenWidth;
	int screenHeight;

	int pictureWidth;
	int pictureHeight;

	AnimatedListView animatedListView;
	AnimatedListViewAdapter sensorsListAdapter;
	BlurTransform blurTransform ;

	FrameLayout.LayoutParams params;
	volatile HashMap<String, SensorMeasurement> sensorMeasurements;
	volatile List<Sensor> sensors;
	ExecutorService executorService;

	SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM", Locale.US);
	SimpleDateFormat simpleDateFormatHour = new SimpleDateFormat("HH:mm", Locale.US);

	Logger log = Logger.getLogger(getClass().getSimpleName());

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		getSherlockActivity().getSupportActionBar().setCustomView(null);
		getSherlockActivity().getSupportActionBar().setTitle("Near You");

		executorService = Executors.newFixedThreadPool(20);

		sensorMeasurements = new HashMap<String, SensorMeasurement>();
		sensors = new LinkedList<Sensor>();

		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				new UserLocationDbExecutors().put(getActivity(), new UserLocation(location.getLatitude(), location.getLongitude(), new ExecuteManagementMethods().getTokenAndUserId(getActivity()).getTokenUserId(), null));
				sensors = sortSensorsByDistance(getActivity(), sensors, sensorsListAdapter);
				sensorsListAdapter.notifyDataSetChanged();
			}
		};
		LocationRetriever myLocation = new LocationRetriever();
		myLocation.getLocation(getActivity(), locationResult);

		main = inflater.inflate(R.layout.old_near_you_fragment, null);
		screenWidth = Utils.getInstance().getScreenSizes(getActivity())[0];
		screenHeight = Utils.getInstance().getScreenSizes(getActivity())[1];
		pictureWidth = screenWidth / 3;
		pictureHeight = screenHeight / 3;

		if (screenHeight < screenWidth) {
			pictureWidth = screenWidth / 3;
			pictureHeight = screenHeight / 3;
		}
		params = new FrameLayout.LayoutParams((int) (pictureWidth / 2.5), (int) (pictureHeight / 2.5));
		params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		
		blurTransform = new BlurTransform(getActivity());

		AnimatedListViewObjectMapper objectMapper = new AnimatedListViewObjectMapper() {
			public void bindObjectToView(Object object, final View view) {

				final Sensor sensor = (Sensor) object;
				ImageView sensorCityImageView = ((ImageView) view.findViewById(R.id.sensorCityImageView));
				sensorCityImageView.setLayoutParams(new FrameLayout.LayoutParams(pictureWidth, pictureHeight));

				ImageView sensorCityCloudImageView = ((ImageView) view.findViewById(R.id.sensorCityCloudImageView));
				sensorCityCloudImageView.setLayoutParams(params);
				TextView sensorCityTextView = ((TextView) view.findViewById(R.id.sensorCityTextView));
				sensorCityTextView.setText(sensor.getSensorCity());
				sensorCityTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
				sensorCityTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
				TextView sensorCountryTextView = ((TextView) view.findViewById(R.id.sensorCountryTextView));
				sensorCountryTextView.setText(sensor.getSensorCountry().replace(" ", "\n"));
				sensorCountryTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
				sensorCountryTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

				SensorMeasurement sensorMeasurement = sensorMeasurements.get(sensor.getSQLObjectKey());
				if (sensorMeasurement != null) {
					TextView sensorUpdateDateTextView = ((TextView) view.findViewById(R.id.sensorUpdateDateTextView));
					sensorUpdateDateTextView.setText(simpleDateFormatDate.format(new Date(sensorMeasurement.getSensorMeasurementTimeStamp())));
					sensorUpdateDateTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
					sensorUpdateDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
					sensorUpdateDateTextView.setText(simpleDateFormatHour.format(new Date(sensorMeasurement.getSensorMeasurementTimeStamp())));
					sensorUpdateDateTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
					sensorUpdateDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
					View sensorBackgroundView = ((View) view.findViewById(R.id.sensorBackgroundView));
					sensorBackgroundView.setBackgroundColor(Color.parseColor(AQICalculator.getInstance().getAQILevelForAQILevelNumber(sensorMeasurement.getSensorMeasurementAQILevel()).getaQILevelsColor()));
					sensorBackgroundView.setAlpha(0.5f);
					executorService.submit(new Runnable() {

						public void run() {
							Picasso.with(view.getContext()).load(sensor.getSensorAddressImage()).transform(blurTransform).into((ImageView) view.findViewById(R.id.sensorBackgroundImageView));
						}
					});
				}

				if (sensor.getSensorDistance() != null) {
					TextView sensorDistanceTextView = ((TextView) view.findViewById(R.id.sensorDistanceTextView));
					sensorDistanceTextView.setText(AerovibeUtils.getInstance().formatDist(sensor.getSensorDistance()) + " from you");
					sensorDistanceTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
					sensorDistanceTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
				}

				view.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						SensorView sensorView = SensorView.newInstance(sensor.getObjectKey());
						Utils.getInstance().showDialog(getSherlockActivity(), null, sensorView.toString(), sensorView);
					}
				});

			}
		};

		sensors.addAll(DataScheduleTaskRefresher.getInstance(getActivity()).getFreshSensorsData().values());
		sensorMeasurements.putAll(DataScheduleTaskRefresher.getInstance(getActivity()).getFreshSensorMeasurementData());

		sensors = sortSensorsByDistance(getActivity(), sensors, sensorsListAdapter);

		listViewSpacerView = new View(getActivity());
		listViewSpacerView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, Utils.getInstance().getActionBarHeight((SherlockFragmentActivity) getActivity())));

		animatedListView = (AnimatedListView) main.findViewById(R.id.near_you_list_view);
		animatedListView.addHeaderView(listViewSpacerView);
		sensorsListAdapter = new AnimatedListViewAdapter(getActivity(), R.layout.sensor_list_item, sensors, objectMapper);
		animatedListView.setAdapter(sensorsListAdapter);
		return main;
	}

	List<Sensor> sortSensorsByDistance(Context context, List<Sensor> sensors, AnimatedListViewAdapter postListAdapter) {
		Location location = new Location("");
		UserLocation userLocation = new UserLocationDbExecutors().get(new ExecuteManagementMethods().getTokenAndUserId(context).getTokenUserId(), context);
		if (userLocation == null) {
			return sensors;
		}
		location.setLatitude(userLocation.getUserLocationLatitude());
		location.setLongitude(userLocation.getUserLocationLongitude());
		Location sensorLocation = new Location("");
		for (Sensor sensor : sensors) {
			sensorLocation.setLatitude(sensor.getSensorLatitude());
			sensorLocation.setLongitude(sensor.getSensorLongitude());
			sensor.setSensorDistance(location.distanceTo(sensorLocation));
		}
		Collections.sort(sensors);
		return sensors;
	}
}