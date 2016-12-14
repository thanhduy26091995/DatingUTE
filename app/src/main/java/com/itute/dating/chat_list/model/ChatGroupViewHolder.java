package com.itute.dating.chat_list.model;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.itute.dating.R;

/**
 * Created by buivu on 07/12/2016.
 */
public class ChatGroupViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgAvatarGroup;
    public TextView txtMember, txtLastMessage;

    public ChatGroupViewHolder(View itemView) {
        super(itemView);

        imgAvatarGroup = (ImageView) itemView.findViewById(R.id.iv_avatar_group);
        txtMember = (TextView) itemView.findViewById(R.id.txtMember);
        txtLastMessage = (TextView) itemView.findViewById(R.id.txt_last_message);
    }
}
