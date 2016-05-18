package com.lbis.aerovibe.views;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.lbis.aerovibe.aqi.calc.AQICalculator;
import com.lbis.aerovibe.concurrency.DataScheduleTaskRefresher;
import com.lbis.aerovibe.model.Sensor;
import com.lbis.aerovibe.model.SensorMeasurement;
import com.lbis.aerovibe.model.SensorMeasurementValue;
import com.lbis.aerovibe.views.components.CircleTransform;
import com.lbis.aerovibe.views.components.DialogUserFragmentBase;
import com.lbis.aerovibe_android.R;
import com.lbis.database.executors.model.SensorDbExecutors;
import com.lbis.mobile.utils.Utils;
import com.squareup.picasso.Picasso;

public class SensorView extends DialogUserFragmentBase {

	public static String currentDisplayedSensor = "currentDisplayedSensor";
	ImageView sensorViewImageView;

	public static SensorView newInstance(String currentSensor) {
		SensorView sensorView = new SensorView();
		Bundle args = new Bundle();
		args.putString(currentDisplayedSensor, currentSensor);
		sensorView.setArguments(args);
		return sensorView;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		String currentSensor = getArguments().getString(currentDisplayedSensor);
		Sensor sensor = new SensorDbExecutors().get(currentSensor, getActivity());
		SensorMeasurement sensorMeasurement = DataScheduleTaskRefresher.getInstance(getActivity()).getFreshSensorMeasurementData().get(currentSensor);
		LinearLayout view = (LinearLayout) inflater.inflate(R.layout.sensor_view, null);

		int screenWidth = Utils.getInstance().getScreenSizes(getActivity())[0];
		int screenHeight = Utils.getInstance().getScreenSizes(getActivity())[1];

		int pictureWidth = screenWidth / 3;
		int pictureHeight = screenHeight / 5;

		if (screenHeight < screenWidth) {
			pictureWidth = screenWidth / 5;
			pictureHeight = screenHeight / 3;
		}

		((ImageView) view.findViewById(R.id.sensorViewCityImageView)).setLayoutParams(new FrameLayout.LayoutParams(pictureWidth, pictureHeight));
		Picasso.with(getActivity()).load(sensor.getSensorAddressImage()).transform(new CircleTransform()).fit().centerInside().into((ImageView) view.findViewById(R.id.sensorViewCityImageView));
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (pictureWidth / 2.5), (int) (pictureHeight / 2.5));
		params.gravity = Gravity.BOTTOM | Gravity.RIGHT;
		((ImageView) view.findViewById(R.id.sensorViewCityCloudImageView)).setLayoutParams(params);
		((TextView) view.findViewById(R.id.sensorViewCityTextView)).setText(sensor.getSensorCity());
		((TextView) view.findViewById(R.id.sensorViewCityTextView)).setTypeface(Utils.getInstance().getFont(getActivity()));
		((TextView) view.findViewById(R.id.sensorViewCityTextView)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);
		((TextView) view.findViewById(R.id.sensorViewCountryTextView)).setText(sensor.getSensorCountry().replace(" ", "\n"));
		((TextView) view.findViewById(R.id.sensorViewCountryTextView)).setTypeface(Utils.getInstance().getFont(getActivity()));
		((TextView) view.findViewById(R.id.sensorViewCountryTextView)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

		if (sensorMeasurement != null) {
			((TextView) view.findViewById(R.id.sensorViewUpdateDateTextView)).setText(new SimpleDateFormat("dd/MM/yyyy HH:mma", Locale.US).format(new Date(sensorMeasurement.getSensorMeasurementTimeStamp())).replaceAll(" ", "\n"));
			((TextView) view.findViewById(R.id.sensorViewUpdateDateTextView)).setTypeface(Utils.getInstance().getFont(getActivity()));
			((TextView) view.findViewById(R.id.sensorViewUpdateDateTextView)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

			((TextView) view.findViewById(R.id.sensorViewAQITitleTextView)).setTypeface(Utils.getInstance().getFont(getActivity()));
			((TextView) view.findViewById(R.id.sensorViewAQITitleTextView)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

			((TextView) view.findViewById(R.id.sensorViewAQITextView)).setText(String.valueOf(sensorMeasurement.getSensorMeasurementAQILevel()));
			((TextView) view.findViewById(R.id.sensorViewAQITextView)).setTypeface(Utils.getInstance().getFont(getActivity()));
			((TextView) view.findViewById(R.id.sensorViewAQITextView)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30);

			((TextView) view.findViewById(R.id.sensorViewSensorMeasurementValuesLinearLayoutTitle)).setTypeface(Utils.getInstance().getFont(getActivity()));
			((TextView) view.findViewById(R.id.sensorViewSensorMeasurementValuesLinearLayoutTitle)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

			((TextView) view.findViewById(R.id.sensorViewDataProviderTitle)).setText(String.valueOf(sensorMeasurement.getSensorMeasurementDataProvidor().getName().replaceAll(" ", "\n")));
			((TextView) view.findViewById(R.id.sensorViewDataProviderTitle)).setTypeface(Utils.getInstance().getFont(getActivity()));
			((TextView) view.findViewById(R.id.sensorViewDataProviderTitle)).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

			((ImageView) view.findViewById(R.id.sensorViewDataProviderImageView)).setLayoutParams(new LinearLayout.LayoutParams(pictureWidth, pictureHeight));
			Picasso.with(getActivity()).load(sensorMeasurement.getSensorMeasurementDataProvidor().getPhotoUrl()).transform(new CircleTransform()).fit().centerInside().into((ImageView) view.findViewById(R.id.sensorViewDataProviderImageView));

			((GradientDrawable) ((View) view.findViewById(R.id.sensorViewBackgroundView)).getBackground()).setColor(Color.parseColor(AQICalculator.getInstance().getAQILevelForAQILevelNumber(sensorMeasurement.getSensorMeasurementAQILevel()).getaQILevelsColor()));
			((View) view.findViewById(R.id.sensorViewBackgroundView)).setAlpha(0.5f);

			LinearLayout.LayoutParams paramsForValues = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			paramsForValues.setMargins(4, 4, 4, 4);

			for (int i = 0; i < sensorMeasurement.getSensorMeasurementValues().size(); i++) {
				SensorMeasurementValue sensorMeasurementValue = sensorMeasurement.getSensorMeasurementValues().get(i);
				TextView sensorMeasurementValueTextView = new TextView(getActivity());
				StringBuilder measurementValueText = new StringBuilder();
				measurementValueText.append(sensorMeasurementValue.getSensorMeasurementPollutantsEnums().getPollutantsEnumUnitsTitle()).append(" level is ").append(sensorMeasurementValue.getSensorMeasurementValueValue()).append(sensorMeasurementValue.getSensorMeasurementPollutantsEnums().getPollutantsEnumUnitsName());
				sensorMeasurementValueTextView.setText(measurementValueText.toString());
				sensorMeasurementValueTextView.setTextAppearance(getActivity(), R.style.BlackTextWithWhiteShadow);
				sensorMeasurementValueTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
				sensorMeasurementValueTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
				sensorMeasurementValueTextView.setLayoutParams(paramsForValues);
				sensorMeasurementValueTextView.setPadding(4, 4, 4, 4);
				((LinearLayout) view.findViewById(R.id.sensorViewSensorMeasurementValuesLinearLayout)).addView(sensorMeasurementValueTextView, i + 1);
			}

		}

		return view;
	}

	@Override
	public void onDestroyView() {
		if (getDialog() != null && getRetainInstance())
			getDialog().setDismissMessage(null);
		super.onDestroyView();
	}

}
