package com.itute.dating.chat_group.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.itute.dating.chat_group.model.ChatGroupSubmitter;
import com.itute.dating.chat_group.view.ChatGroupActivity;

import java.util.List;


/**
 * Created by buivu on 05/12/2016.
 */
public class ChatGroupPresenter {
    private ChatGroupSubmitter submitter;
    private ChatGroupActivity view;
    private DatabaseReference mDatabase;

    public ChatGroupPresenter(ChatGroupActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ChatGroupSubmitter(mDatabase);
    }

    public Query getAllFriends(String uid) {
        return submitter.getAlllFriends(uid);
    }

    public void addMemberToGroup(List<String> listMember) {
        submitter.addMemberToGroup(listMember);
    }
}
