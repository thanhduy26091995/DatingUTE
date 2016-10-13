package com.itute.dating.chat.presenter;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.chat.model.ChatMessage;
import com.itute.dating.chat.model.ChatMessageSubmitter;
import com.itute.dating.chat.view.ChatActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;

/**
 * Created by buivu on 13/10/2016.
 */
public class ChatMessagePresenter {
    private static final String TAG = "ChatMessagePresenter";
    private ChatActivity view;
    private ChatMessageSubmitter submitter;
    private DatabaseReference mDatabase;

    public ChatMessagePresenter(ChatActivity view) {
        this.view = view;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        submitter = new ChatMessageSubmitter(mDatabase);
    }

    //lấy thông tin user
    public DatabaseReference getUser(String uid) {
        return submitter.getUser(uid);
    }

    //2 hàm getAllChat và getUser dc gọi từ Submitter
    public Query getAllChat(String currentID, String partnerID) {
        return submitter.getAllChat(currentID, partnerID);
    }

    //thêm 1 tin nhắn vào database
    public void addChatMessage(final String partnerID, final ChatMessage chatMessage) {
        mDatabase.child(Constants.USERS).child(view.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        submitter.addChatMessage(view.getUid(), partnerID, chatMessage, true);
                        submitter.addChatMessage(partnerID, view.getUid(), chatMessage, false);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }
}
