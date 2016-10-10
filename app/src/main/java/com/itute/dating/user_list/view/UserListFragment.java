package com.itute.dating.user_list.view;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.itute.dating.R;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.user_list.model.UserListViewHolder;
import com.itute.dating.user_list.presenter.UserListPresenter;
import com.itute.dating.util.MyLinearLayoutManager;

/**
 * Created by buivu on 08/10/2016.
 */
public class UserListFragment extends Fragment {
    private static final String TAG = "UserListFragment";
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<User, UserListViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private MyLinearLayoutManager customLinearLayoutManager;
    private UserListPresenter presenter;
    private ProgressDialog mProgressDialog;

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
        presenter = new UserListPresenter(this);

        Query query = presenter.getUserList();
        loadData(query);
        //init info
        return rootView;
    }

    private void loadData(Query query) {
        showProgessDialog();
        try {
            mAdapter = new FirebaseRecyclerAdapter<User, UserListViewHolder>(User.class, R.layout.item_list_friend,
                    UserListViewHolder.class, query) {
                @Override
                protected void populateViewHolder(UserListViewHolder viewHolder, User model, int position) {

                    viewHolder.bindToViewHolder(model);
                    //load avatar
                    Glide.with(getActivity())
                            .load(model.getPhotoURL())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .error(R.drawable.avatar)
                            .centerCrop()
                            .into(viewHolder.imgAvatar);
                    hideProgressDialog();

                }
            };

        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
            hideProgressDialog();
        }
        mRecycler.setLayoutManager(customLinearLayoutManager);
        mRecycler.setAdapter(mAdapter);

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
