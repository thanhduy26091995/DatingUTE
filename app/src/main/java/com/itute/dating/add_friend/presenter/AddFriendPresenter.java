package com.itute.dating.add_friend.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.itute.dating.add_friend.model.AddFriendSubmitter;
import com.itute.dating.add_friend.model.AddFriendViewHolder;
import com.itute.dating.add_friend.view.AddFriendFragment;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 02/11/2016.
 */
public class AddFriendPresenter {
    private AddFriendFragment view;
    private AddFriendSubmitter submitter;
    private DatabaseReference mDatabase;

    public AddFriendPresenter(AddFriendFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new AddFriendSubmitter(mDatabase);
    }

    //get all friend add
    public Query getAllFriend(String uid) {
        return submitter.getAllFriend(uid);
    }
}
