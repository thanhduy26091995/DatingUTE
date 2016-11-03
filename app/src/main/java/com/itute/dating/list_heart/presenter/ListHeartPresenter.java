package com.itute.dating.list_heart.presenter;

import android.support.v4.app.Fragment;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.itute.dating.list_heart.model.ListHeartSubmitter;
import com.itute.dating.list_heart.view.ListHeartFragment;

/**
 * Created by buivu on 16/10/2016.
 */
public class ListHeartPresenter {
    private Fragment view;
    private ListHeartSubmitter submitter;
    private DatabaseReference mDatabase;

    public ListHeartPresenter(Fragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ListHeartSubmitter(mDatabase);
    }

    public Query getAllHeart(String uid) {
        return submitter.getAllHeart(uid);
    }

    //get all friends
    public Query getAllFriends(String uid) {
        return submitter.getAlllFriends(uid);
    }
}
