package android.bignerdranch.criminalintent.model;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class Crime {


    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private boolean mSerious;
    private String mSuspect; //Listing 15.2
    private String mPhoneNumber;//Challenge: Another Implicit Intent


    /**
     * Constructor
     */
    public Crime() {
        this(UUID.randomUUID()); //Listing 14.15
        // mId = UUID.randomUUID();
        // mDate = new Date();
    }

    public Crime(UUID id) {//Listing 14.15
        mId = id;
        mDate = new Date();
    }


    /**
     * Getter() & Setter() methods
     */


    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public void setSerious(boolean serious) {
        mSerious = serious;
    }

    public boolean isSerious() {
        return mSerious;
    }

    public String getSuspect() {//Listing 15.2
        return mSuspect;
    }

    public void setSuspect(String suspect) {//Listing 15.2
        this.mSuspect = suspect;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.mPhoneNumber = phoneNumber;
    }


    //Listing 16.5 Adding the filename-derived property
    public String getPhotoFilename(){
        return "IMG_" +getId().toString()+".jpg";
    }

}
