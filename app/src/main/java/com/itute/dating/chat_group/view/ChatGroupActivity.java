package com.itute.dating.chat_group.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.chat_group.CustomInviteChatGroupAdapter;
import com.itute.dating.chat_group.presenter.ChatGroupPresenter;
import com.itute.dating.util.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 05/12/2016.
 */
public class ChatGroupActivity extends BaseActivity implements View.OnClickListener {

    private ChatGroupPresenter presenter;
    private List<String> listUserId;
    private CustomInviteChatGroupAdapter customAdapter;
    private RecyclerView mRecycler;
    private MyLinearLayoutManager mCustomManager;
    private static final String TAG = "ChatGroupActivity";
    private TextView txtAppBack, txtCreate;
    private List<String> listMember;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_group);
        //init components
        mRecycler = (RecyclerView) findViewById(R.id.frame_all_friends);
        txtAppBack = (TextView) findViewById(R.id.btnAppBack);
        txtCreate = (TextView) findViewById(R.id.txt_create_group);
        //event click
        txtAppBack.setOnClickListener(this);
        txtCreate.setOnClickListener(this);
        //init
        presenter = new ChatGroupPresenter(this);
        listUserId = new ArrayList<>();
        listMember = new ArrayList<>();
        //add currentUid
        listMember.add(getUid());
        customAdapter = new CustomInviteChatGroupAdapter(this, listUserId, listMember);
        mCustomManager = new MyLinearLayoutManager(this);
        loadData();
        //set data for recyclerview
        mRecycler.setLayoutManager(mCustomManager);
        mRecycler.setAdapter(customAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view == txtAppBack) {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            presenter.addMemberToGroup(listMember);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }

    private void loadData() {
        final Query myQuery = presenter.getAllFriends(BaseActivity.getUid());
        myQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        listUserId.add(data.getKey());
                        customAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
