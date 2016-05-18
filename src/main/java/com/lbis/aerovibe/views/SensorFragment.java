package com.lbis.aerovibe.views;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.lbis.aerovibe.aqi.calc.AQICalculator;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.model.SensorMeasurementValue;
import com.lbis.aerovibe.utils.AerovibeUtils;
import com.lbis.aerovibe.views.components.BlurTransform;
import com.lbis.aerovibe.views.components.CircleTransform;
import com.lbis.aerovibe.views.components.SlidingUpPanelLayout;
import com.lbis.aerovibe_android.R;
import com.lbis.mobile.utils.Utils;
import com.squareup.picasso.Picasso;

public class SensorFragment extends SherlockFragment {

	private static ExecutorService executorService;
	Logger log = Logger.getLogger(SensorFragment.class.getSimpleName());

	SensorMeasurement sensorMeasurement;
	Sensor sensor;
	View main;
	BlurTransform blurTransform;
	SimpleDateFormat simpleDateFormatDate = new SimpleDateFormat("dd MMM ,", Locale.US);
	SimpleDateFormat simpleDateFormatHour = new SimpleDateFormat("HH:mm", Locale.US);
	RelativeLayout sensorFragmentDetailsRelativeLayout;
	TextView sensorFragmentPollutionLevelTextView;
	FrameLayout.LayoutParams params;
	SlidingUpPanelLayout sensorFragmentMainSlidingUpPanelLayout;
	LinearLayout sensorFragmentValuesLinearLayout;

	public static SensorFragment newInstance(Sensor sensor, SensorMeasurement sensorMeasurement) {
		SensorFragment fragment = new SensorFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable(Sensor.class.getName(), sensor);
		bundle.putSerializable(SensorMeasurement.class.getName(), sensorMeasurement);
		fragment.setArguments(bundle);
		if (executorService == null)
			executorService = Executors.newFixedThreadPool(20);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		main = inflater.inflate(R.layout.sensor_fragment, null);

		sensorFragmentMainSlidingUpPanelLayout = (SlidingUpPanelLayout) main.findViewById(R.id.sensorFragmentMainSlidingUpPanelLayout);

		sensor = (Sensor) getArguments().getSerializable(Sensor.class.getName());
		sensorMeasurement = (SensorMeasurement) getArguments().getSerializable(SensorMeasurement.class.getName());

		blurTransform = new BlurTransform(getActivity());

		params = new FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.topMargin = Utils.getInstance().getActionBarHeight((SherlockFragmentActivity) getActivity());

		sensorFragmentDetailsRelativeLayout = (RelativeLayout) main.findViewById(R.id.sensorFragmentDetailsRelativeLayout);
		sensorFragmentDetailsRelativeLayout.setLayoutParams(params);

		TextView sensorFragmentCityTextView = ((TextView) main.findViewById(R.id.sensorFragmentCityTextView));
		sensorFragmentCityTextView.setText(sensor.getSensorCity());
		sensorFragmentCityTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		sensorFragmentCityTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
		TextView sensorFragmentCountryTextView = ((TextView) main.findViewById(R.id.sensorFragmentCountryTextView));
		sensorFragmentCountryTextView.setText(sensor.getSensorCountry().replace(" ", "\n"));
		sensorFragmentCountryTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		sensorFragmentCountryTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		TextView sensorFragmentUpdateViewDateTextView = ((TextView) main.findViewById(R.id.sensorFragmentUpdateViewDateTextView));
		sensorFragmentUpdateViewDateTextView.setText(simpleDateFormatDate.format(new Date(sensorMeasurement.getSensorMeasurementTimeStamp())));
		sensorFragmentUpdateViewDateTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		sensorFragmentUpdateViewDateTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		TextView sensorFragmentUpdateViewTimeTextView = ((TextView) main.findViewById(R.id.sensorFragmentUpdateViewTimeTextView));
		sensorFragmentUpdateViewTimeTextView.setText(simpleDateFormatHour.format(new Date(sensorMeasurement.getSensorMeasurementTimeStamp())));
		sensorFragmentUpdateViewTimeTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		sensorFragmentUpdateViewTimeTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);
		View sensorFragmentAQIBackgroundView = ((View) main.findViewById(R.id.sensorFragmentAQIBackgroundView));
		sensorFragmentAQIBackgroundView.setBackgroundColor(Color.parseColor(AQICalculator.getInstance().getAQILevelForAQILevelNumber(sensorMeasurement.getSensorMeasurementAQILevel()).getaQILevelsColor()));
		sensorFragmentAQIBackgroundView.setAlpha(0.5f);
		TextView sensorFragmentPollutionLevelTextView = ((TextView) main.findViewById(R.id.sensorFragmentPollutionLevelTextView));
		sensorFragmentPollutionLevelTextView.setText(AQICalculator.getInstance().getAQILevelForAQILevelNumber(sensorMeasurement.getSensorMeasurementAQILevel()).getaQILevelsName());
		sensorFragmentPollutionLevelTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		sensorFragmentPollutionLevelTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

