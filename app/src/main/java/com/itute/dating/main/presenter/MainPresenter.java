package com.itute.dating.main.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.itute.dating.main.model.MainSubmitter;
import com.itute.dating.main.view.MainActivity;

/**
 * Created by buivu on 09/10/2016.
 */
public class MainPresenter {
    private MainActivity view;
    private MainSubmitter submitter;
    private DatabaseReference mDatabase;

    public MainPresenter(MainActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new MainSubmitter(mDatabase);
    }

    public DatabaseReference getUser(String uid) {
        return submitter.getUser(uid);
    }
}
