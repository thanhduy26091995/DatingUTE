package com.itute.dating.add_friend.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.add_friend.CustomAddFriendAdapter;
import com.itute.dating.add_friend.model.AddFriendViewHolder;
import com.itute.dating.add_friend.presenter.AddFriendPresenter;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;
import com.itute.dating.util.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 01/11/2016.
 */
public class AddFriendFragment extends Fragment {


    private DatabaseReference mDatabase;
    private AddFriendPresenter presenter;
    private MyLinearLayoutManager mCustomManager;
    private CustomAddFriendAdapter mAdapter;
    private List<User> listUser;
    private List<String> listUserId;
    private static final String TAG = "AddFriendFragment";
    private RecyclerView mRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init
        presenter = new AddFriendPresenter(this);
        mCustomManager = new MyLinearLayoutManager(getActivity());
        mDatabase = FirebaseDatabase.getInstance().getReference();
        listUser = new ArrayList<>();
        listUserId = new ArrayList<>();
        mAdapter = new CustomAddFriendAdapter(getActivity(), listUserId);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_friend_added, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.frame_friend_added);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        //hiển thị data
        loadData();
        mRecycler.setLayoutManager(mCustomManager);
        mRecycler.setAdapter(mAdapter);
        //event rehresh
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        mAdapter.clear();
                        loadData();
                    }
                });
            }
        });

        return rootView;
    }

    private void loadData() {

        final Query myQuery = presenter.getAllFriend(BaseActivity.getUid());
        myQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                final List<Boolean> result = new ArrayList<>();
                result.add(true);

                for (int i = 0; i < listUserId.size(); i++) {
                    if (dataSnapshot.getKey().equals(listUserId.get(i))) {
                        result.set(0, false);
                    }
                }
                if (result.get(0)) {
                    listUserId.add(dataSnapshot.getKey());
                    mAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //  mAdapter.clear();
                listUser.remove(dataSnapshot.getKey());

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
