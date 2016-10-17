package com.itute.dating.list_heart.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
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
public class ListHeartActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.frame_list_heart)
    RecyclerView mRecycler;
    @BindView(R.id.btn_back)
    TextView txtBack;


    private ListHeartPresenter presenter;
    private MyLinearLayoutManager customLayoutManager;
    private FirebaseRecyclerAdapter<User, ListHeartViewHolder> mAdapter;
    public static final String TAG = "ListHeartActivity";
    private DatabaseReference mDatabase;
    private CustomListHeartAdapter customAdapter;
    private List<User> listUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_all_list_heart);
        ButterKnife.bind(this);
        //event click
        txtBack.setOnClickListener(this);
        //init
        mDatabase = FirebaseDatabase.getInstance().getReference();
        presenter = new ListHeartPresenter(this);
        customLayoutManager = new MyLinearLayoutManager(this);

        listUser = new ArrayList<>();
        customAdapter = new CustomListHeartAdapter(ListHeartActivity.this, listUser);
        loadData();
        mRecycler.setLayoutManager(customLayoutManager);
        mRecycler.setAdapter(customAdapter);

    }

    private void loadData() {
        final Query query = presenter.getAllHeart(getUid());
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    Log.d(TAG, dataSnapshot.getKey());
                    mDatabase.child(Constants.USERS).child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                User user = dataSnapshot.getValue(User.class);
                                Log.d(TAG, user.getDisplayName());
                                listUser.add(user);
                                customAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    mDatabase.child(Constants.USERS).child(dataSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot != null) {
                                User user = dataSnapshot.getValue(User.class);
                                Log.d(TAG, user.getDisplayName());
                                listUser.remove(user);
                                customAdapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
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
        if (view == txtBack) {
            finish();
        }
    }
}
