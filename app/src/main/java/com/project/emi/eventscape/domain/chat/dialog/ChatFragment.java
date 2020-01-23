package com.project.emi.eventscape.domain.chat.dialog;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.project.emi.eventscape.R;
import com.project.emi.eventscape.core.managers.DatabaseHelper;
import com.project.emi.eventscape.domain.chat.ChatHelper;
import com.project.emi.eventscape.domain.chat.Message;
import com.project.emi.eventscape.domain.chat.chat.ChatActivity;
import com.project.emi.eventscape.models.User;
import com.project.emi.eventscape.util.CodesUtil;
import com.project.emi.eventscape.util.PreferencesUtil;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChatFragment extends DemoDialogsFragment
        implements DateFormatter.Formatter {

    private DialogsList dialogsList;
    private String userId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_custom_layout_dialogs, container, false);
        dialogsList = view.findViewById(R.id.dialogsList);
        initAdapter();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getUserDialogsFromDatabase();
    }

    @Override
    public void onDialogClick(Dialog dialog) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(CodesUtil.CHAT_ID, dialog.getId());
        intent.putExtra(CodesUtil.RECIPIENT_ID, dialog.getUsers().get(0).getId());
        startActivity(intent);
    }

    @Override
    public String format(Date date) {
        if (date != null) {

            if (DateFormatter.isToday(date)) {
                return DateFormatter.format(date, DateFormatter.Template.TIME);
            } else if (DateFormatter.isYesterday(date)) {
                return "yesterday";
            } else if (DateFormatter.isCurrentYear(date)) {
                return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH);
            } else {
                return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
            }
        } else {
            return "invalid date";
        }

    }

    private void initAdapter() {
        super.dialogsAdapter = new DialogsListAdapter<>(super.imageLoader);
        super.dialogsAdapter.setOnDialogClickListener(this);
        super.dialogsAdapter.setOnDialogLongClickListener(this);
        super.dialogsAdapter.setDatesFormatter(this);
        dialogsList.setAdapter(super.dialogsAdapter);
    }

    private void getUserDialogsFromDatabase() {
        List<Dialog> result = new ArrayList<>();
        userId = PreferencesUtil.getUserUid(getContext());
        ChatHelper.dialogsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dialog : dataSnapshot.getChildren()) {
                    if (dialog.child("users").getValue().toString().contains(userId)) {
                        generateDialogFromSnapSht(dialog);
                    }
                }
                dialogsAdapter.setItems(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void generateDialogFromSnapSht(DataSnapshot dataSnapshot) {
        String dialogId = dataSnapshot.getKey();
        String recipientId = dataSnapshot.child("users").getValue().toString().replace(";", "").replace(userId, "");
        DatabaseHelper.getInstance(getContext()).getDatabaseReference().child("users").child(recipientId.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapShot) {
                User user = userSnapShot.getValue(User.class);
                Dialog dialog = new Dialog();
                dataSnapshot.child("messages").getRef().orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot messageSnapShot) {
                        for (DataSnapshot messageSnap : messageSnapShot.getChildren()) {
                            Message message = messageSnap.getValue(Message.class);
                            message.setCreatedDate(new Date(message.getTime()));

                            String authorId = message.getAuthorId();
                            DatabaseHelper.getInstance(getContext()).getDatabaseReference().child("users").child(recipientId.trim()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    message.setUser(dataSnapshot.getValue(User.class));
                                    dialog.setLastMessage(message);
                                    dialogsAdapter.addItem(dialog);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                dialog.setId(dialogId);
                dialog.setDialogPhoto(user.getPhotoUrl());
                dialog.setDialogName(user.getUsername());
                dialog.setUnreadCount(3);
                dialog.addUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


}