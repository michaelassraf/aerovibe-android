package com.lbis.aerovibe.fragments;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Xml.Encoding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filter;
import android.widget.Filterable;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.ui.IconGenerator;
import com.lbis.aerovibe.concurrency.AsyncTaskMap;
import com.lbis.aerovibe.concurrency.DataScheduleTaskRefresher;
import com.lbis.aerovibe.enums.URLEnums;
import com.lbis.aerovibe.map.SensorMarkerWrapper;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.views.SensorView;
import com.lbis.aerovibe_android.R;
import com.lbis.mobile.utils.Utils;
import com.lbis.server.DummyWebRequester;

public class TheWorldFragment extends SherlockFragment implements ClusterManager.OnClusterClickListener<SensorMarkerWrapper>, ClusterManager.OnClusterInfoWindowClickListener<SensorMarkerWrapper>, ClusterManager.OnClusterItemClickListener<SensorMarkerWrapper>, ClusterManager.OnClusterItemInfoWindowClickListener<SensorMarkerWrapper>, OnItemClickListener {

	HashMap<String, SensorMeasurement> sensorMeasurements = new HashMap<String, SensorMeasurement>();
	List<Sensor> sensors = new LinkedList<Sensor>();
	static IconGenerator worldIconGenerator;
	static IconGenerator worldClusterIconGenerator;
	static BitmapDescriptor bitmapDescriptorWorldIconGenerator;
	static BitmapDescriptor bitmapDescriptorWorldClusterIconGenerator;

	View main;

	AutoCompleteTextView worldAutoCompleteTextView;
	MapView worldMapView;
	GoogleMap worldGoogleMap;
	ClusterManager<SensorMarkerWrapper> worldSensorMarkerWrapperClusterManager;

	Logger log = Logger.getLogger(getClass().getSimpleName());

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		main = inflater.inflate(R.layout.the_world_fragment, container, false);
		worldIconGenerator = new IconGenerator(getActivity().getApplicationContext());

		sensorMeasurements.putAll(DataScheduleTaskRefresher.getInstance(getActivity()).getFreshSensorMeasurementData());
		sensors.addAll(DataScheduleTaskRefresher.getInstance(getActivity()).getFreshSensorsData().values());

		getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSherlockActivity().getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSherlockActivity().getSupportActionBar().setCustomView(((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.action_bar_autocomplete, null));
		getSherlockActivity().getSupportActionBar().setTitle("The World");

		worldMapView = (MapView) main.findViewById(R.id.worldMapView);
		worldMapView.onCreate(savedInstanceState);

		worldGoogleMap = worldMapView.getMap();
		worldGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
		worldGoogleMap.setMyLocationEnabled(true);
		worldGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
		worldGoogleMap.getUiSettings().setCompassEnabled(false);
		worldGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
		worldGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

		worldAutoCompleteTextView = (AutoCompleteTextView) getSherlockActivity().getSupportActionBar().getCustomView().findViewById(R.id.auto_complete_text_view_adapter);
		worldAutoCompleteTextView.setAdapter(new PlacesAutoCompleteAdapter(getActivity(), R.layout.auto_complete_list_item));
		worldAutoCompleteTextView.setDropDownBackgroundResource(android.R.color.transparent);
		worldAutoCompleteTextView.setOnItemClickListener(this);

		try {
			MapsInitializer.initialize(this.getActivity());
		} catch (Throwable th) {
			log.error("Can't load map.", th);
		}
		Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
		worldSensorMarkerWrapperClusterManager = new ClusterManager<SensorMarkerWrapper>(getActivity(), worldGoogleMap);

		worldGoogleMap.setOnCameraChangeListener(worldSensorMarkerWrapperClusterManager);
		worldGoogleMap.setOnMarkerClickListener(worldSensorMarkerWrapperClusterManager);
		worldGoogleMap.setOnInfoWindowClickListener(worldSensorMarkerWrapperClusterManager);

		worldSensorMarkerWrapperClusterManager.setOnClusterClickListener(this);
		worldSensorMarkerWrapperClusterManager.setOnClusterInfoWindowClickListener(this);
		worldSensorMarkerWrapperClusterManager.setOnClusterItemClickListener(this);
		worldSensorMarkerWrapperClusterManager.setOnClusterItemInfoWindowClickListener(this);

		for (Sensor sensor : sensors) {
			worldSensorMarkerWrapperClusterManager.addItem(new SensorMarkerWrapper(sensor));
		}
		worldSensorMarkerWrapperClusterManager.cluster();
		bitmapDescriptorWorldIconGenerator = BitmapDescriptorFactory.fromBitmap(worldIconGenerator.makeIcon());
		worldClusterIconGenerator = new IconGenerator(getActivity().getApplicationContext());
		bitmapDescriptorWorldClusterIconGenerator = BitmapDescriptorFactory.fromBitmap(worldClusterIconGenerator.makeIcon());
		return main;
	}

