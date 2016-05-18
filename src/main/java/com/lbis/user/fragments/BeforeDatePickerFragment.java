package com.lbis.user.fragments;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class BeforeDatePickerFragment extends DialogFragment {

	DatePickerDialog.OnDateSetListener listener = null;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		listener = listener == null ? (DatePickerDialog.OnDateSetListener) getActivity()
				: listener;
		LimitedRangeDatePickerDialog dialog = new LimitedRangeDatePickerDialog(
				getActivity(), listener, year, month, day, null,
				Calendar.getInstance());
		return dialog;
	}

	public void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
		this.listener = listener;
	}
}