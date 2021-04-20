package android.bignerdranch.criminalintent;

import android.os.Bundle;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

public abstract class SingleFragmentActivity extends AppCompatActivity {

    protected abstract Fragment createFragment();

    @LayoutRes
    protected int getLayoutResId(){ //Listing 17.1 Making SingleFragmentActivity Flexible
        return R.layout.activity_fragment;
    }
    /**Called when the activity is first created.*/
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       // setContentView(R.layout.activity_fragment);
          setContentView(getLayoutResId()); //Listing 17.1
        /**This call to CrimeActivity.onCreate(Bundle) could be in response to CrimeActivity being
         * re-created after being destroyed on rotation or to reclaim memory.
         * When an activity is destroyed, its FragmentManager saves out its list of fragments.
         * When the activity is re-created, the new FragmentManager retrieves the list and re-creates the listed
         * fragments to make everything as it was before.*/
        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        if(fragment == null){
            fragment = createFragment();
            fm.beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }
}
