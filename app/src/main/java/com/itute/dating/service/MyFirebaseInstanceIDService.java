package com.itute.dating.service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.itute.dating.base.model.DeviceToken;
import com.itute.dating.notification.Config;

/**
 * Created by buivu on 28/10/2016.
 */
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    private static final String TAG = "InstanceIDService";
    private DatabaseReference mDatabase;

    @Override
    public void onTokenRefresh() {

        try {
            String tokenRefresh = FirebaseInstanceId.getInstance().getToken();
            //cập nhật database
            mDatabase = FirebaseDatabase.getInstance().getReference();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DeviceToken.getInstance().addDeviceToken(mDatabase, uid, tokenRefresh);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        // Saving reg id to shared preferences
        storeRegIdInPref(refreshedToken);

        // sending reg id to your server
        sendRegistrationToServer(refreshedToken);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void sendRegistrationToServer(final String token) {
        // sending gcm token to server
        Log.e(TAG, "sendRegistrationToServer: " + token);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
