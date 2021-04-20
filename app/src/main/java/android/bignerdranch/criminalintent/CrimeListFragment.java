package android.bignerdranch.criminalintent;

import android.app.Activity;
import android.bignerdranch.criminalintent.model.Crime;
import android.bignerdranch.criminalintent.model.CrimeLab;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.UUID;

public class CrimeListFragment extends Fragment {

    private static final int REQUEST_CRIME = 1;
    private static final int REQUEST_CRIME_SERIOUS = 2;

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";


    /**
     * We use getAdapterPosition for the current item position of the recycler view.
     */
    private int mLastUpdatedPosition = -1;
    private RecyclerView mCrimeRecyclerView;
    private CrimeAdapter mAdapter;
    private boolean mSubtitleVisible;
    private Callbacks mCallbacks; //Listing 17.6 Adding callback interface

    // private View mFirstCrimeLayout; //Chapter 13: Challenge 3
    //  private Button mAddFirstCrimeButton; //Chapter 13: Challenge 3

    /**
     * Required interface for hosting activities
     */
    public interface Callbacks { //Listing 17.6 Adding callback interface
        void onCrimeSelected(Crime crime, int requestCode);
    }

    @Override
    public void onAttach(Context context) { //Listing 17.6 Adding callback interface
        super.onAttach(context);
        mCallbacks = (Callbacks) context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        setHasOptionsMenu(true); //Telling the FragmentManager that CrimeListFragment needs to receive menu callbacks.
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    @Override
    public void onDetach() { //Listing 17.6 Adding callback interface
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * Chapter 13: The Toolbar
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);//Conventional work, any menu functionality defined by the superclass will still work.
        inflater.inflate(R.menu.fragment_crime_list, menu);

        MenuItem subTitleItem = menu.findItem(R.id.show_subtitle);
        if (mSubtitleVisible) {
            subTitleItem.setTitle(R.string.hide_subtitle);
        } else {
            subTitleItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.new_crime:
                Crime crime = new Crime();
                CrimeLab.get(getActivity()).addCrime(crime);
                // Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                // startActivity(intent);
                updateUI();
                mCallbacks.onCrimeSelected(crime, 0); //Listing 17.9 Calling all callbacks!
                return true;
            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.get(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subTitle;
        if (crimeCount == 0) {
            subTitle = getString(R.string.subtitle_format);
        } else {
            subTitle = getResources().getQuantityString(R.plurals.subtitle_plural, crimeCount, crimeCount);
        }

        /**Showing or hiding the subtitle in the toolbar*/
        if (!mSubtitleVisible) {
            subTitle = null;
        }
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subTitle);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_crime_list, container, false);
        mCrimeRecyclerView = (RecyclerView) view.findViewById(R.id.crime_recycler_view);
        mCrimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(mAdapter));
        itemTouchHelper.attachToRecyclerView(mCrimeRecyclerView);


