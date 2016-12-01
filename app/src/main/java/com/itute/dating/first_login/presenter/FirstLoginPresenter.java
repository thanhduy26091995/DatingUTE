package com.itute.dating.first_login.presenter;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.view.GoogleAuthController;
import com.itute.dating.first_login.model.FirstLoginSubmitter;
import com.itute.dating.first_login.view.FirstLoginActivity;
import com.itute.dating.main.view.MainActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by buivu on 09/10/2016.
 */
public class FirstLoginPresenter {
    private FirstLoginActivity view;
    private DatabaseReference mDatabase;
    private FirstLoginSubmitter submitter;

    public FirstLoginPresenter(FirstLoginActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new FirstLoginSubmitter(mDatabase);
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

    public void updateDataUser(final String uid, final int gender, final Map<String, Object> address, final long dateOfBirth, final int old) {
        mDatabase.child(Constants.USERS).child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user == null) {
                        view.showToast(view.getResources().getString(R.string.loiThongTinNguoiDung));
                    } else {
                        submitter.updateDataUser(uid, gender, address, dateOfBirth, old);
                        view.startActivity(new Intent(view, MainActivity.class));
                        view.finish();
                        view.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //log out facebook and google
    public void logOut(final GoogleApiClient mGoogleApiClient) {
        GoogleAuthController.getInstance().signOut(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(@NonNull Status status) {
                            if (status.isSuccess()) {
                                view.finish();
                            }
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("Error", "GoogleSubmitter API Client Connection Suspended");
            }
        });
    }
}
