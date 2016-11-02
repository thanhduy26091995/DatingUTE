package com.itute.dating.user_list.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 08/10/2016.
 */
public class UserListSubmitter {
    private DatabaseReference mDatabase;

    public UserListSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //lấy toàn bộ danh sách User
    public Query getUserList() {
        Query userList = mDatabase.child(Constants.USERS).orderByChild(Constants.TIMESTAMP);
        return userList;
    }

    //lấy danh sách User có giới tính Nam
    public Query getUserGender(int gender){
        Query userGender = mDatabase.child(Constants.USERS).orderByChild(Constants.GENDER).equalTo(gender);
        return userGender;
    }
}
