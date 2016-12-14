package com.itute.dating.chat_group.presenter;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.storage.UploadTask;
import com.itute.dating.chat_group.model.ChatGroupMessage;
import com.itute.dating.chat_group.model.ChatGroupSubmitter;
import com.itute.dating.chat_group.view.ChatGroupActivity;

/**
 * Created by buivu on 08/12/2016.
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

    //lấy toàn bộ danh sách chat của group
    public Query getAllChatGroup(String groupId) {
        return submitter.getAllChatGroup(groupId);
    }

    //thêm data chat vào messages
    public void addChatGroupMessage(String groupId, ChatGroupMessage chatGroupMessage) {
        submitter.addChatGroupMessage(groupId, chatGroupMessage);
    }

    public void addChatGroupInfo(final String groupId, final String groupName) {
        submitter.addImage(groupId, groupName, new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                submitter.addChatGroupInfo(groupId, groupName, taskSnapshot.getDownloadUrl().toString());
            }
        });
    }
}
