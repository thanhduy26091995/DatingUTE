package com.itute.dating.settings.model;

import com.google.firebase.database.DatabaseReference;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 10/10/2016.
 */
public class SettingsSubmitter {
    private DatabaseReference mDatabase;

    public SettingsSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }
    public DatabaseReference getUser(String uid) {
        return mDatabase.child(Constants.USERS).child(uid);
    }

}
