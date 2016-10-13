package com.itute.dating.chat.model;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.itute.dating.R;
import com.itute.dating.chat.view.ChatView;

/**
 * Created by buivu on 13/10/2016.
 */
public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    public ChatView chatView;

    public ChatMessageViewHolder(View itemView) {
        super(itemView);
        chatView = (ChatView) itemView.findViewById(R.id.v_chat);
    }

    public void bindToView(ChatMessage chatMessage) {

        if (chatMessage.getIsMine()) {
            chatView.setType(0);
            chatView.setOwner(true);
            chatView.setMessage(chatMessage.getMessage());
        } else {
            chatView.setType(0);
            chatView.setOwner(false);
            chatView.setMessage(chatMessage.getMessage());
        }

    }
}
