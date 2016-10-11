package com.itute.dating.profile_user.model;

import com.google.firebase.database.DatabaseReference;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 11/10/2016.
 */
public class ProfileUserSubmitter {
    private DatabaseReference mDatabase;

    public ProfileUserSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //get user theo uid
    public DatabaseReference getUser(String uid) {
        return mDatabase.child(Constants.USERS).child(uid);
    }


}
