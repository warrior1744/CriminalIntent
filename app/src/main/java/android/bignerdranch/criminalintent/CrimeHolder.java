package android.bignerdranch.criminalintent;

import android.bignerdranch.criminalintent.model.Crime;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;


import java.util.Calendar;
import java.util.Locale;

public abstract class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private TextView mTitleTextView;
    private TextView mDateTextView;
    public ImageView mSolvedImageView;
    Crime mCrime;

    public void CrimeHolder() {
        //Default constructor
    }

    //We add one additional parameter in order to find out what different kinds of layout we're using.
    public CrimeHolder(View itemView) {
        super(itemView);
        /**Set each View that answer for onClickListener. We can modify the ViewHolder class
         * to implement the onClick Interface*/
        itemView.setOnClickListener(this);//itemView is the View for the entire row.
        mTitleTextView = (TextView) itemView.findViewById(R.id.crime_title);
        mDateTextView = (TextView) itemView.findViewById(R.id.crime_date);
        mSolvedImageView = (ImageView) itemView.findViewById(R.id.crime_solved);
    }

    public void bind(Crime crime) {
        mCrime = crime;
        mTitleTextView.setText(mCrime.getTitle());
        // mDateTextView.setText(mCrime.getDate().toString());
        //    String newDate = (String) DateFormat.format("MMM-dd-yyyy HH:mm", mCrime.getDate());
        //  String newDate = mCrime.getDate().toString();
        String dateFormat = DateFormat.getBestDateTimePattern(Locale.getDefault(), "EEEE MMMMM dd日  yyyy年 aa HH時 mm分");
        //java.text.DateFormat mDateFormat = DateFormat.getDateFormat(getAppContext());
        //  String newDateFormat = mDateFormat.format(mCrime.getDate());

        CharSequence newDate = DateFormat.format(dateFormat, mCrime.getDate());
        mDateTextView.setText(newDate);
        mSolvedImageView.setVisibility(crime.isSolved() ? View.VISIBLE : View.GONE);


    }

    @Override
    public abstract void onClick(View v);
}
