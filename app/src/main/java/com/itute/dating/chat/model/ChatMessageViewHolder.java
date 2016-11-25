package com.itute.dating.chat.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.itute.dating.R;
import com.itute.dating.chat.view.ChatView;

/**
 * Created by buivu on 13/10/2016.
 */
public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = "ChatMessageViewHolder";
    public ChatView chatView;

    public ChatMessageViewHolder(View itemView) {
        super(itemView);
        chatView = (ChatView) itemView.findViewById(R.id.v_chat);
    }

    public void bindToView(ChatMessage chatMessage) {

        if (chatMessage.getIsMine()) {
            if (chatMessage.getMessage().startsWith("<img>")) {
                chatView.setOwner(true);
                chatView.setType(1);
                String content = chatMessage.getMessage().substring(5, chatMessage.getMessage().length());
                // Log.d(TAG, base64content);
                chatView.setImage(content);
            } else {
                chatView.setOwner(true);
                chatView.setType(0);
                chatView.setMessage(chatMessage.getMessage());
            }


        } else {
            if (chatMessage.getMessage().startsWith("<img>")) {
                chatView.setOwner(false);
                chatView.setType(1);
                String content = chatMessage.getMessage().substring(5, chatMessage.getMessage().length());
                // Log.d(TAG, base64content);
                chatView.setImage(content);

            } else {
                chatView.setOwner(false);
                chatView.setType(0);

                chatView.setMessage(chatMessage.getMessage());
            }
        }

    }
}
