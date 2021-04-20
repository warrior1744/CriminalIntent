package android.bignerdranch.criminalintent;

import android.bignerdranch.criminalintent.model.Crime;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class CrimeListActivity extends SingleFragmentActivity
implements CrimeListFragment.Callbacks{ //Listing 17.7 Implementing callbacks


    
    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }

    @Override
    protected int getLayoutResId(){ //Listing 17.2 Changing to two-pane layout file
       // return R.layout.activity_twopane;
        return R.layout.activity_masterdetail; //Listing 17.4 Switching layout again
    }


    @Override
    public void onCrimeSelected(Crime crime, int requestCode) { //Listing 17.7 Implementing callbacks
                                               //Listing 17.8 Conditional CrimeFragment startup
        if (findViewById(R.id.detail_fragment_container) == null){
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivityForResult(intent, requestCode);
        } else {
            Fragment newDetail = CrimeFragment.newInstance(crime.getId());
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_fragment_container, newDetail)
                    .commit();
        }
    }
}
