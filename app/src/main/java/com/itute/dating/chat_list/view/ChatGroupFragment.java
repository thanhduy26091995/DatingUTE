package com.itute.dating.chat_list.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.chat_list.CustomChatGroupAdapter;
import com.itute.dating.chat_list.presenter.ChatGroupPresenter;
import com.itute.dating.util.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 05/12/2016.
 */
public class ChatGroupFragment extends Fragment {

    private static final String TAG = "ChatGroupFragment";
    private RecyclerView mRecycler;
    private CustomChatGroupAdapter customAdapter;
    private List<String> listGroup;
    private ChatGroupPresenter presenter;
    private MyLinearLayoutManager customManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        listGroup = new ArrayList<>();
        customAdapter = new CustomChatGroupAdapter(this, listGroup);
        customManager = new MyLinearLayoutManager(getActivity());
        presenter = new ChatGroupPresenter(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_all_chat_group, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.frame_chat_group);
        loadData();
        mRecycler.setAdapter(customAdapter);
        mRecycler.setLayoutManager(customManager);
        return rootView;
    }

    private void loadData() {
        Query query = presenter.getAllChatGroup(BaseActivity.getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    final List<Boolean> result = new ArrayList<>();
                    result.add(true);

                    for (int i = 0; i < listGroup.size(); i++) {
                        if (dataSnapshot.getKey().equals(listGroup.get(i))) {
                            result.set(0, false);
                        }
                    }
                    if (result.get(0)) {
                        listGroup.add(dataSnapshot.getKey());
                        Log.d(TAG, dataSnapshot.getKey());
                        customAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    //  mAdapter.clear();
                    listGroup.remove(dataSnapshot.getKey());
                    //   customAdapter.notifyDataSetChanged();
                }
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
