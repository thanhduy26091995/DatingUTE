package com.itute.dating.user_list.view;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.itute.dating.chat.view.ChatActivity;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.profile_user.view.ProfileUserActivity;
import com.itute.dating.user_list.CustomFilterUserListAdapter;
import com.itute.dating.user_list.model.UserListViewHolder;
import com.itute.dating.user_list.presenter.UserListPresenter;
import com.itute.dating.util.Constants;
import com.itute.dating.util.MyLinearLayoutManager;

import java.util.Date;
import java.util.List;

/**
 * Created by buivu on 08/10/2016.
 */
public class UserListFragment extends Fragment {
    public static final String TAG = "UserListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<User, UserListViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private MyLinearLayoutManager customLinearLayoutManager;
    private UserListPresenter presenter;
    private ProgressDialog mProgressDialog;
    private int gender, from, to;
    private Query query;
    private CustomFilterUserListAdapter customAdapter;
    private List<User> listUser;

    public UserListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_list_friend, container, false);

        //create database ref
        mDatabase = FirebaseDatabase.getInstance().getReference();
        customLinearLayoutManager = new MyLinearLayoutManager(getContext());
        mRecycler = (RecyclerView) rootView.findViewById(R.id.frame_user_list);
        //cache recycler view
        mRecycler.setHasFixedSize(true);
        mRecycler.setItemViewCacheSize(20);
        mRecycler.setDrawingCacheEnabled(true);
        mRecycler.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        //end cache
        presenter = new UserListPresenter(this);
        initInfo();
        changeSearch();
        //init info
        return rootView;
    }

    private void changeSearch() {
        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).child(Constants.SEARCH).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    initInfo();
                }
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

    private void initInfo() {
        Log.d(TAG, "onInitInfo");
        // showProgessDialog();

        mDatabase.child(Constants.USERS).child(BaseActivity.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                double start = new Date().getTime() / 1000;

                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        gender = Integer.parseInt(user.getSearch().get(Constants.GENDER).toString());
                        from = Integer.parseInt(user.getSearch().get(Constants.FROM).toString());
                        to = Integer.parseInt(user.getSearch().get(Constants.TO).toString());
                        if (gender == 0) {
                            query = presenter.getUserList();
                        } else if (gender == 1) {
                            query = presenter.getUserGender(1);
                        } else {
                            query = presenter.getUserGender(2);
                        }
                        //hideProgressDialog();
                        //sủ dụng customAdapter để xử lý tuổi
//                        listUser = new ArrayList<>();
//                        customAdapter = new CustomFilterUserListAdapter(listUser, getActivity());
//
//                        query.addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                if (dataSnapshot != null) {
//                                    for (DataSnapshot data : dataSnapshot.getChildren()) {
//                                        User user = data.getValue(User.class);
//                                        if (user != null) {
//                                            if (from <= user.getOld() && user.getOld() <= to) {
//                                                customAdapter.addItem(listUser.size(), user);
//                                                Log.d(TAG, "" + listUser.size());
//                                                customAdapter.notifyDataSetChanged();
//                                            }
//                                            //Log.d(TAG, "" + listUser.size());
//                                        }
//                                    }
//                                    mRecycler.setLayoutManager(customLinearLayoutManager);
//                                    mRecycler.setAdapter(customAdapter);
//                                }
//                            }
//
//                            @Override
//                            public void onCancelled(DatabaseError databaseError) {
//
//                            }
//                        });
                        loadData(query);

                        double end = new Date().getTime() / 1000;
                        Log.d(TAG, "time: " + (end - start));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadData(Query query) {
        showProgessDialog();
        try {
            mAdapter = new FirebaseRecyclerAdapter<User, UserListViewHolder>(User.class, R.layout.item_list_friend,
                    UserListViewHolder.class, query) {
                @Override
                protected void populateViewHolder(UserListViewHolder viewHolder, User model, final int position) {

                    //lấy id root
                    final DatabaseReference userRef = getRef(position);
                    // gắn click listener
                    final String userKey = userRef.getKey();
                    //hiển thị data
                    viewHolder.bindToViewHolder(model);
                    //highlight nếu thả tim
                    if (model.getHearts().containsKey(BaseActivity.getUid())) {
                        viewHolder.txtIconHeart.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                        viewHolder.txtHeart.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    } else {
                        viewHolder.txtIconHeart.setTextColor(ContextCompat.getColor(getContext(), R.color.md_black_1000));
                        viewHolder.txtHeart.setTextColor(ContextCompat.getColor(getContext(), R.color.md_black_1000));
                    }
                    //highlight nếu đã gửi lời mời
                    if (model.getRequests().containsKey(BaseActivity.getUid())) {
                        viewHolder.txtIconAddFriend.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                        viewHolder.txtAddFriend.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                    } else {
                        viewHolder.txtIconAddFriend.setTextColor(ContextCompat.getColor(getContext(), R.color.md_black_1000));
                        viewHolder.txtAddFriend.setTextColor(ContextCompat.getColor(getContext(), R.color.md_black_1000));
                    }
                    //load avatar
                    Glide.with(getActivity())
                            .load(model.getPhotoURL())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.avatar)
                            .centerCrop()
                            .into(viewHolder.imgAvatar);

                    hideProgressDialog();
                    //event click xem profile
                    viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getActivity(), ProfileUserActivity.class);
                            intent.putExtra(ProfileUserActivity.EXTRA_UID, userKey);
                            startActivity(intent);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });
                    //click chat
                    viewHolder.linearChat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent myIntent = new Intent(getActivity(), ChatActivity.class);
                            myIntent.putExtra(ChatActivity.PARTNER_ID, userKey);
                            startActivity(myIntent);
                            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        }
                    });
                    //click heart
                    viewHolder.linearHeart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference userPostRef = mDatabase.child(Constants.USERS).child(userKey);
                            presenter.onHeartClicked(userPostRef, BaseActivity.getUid());
                        }
                    });
                    //click add friend
                    viewHolder.linearAddFriend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            DatabaseReference userPostRef = mDatabase.child(Constants.USERS).child(userKey);
                            presenter.onAddFriendClicked(userPostRef, BaseActivity.getUid());

                        }
                    });

                }
            };

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            hideProgressDialog();
        }
        mRecycler.setLayoutManager(customLinearLayoutManager);
        mRecycler.setAdapter(mAdapter);

    }

    public void showToast(String content) {
        Toast.makeText(getActivity(), content, Toast.LENGTH_SHORT).show();
    }

    public void showProgessDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("Đang tải...");
            mProgressDialog.setCancelable(false);
        }
        try {
            mProgressDialog.show();
        } catch (Exception e) {
        }
        ;
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            try {
                mProgressDialog.dismiss();
            } catch (Exception e) {
            }

        }
    }
}
