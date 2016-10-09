package com.itute.dating.first_login.model;

import com.google.firebase.database.DatabaseReference;
import com.itute.dating.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 09/10/2016.
 */
public class FirstLoginSubmitter {
    private DatabaseReference mDatabase;

    public FirstLoginSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void updateDataUser(String uid, int gender, Map<String, Object> address, long dateOfBirth) {
        Map<String, Object> updateChildren = new HashMap<>();
        updateChildren.put(Constants.GENDER, gender);
        updateChildren.put(Constants.ADDRESS, address);
        updateChildren.put(Constants.DATE_OF_BIRTH, dateOfBirth);
        mDatabase.child(Constants.USERS).child(uid).updateChildren(updateChildren);
    }
}
