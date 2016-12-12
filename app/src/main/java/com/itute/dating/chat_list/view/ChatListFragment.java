package com.itute.dating.chat_list.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.itute.dating.add_chat_group.view.ChatGroupActivity;
import com.itute.dating.chat_list.model.ChatListViewHolder;
import com.itute.dating.chat_list.presenter.ChatListPresenter;
import com.itute.dating.util.MyLinearLayoutManager;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 09/10/2016.
 */
public class ChatListFragment extends Fragment implements View.OnClickListener {
    @BindView(R.id.frame_chat)
    RecyclerView mRecycler;
    @BindView(R.id.txt_add_member_to_group)
    TextView txtAddMember;

    private LinearLayoutManager mManager;
    private ChatListPresenter presenter;
    private FirebaseRecyclerAdapter<ChatMessage, ChatListViewHolder> mAdapter;
    private DatabaseReference mDatabase;
    private MyLinearLayoutManager customLinearLayoutManager;
    private static final String TAG = "ChatListFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ChatListPresenter(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mManager = new LinearLayoutManager(getContext());
        customLinearLayoutManager = new MyLinearLayoutManager(getContext());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_chat, container, false);
        ButterKnife.bind(this, rootView);
        //event click
        txtAddMember.setOnClickListener(this);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                double start = new Date().getTime() / 1000;
                loadData();
                double end = new Date().getTime() / 1000;
                Log.d(TAG, "time: " + (end - start));
            }
        });
        thread.start();

        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view == txtAddMember) {
            startActivity(new Intent(getActivity(), ChatGroupActivity.class));
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
    }

    private void loadData() {
        final Query query = presenter.getAllChat(BaseActivity.getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                mAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatListViewHolder>(ChatMessage.class, R.layout.item_chat_list,
                        ChatListViewHolder.class, query) {
                    @Override
                    protected void populateViewHolder(final ChatListViewHolder viewHolder, ChatMessage model, int position) {

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
                                getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                            }
                        });


                    }
                };
                mRecycler.setLayoutManager(customLinearLayoutManager);
                mRecycler.setAdapter(mAdapter);

                Log.d(TAG, "onChildAdded");
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged");
                //mAdapter.notifyItemMoved(5, 0);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onChildRemoved");
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled");
            }
        });

    }
}
