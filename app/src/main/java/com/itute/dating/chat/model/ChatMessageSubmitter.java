package com.itute.dating.chat.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.StorageReference;
import com.itute.dating.util.Constants;

import java.util.Map;

/**
 * Created by buivu on 13/10/2016.
 */
public class ChatMessageSubmitter {
    private DatabaseReference mDatabase;


    public ChatMessageSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
    }

    //lấy thông tin user
    public DatabaseReference getUser(String uid) {
        return mDatabase.child(Constants.USERS).child(uid);
    }

    //lấy toàn bộ danh sách chat giữa 2 người
    public Query getAllChat(String currentID, String partnerID) {
        return mDatabase.child(Constants.MESSAGES).child(currentID).child(partnerID);
    }

    //thêm data chat vào messages
    public void addChatMessage(String currentID, String partnerID, ChatMessage chatMessage, boolean isMine) {
        chatMessage.setIsMine(isMine);
        String key = mDatabase.child(Constants.MESSAGES).child(currentID).child(partnerID).push().getKey();
        Map<String, Object> myMap = chatMessage.toMap();
        mDatabase.child(Constants.MESSAGES).child(currentID).child(partnerID).child(key).setValue(myMap);
    }
}
