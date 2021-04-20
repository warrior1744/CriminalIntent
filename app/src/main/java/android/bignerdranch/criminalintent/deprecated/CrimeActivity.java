package android.bignerdranch.criminalintent.deprecated;

import androidx.fragment.app.Fragment;

import android.bignerdranch.criminalintent.CrimeFragment;
import android.bignerdranch.criminalintent.SingleFragmentActivity;
import android.content.Context;
import android.content.Intent;

import java.util.UUID;

public class CrimeActivity extends SingleFragmentActivity {

public static final String EXTRA_CRIME_ID = "com.bignerdranch.android.criminalintent.crime_id";



public static Intent newIntent(Context packageContext, UUID crimeId){
    Intent intent = new Intent(packageContext, CrimeActivity.class);
    intent.putExtra(EXTRA_CRIME_ID, crimeId);
    return intent;
}

/**In Geo Quiz, we pass data between activities and get result from the child Activity and callback to the parent activity.
 * In this section, we're sending the result back to the Fragment (CrimeListFragment) which start this activity For result
 *
 * There are two methods you can call in the activity to send data back to the Fragment
 *
 * public final void setResult(int resultCode)
 * public final void setResult(int resultCode, Intent data)*/

    @Override
    protected Fragment createFragment() {
    UUID crimeId = (UUID) getIntent().getSerializableExtra(EXTRA_CRIME_ID);
        return CrimeFragment.newInstance(crimeId);
    }
}