        return view;
    }

    private void updateUI() {

        List<Crime> crimes = CrimeLab.get(getActivity()).getCrimes();
        // mFirstCrimeLayout.setVisibility((crimes.size() > 0? View.GONE : View.VISIBLE));

        if (mAdapter == null) {
            mAdapter = new CrimeAdapter(crimes);
            mCrimeRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setCrimes(crimes); //Listing 14.21
            /**Explaination: When we added a new Crime on the onOptionsItemSelected() method,
             we didn't explicitly update the crimes data for the Adapter, surely the object was added
             to the List, but the Adapter didn't, so we to set the updated crimes for the Adapter,
             the Adapter's crimes data is created when there is no Adapter, so the Adapter
             needs to be either updated in order to meet the requirements.*/
            if (mLastUpdatedPosition > 0) {
                mAdapter.notifyItemChanged(mLastUpdatedPosition);
                /**notifyItemRemoved needs to be call once the item are removed rather than changed in
                 * order to meet the consistency of the Index*/
                mAdapter.notifyItemRemoved(mLastUpdatedPosition);
                mLastUpdatedPosition = -1;
            } else {
                mAdapter.notifyDataSetChanged();
            }
        }
        updateSubtitle();
    }//End updateUI()


    /***********************Inner Class CrimeAdapter*********************/
    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {

        private List<Crime> mCrimes;

        public CrimeAdapter(List<Crime> crimes) {
            mCrimes = crimes;
        }

        @NonNull
        @Override
        public CrimeHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            if (viewType == 1) {
                return new NormalCrimeHolder(layoutInflater, parent);
            } else {
                return new SeriousCrimeHolder(layoutInflater, parent);
            }
        }

        @Override
        public void onBindViewHolder(@NonNull CrimeHolder holder, int position) {
            Crime crime = mCrimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return mCrimes.size();
        }

        /**
         * This override method is optional. It is called before onCreateViewHolder(),
         * and AFTER getItemCount(). It get the position of the stable ID of the adapter.
         */
        @Override
        public int getItemViewType(int position) {

            if (mCrimes.get(position).isSerious()) {
                return 0;
            } else {
                return 1;
            }
        }

        public void setCrimes(List<Crime> crimes) { //Listing 14.20
            mCrimes = crimes;

            for (Crime crime : crimes) {
                UUID id = crime.getId();
                Log.i("setCrimes", String.valueOf(id));
            }
        }

        public void deleteItem(int position) {
            Log.i("deleteItem", String.valueOf(position));

            Crime crime = mCrimes.get(position);

            mLastUpdatedPosition = position;
            CrimeLab.get(getActivity()).deleteCrime(crime);
            mCrimes.remove(position);
            notifyItemRemoved(position);
        }


    }//End of Inner

    /***********************Inner Class CrimeHolder*********************/
    private class NormalCrimeHolder extends CrimeHolder {

        public NormalCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent, R.layout.list_item_crime);
        }//End NormalCrimeHolder's Constructor

        @Override
        public void onClick(View v) {
            /**Method 1: starting an Activity from fragment (creates an explicit intent)*/
            // Intent intent = new Intent(getActivity(),CrimeActivity.class);
            /**Method 2: staring an Activity by calling the Activity static method, put the Extra,
             * and receive the returned intent*/
            //Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            /**get updated data position and pass the value to mLastUpdatedPosition*/
            mLastUpdatedPosition = this.getBindingAdapterPosition();
            /**Getting Result from the started intent*/
            //startActivityForResult(intent, REQUEST_CRIME);
            mCallbacks.onCrimeSelected(mCrime, REQUEST_CRIME);
        }

        @Override
        public void bind(Crime crime){
            super.bind(crime);
            //Challenge: Improving the List
            mSolvedImageView.setContentDescription(crime.isSolved()? getString(R.string.crime_list_with_handcuff_icon_description) :
                    getString(R.string.crime_list_no_handcuff_icon_description));

        }
    }//End Inner class

    /***********************Inner Class CrimeHolder for Serious*********************/
    private class SeriousCrimeHolder extends CrimeHolder {

        private Button mButtonSerious;

        public SeriousCrimeHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater, parent, R.layout.list_item_crime_serious);
            mButtonSerious = (Button) itemView.findViewById(R.id.crime_contact);
            mButtonSerious.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getActivity(), mCrime.getTitle() + " is called to the police", Toast.LENGTH_SHORT).show();
                }
            });
        }//End SeriousCrimeHolder's Constructor

        @Override
        public void onClick(View v) {
            /**Method 1: starting an Activity from fragment (creates an explicit intent)*/
            // Intent intent = new Intent(getActivity(),CrimeActivity.class);
            /**Method 2: staring an Activity by calling the Activity static method, put the Extra,
             * and receive the returned intent*/
            // Intent intent = CrimePagerActivity.newIntent(getActivity(), mCrime.getId());
            mLastUpdatedPosition = this.getBindingAdapterPosition();
            /**Getting Result from the started intent*/
            // startActivityForResult(intent, REQUEST_CRIME_SERIOUS);
            mCallbacks.onCrimeSelected(mCrime, REQUEST_CRIME_SERIOUS);
        }
        @Override
        public void bind(Crime crime){
            super.bind(crime);

            //Challenge: Improving the List
            mSolvedImageView.setContentDescription(crime.isSolved()? getString(R.string.crime_list_with_handcuff_icon_description) :
                    getString(R.string.crime_list_no_handcuff_icon_description));

        }

    }//End inner class

    /**
     * For more information about passing data between Activities please
     * refer to 5.Your Second Activity : Passing Data between Activities
     * Please note that the codes within onActivityResult() method doesn't actually do anything but
     * rather test the passing result from child activity/fragment when it finished its tasks.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intentData) {
        Log.i("onActivityResult", "request code is " + requestCode);
        Log.i("onActivityResult", "result code is " + resultCode);
        Log.i("onActivityResult", "Activity.RESULT_OK is " + Activity.RESULT_OK);
        Log.i("onActivityResult", "Activity.RESULT_CANCELED is " + Activity.RESULT_CANCELED);

        if (resultCode != Activity.RESULT_OK) {
            Log.i("onActivityResult", "resultCode != Activity.RESULT_OK is " + (resultCode != Activity.RESULT_OK));
            return;
        }
        if (requestCode == REQUEST_CRIME) {
            Log.i("onActivityResult", "requestCode == REQUEST_CRIME is " + (requestCode == REQUEST_CRIME));
            Log.i("onActivityResult", "Intent data is " + CrimeFragment.wasAnswerSolved(intentData));
            Log.i("onActivityResult", "Intent data is " + CrimeFragment.wasSeriousChecked(intentData));
        } else if (requestCode == REQUEST_CRIME_SERIOUS) {
            Log.i("onActivityResult", "requestCode == REQUEST_CRIME_SERIOUS is " + (requestCode == REQUEST_CRIME_SERIOUS));
            Log.i("onActivityResult", "Intent data is " + CrimeFragment.wasAnswerSolved(intentData));
            Log.i("onActivityResult", "Intent data is " + CrimeFragment.wasSeriousChecked(intentData));
        }
    }//End onActivityResult()

    public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {


        public SwipeToDeleteCallback(CrimeAdapter adapter) {
            super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
            mAdapter = adapter;
        }


        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAbsoluteAdapterPosition();
            mAdapter.deleteItem(position);
        }


    }


}//End of Outer class
