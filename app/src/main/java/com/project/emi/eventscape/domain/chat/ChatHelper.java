package com.project.emi.eventscape.domain.chat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ChatHelper {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference dialogsReference = database.getReference("dialogs");
    public static DatabaseReference dialogUsersReference = database.getReference("users");
    public static DatabaseReference dialogMessagesReference = database.getReference("messages");

}
