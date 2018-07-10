package com.haoji.haoji.custom;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
	
	private TextView view;
	
	public DatePickerFragment(TextView view) {
		this.view=view;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		Log.d("OnDateSet", "select year:" + year + ";month:" + month + ";day:" + day);
		String y=null,m = null,d=null;
		month++;
		y=year+"-";
		if (month<10) {
			m="0"+month+"-";
		}else{
			m=month+"-";
		}
		if (day<10) {
			d="0"+day;
		}else{
			d=day+"";
		}
		this.view.setText(y+m+d);
	}
}
