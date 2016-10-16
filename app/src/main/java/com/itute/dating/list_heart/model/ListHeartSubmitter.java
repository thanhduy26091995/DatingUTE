package com.itute.dating.list_heart.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 16/10/2016.
 */
public class ListHeartSubmitter {
    private DatabaseReference mDatabase;

    public ListHeartSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    public Query getAllHeart(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.HEARTS);

    }
}
