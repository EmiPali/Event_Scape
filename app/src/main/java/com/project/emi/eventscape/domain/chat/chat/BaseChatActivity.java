package com.project.emi.eventscape.domain.chat.chat;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.core.managers.DatabaseHelper;
import com.project.emi.eventscape.domain.chat.AppUtils;
import com.project.emi.eventscape.domain.chat.ChatHelper;
import com.project.emi.eventscape.domain.chat.Message;
import com.project.emi.eventscape.models.User;
import com.project.emi.eventscape.util.MySharedPref;
import com.project.emi.eventscape.util.PreferencesUtil;
import com.squareup.picasso.Picasso;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;



public class BaseChatActivity extends AppCompatActivity implements MessagesListAdapter.SelectionListener,
        MessagesListAdapter.OnLoadMoreListener {

    private static long TOTAL_MESSAGES_COUNT = 10;
    protected final String senderId = "0";
    protected ImageLoader imageLoader;
    protected MessagesListAdapter<Message> messagesAdapter;
    protected String CHAT_ID;
    protected Menu menu;
    protected String RECIPIENT_ID;
    private String USER_ID;
    private User user;
    private User recipient;
    private int selectionCount;
    private Date lastLoadedDate;
    private ActionBar actionBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        imageLoader = new ImageLoader() {

            @Override
            public void loadImage(ImageView imageView, @Nullable String url, @Nullable Object payload) {
                Picasso.with(BaseChatActivity.this).load(url).into(imageView);
            }
        };
        USER_ID = PreferencesUtil.getUserUid(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        loadMessagesFirebaseNoQuery(USER_ID, CHAT_ID);
    }

    protected void fetchUsers() {
        DatabaseHelper.getInstance(this).getDatabaseReference().child("users").child(USER_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                user = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseHelper.getInstance(this).getDatabaseReference().child("users").child(RECIPIENT_ID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                recipient = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.chat_actions_menu, menu);
        onSelectionChanged(0);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                messagesAdapter.deleteSelectedMessages();
                break;
            case R.id.action_copy:
                messagesAdapter.copySelectedMessagesText(this, getMessageStringFormatter(), true);
                AppUtils.showToast(this, "Copy text", true);
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (selectionCount == 0) {
            super.onBackPressed();
        } else {
            messagesAdapter.unselectAllItems();
        }
    }

    @Override
    public void onLoadMore(int page, int totalItemsCount) {
        if (totalItemsCount < TOTAL_MESSAGES_COUNT) {
            loadMessagesFirebaseNoQuery(USER_ID, USER_ID);
        }
    }

    @Override
    public void onSelectionChanged(int count) {
        this.selectionCount = count;
        menu.findItem(R.id.action_delete).setVisible(count > 0);
        menu.findItem(R.id.action_copy).setVisible(count > 0);
    }


    protected void loadMessagesFirebaseNoQuery(final String userId, final String chatId) {
        //setSeenMessages(userId, chatId);
        ChatHelper.dialogsReference.child(chatId).child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                ArrayList<Message> result = new ArrayList<>();

                for (DataSnapshot dataSnap : dataSnapshot.getChildren()) {
                    MySharedPref mySharedPref = new MySharedPref(BaseChatActivity.this);
                    Long time = (Long) dataSnap.child("createdAt").getValue();
                    String text = dataSnap.child("text").getValue().toString();
                    String id = dataSnap.child("authorId").getValue().toString();
                    if (id.equals(userId)) {
                        result.add(new Message(id, user, text, user.getName(), new Date(time)));
                    } else {
                        result.add(new Message(id, recipient, text, recipient.getName(), new Date(time)));
                    }
                }
                if(messagesAdapter.getItemCount()>0 && result.size()>0){
                    messagesAdapter.clear();
                    messagesAdapter.notifyDataSetChanged();
                }
                messagesAdapter.addToEnd(result, true);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private MessagesListAdapter.Formatter<Message> getMessageStringFormatter() {
        return new MessagesListAdapter.Formatter<Message>() {
            @Override
            public String format(Message message) {
                String createdAt = new SimpleDateFormat("MMM d, EEE 'at' h:mm a", Locale.getDefault())
                        .format(message.getCreatedAt());

                String text = message.getText();
                if (text == null) text = "[attachment]";

                return String.format(Locale.getDefault(), "%s: %s (%s)",
                        message.getUser().getName(), text, createdAt);
            }
        };
    }

    private static class TransportMessage {

        private long createdAt;
        private String id;
        private String name;
        private String status;
        private String text;

        public TransportMessage(long createdAt, String id, String name, String status, String text) {
            this.createdAt = createdAt;
            this.id = id;
            this.name = name;
            this.status = status;
            this.text = text;
        }

        public long getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(long createdAt) {
            this.createdAt = createdAt;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

}
