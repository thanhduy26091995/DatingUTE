package com.itute.dating.chat_list.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 14/10/2016.
 */
public class ChatListSubmitter {
    private DatabaseReference mDatabase;

    public ChatListSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //lấy danh sách những người đã chat với bạn, dựa vào userID
    public Query getAllChat(String currentID) {
        Query query = mDatabase.child(Constants.MESSAGES).child(currentID);
        return query;
    }

    //lấy danh sách những group mà bạn đã chat
    public Query getAllChatGroup(String currentId) {
        Query query = mDatabase.child(Constants.USERS).child(currentId).child(Constants.GROUPS);
        return query;
    }
}
