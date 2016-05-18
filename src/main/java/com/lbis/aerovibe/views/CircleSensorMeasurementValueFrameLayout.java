package com.lbis.aerovibe.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lbis.aerovibe.model.SensorMeasurementValue;
import com.lbis.aerovibe_android.R;
import com.lbis.mobile.utils.Utils;

public class CircleSensorMeasurementValueFrameLayout extends FrameLayout {
	TextView circleSensorMeasurementValueValueTextView;
	TextView circleSensorMeasurementValueTitleTextView;

	public CircleSensorMeasurementValueFrameLayout(Context context) {
		super(context);
	}

	public CircleSensorMeasurementValueFrameLayout(Context context, SensorMeasurementValue sensorMeasurementValue) {
		super(context);
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.circle_sensor_measurement_value_frame_layout, this, true);
		circleSensorMeasurementValueValueTextView = (TextView) findViewById(R.id.circleSensorMeasurementValueValueTextView);
		circleSensorMeasurementValueValueTextView.setText(sensorMeasurementValue.getSensorMeasurementPollutantsEnums().getPollutantsEnumUnitsFormattedTitle());
		circleSensorMeasurementValueValueTextView.setTypeface(Utils.getInstance().getFont(context));
		circleSensorMeasurementValueTitleTextView = (TextView) findViewById(R.id.circleSensorMeasurementValueTitleTextView);
		circleSensorMeasurementValueTitleTextView.setText(new StringBuilder().append(String.valueOf(sensorMeasurementValue.getSensorMeasurementValueValue())).append(" ").append(sensorMeasurementValue.getSensorMeasurementPollutantsEnums().getPollutantsEnumUnitsName()).toString());
		circleSensorMeasurementValueTitleTextView.setTypeface(Utils.getInstance().getFont(context));
	}

}
