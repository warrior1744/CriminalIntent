package android.bignerdranch.criminalintent.database;

import android.bignerdranch.criminalintent.database.CrimeDbSchema.CrimeTable; //import static inner class
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CrimeBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "crimeBase.db";

    public CrimeBaseHelper(Context context) { //Listing 14.3
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) { //Listing 14.3
        db.execSQL("create table " + CrimeTable.NAME + "(" + " _id integer primary key autoincrement, " +
                CrimeTable.Cols.UUID   + ", " +
                CrimeTable.Cols.TITLE  + ", " +
                CrimeTable.Cols.DATE   + ", " +
                CrimeTable.Cols.SOLVED + ", " +
                CrimeTable.Cols.SERIOUS+ ", " +
                CrimeTable.Cols.SUSPECT+ ", " +
                CrimeTable.Cols.PHONE_NUMBER  + ")" //Listing 15.4
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { //Listing 14.3

    }
}
