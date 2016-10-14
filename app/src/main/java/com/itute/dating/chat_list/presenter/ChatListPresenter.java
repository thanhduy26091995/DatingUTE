package com.itute.dating.chat_list.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.itute.dating.chat_list.model.ChatListSubmitter;
import com.itute.dating.chat_list.view.ChatListFragment;

/**
 * Created by buivu on 14/10/2016.
 */
public class ChatListPresenter {
    private ChatListFragment view;
    private ChatListSubmitter submitter;
    private DatabaseReference mDatabase;

    public ChatListPresenter(ChatListFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ChatListSubmitter(mDatabase);
    }

    public Query getAllChat(String currentID) {
        return submitter.getAllChat(currentID);
    }
}
