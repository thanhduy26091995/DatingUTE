package com.itute.dating.user_list.presenter;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.itute.dating.profile_user.model.User;
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

    //heart clicked
    public void onHeartClicked(DatabaseReference databaseReference, final String uid) {
        databaseReference.runTransaction(
                new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        User user = mutableData.getValue(User.class);
                        if (user == null) {
                            return Transaction.success(mutableData);
                        }
                        if (user.getHearts().containsKey(uid)) {
                            user.setHeartCount(user.getHeartCount() - 1);
                            user.getHearts().remove(uid);
                        } else {
                            user.setHeartCount(user.getHeartCount() + 1);
                            user.getHearts().put(uid, true);
                        }
                        // Set value and report transaction succes
                        mutableData.setValue(user);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                        Log.d(view.TAG, "postTransaction:onComplete:" + databaseError);
                    }
                }
        );
    }
}
