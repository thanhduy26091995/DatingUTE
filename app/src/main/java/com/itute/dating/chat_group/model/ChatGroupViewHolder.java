package com.itute.dating.chat_group.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.itute.dating.R;
import com.itute.dating.chat_group.view.ChatView;


/**
 * Created by buivu on 08/12/2016.
 */
public class ChatGroupViewHolder extends RecyclerView.ViewHolder {

    public ChatView chatView;

    public ChatGroupViewHolder(View itemView) {
        super(itemView);
        chatView = (ChatView) itemView.findViewById(R.id.v_chat);
    }

    public void bindToView(String currentUid, ChatGroupMessage chatGroupMessage) {
        if (chatGroupMessage.getSendBy().equals(currentUid)) {
            chatView.setType(0);
            chatView.setOwner(true);
            chatView.setMessage(chatGroupMessage.getContent());
            //chatView.setTitle("Thanh Duy");
            // chatView.setAvatar(chatGroupMessage.getAvatarSender());
        } else {
            chatView.setType(0);
            chatView.setOwner(false);
            chatView.setMessage(chatGroupMessage.getContent());
            // chatView.setTitle("Quỳnh Hương");
            // chatView.setAvatar(chatGroupMessage.getAvatarSender());
        }
    }
}

