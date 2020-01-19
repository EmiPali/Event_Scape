package com.project.emi.eventscape.domain.chatmessage;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.emi.eventscape.models.User;

public class ChatHelper {

    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference dialogsReference = database.getReference("dialogs");
    public static DatabaseReference dialogUsersReference = database.getReference("users");
    public static DatabaseReference dialogMessagesReference = database.getReference("messages");

    public static void createChatInFireBaseOneTime(final Context ctx, final User user, String recipientId){
        dialogMessagesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = false;
                Log.d("SkerdiFirebase", "Numri i children: " + dataSnapshot.getChildrenCount());

                for(DataSnapshot childSnap : dataSnapshot.getChildren()){
                    if(childSnap.child("users").getValue().toString().contains(user.getId()) && childSnap.child("users").getValue().toString().contains(recipientId)) {
                        exists = true;
                        //  new MySharedPref(ctx).saveStringInSharedPref(CHAT_FIREBASE_ID, childSnap.getKey());
                        Log.d("SkerdiFirebase", "Key i Chat te krijuar eshte = " + childSnap.getKey());
                        break;
                    }
                    Log.d("SkerdiFirebase", "driver nuk u gjet");
                }

                if(!exists){
                    DatabaseReference actualChat = dialogMessagesReference.push();
                    String chatId = actualChat.getKey();
                    actualChat.child("users").setValue(user.getId()+";" + recipientId);
                    // new MySharedPref(ctx).saveStringInSharedPref(CHAT_FIREBASE_ID, chatId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
