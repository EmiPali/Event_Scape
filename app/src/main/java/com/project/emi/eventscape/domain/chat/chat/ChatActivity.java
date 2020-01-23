package com.project.emi.eventscape.domain.chat.chat;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.project.emi.eventscape.R;
import com.project.emi.eventscape.domain.chat.AppUtils;
import com.project.emi.eventscape.domain.chat.Message;
import com.project.emi.eventscape.util.CodesUtil;
import com.project.emi.eventscape.util.MySharedPref;
import com.project.emi.eventscape.util.PreferencesUtil;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;


public class ChatActivity extends BaseChatActivity implements MessageInput.InputListener,
        MessageInput.AttachmentsListener {

    private ActionBar actionBar;
    private String userId;
    private MySharedPref mySharedPref;
    private boolean fromMessagingService = false;

    private Toolbar toolbar;
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
        if (getIntent().getStringExtra(CodesUtil.START_CHAT_ACTIVITY_CODE) != null) {
            if (getIntent().getStringExtra(CodesUtil.START_CHAT_ACTIVITY_CODE).equals(CodesUtil.START_CHAT_FROM_BUBBLE)) {
                fromMessagingService = true;
            }
        }
        this.CHAT_ID = getIntent().getStringExtra(CodesUtil.CHAT_ID);
        this.RECIPIENT_ID = getIntent().getStringExtra(CodesUtil.RECIPIENT_ID);
        if(CHAT_ID != null){
            toolbar.setTitle("Abetare");
            fetchUsers();
            loadMessagesFirebaseNoQuery(userId, CHAT_ID);
        } else {
            CHAT_ID = "dialogId";
            toolbar.setTitle("Error!");
        }
    }

    @Override
    public boolean onSubmit(CharSequence input) {
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
