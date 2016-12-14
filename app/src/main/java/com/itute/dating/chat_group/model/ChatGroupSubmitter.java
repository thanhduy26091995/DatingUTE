package com.itute.dating.chat_group.model;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itute.dating.util.Constants;
import com.itute.dating.util.EncodeImage;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by buivu on 08/12/2016.
 */
public class ChatGroupSubmitter {
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    public ChatGroupSubmitter(DatabaseReference mDatabase) {
        this.mDatabase = mDatabase;
        mStorage = FirebaseStorage.getInstance().getReference();
    }

    //lấy toàn bộ danh sách chat của group
    public Query getAllChatGroup(String groupId) {
        return mDatabase.child(Constants.CHAT_GROUP).child(groupId).child(Constants.CONTENTS);
    }


    //thêm data chat vào messages
    public void addChatGroupMessage(String groupId, ChatGroupMessage chatGroupMessage) {

        String key = mDatabase.child(Constants.CHAT_GROUP).child(groupId).child(Constants.CONTENTS).push().getKey();
        Map<String, Object> myMap = chatGroupMessage.toMap();
        mDatabase.child(Constants.CHAT_GROUP).child(groupId).child(Constants.CONTENTS).child(key).setValue(myMap);
    }

    //thêm thông tin: tên nhóm chat, avatar nhóm chat
    public void addChatGroupInfo(String groupId, String groupName, String groupAvatar) {
        Map<String, Object> myMap = new HashMap<>();
        myMap.put(Constants.GROUP_NAME, groupName);
        myMap.put(Constants.GROUP_AVATAR, groupAvatar);
        mDatabase.child(Constants.CHAT_GROUP).child(groupId).child(Constants.GROUP_INFO).setValue(myMap);
    }

    public void addImage(String groupId, String groupName, OnSuccessListener<UploadTask.TaskSnapshot> listener) {
        byte[] bitmapGroupAvatar = null;
        if (Constants.AVATAR_GROUP_CHAT != null) {
            bitmapGroupAvatar = EncodeImage.encodeImage(Constants.AVATAR_GROUP_CHAT);
        }
        if (bitmapGroupAvatar != null) {
            StorageReference filePathAvatar = mStorage.child(Constants.GROUP_PHOTO).child(groupId);
            UploadTask uploadTask = filePathAvatar.putBytes(bitmapGroupAvatar);
            uploadTask.addOnSuccessListener(listener);

            //restart bitmap
            Constants.USER_FILE_PATH = null;
        }
    }
}
