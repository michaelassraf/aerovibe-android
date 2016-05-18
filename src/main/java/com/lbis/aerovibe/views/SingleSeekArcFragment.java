package com.lbis.aerovibe.views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.lbis.aerovibe.views.SeekArc.OnSeekArcChangeListener;
import com.lbis.aerovibe_android.R;
import com.lbis.mobile.utils.Utils;

public class SingleSeekArcFragment extends SherlockFragment {

	public static final String singleSeekArcFragmentCurrentParamName = "singleSeekArcFragmentCurrentParamName";

	LinearLayout main;

	TextView singleSeekArcFragmentTitleTextView;
	TextView singleSeekArcFragmentPorgressTextView;
	TextView singleSeekArcFragmentTitlePorgressTextView;
	TextView singleSeekArcFragmentTitleTitleTextView;

	SeekArc singleSeekArcFragmentSeekArc;

	String singleSeekArcFragmentCurrentParam;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		main = (LinearLayout) inflater.inflate(R.layout.single_seek_arc_fragment, null);
		singleSeekArcFragmentCurrentParam = getArguments().getString(singleSeekArcFragmentCurrentParamName);
		singleSeekArcFragmentTitleTextView = (TextView) main.findViewById(R.id.singleSeekArcFragmentTitleTextView);
		singleSeekArcFragmentPorgressTextView = (TextView) main.findViewById(R.id.singleSeekArcFragmentPorgressTextView);
		singleSeekArcFragmentTitlePorgressTextView = (TextView) main.findViewById(R.id.singleSeekArcFragmentTitlePorgressTextView);
		singleSeekArcFragmentTitleTitleTextView = (TextView) main.findViewById(R.id.singleSeekArcFragmentTitleTitleTextView);
		singleSeekArcFragmentSeekArc = (SeekArc) main.findViewById(R.id.singleSeekArcFragmentSeekArc);

		singleSeekArcFragmentTitleTextView.setText(singleSeekArcFragmentCurrentParam);
		singleSeekArcFragmentTitleTitleTextView.setText(singleSeekArcFragmentCurrentParam);
		singleSeekArcFragmentTitlePorgressTextView.setText("0% of the highest " + singleSeekArcFragmentCurrentParam + " measured level.");

		singleSeekArcFragmentTitleTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		singleSeekArcFragmentPorgressTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		singleSeekArcFragmentTitleTitleTextView.setTypeface(Utils.getInstance().getFont(getActivity()));
		singleSeekArcFragmentTitlePorgressTextView.setTypeface(Utils.getInstance().getFont(getActivity()));

		singleSeekArcFragmentSeekArc.setOnSeekArcChangeListener(new OnSeekArcChangeListener() {

			public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {
				singleSeekArcFragmentPorgressTextView.setText(String.valueOf(progress) + "%");
				singleSeekArcFragmentTitlePorgressTextView.setText(String.valueOf(progress) + "% of the highest measured level.");
			}

			public void onStartTrackingTouch(SeekArc seekArc) {
			}

			public void onStopTrackingTouch(SeekArc seekArc) {

			}

		});

		// addView(main);
		// Fragment.LayoutParams params = new
		// Fragment.LayoutParams(Fragment.LayoutParams.WRAP_CONTENT,
		// Utils.getInstance().getScreenSizes(context)[1] / 2);
		// setLayoutParams(params);
		return main;
	}
}