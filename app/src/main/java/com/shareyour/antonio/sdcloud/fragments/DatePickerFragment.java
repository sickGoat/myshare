package com.shareyour.antonio.sdcloud.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.DatePicker;

import com.shareyour.antonio.sdcloud.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by antonio on 19/06/15.
 */
public class DatePickerFragment extends DialogFragment {

    public static final String EXTRA_DATE_DIALOG = "dialogFragment_date";

    private Date mDate = new Date();
    private Calendar mCalendar = Calendar.getInstance();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if( savedInstanceState != null ){
            mDate = (Date) savedInstanceState.getSerializable(EXTRA_DATE_DIALOG);
            mCalendar.setTime(mDate);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date,null);
        DatePicker picker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mCalendar.setTime(mDate);
        picker.init(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        mCalendar.set(Calendar.YEAR,year);
                        mCalendar.set(Calendar.MONTH,monthOfYear);
                        mCalendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                        mDate = mCalendar.getTime();
                    }
                });

        return new AlertDialog.Builder(getActivity()).setView(v).setTitle(R.string.date_dialog)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        }).create();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(EXTRA_DATE_DIALOG,mDate);
    }

    private void sendResult(int resultCode){
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE_DIALOG,mDate);
        getTargetFragment().onActivityResult(getTargetRequestCode(),resultCode,intent);
    }


}
