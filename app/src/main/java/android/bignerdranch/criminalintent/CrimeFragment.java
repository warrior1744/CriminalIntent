package android.bignerdranch.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.bignerdranch.criminalintent.Utils.PictureUtils;
import android.bignerdranch.criminalintent.dialog.DatePickerFragment;
import android.bignerdranch.criminalintent.dialog.TimePickerFragment;
import android.bignerdranch.criminalintent.dialog.ZoomedInDialogFragment;
import android.bignerdranch.criminalintent.model.Crime; /**import model package*/
import android.bignerdranch.criminalintent.model.CrimeLab;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.format.DateFormat;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID = "crime_id";
    private static final String CRIME_RESOLVED = "crime_resolved"; //added by Jim
    private static final String CRIME_SERIOUS = "crime_serious";//added by Jim
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PHOTO = "DialogPhoto"; //Challenge Detailed image

    /**
     * Challenge:More Dialogs
     */
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 10;
    /**
     * Challenge:More Dialogs
     */
    private static final int REQUEST_CONTACT = 1;//Listing 15.11  Adding a constant for result
    private static final int REQUEST_DIAL = 11;//Challenge: Another Implicit Intent


    //  private static final String[] CONTACTS_PERMISSIONS = {"Manifest.permission.READ_CONTACTS", "Manifest.permission.CALL_PHONE", "Manifest.permission.READ_PHONE_NUMBERS"};  //this can't work
    private static final int REQUEST_CONTACTS_PERMISSIONS = 598;
    private static final String CONTACTS_PERMISSION = Manifest.permission.READ_CONTACTS;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton; //Challenge:More Dialogs
    private CheckBox mSolvedCheckBox;
    private CheckBox mSeriousCheckBox; //added by Jim
    private Button mReportButton;//Listing 15.9
    private Button mSuspectButton;//Listing 15.11  Adding field for suspect button


    private Button mReportButtonS;//Challenge: ShareCompat
    private Button mCallSuspectButton;//Challenge: Another Implicit Intent
    private String mSuspectId;//Challenge: Another Implicit Intent

    private PackageManager packageManager;

    private ImageButton mPhotoButton;//Listing 16.1
    private ImageView mPhotoView;
    private File mPhotoFile; //Listing 16.7 Grabbing photo file location
    private static final int REQUEST_PHOTO = 12;//Listing 16.8 Firing a camera intent
    private Locale mLocale;//Challenge: Localizing Dates


    /**
     * Adding permission constants for Challenge:Another Implicit Intent
     */


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        //mCrime = new Crime();
        /**Method 1: Retrieve the data from an Activity (using getActivity() to access the
         * Activity's intent directly.*/
        //  UUID crimeId = (UUID) getActivity().getIntent().getSerializableExtra(CrimeActivity.EXTRA_CRIME_ID);
        //  mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        /**Method 2: Retrieve the data from Fragment Arguments without relying on the particular
         * extra in the activity's intent.*/
        UUID crimeID = (UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime = CrimeLab.get(getActivity()).getCrime(crimeID);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);//Listing 16.7 Grabbing photo file location
        Log.i("mPhotoFile", mPhotoFile.getAbsolutePath());

    }

    @Override
    public void onPause() { //Listing 14.11
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_crime, menu);

    }


    /**
     * Adding a new action item to the CrimeFragment that allows the user to delete the
     * current crime. Once the user presses the new delete action item, be sure to pop
     * the user back to the previous activity with a call to the finish() method on the
     * CrimeFragment's hosting activity
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_crime:
                CrimeLab.get(getActivity()).deleteCrime(mCrime);
                getActivity().finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);
        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText) v.findViewById(R.id.crime_title);
        /**This line of codes is updating the View with Crime data which passed on onCreate()*/
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            /**This method returns a string*/
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString()); //save to the Crime model when onPause() is called
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mDateButton = (Button) v.findViewById(R.id.crime_date);
        updateDate();
        // mDateButton.setEnabled(false);
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager manager = getFragmentManager();
                // DatePickerFragment dialog = new DatePickerFragment();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);

            }
        });


        /** Challenge:More Dialogs */
        mTimeButton = (Button) v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });
        /** Challenge:More Dialogs */

        mSolvedCheckBox = (CheckBox) v.findViewById(R.id.crime_solved);
        /**This line of codes is updating the View with Crime data which passed on onCreate()*/
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked); //save to the Crime model when onPause() is called
                returnResult(mCrime.isSolved());
            }
        });
        /**Added by Jim: Add a serious check box to determine if its a serious crime,
         * any serious crime comes with Serious will add Button for the Crime on CrimeListFragment class*
         */

        mSeriousCheckBox = (CheckBox) v.findViewById(R.id.crime_serious);
        mSeriousCheckBox.setChecked(mCrime.isSerious());
        mSeriousCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSerious(isChecked);//save to the Crime model when onPause() is called
                returnResult(mCrime.isSerious());
            }
        });

        //Listing 15.9
        mReportButton = (Button) v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getCrimeReport());//Listing 15.10
                i.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.crime_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        //Challenge: ShareCompat
        mReportButtonS = (Button) v.findViewById(R.id.crime_report_s);
        mReportButtonS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder builder = ShareCompat.IntentBuilder.from(getActivity());
                builder.setType("text/plain");
                builder.setChooserTitle(R.string.crime_report_subject);
                builder.setText(getCrimeReport());
                builder.startChooser();
            }
        });




        //Listing 15.12 Sending an implicit intent
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        //pickContact.addCategory(Intent.CATEGORY_HOME);//Listing 15.15
        mSuspectButton = (Button) v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });
        /*******************************************************/
        //Challenge:Another Implicit Intent
        final Intent dialIntent = new Intent(Intent.ACTION_DIAL);
        mCallSuspectButton = (Button) v.findViewById(R.id.crime_dial);
        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri phoneNumber = Uri.parse("tel:" + mCrime.getPhoneNumber());
                Log.i("getPhoneNumber()", mCrime.getPhoneNumber());
                dialIntent.setData(phoneNumber);
                startActivity(dialIntent);
            }
        });

        /*******************************************************/


        /*******************************************************/
        if (mCrime.getSuspect() != null) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        //Listing 15.14 Guarding against no contacts app
        packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }


        //Listing 16.8 Firing a camera intent
        /*******************************************************/
        mPhotoButton = (ImageButton) v.findViewById(R.id.crime_camera);

        //Firing the intent
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**For a full-resolution output, you need to tell it WHERE to Save the image on the
                 * filesystem. This can be done by passing a Uri pointing to where you want to save
                 * the file in MediaStore.EXTRA_OUTPUT. This Uri will point at a location serviced
                 * by FileProvider*/


                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.criminalintent.fileprovider",
                        mPhotoFile);
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity()
                        .getPackageManager()
                        .queryIntentActivities(captureImage,
                                PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity : cameraActivities) {
                    getActivity().grantUriPermission(activity.activityInfo.packageName,
                            uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        mPhotoView = (ImageView) v.findViewById(R.id.crime_photo);
        mPhotoView.setOnClickListener(new View.OnClickListener() { //Challenge: Detail Display
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                ZoomedInDialogFragment dialog = ZoomedInDialogFragment.newInstance(mPhotoFile, mCrime.getPhotoFilename());
                //dialog.setTargetFragment(CrimeFragment.this, REQUEST_PHOTO);
                dialog.show(manager, DIALOG_PHOTO);
            }
        });

        final ViewTreeObserver observer = mPhotoView.getViewTreeObserver();//Challenge: Efficient Thumbnail Load
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                updatePhotoView(); //Listing 16.12 Calling updatePhotoView()
                mPhotoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        /*******************************************************/
        return v;
    }//End onCreateView()

    /**
     * You tell the host activity to return a value because Fragment DOESN'T have its own result
     */
    public void returnResult(boolean isChecked) {
        Intent data = new Intent();
        data.putExtra(CRIME_RESOLVED, isChecked);
        data.putExtra(CRIME_SERIOUS, isChecked);
        Log.i("returnResult", "mCrime.isCheck is " + isChecked);
        getActivity().setResult(Activity.RESULT_OK, data);
    }

    public static boolean wasAnswerSolved(Intent intentData) {
        return intentData.getBooleanExtra(CRIME_RESOLVED, false);
    }

    public static boolean wasSeriousChecked(Intent intentData) {
        return intentData.getBooleanExtra(CRIME_SERIOUS, false);
    }

    /**
     * This method is the date exchange between CrimeFragment(Target) and DatePickerFragment
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_DATE && intentData != null) {
            Date date = (Date) intentData.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mCrime.setDate(date);
            updateDate();

        } else if (requestCode == REQUEST_TIME && intentData != null) {
            Date time = (Date) intentData.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mCrime.setDate(time);
            updateTime();/** Challenge:More Dialogs */

        } else if (requestCode == REQUEST_CONTACT && intentData != null) { //Listing 15.13

            Uri contactUri = intentData.getData();


            String contactLookupKey;

            //Specify which fields you want your query to return values for
            String[] queryFields = new String[]{
                    ContactsContract.Contacts.LOOKUP_KEY,
                    ContactsContract.Contacts.DISPLAY_NAME
            };
            //Perform your query - the contactUri is like a "where" clause here.
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            try {
                //Double-Check  that you actually got result.
                if (c.getCount() == 0) {
                    return;
                }

                c.moveToFirst();
                //       int lookupColumn = c.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY);
                //       int nameColumn = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                contactLookupKey = c.getString(0);
                String suspectName = c.getString(1);
                mCrime.setSuspect(suspectName);
                mSuspectButton.setText(suspectName);

            } finally {
                c.close();
            }

            //Get the suspect's mobile phone number
            if (hasContactPermission()) {
                Log.i("hasContactPermission()", String.valueOf(hasContactPermission()));
                getSuspectPhoneNumber(contactLookupKey);

            } else {
                //This will call onRequestPermissionResult....
                requestPermissions(new String[]{CONTACTS_PERMISSION}, REQUEST_CONTACTS_PERMISSIONS);
            }
        } else if (requestCode == REQUEST_PHOTO) { //Listing 16.12  Calling updatePhotoView()
            Uri uri = FileProvider.getUriForFile(getActivity(), "com.bignerdranch.android.criminalintent.fileprovider",
                    mPhotoFile);
            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            updatePhotoView();
            mPhotoView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mPhotoView.announceForAccessibility(getString(R.string.crime_mPhotoView_announceForAccessibility));

                }
            }, 1000);

        }
    }//End onActivityResult()


    private boolean hasContactPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), CONTACTS_PERMISSION);
        Log.i("hasContactPermission()", String.valueOf(result));
        return result == PackageManager.PERMISSION_GRANTED;
    }


    private void getSuspectPhoneNumber(String contactLookupKey) {

        //The content URI of the CommonDateKinds.Phone
        Uri ContactUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

        //The columns to return for each row
        String[] queryFields = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER};

        //Does a query gains the table and returns a Cursor object
        Cursor c = getActivity().getContentResolver().query(ContactUri, queryFields,
                ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY + " = ?",
                new String[]{contactLookupKey}, null);
        try {
            //Double-check that you actually got results.
            if (c.getCount() == 0) {
                return;
            }
            c.moveToFirst();
            String phone = c.getString(0);
            mCallSuspectButton.setEnabled(true);
            mCrime.setPhoneNumber(phone);
        } finally {
            c.close();
        }
    }//End of getSuspectPhoneNumber()

    private void updateDate() {
        //save to the Crime model when onPause() is called
        java.text.DateFormat mDateFormat = DateFormat.getDateFormat(getActivity());

        String newDateFormat = mDateFormat.format(mCrime.getDate());
        // String newDateFormat = (String) DateFormat.format("EEE, MMM dd",mCrime.getDate());
        //CharSequence localeDateFormat = DateFormat.getBestDateTimePattern(mLocale,newDateFormat);
        mDateButton.setText(newDateFormat);

    }

    /**
     * Challenge:More Dialogs
     */
    private void updateTime() {

        java.text.DateFormat timeFormat = DateFormat.getTimeFormat(getActivity());
        //java.text.DateFormat mDateFormat = DateFormat.getDateFormat(getAppContext());
        //  String newDateFormat = mDateFormat.format(mCrime.getDate());
        String newTimeFormat = timeFormat.format(mCrime.getDate());
        Log.i("newTimeFormat", newTimeFormat);
        mTimeButton.setText(newTimeFormat);


    }


    private String getCrimeReport() {//Listing 15.8
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dataFormat = "EEE, MMM dd";
        String dataString = DateFormat.format(dataFormat, mCrime.getDate()).toString();

        String suspect = mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);//the second argument is mCrime.getSuspect()
        }

        String report = getString(R.string.crime_report,
                mCrime.getTitle(), dataString, solvedString, suspect);
        return report;
    }//End getCrimeReport()

    private void updatePhotoView() { //Listing 16.11 Updating mPhotoView
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_no_image_description)); //Listing 19.4  Dynamically setting content description
        } else {
            /*
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.outHeight = mPhotoView.getMaxHeight();
            options.outWidth = mPhotoView.getMaxWidth();
            Bitmap bitMap = BitmapFactory.decodeFile(mPhotoFile.getPath(), options);
//the above codes does not work well
             */

            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setContentDescription(getString(R.string.crime_photo_image_description)); //Listing 19.4  Dynamically setting content description

        }
    }


}
