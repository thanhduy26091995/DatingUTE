package com.itute.dating.chat_list.presenter;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.itute.dating.chat_list.model.ChatListSubmitter;
import com.itute.dating.chat_list.view.ChatGroupFragment;

/**
 * Created by buivu on 07/12/2016.
 */
public class ChatGroupPresenter {
    private ChatListSubmitter submitter;
    private ChatGroupFragment view;
    private DatabaseReference mDatabase;

    public ChatGroupPresenter(ChatGroupFragment view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ChatListSubmitter(mDatabase);
    }

    //lấy danh sách những group mà bạn đã chat
    public Query getAllChatGroup(String currentId) {
        return submitter.getAllChatGroup(currentId);
    }
}
