package android.bignerdranch.criminalintent.dialog;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bignerdranch.criminalintent.CrimeFragment;
import android.bignerdranch.criminalintent.R;
import android.bignerdranch.criminalintent.model.Crime;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    private static final String ARG_DATE = "date";

    private DatePicker mDatePicker;

    public static final String EXTRA_DATE = "com.bignerdranch.android.criminalintent.date";

    public static DatePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);
        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        /**Extracting the date and initializing DatePicker
         * Initializing the DatePicker requires integers for the month, day, and year.
         * Date is more of a timestamp and connot provide integers like this directly.
         * So, we need a Calendar object and use the Date to configure the Calendar.*/
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //Adding a DatePicker widget to AlertDialog using AlertDialog.Builder setView(View view) method
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_date, null);


        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);

        mDatePicker.init(year, month, day, null);

        return new AlertDialog.Builder(getActivity()) //pass a Context into the AlertDialog.builder constructor.
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();
                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date); /**Calling sendResult() method by
                         implementing Dialog's PositiveButton listener*/
                    }
                })
                .create(); //this returns the configured AlertDialog instance
    }


    /**
     * When dealing with two fragments hosted by the same activity, we can borrow
     * Fragment.onActivityResult(...). But we have to call it DIRECTLY on the TARGET fragment,
     * and then pass back the extra data as an Intent and tag it with a request code.
     */
    private void sendResult(int resultCode, Date date) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATE, date);
        //This is a bit different than we did to the data exchange on two fragment hosted on different Activities.
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
