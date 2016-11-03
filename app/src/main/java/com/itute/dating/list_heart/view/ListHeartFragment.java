package com.itute.dating.list_heart.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.list_heart.CustomListHeartAdapter;
import com.itute.dating.list_heart.model.ListHeartViewHolder;
import com.itute.dating.list_heart.presenter.ListHeartPresenter;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;
import com.itute.dating.util.MyLinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by buivu on 16/10/2016.
 */
public class ListHeartFragment extends Fragment implements View.OnClickListener {


    private ListHeartPresenter presenter;
    private MyLinearLayoutManager customLayoutManager;
    private FirebaseRecyclerAdapter<User, ListHeartViewHolder> mAdapter;
    public static final String TAG = "ListHeartFragment";
    private DatabaseReference mDatabase;
    private CustomListHeartAdapter customAdapter;
    private List<String> listUserId;
    private RecyclerView mRecycler;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //event click
        //init
        mDatabase = FirebaseDatabase.getInstance().getReference();
        presenter = new ListHeartPresenter(this);
        customLayoutManager = new MyLinearLayoutManager(getActivity());

        listUserId = new ArrayList<>();
        customAdapter = new CustomListHeartAdapter(getActivity(), listUserId);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_all_list_heart, container, false);
        mRecycler = (RecyclerView) rootView.findViewById(R.id.frame_list_heart);
        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        //event for swipe
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                customAdapter.clear();
                loadData();
            }
        });
        loadData();
        mRecycler.setLayoutManager(customLayoutManager);
        mRecycler.setAdapter(customAdapter);

        return rootView;
    }

    private void loadData() {
        final Query query = presenter.getAllHeart(BaseActivity.getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    final List<Boolean> result = new ArrayList<>();
                    result.add(true);

                    for (int i = 0; i < listUserId.size(); i++) {
                        if (dataSnapshot.getKey().equals(listUserId.get(i))) {
                            result.set(0, false);
                        }
                    }
                    if (result.get(0)) {
                        listUserId.add(dataSnapshot.getKey());
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
                    listUserId.remove(dataSnapshot.getKey());
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

    @Override
    public void onClick(View view) {

    }
}
