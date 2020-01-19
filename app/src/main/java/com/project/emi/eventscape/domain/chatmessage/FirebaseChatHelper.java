package com.project.emi.eventscape.domain.chatmessage;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.project.emi.eventscape.models.User;
import com.project.emi.eventscape.util.MySharedPref;
import com.project.emi.eventscape.util.PreferencesUtil;


public class FirebaseChatHelper {
    public static final String DRIVER_FIREBASE_ID = "driver_firebase_id";
    public static final String CHAT_FIREBASE_ID = "chat_firebase_id";


    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference chatsReference = database.getReference("chats");
    private static DatabaseReference chatMembersReference = database.getReference("chatMembers");
    public static DatabaseReference chatMessagesReference = database.getReference("chatMessages");
    private static DatabaseReference drivers = database.getReference("drivers");

    public static void sendMessage(final Message message, final String driverID) {

               chatMembersReference.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boolean flag = true;
                        //Get map of users in datasnapshot
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            if(childSnapshot.child("driver").getValue().toString().equals(driverID)){
                                Log.d("SkerdiFirebaseTEST", "Emri u perputh"+childSnapshot.getKey() + " " + childSnapshot.child("driver").getValue());
                                chatMessagesReference.child(childSnapshot.getKey().toString()).push().setValue(message);
                                chatsReference.child(childSnapshot.getKey().toString()).child("lastMessage").setValue(message.getText());
                                chatsReference.child(childSnapshot.getKey().toString()).child("timestamp").setValue(message.getCreatedAt().getTime());
                                flag=false;
                                break;
                            }
                            Log.d("SkerdiFirebaseTEST", "Emri nuk u perputh "+ childSnapshot.getKey() + " " + childSnapshot.child("driver").getValue());
                        }
                        if(flag)
                        chatMessagesReference.push().push().setValue(message);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    public static void sendMessageInfirebase (Message message, String chatId ){
        chatMessagesReference.child(chatId).push().setValue(message);
    }


    public static String getDriverFirebaseId(Context ctx){
        String driverID;
        MySharedPref mySharedPref = new MySharedPref(ctx);
        if(mySharedPref.getSavedObjectFromPreference(DRIVER_FIREBASE_ID, String.class)!=null){
            //e kemi marre id-ne e driverit nga sharedprefs
            driverID = mySharedPref.getSavedObjectFromPreference(DRIVER_FIREBASE_ID, String.class);
        }
        //perndryshe mund te ndodhe qe useri te kete fshire app data nga device dhe ne kete rast duhet te behet nje thirrje ne server per marrjen e unique Firebase DB
        //por per momentin bejme krijimin e ri te userit ne firebase per testim
        else {
            //perndryshe nese ky user nuk ekziston ne shared pref e krijojme
            driverID = drivers.push().getKey();
            mySharedPref.saveStringInSharedPref(DRIVER_FIREBASE_ID, driverID);
        }
        return driverID;
    }

    public static String getDriverFirebaseUId(Context ctx){
        return new MySharedPref(ctx).getSavedObjectFromPreference(DRIVER_FIREBASE_ID, String.class);
    }

    public static String getChatFirebaseUId(Context ctx){
        return new MySharedPref(ctx).getSavedObjectFromPreference(CHAT_FIREBASE_ID, String.class);
    }

    public static void saveUserInFirebaseOneTime(final User user){
        final boolean[] exists = {false};
        drivers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childSnap : dataSnapshot.getChildren()){
                    if(childSnap.getKey().equals(user.getId())) {
                        exists[0] = true;
                        break;
                    }
                }

                // nese nuk ekziston shtoje
                if(!exists[0]){
                    drivers.child(user.getId()).setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public static void createChatInFireBaseOneTime(final Context ctx, final User user){
        chatMembersReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                boolean exists = false;
                Log.d("SkerdiFirebase", "Numri i children: " + dataSnapshot.getChildrenCount());

                for(DataSnapshot childSnap : dataSnapshot.getChildren()){
                    if(childSnap.child("driver").getValue().toString().equals(user.getId())) {
                        exists = true;
                      //  new MySharedPref(ctx).saveStringInSharedPref(CHAT_FIREBASE_ID, childSnap.getKey());
                        Log.d("SkerdiFirebase", "Key i Chat te krijuar eshte = " + childSnap.getKey());
                        break;
                    }
                    Log.d("SkerdiFirebase", "driver nuk u gjet");
                }

                if(!exists){
                    DatabaseReference actualChat = chatMembersReference.push();
                    String chatId = actualChat.getKey();
                    actualChat.child("driver").setValue(user.getId());
                    actualChat.child("headquarter").setValue("HeadQuarterProve");
                   // new MySharedPref(ctx).saveStringInSharedPref(CHAT_FIREBASE_ID, chatId);
                    Log.d("SkerdiFirebase", "Key i Chat te sapo krijuar eshte = " + chatId);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }







}
