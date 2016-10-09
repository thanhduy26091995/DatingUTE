package com.itute.dating.main.model;

import com.google.firebase.database.DatabaseReference;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 09/10/2016.
 */
public class MainSubmitter {
    private DatabaseReference mDatabase;

    public MainSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public DatabaseReference getUser(String uid) {
        return mDatabase.child(Constants.USERS).child(uid);
    }
}
