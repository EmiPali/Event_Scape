package com.project.emi.eventscape.domain.chat.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.domain.chat.AppUtils;
import com.project.emi.eventscape.domain.chat.ChatHelper;
import com.project.emi.eventscape.domain.chat.Message;
import com.project.emi.eventscape.domain.chat.dialog.DialogDatabase;
import com.project.emi.eventscape.util.CodesUtil;
import com.project.emi.eventscape.util.MySharedPref;
import com.project.emi.eventscape.util.PreferencesUtil;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatActivity extends BaseChatActivity implements MessageInput.InputListener,
        MessageInput.AttachmentsListener {

    private ActionBar actionBar;
    private String userId;
    private MySharedPref mySharedPref;
    private boolean fromMessagingService = false;

    private MessagesList messagesList;
    private MessageInput input;


    public static void open(Context context) {
        context.startActivity(new Intent(context, ChatActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        toolbar = findViewById(R.id.toolbar);
        messagesList = findViewById(R.id.messagesList);
        input = findViewById(R.id.input);
        input.setInputListener(this);
        mySharedPref = new MySharedPref(this);
        userId = PreferencesUtil.getUserUid(this);
        initAdapter();
        this.CHAT_ID = getIntent().getStringExtra(CodesUtil.CHAT_ID);
        this.RECIPIENT_ID = getIntent().getStringExtra(CodesUtil.RECIPIENT_ID);
        if (CHAT_ID != null) {
            fetchUsers();
            loadMessagesFirebaseNoQuery(userId, CHAT_ID);
        } else if (this.RECIPIENT_ID != null) {
            findChatId();
        } else {
            CHAT_ID = "dialogId";
            toolbar.setTitle("Error!");
        }
    }

    private void findChatId() {
        ChatHelper.dialogsReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dialog : dataSnapshot.getChildren()) {
                    String users = Objects.requireNonNull(dialog.child("users").getValue()).toString();
                    if (users.contains(RECIPIENT_ID) && users.contains(userId)) {
                        CHAT_ID = dialog.getKey();
                        fetchUsers();
                        loadMessagesFirebaseNoQuery(userId, CHAT_ID);
                        break;
                    }
                }
                if (CHAT_ID == null || CHAT_ID.isEmpty()) {
                    createNewEmptyDialog();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void createNewEmptyDialog() {
        DialogDatabase dialogDatabase = new DialogDatabase();
        dialogDatabase.setMessages(new ArrayList<>());
        dialogDatabase.setUsers(userId + ";" + RECIPIENT_ID);
        CHAT_ID = ChatHelper.dialogsReference.push().getKey();
        assert CHAT_ID != null;
        ChatHelper.dialogsReference.child(CHAT_ID).setValue(dialogDatabase);
        fetchUsers();
        loadMessagesFirebaseNoQuery(userId, CHAT_ID);
    }

    @Override
    public boolean onSubmit(CharSequence input) {

        Message message = new Message();
        message.setAuthorId(userId);
        message.setUser(user);
        message.setSenderName(user.getUsername());
        message.setText(input.toString());
        long time = new Date().getTime();
        message.setCreatedAt(time);
        ChatHelper.dialogsReference.child(CHAT_ID).child("messages").push().setValue(message);
        loadMessagesFirebaseNoQuery(userId, CHAT_ID);

//        FirebaseChatUtils.sendMessage(this , new Message(FirebaseChatUtils.getDriverFirebaseUId(ChatActivity.this),
//                                                    new User(FirebaseChatUtils.getDriverFirebaseUId(ChatActivity.this)
//                                                            , FirebaseChatUtils.getDriverName(this), "sdfsfsdf", true)
//                                                    ,input.toString(), FirebaseChatUtils.getDriverName(this), new Date() ) , firebaseUserId , companyID );
        return true;
    }

    @Override
    public void onAddAttachments() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    private void initAdapter() {
        super.messagesAdapter = new MessagesListAdapter<>(userId, super.imageLoader);
        super.messagesAdapter.enableSelectionMode(this);
        super.messagesAdapter.setLoadMoreListener(this);
        super.messagesAdapter.registerViewClickListener(R.id.messageUserAvatar,
                new MessagesListAdapter.OnMessageViewClickListener<Message>() {
                    @Override
                    public void onMessageViewClick(View view, Message message) {
                        AppUtils.showToast(ChatActivity.this,
                                message.getUser().getName() + " clicked",
                                false);
                    }
                });
        this.messagesList.setAdapter(super.messagesAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        userId = PreferencesUtil.getUserUid(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