	public boolean onClusterClick(Cluster<SensorMarkerWrapper> cluster) {
//		worldGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), 10.5f), 3000, null);
		return true;
	}

	public void onClusterInfoWindowClick(Cluster<SensorMarkerWrapper> cluster) {
	}

	public boolean onClusterItemClick(SensorMarkerWrapper item) {
		SensorView sensorView = SensorView.newInstance(item.getObjectKey());
		Utils.getInstance().showDialog(getSherlockActivity(), null, sensorView.toString(), sensorView);
		return false;
	}

	public void onClusterItemInfoWindowClick(SensorMarkerWrapper item) {
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (worldMapView != null)
			worldMapView.onDestroy();
		getSherlockActivity().getSupportActionBar().setCustomView(null);
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		worldMapView.onLowMemory();
	}

	@Override
	public void onResume() {
		worldMapView.onResume();
		super.onResume();
	}

	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		AsyncTaskMap.getAsyncHashMap().cancelAll();
		TakeMeToThePlace task = new TakeMeToThePlace();
		AsyncTaskMap.getAsyncHashMap().add(task);
		task.execute(str);
	}

	class TakeMeToThePlace extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {
			try {
				String place = params[0].trim().replace(" ", "+");
				String url = URLEnums.GoogleMapsAPI.GeoCodingAPI.getUrl().replaceAll("%10%", place);
				JSONObject jsonObj = new JSONObject(new DummyWebRequester().invokeHTTPGetRequest(url));
				JSONArray placesResult = jsonObj.getJSONArray("results");
				JSONObject resultsResult = placesResult.getJSONObject(0);
				JSONObject geoResult = resultsResult.getJSONObject("geometry");
				JSONObject locResult = geoResult.getJSONObject("location");
				final Double longtitude = locResult.getDouble("lng");
				final Double latitude = locResult.getDouble("lat");
				final String formattedAddress = resultsResult.getString("formatted_address");
				getActivity().runOnUiThread(new Runnable() {

					public void run() {
						CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longtitude), 14f);
						worldGoogleMap.animateCamera(cameraUpdate);
						worldGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longtitude)).title(formattedAddress).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE))).showInfoWindow();
						worldAutoCompleteTextView.setText("");
					}
				});

			} catch (Throwable th) {
				log.error("Can't acquire Geo point from location", th);
			}
			return null;
		}
	}

	class PlacesAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
		private ArrayList<String> resultList;

		public PlacesAutoCompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		public int getCount() {
			return resultList.size();
		}

		public String getItem(int index) {
			return resultList.get(index);
		}

		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						try {
							AsyncTaskMap.getAsyncHashMap().cancelAll();
							GetPlacesAsyncTask task = new GetPlacesAsyncTask();
							AsyncTaskMap.getAsyncHashMap().add(task);
							task.execute(constraint.toString());
							resultList = task.get();
							filterResults.values = resultList;
							filterResults.count = resultList.size();
						} catch (Throwable th) {
							log.error("Couldn't preform Google Places API call", th);
						}
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}

	class GetPlacesAsyncTask extends AsyncTask<String, Void, ArrayList<String>> {

		String PREDICTIONS_PROPERTY_VALUE = "predictions";
		String DESCRIPTION_PROPERTY_VALUE = "description";

		@Override
		protected ArrayList<String> doInBackground(String... args) {
			ArrayList<String> resultList = null;
			String jsonResults = new String();
			try {
				String aPICallUrl = URLEnums.GoogleMapsAPI.PlacesAPI.getUrl() + URLEncoder.encode(args[0], Encoding.UTF_8.toString().toLowerCase(Locale.US));
				jsonResults = new DummyWebRequester().invokeHTTPGetRequest(aPICallUrl);
			} catch (Throwable th) {
				log.error("Error processing Places API URL", th);
				return resultList;
			}
			try {
				JSONObject jsonObj = new JSONObject(jsonResults);
				JSONArray predsJsonArray = jsonObj.getJSONArray(PREDICTIONS_PROPERTY_VALUE);
				resultList = new ArrayList<String>(predsJsonArray.length());
				for (int i = 0; i < predsJsonArray.length(); i++) {
					resultList.add(predsJsonArray.getJSONObject(i).getString(DESCRIPTION_PROPERTY_VALUE));
				}
			} catch (Throwable th) {
				log.error("Cannot process JSON results", th);
			}
			return resultList;

		}
	}
}