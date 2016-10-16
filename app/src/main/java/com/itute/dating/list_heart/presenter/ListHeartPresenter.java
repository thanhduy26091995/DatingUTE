package com.itute.dating.list_heart.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.itute.dating.list_heart.model.ListHeartSubmitter;
import com.itute.dating.list_heart.view.ListHeartActivity;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 16/10/2016.
 */
public class ListHeartPresenter {
    private ListHeartActivity view;
    private ListHeartSubmitter submitter;
    private DatabaseReference mDatabase;

    public ListHeartPresenter(ListHeartActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ListHeartSubmitter(mDatabase);
    }

    public Query getAllHeart(String uid) {
        return submitter.getAllHeart(uid);

    }
}
