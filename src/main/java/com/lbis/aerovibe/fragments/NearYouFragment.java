package com.lbis.aerovibe.fragments;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.lbis.aerovibe.concurrency.DataScheduleTaskRefresher;
import com.lbis.aerovibe.location.LocationRetriever;
import com.lbis.aerovibe.location.LocationRetriever.LocationResult;
import com.lbis.aerovibe.management.ExecuteManagementMethods;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.model.UserLocation;
import com.lbis.aerovibe.views.SensorFragment;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.model.UserLocationDbExecutors;

public class NearYouFragment extends SherlockFragment {

	ViewPager sensorsViewPager;
	SensorsPagerAdapter sensorsPagerAdapter;
	View main;
	Logger log = Logger.getLogger(NearYouFragment.class.getSimpleName());
	volatile HashMap<String, SensorMeasurement> sensorMeasurements;
	volatile List<Sensor> sensors;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		main = inflater.inflate(R.layout.near_you_fragment, null);

		getSherlockActivity().getSupportActionBar().setCustomView(null);
		getSherlockActivity().getSupportActionBar().setTitle("Near You");
		sensorMeasurements = new HashMap<String, SensorMeasurement>();
		sensors = new LinkedList<Sensor>();

		LocationResult locationResult = new LocationResult() {
			@Override
			public void gotLocation(Location location) {
				new UserLocationDbExecutors().put(getActivity(), new UserLocation(location.getLatitude(), location.getLongitude(), new ExecuteManagementMethods().getTokenAndUserId(getActivity()).getTokenUserId(), null));
				sensors = sortSensorsByDistance(getActivity(), sensors);
			}
		};

		LocationRetriever locationRetriever = new LocationRetriever();
		locationRetriever.getLocation(getActivity(), locationResult);

		sensors = new LinkedList<Sensor>(DataScheduleTaskRefresher.getInstance(getActivity()).getFreshSensorsData().values());
		sensorMeasurements = new HashMap<String, SensorMeasurement>(DataScheduleTaskRefresher.getInstance(getActivity()).getFreshSensorMeasurementData());

		sensors = sortSensorsByDistance(getActivity(), sensors);

		sensorsPagerAdapter = new SensorsPagerAdapter(getChildFragmentManager());
		sensorsViewPager = (ViewPager) main.findViewById(R.id.nearYouFragmentViewPager);
		sensorsViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {

			public void transformPage(View page, float position) {
				page.setRotationY(position * -30);
			}
		});
		sensorsViewPager.setAdapter(sensorsPagerAdapter);
		return main;
	}



	List<Sensor> sortSensorsByDistance(Context context, List<Sensor> sensors) {
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

	private class SensorsPagerAdapter extends FragmentStatePagerAdapter {

		public SensorsPagerAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return sensors.size();
		}

		@Override
		public Fragment getItem(int position) {
			return SensorFragment.newInstance(sensors.get(position), sensorMeasurements.get(sensors.get(position).getObjectKey()));
		}

	}

}
