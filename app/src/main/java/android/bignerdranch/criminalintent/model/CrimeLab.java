package android.bignerdranch.criminalintent.model;

import android.bignerdranch.criminalintent.database.CrimeBaseHelper;
import android.bignerdranch.criminalintent.database.CrimeCursorWrapper;
import android.bignerdranch.criminalintent.database.CrimeDbSchema;
import android.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class CrimeLab {

    private static CrimeLab sCrimeLab;
    // private List<Crime> mCrimes;
    //  private Map<UUID, Crime> mCrimes; //using mDatabase instead
    private Context mContext; //Listing 14.4
    private SQLiteDatabase mDatabase; //Listing 14.4


    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    /**
     * when the constructor is called by the internal static method get(),
     * a hundred of Crime will be created and put into the ArrayList
     */
    private CrimeLab(Context context) {

        mContext = context.getApplicationContext();//Listing 14.4
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();//Listing 14.4
        //  mCrimes = new LinkedHashMap<>();//using mDatabase instead

        /*
        for (int i=0;i<100;i++){
            Crime crime = new Crime();
            crime.setTitle("Crime #"+i);
            crime.setSolved((i%2 ==0)); //Every other one will be set as True
            crime.setSerious((i%5==0)); //Every fifth item will be set as Serious<Crime>
            mCrimes.put(crime.getId(),crime);
        }*/
    }

    /**
     * dataStore class: ContentValues
     */
    private static ContentValues getContentValues(Crime crime) {//Listing 14.8
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SERIOUS, crime.isSerious() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, crime.getSuspect());//Listing 15.5
        values.put(CrimeTable.Cols.PHONE_NUMBER, crime.getPhoneNumber());//Challenge 2
/**conditional operator. BooleanExpression ? Expr1 : Expr2
 * The BooleanExpression is evaluated.If it's true, the value of the whole expression is Expr1.
 * If it's false, the value of the whole expression is Expr2*/
        return values;
    }



    /**
     * add, remove, and update Crime object
     */
    //Add a new Crime to the list
    public void addCrime(Crime crime) { //Listing 14.9
        //    mCrimes.put(crime.getId(), crime); //using mDatabase instead

        ContentValues values = getContentValues(crime);
        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void deleteCrime(Crime crime) { //added by Jim
        //   mCrimes.remove(crime.getId(), crime); //using mDatabase instead
        String uuidString = crime.getId().toString();

        //delete(String table, String whereClause, String[] whereArgs)
        mDatabase.delete(CrimeTable.NAME,
                CrimeTable.Cols.UUID + " =?",
                new String[]{uuidString}); //similar to WHERE statement
    }


    public void updateCrime(Crime crime) { //Listing 14.10
        String uuidString = crime.getId().toString();
        ContentValues values = getContentValues(crime);

        //update(String table, ContentValues values, String whereClause, String[] whereArgs)
        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " =?",
                new String[]{uuidString});  //similar to WHERE statement
    }
    /*****************************************/




    public List<Crime> getCrimes() { //Listing 14.18
        //     return new ArrayList<>(mCrimes.values());//using mDatabase instead

        List<Crime> crimes = new ArrayList<>();
        CrimeCursorWrapper cursor = queryCrimes(null, null);
        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) { // the pointer is NOT yet the end of the data set
                crimes.add(cursor.getCrime());
                cursor.moveToNext(); //Advance to a new row
            }
        } finally {
            Log.i("Cursor reading status", "isAfterLast is " + cursor.isAfterLast()); //added by Jim
            cursor.close();
        }
        return crimes;
    }//End getCrimes


    // private Cursor queryCrimes(String whereClause, String[] whereArgs) { //Listing 14.12
    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs) { //Listing 14.17
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, //columns, null selects all columns
                whereClause, //first parameter
                whereArgs, //second parameter
                null, //group by
                null,//having
                null //orderBy
        );
        return new CrimeCursorWrapper(cursor);
    }//End queryCrimes()



    public Crime getCrime(UUID id) {
        /*
        for (Crime crime : mCrimes) {
            if (crime.getId().equals(id)) {
                return crime;
            }
        }
        return null;
        */
        //  return mCrimes.get(id); //using mDatabase instead

        //Listing 14.19
        CrimeCursorWrapper cursor = queryCrimes(CrimeTable.Cols.UUID + " =?",
                new String[]{id.toString()});
        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getCrime();
        } finally {
            cursor.close();
        }
    }//Close getCrime()
    /*****************************************/


    //Listing 16.6 Finding photo file location
    public File getPhotoFile(Crime crime){
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, crime.getPhotoFilename());
    }

}
