package com.itute.dating.chat_list;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.chat_group.model.ChatGroupInfo;
import com.itute.dating.chat_group.view.ChatGroupActivity;
import com.itute.dating.chat_list.model.ChatGroupViewHolder;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 07/12/2016.
 */
public class CustomChatGroupAdapter extends RecyclerView.Adapter<ChatGroupViewHolder> {
    private List<String> listChatId;
    private Fragment fragment;
    private DatabaseReference mDatabase;

    public CustomChatGroupAdapter(Fragment fragment, List<String> listChatId) {
        this.fragment = fragment;
        this.listChatId = listChatId;
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }


    @Override
    public ChatGroupViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(fragment.getActivity(), R.layout.item_chat_group, null);
        return new ChatGroupViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ChatGroupViewHolder holder, final int position) {
        final String strGroupId = listChatId.get(position);

        //hiển thị danh sách user
        mDatabase.child(Constants.CHAT_GROUP).child(strGroupId).child(Constants.MEMBER).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot != null) {
                    List<String> listData = new ArrayList<String>();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String member = snapshot.getValue(String.class);

                        listData.add(member);
                        mDatabase.child(Constants.USERS).child(member).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    User user = dataSnapshot.getValue(User.class);
                                    if (user != null) {
                                        holder.txtMember.append(user.getDisplayName() + ", ");
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //hiển thị avatar group chat
        mDatabase.child(Constants.CHAT_GROUP).child(strGroupId).child(Constants.GROUP_INFO).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    ChatGroupInfo chatGroupInfo = dataSnapshot.getValue(ChatGroupInfo.class);
                    if (chatGroupInfo != null) {
                        Log.d("Chat", chatGroupInfo.getGroupAvatar());
                        ImageLoader.getInstance().loadImage(fragment.getActivity(), chatGroupInfo.getGroupAvatar(), holder.imgAvatarGroup);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //event click item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent myIntent = new Intent(fragment.getActivity(), ChatGroupActivity.class);
                myIntent.putExtra(ChatGroupActivity.GROUP_CHAT_ID, strGroupId);
                fragment.startActivity(myIntent);
                fragment.getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listChatId.size();
    }

    public void clearAllData() {
        listChatId.clear();
        notifyDataSetChanged();
    }
}
