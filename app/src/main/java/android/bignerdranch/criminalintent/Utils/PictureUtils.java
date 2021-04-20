package android.bignerdranch.criminalintent.Utils;

import android.app.Activity;
import android.bignerdranch.criminalintent.R;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;


public class PictureUtils {

    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {//Listing 16.9 Creating getScaledBitmap(...)

        //Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        Log.i("getScaledBitmap,", "srcWidth = " + srcWidth);
        float srcHeight = options.outHeight;
        Log.i("getScaledBitmap,", "srcHeight = " + srcHeight);
        //Figure out how much to scale down by
        int newInSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            Log.i("getScaledBitmap,", "heightScale = " + heightScale);
            float widthScale = srcWidth / destWidth;
            Log.i("getScaledBitmap,", "widthScale = " + widthScale);
            newInSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
            Log.i("getScaledBitmap,", "newInSampleSize = " + newInSampleSize);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = newInSampleSize;

        //Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    public static Bitmap getScaledBitmap(String path, Activity activity) { //Listing 16.10 Writing conservative scale method
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }


}//End of class
