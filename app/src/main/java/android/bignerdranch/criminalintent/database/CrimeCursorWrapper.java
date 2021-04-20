package android.bignerdranch.criminalintent.database;

import android.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable;
import android.bignerdranch.criminalintent.model.Crime;
import android.database.Cursor;
import android.database.CursorWrapper;

import java.util.Date;
import java.util.UUID;

public class CrimeCursorWrapper extends CursorWrapper {//Listing 14.13

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public CrimeCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Crime getCrime() { //Listing 14.14
        String uuidString = getString(getColumnIndex(CrimeTable.Cols.UUID));
        String title = getString(getColumnIndex(CrimeTable.Cols.TITLE));
        long date = getLong(getColumnIndex(CrimeTable.Cols.DATE));
        int isSolved = getInt(getColumnIndex(CrimeTable.Cols.SOLVED));
        int isSerious = getInt(getColumnIndex(CrimeTable.Cols.SERIOUS));
        String suspect = getString(getColumnIndex(CrimeTable.Cols.SUSPECT));//Listing 15.6
        String phoneNumber  = getString(getColumnIndex(CrimeTable.Cols.PHONE_NUMBER));//Challenge 2
        //Listing 14.16
        Crime crime = new Crime(UUID.fromString(uuidString));
        crime.setTitle(title);
        crime.setDate(new Date(date));
        crime.setSolved(isSolved != 0);
        crime.setSerious(isSerious != 0);
        crime.setSuspect(suspect);//Listing 15.6
        crime.setPhoneNumber(phoneNumber);//Challenge 2

        return crime;
    }

}
