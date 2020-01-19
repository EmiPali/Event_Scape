package com.project.emi.eventscape.domain.chat;


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
import com.project.emi.eventscape.core.managers.ProfileManager;
import com.project.emi.eventscape.domain.chatmessage.ChatActivity;
import com.project.emi.eventscape.domain.chatmessage.ChatHelper;
import com.project.emi.eventscape.domain.chatmessage.DemoDialogsFragment;
import com.project.emi.eventscape.domain.chatmessage.Dialog;
import com.project.emi.eventscape.domain.chatmessage.DialogsFixtures;
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
    }


    @Override
    public void onDialogClick(Dialog dialog) {
        Intent intent = new Intent(getContext(), ChatActivity.class);
        intent.putExtra(CodesUtil.CHAT_ID, dialog.getId());
        startActivity(intent);
    }

    @Override
    public String format(Date date) {
        if (DateFormatter.isToday(date)) {
            return DateFormatter.format(date, DateFormatter.Template.TIME);
        } else if (DateFormatter.isYesterday(date)) {
            return "yesterday";
        } else if (DateFormatter.isCurrentYear(date)) {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH);
        } else {
            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
        }
    }

    private void initAdapter() {
        super.dialogsAdapter = new DialogsListAdapter<>(super.imageLoader);
        super.dialogsAdapter.setOnDialogClickListener(this);
        super.dialogsAdapter.setOnDialogLongClickListener(this);
        super.dialogsAdapter.setDatesFormatter(this);

        dialogsList.setAdapter(super.dialogsAdapter);
    }

    private void getUserDialogsFromDatabase(){
        List<Dialog> result = new ArrayList<>();
        userId = PreferencesUtil.getUserUid(getContext());
        ChatHelper.dialogsReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot dialog : dataSnapshot.getChildren()){
                    if(dialog.child("users").getValue().toString().contains(userId)){
                        result.add(generateDialogFromSnapSht(dialog));
                    }
                }
                dialogsAdapter.setItems(result);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private Dialog generateDialogFromSnapSht(DataSnapshot dialog){
        String dialogId = dialog.getKey();
        String recipientId = dialog.child("users").toString().replace(";","").replace(userId,"");
        DatabaseHelper.getInstance(getContext()).getDatabaseReference().child("users").child(recipientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return new Dialog();
    }


}


