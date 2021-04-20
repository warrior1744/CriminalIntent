package android.bignerdranch.criminalintent.database;

public class CrimeDbSchema {

    public static final class CrimeTable{

        public static final String NAME = "crimes";



        public static final class Cols{

            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String SERIOUS = "serious"; //added by Jim
            public static final String SUSPECT = "suspect"; //Listing 15.3
            public static final String PHONE_NUMBER = "phone_number"; //Challenge 2
        }
    }
}
