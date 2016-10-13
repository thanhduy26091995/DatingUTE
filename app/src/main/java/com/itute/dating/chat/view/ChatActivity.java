package com.itute.dating.chat.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.itute.dating.R;
import com.itute.dating.base.model.ImageLoader;
import com.itute.dating.base.view.BaseActivity;
import com.itute.dating.chat.model.ChatMessage;
import com.itute.dating.chat.model.ChatMessageViewHolder;
import com.itute.dating.chat.presenter.ChatMessagePresenter;
import com.itute.dating.profile_user.model.User;
import com.itute.dating.util.Constants;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.rockerhieu.emojicon.EmojiconGridFragment;
import io.github.rockerhieu.emojicon.EmojiconGridView;
import io.github.rockerhieu.emojicon.EmojiconsFragment;
import io.github.rockerhieu.emojicon.emoji.Emojicon;


/**
 * Created by buivu on 12/10/2016.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener, EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    @BindView(R.id.et_message)
    EditText edtMessage;
    @BindView(R.id.rc_chat)
    RecyclerView mRecycler;
    @BindView(R.id.btn_confirm)
    TextView txtSend;
    @BindView(R.id.txt_partner_username)
    TextView txtPartnerName;
    @BindView(R.id.txt_partner_old)
    TextView txtOld;
    @BindView(R.id.txt_partner_address)
    TextView txtAddress;
    @BindView(R.id.iv_avatar)
    ImageView imgAvatarPartner;
    @BindView(R.id.txt_smile)
    TextView txtIcon;
    @BindView(R.id.emojicons)
    FrameLayout frameIcon;
    @BindView(R.id.root_view)
    RelativeLayout rootView;

    private ChatMessagePresenter presenter;
    private FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder> mAdapter;
    public static final String PARTNER_ID = "partnerId";
    public static final String TAG = "ChatActivity";
    private String partnerID;
    private DatabaseReference mUserPartnerReference, mUserCurrentReference;
    private String fromName, toName;
    private LinearLayoutManager mManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        //get intent
        partnerID = getIntent().getStringExtra(PARTNER_ID);
        //init firebase
        presenter = new ChatMessagePresenter(this);
        mUserPartnerReference = FirebaseDatabase.getInstance().getReference();
        mUserCurrentReference = FirebaseDatabase.getInstance().getReference();
        mUserCurrentReference = presenter.getUser(getUid());
        mUserPartnerReference = presenter.getUser(partnerID);
        //end init firebase
        mManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mManager);
        //load data tin nhắn
        loadData();
        //init info partner
        initPartnerInfo();
        //event click
        txtSend.setOnClickListener(this);
        txtIcon.setOnClickListener(this);
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            public void onGlobalLayout() {
//                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
//
//                if (heightDiff > 100) {
//                    frameIcon.setVisibility(View.GONE);
//                } else {
//                    // keyboard is down
//                }
//            }
//        });
    }

    private void loadData() {
        try {
            Query query = presenter.getAllChat(getUid(), partnerID);
            mAdapter = new FirebaseRecyclerAdapter<ChatMessage, ChatMessageViewHolder>(ChatMessage.class, R.layout.row_chat,
                    ChatMessageViewHolder.class, query) {
                @Override
                protected void populateViewHolder(ChatMessageViewHolder viewHolder, ChatMessage model, int position) {
                    viewHolder.bindToView(model);

                }
            };
            mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    super.onItemRangeInserted(positionStart, itemCount);
                    int count = mAdapter.getItemCount();
                    int lastPosition = mManager.findLastVisibleItemPosition();
                    // If the recycler view is initially being loaded or the
                    // user is at the bottom of the list, scroll to the bottom
                    // of the list to show the newly added message.
                    if (lastPosition == -1 || (positionStart >= (count - 1) && lastPosition == (positionStart - 1))) {
                        mRecycler.scrollToPosition(positionStart);
                    }
                }
            });
            mRecycler.setAdapter(mAdapter);
        } catch (Exception e) {
            Log.d(TAG, e.getMessage());
        }
    }

    @Override
    public void onClick(View view) {
        if (view == txtSend) {
            addNewMessage();
        } else if (view == txtIcon) {
            openIcons();
        }
    }

    private void openIcons() {
        setEmojiconFragment(false);
        frameIcon.setVisibility(View.VISIBLE);
    }

    private void setEmojiconFragment(boolean useSystemDefault) {


        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {

        EmojiconsFragment.input(edtMessage, emojicon);
    }

    /**
     * It called, when backspace button of Emoticons pressed
     *
     * @param view
     */
    @Override
    public void onEmojiconBackspaceClicked(View view) {

        EmojiconsFragment.backspace(edtMessage);
    }

    private void addNewMessage() {
        frameIcon.setVisibility(View.GONE);
        boolean result = true;
        if (TextUtils.isEmpty(edtMessage.getText().toString())) {
            result = false;
            edtMessage.setError(getResources().getString(R.string.batBuoc));
        }
        if (result) {
            ChatMessage message = new ChatMessage(fromName, edtMessage.getText().toString(), new Date().getTime() / 1000, toName);
            presenter.addChatMessage(partnerID, message);
            //clear text and show keyboard
            edtMessage.setText(null);
//            edtMessage.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }

    private void initPartnerInfo() {
        mUserPartnerReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        txtPartnerName.setText(user.getDisplayName());
                        toName = user.getDisplayName();
                        txtOld.setText(String.valueOf(user.getOld()));
                        txtAddress.setText(user.getAddress().get(Constants.ADDRESS).toString());
                        ImageLoader.getInstance().loadImage(ChatActivity.this, user.getPhotoURL(), imgAvatarPartner);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
        //current user
        mUserCurrentReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        fromName = user.getDisplayName();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    frameIcon.setVisibility(View.GONE);
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }

            }
        }
        return super.dispatchTouchEvent(event);
    }
}
