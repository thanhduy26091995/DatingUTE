package com.itute.dating.list_heart.view;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.list_heart.CustomListFriendAdapter;
import com.itute.dating.list_heart.presenter.ListHeartPresenter;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;
import com.itute.dating.util.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by buivu on 03/11/2016.
 */
public class ListFriendFragment extends Fragment implements View.OnClickListener {

    private ListHeartPresenter presenter;
    private CustomListFriendAdapter mCustomAdapter;
    private MyLinearLayoutManager mCustomManager;
    private List<String> listUserId;
    private RecyclerView mRecycler;
    private static final String TAG = "ListFriendFragment";
    private DatabaseReference mDatabase;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        presenter = new ListHeartPresenter(this);
        mCustomManager = new MyLinearLayoutManager(getActivity());
        listUserId = new ArrayList<>();
        mCustomAdapter = new CustomListFriendAdapter(getActivity(), listUserId);
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_profile_list_friend, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.frame_profile_list_friend);
        loadData();
        mRecycler.setLayoutManager(mCustomManager);
        mRecycler.setAdapter(mCustomAdapter);

        return rootView;
    }


    private void loadData() {

        final Query myQuery = presenter.getAllFriends(BaseActivity.getUid());
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
                    mCustomAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                //  mAdapter.clear();
                listUserId.remove(dataSnapshot.getKey());
                mCustomAdapter.notifyDataSetChanged();

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {

    }
}
