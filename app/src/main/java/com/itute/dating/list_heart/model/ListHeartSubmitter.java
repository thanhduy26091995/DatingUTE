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

    //lấy danh sách các bạn đã thả tim
    public Query getAllHeart(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.HEARTS);
    }

    //lấy danh sách bạn bè
    public Query getAlllFriends(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.FRIENDS);
    }
}

