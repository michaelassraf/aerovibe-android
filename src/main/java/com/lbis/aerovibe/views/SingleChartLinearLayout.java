package com.lbis.aerovibe.views;

import java.util.Collections;
import java.util.LinkedList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LineGraph.OnPointClickedListener;
import com.echo.holographlibrary.LinePoint;
import com.lbis.aerovibe.model.SensorMeasurementValue;
import com.lbis.mobile.utils.Utils;

public class SingleChartLinearLayout extends LinearLayout {

	View main;
	LayoutInflater layoutInflater;
	Line seriesLine;
	TextView lineTitle;
	LineGraph lineGraph;
	View lineGraphTitleSpacer;

	public SingleChartLinearLayout(Context context, final LinkedList<SensorMeasurementValue> sensorMeasurementValues, String header) {
		super(context);
		seriesLine = new Line();
		seriesLine.setColor(Color.parseColor("#FFBB33"));
		float max = Float.MIN_VALUE;
		float min = Float.MAX_VALUE;
		Collections.sort(sensorMeasurementValues);
		int i = 0;
		for (SensorMeasurementValue sensorMeasurementValue : sensorMeasurementValues) {
			max = Math.max(max, sensorMeasurementValue.getSensorMeasurementValueValue());
			min = Math.min(min, sensorMeasurementValue.getSensorMeasurementValueValue());
			LinePoint currentPoint = new LinePoint(i, sensorMeasurementValue.getSensorMeasurementValueValue());
			currentPoint.setColor("#FF0000");
			seriesLine.addPoint(currentPoint);
			i++;
		}

		lineGraph = new LineGraph(context);
		lineGraph.addLine(seriesLine);
		lineGraph.setRangeY((int) (min - (min * 0.3)), (int) (max + (max * 0.3)));
		lineGraph.setLineToFill(0);

		lineGraph.setOnPointClickedListener(new OnPointClickedListener() {

			public void onClick(int lineIndex, int pointIndex) {
				Toast.makeText(getContext(), "Values are " + lineIndex + " and " + sensorMeasurementValues.get(pointIndex).getSensorMeasurementPollutantsEnums().getPollutantsEnumAliases(), 2000).show();
			}

		});
		lineGraph.setPadding(5, 5, 5, 5);
		setOrientation(LinearLayout.VERTICAL);
		setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, (int) (Utils.getInstance().getScreenSizes(context)[1] / 1.5)));
		setPadding(10, 10, 10, 10);
		lineTitle = new TextView(context);
		lineTitle.setText(header);
		lineTitle.setTextSize(30f);
		lineTitle.setTypeface(Utils.getInstance().getFont(getContext()));
		lineTitle.setPadding(5, 5, 5, 5);
		lineGraphTitleSpacer = new LinearLayout(context);
		lineGraphTitleSpacer.setLayoutParams(new LinearLayout.LayoutParams(Utils.getInstance().getScreenSizes(context)[0] - 20, 3));
		lineGraphTitleSpacer.setBackgroundColor(context.getResources().getColor(android.R.color.black));
		lineGraphTitleSpacer.setPadding(5, 5, 5, 5);
		addView(lineTitle);
		addView(lineGraphTitleSpacer);
		addView(lineGraph);
	}
}