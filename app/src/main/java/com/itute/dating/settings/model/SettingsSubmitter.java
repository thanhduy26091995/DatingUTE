package com.itute.dating.settings.model;

import com.google.firebase.database.DatabaseReference;
import com.itute.dating.util.Constants;

import java.util.HashMap;
import java.util.Map;

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

    //cập nhật isLogin = false nếu log out
    public void updateState(String uid, boolean state) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.IS_LOGIN, state);
        mDatabase.child(Constants.USERS).child(uid).updateChildren(myMap);
    }

    //cập nhật gender search
    public void updateSearchGender(String uid, int gender) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.GENDER, gender);
        mDatabase.child(Constants.USERS).child(uid).child(Constants.SEARCH).updateChildren(myMap);
    }

    //cập nhật độ tuổi
    public void updateAge(String uid, int left, int right) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.FROM, left);
        myMap.put(Constants.TO, right);
        mDatabase.child(Constants.USERS).child(uid).child(Constants.SEARCH).updateChildren(myMap);
    }

}
