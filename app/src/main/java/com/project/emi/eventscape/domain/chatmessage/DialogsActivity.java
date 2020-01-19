package com.project.emi.eventscape.domain.chatmessage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.project.emi.eventscape.R;
import com.stfalcon.chatkit.dialogs.DialogsList;
import com.stfalcon.chatkit.dialogs.DialogsListAdapter;
import com.stfalcon.chatkit.utils.DateFormatter;

import java.util.Date;

public class DialogsActivity {
//        implements DateFormatter.Formatter {
//
//    private DialogsList dialogsList;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_custom_layout_dialogs);
//
//        dialogsList = (DialogsList) findViewById(R.id.dialogsList);
//        initAdapter();
//    }
//
//    @Override
//    public void onDialogClick(Dialog dialog) {
//       Intent intent = new Intent(this, ChatActivity.class);
//       startActivity(intent);
//    }
//
//    @Override
//    public String format(Date date) {
//        if (DateFormatter.isToday(date)) {
//            return DateFormatter.format(date, DateFormatter.Template.TIME);
//        } else if (DateFormatter.isYesterday(date)) {
//            return "yesterday";
//        } else if (DateFormatter.isCurrentYear(date)) {
//            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH);
//        } else {
//            return DateFormatter.format(date, DateFormatter.Template.STRING_DAY_MONTH_YEAR);
//        }
//    }
//
//    private void initAdapter() {
//        super.dialogsAdapter = new DialogsListAdapter<>(super.imageLoader);
//        super.dialogsAdapter.setItems(DialogsFixtures.getDialogs());
//
//        super.dialogsAdapter.setOnDialogClickListener(this);
//        super.dialogsAdapter.setOnDialogLongClickListener(this);
//        super.dialogsAdapter.setDatesFormatter(this);
//
//        dialogsList.setAdapter(super.dialogsAdapter);
//    }
}