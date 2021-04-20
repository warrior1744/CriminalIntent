package android.bignerdranch.criminalintent.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.bignerdranch.criminalintent.R;
import android.bignerdranch.criminalintent.Utils.PictureUtils;
import android.bignerdranch.criminalintent.model.Crime;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.File;
import java.util.Date;

public class ZoomedInDialogFragment extends DialogFragment {//Challenge: Detail Display

    private static final String ARG_PICTURE = "picture";
    private ImageView mPhotoZoomed;
    private static String mPhotoFileName;

    public static ZoomedInDialogFragment newInstance(File file, String photoFileName) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PICTURE, file);
        ZoomedInDialogFragment fragment = new ZoomedInDialogFragment();
        fragment.setArguments(args);
        mPhotoFileName = photoFileName;
        return fragment;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        File file = (File) getArguments().getSerializable(ARG_PICTURE);
        Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), 300, 300);

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_zoomedin, null);
        mPhotoZoomed = (ImageView) v.findViewById(R.id.crime_photo_zoomed);
        mPhotoZoomed.setImageBitmap(bitmap);
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(mPhotoFileName)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        return;
                    }
                })
                .create();
    }


}
