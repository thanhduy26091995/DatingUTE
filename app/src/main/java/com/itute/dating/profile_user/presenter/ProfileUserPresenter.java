package com.itute.dating.profile_user.presenter;

import android.content.Intent;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itute.dating.R;
import com.itute.dating.profile_user.model.ProfileUserSubmitter;
import com.itute.dating.profile_user.view.ProfileUserActivity;
import com.itute.dating.util.Constants;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by buivu on 11/10/2016.
 */
public class ProfileUserPresenter {
    private ProfileUserSubmitter submitter;
    private ProfileUserActivity view;
    private DatabaseReference mDatabase;

    public ProfileUserPresenter(ProfileUserActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ProfileUserSubmitter(mDatabase);
    }

    //get user theo uid
    public DatabaseReference getUser(String uid) {
        return submitter.getUser(uid);
    }

    //hàm hiển thị DatePickerDialog
    public void onPickDateStart() {
        Calendar now = Calendar.getInstance();
        TimeZone timeZone = now.getTimeZone();
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                view,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );
        dpd.vibrate(true);
        dpd.setThemeDark(false);
        dpd.setAccentColor(view.getResources().getColor(R.color.mdtp_accent_color));
        dpd.show(view.getFragmentManager(), "Datepickerdialog");
    }

    //mở google place
    public void showPlace() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        Intent myIntent;

        try {
            myIntent = builder.build(view);
            view.startActivityForResult(myIntent, Constants.PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }
}
