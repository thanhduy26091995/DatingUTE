package com.itute.dating.user_list.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.itute.dating.user_list.model.UserListSubmitter;
import com.itute.dating.user_list.view.UserListFragment;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 08/10/2016.
 */
public class UserListPresenter {
    private UserListSubmitter submitter;
    private UserListFragment view;
    private DatabaseReference mDatabase;

    public UserListPresenter(UserListFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new UserListSubmitter(mDatabase);
    }

    //lấy danh sách User
    public Query getUserList() {
        return submitter.getUserList();
    }
}
