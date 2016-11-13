package com.itute.dating.service;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.itute.dating.base.model.DeviceToken;
import com.itute.dating.util.Constants;

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
    }
}
