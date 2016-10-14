package com.itute.dating.chat_list.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.chat.model.ChatMessage;
import com.itute.dating.chat.view.ChatActivity;
import com.itute.dating.chat_list.model.ChatListViewHolder;
import com.itute.dating.chat_list.presenter.ChatListPresenter;
import com.itute.dating.profile_user.view.ProfileUserActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 09/10/2016.
 */
public class ChatListFragment extends Fragment {
    @BindView(R.id.frame_chat)
    RecyclerView mRecycler;

    private LinearLayoutManager mManager;
    private ChatListPresenter presenter;
    private FirebaseRecyclerAdapter<ChatMessage, ChatListViewHolder> mAdapter;
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ChatListPresenter(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mManager = new LinearLayoutManager(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_chat, container, false);
        ButterKnife.bind(this, rootView);
        loadData();
        return rootView;
    }

    private void loadData() {
        final Query query = presenter.getAllChat(BaseActivity.getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatListViewHolder>(ChatMessage.class, R.layout.item_chat_list,
                        ChatListViewHolder.class, query) {
                    @Override
                    protected void populateViewHolder(ChatListViewHolder viewHolder, ChatMessage model, int position) {
                        //lấy id root
                        final DatabaseReference userRef = getRef(position);
                        // gắn click listener
                        final String userKey = userRef.getKey();

                        viewHolder.bindToChatList(getContext(), mDatabase, userKey);
                        //event click
                        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent myIntent = new Intent(getActivity(), ChatActivity.class);
                                myIntent.putExtra(ChatActivity.PARTNER_ID, userKey);
                                startActivity(myIntent);
                            }
                        });
                    }
                };
                mRecycler.setLayoutManager(mManager);
                mRecycler.setAdapter(mAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