		TextView sensorFragmentDataProviderMainTitle = ((TextView) main.findViewById(R.id.sensorFragmentDataProviderMainTitle));
		sensorFragmentDataProviderMainTitle.setText(String.valueOf(sensorMeasurement.getSensorMeasurementDataProvidor().getName().replaceAll(" ", "\n")));
		sensorFragmentDataProviderMainTitle.setTypeface(Utils.getInstance().getFont(getActivity()));
		sensorFragmentDataProviderMainTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);

		ImageView sensorFragmentDataProviderMainImageView = ((ImageView) main.findViewById(R.id.sensorFragmentDataProviderMainImageView));
		int screenWidth = Utils.getInstance().getScreenSizes(getActivity())[0];
		int screenHeight = Utils.getInstance().getScreenSizes(getActivity())[1];

		int pictureWidth = screenWidth / 5;
		int pictureHeight = screenHeight / 7;

		if (screenHeight < screenWidth) {
			pictureWidth = screenWidth / 7;
			pictureHeight = screenHeight / 5;
		}
		sensorFragmentDataProviderMainImageView.setLayoutParams(new LinearLayout.LayoutParams(pictureWidth, pictureHeight));
		Picasso.with(getActivity()).load(sensorMeasurement.getSensorMeasurementDataProvidor().getPhotoUrl()).transform(new CircleTransform()).fit().centerInside().into(sensorFragmentDataProviderMainImageView);
		TextView sensorFragmentDistanceMainTextView = ((TextView) main.findViewById(R.id.sensorFragmentDistanceMainTextView));
		sensorFragmentDistanceMainTextView.setText(AerovibeUtils.getInstance().formatDist(sensor.getSensorDistance()) + " from you");
		sensorFragmentDistanceMainTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		sensorFragmentDistanceMainTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

		TextView sensorFragmentAQILevelTextView = ((TextView) main.findViewById(R.id.sensorFragmentAQILevelTextView));
		sensorFragmentAQILevelTextView.setText(Long.toString(sensorMeasurement.getSensorMeasurementAQILevel()));
		sensorFragmentAQILevelTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		int padding = Utils.getInstance().getScreenSizes(getActivity())[0] / 10;
		sensorFragmentAQILevelTextView.setPadding(padding, padding, 2, padding);
		sensorFragmentAQILevelTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);

		TextView sensorFragmentAQILevelTitleTextView = ((TextView) main.findViewById(R.id.sensorFragmentAQILevelTitleTextView));
		sensorFragmentAQILevelTitleTextView.setText(" AQI Level");
		sensorFragmentAQILevelTitleTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		sensorFragmentAQILevelTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		LinearLayout sensorFragmentBottomMainDragLinearLayout = (LinearLayout) main.findViewById(R.id.sensorFragmentBottomMainDragLinearLayout);
		HorizontalScrollView sensorFragmentValuesHorizontalScrollView = (HorizontalScrollView) main.findViewById(R.id.sensorFragmentValuesHorizontalScrollView);
		sensorFragmentMainSlidingUpPanelLayout.setPanelHeight(pictureHeight);
		sensorFragmentMainSlidingUpPanelLayout.setDragView(sensorFragmentBottomMainDragLinearLayout);
		sensorFragmentValuesLinearLayout = ((LinearLayout) sensorFragmentValuesHorizontalScrollView.findViewById(R.id.sensorFragmentValuesLinearLayout));
		LinearLayout.LayoutParams lineViewLayoutParams = new LinearLayout.LayoutParams(2, LinearLayout.LayoutParams.MATCH_PARENT);
		lineViewLayoutParams.bottomMargin = 2;
		lineViewLayoutParams.topMargin = 2;
		lineViewLayoutParams.leftMargin= 2;
		lineViewLayoutParams.rightMargin= 2;
		View view  = new View(getActivity());
		view.setBackgroundColor(getResources().getColor(R.color.semi_white));
		view.setLayoutParams(lineViewLayoutParams);
		sensorFragmentValuesLinearLayout.addView(view);
		for (SensorMeasurementValue sensorMeasurementValue : sensorMeasurement.getSensorMeasurementValues()) {
			sensorFragmentValuesLinearLayout.addView(new CircleSensorMeasurementValueFrameLayout(getActivity(), sensorMeasurementValue));
			View currentView  = new View(getActivity());
			currentView.setBackgroundColor(getResources().getColor(R.color.semi_white));
			currentView.setLayoutParams(lineViewLayoutParams);
			sensorFragmentValuesLinearLayout.addView(currentView);
		}
		
		for (int i = 0; i < sensorFragmentValuesLinearLayout.getChildCount(); i++) {
	        View v = sensorFragmentValuesLinearLayout.getChildAt(i);
	        if (v instanceof CircleSensorMeasurementValueFrameLayout) {
	        	
	        } 
	    }

		return main;
	}

	@Override
	public void onResume() {
		executorService.submit(new Runnable() {

			public void run() {
				try {
					Thread.sleep(500);
					if (getActivity() != null) {
						getActivity().runOnUiThread(new Runnable() {

							public void run() {
								Picasso.with(getActivity()).load(sensor.getSensorAddressImage()).transform(blurTransform).fit().centerCrop().into((ImageView) main.findViewById(R.id.sensorFragmentBackgroundImageView));
							}
						});
					}
				} catch (Throwable th) {
					log.error("Problem waiting for picture thread.", th);
				}
			}
		});
		super.onResume();
	}
}
