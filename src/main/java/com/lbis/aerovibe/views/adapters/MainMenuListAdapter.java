package com.lbis.aerovibe.views.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lbis.aerovibe_android.R;
import com.lbis.mobile.utils.Utils;

@SuppressWarnings("rawtypes")
public class MainMenuListAdapter extends ArrayAdapter {

	private Context mContext;
	private int id;
	private List<String> items;

	@SuppressWarnings("unchecked")
	public MainMenuListAdapter(Context context, int textViewResourceId, List<String> list) {
		super(context, textViewResourceId, list);
		mContext = context;
		id = textViewResourceId;
		items = list;
	}

	@Override
	public View getView(int position, View v, ViewGroup parent) {
		View mView = v;
		if (mView == null) {
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mView = vi.inflate(id, null);
		}

		TextView text = (TextView) mView.findViewById(R.id.mainMenuListItemTextView);

		if (items.get(position) != null) {
			text.setText(items.get(position));
			text.setTypeface(Utils.getInstance().getFont(getContext()));
		}

		return mView;
	}

}