package com.itute.dating.sign_in.model;

import com.google.firebase.database.DatabaseReference;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 06/10/2016.
 */
public class Submitter {
    private DatabaseReference mDatabase;

    public Submitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public void addNewUser(String uid, String displayName, String photoURL, Map<String, Object> address, String phone, int gender, long timestamp,
                           String hobby, String star, String job, String language, String religion) {
        User user = new User(displayName, photoURL, address, phone, gender, timestamp, hobby, star, job, language, religion, uid);
        Map<String, Object> myMap = new HashMap<>();
        myMap = user.toMap();
        mDatabase.child(Constants.USERS).child(uid).setValue(myMap);
    }

    public void updateState(String uid, boolean state) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.IS_LOGIN, state);
        mDatabase.child(Constants.USERS).child(uid).updateChildren(myMap);
    }
}
