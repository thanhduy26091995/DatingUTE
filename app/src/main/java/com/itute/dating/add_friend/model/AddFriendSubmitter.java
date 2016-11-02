package com.itute.dating.add_friend.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 02/11/2016.
 */
public class AddFriendSubmitter {
    private DatabaseReference mDatabase;

    public AddFriendSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //get all friend add
    public Query getAllFriend(String uid) {
        return mDatabase.child(Constants.USERS).child(uid).child(Constants.REQUESTS);
    }
}
