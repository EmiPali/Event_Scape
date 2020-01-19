package com.project.emi.eventscape.domain.chatmessage;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.project.emi.eventscape.util.CodesUtil;
import com.project.emi.eventscape.util.MySharedPref;


public class FirebaseChatUtils {
    public static final String DRIVER_FIREBASE_ID = "driver_firebase_id";
    public static final String CHAT_FIREBASE_ID = "chat_firebase_id";
    public static final String COMPANY_ID = " company_ID";
    private static final String USERNAME = " username" ;

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference chatMessagesReference = database.getReference("chatMessages");
    public static DatabaseReference companyReference = database.getReference("company");
    private static DatabaseReference driversReference = database.getReference("drivers");
    private static DatabaseReference headquartersReference = database.getReference("headquarters");

    public static void getCompanyId(Context ctx , final String firebaseUID){
        final MySharedPref mySharedPref = new MySharedPref(ctx);
        final String[] CompanyId = new String[1];
        Log.d("SkerdiNewFirebase", "Filloi Kerkimi me String : " + firebaseUID);
        companyReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot childDataSnapShot : dataSnapshot.getChildren()){

                    for (DataSnapshot driverSnapShot : childDataSnapShot.child("drivers").getChildren()){
                        Log.d("SkerdiNewFirebase", " data SnapShot eshte :  " + driverSnapShot.getKey());
                        if(driverSnapShot.child("id").getValue().equals(firebaseUID)){
                            CompanyId[0] = childDataSnapShot.getKey();
                            Log.d("SkerdiNewFirebase", CompanyId[0]);
                                mySharedPref.saveStringInSharedPref(COMPANY_ID, CompanyId[0]);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public static void sendMessage (Context ctx, Message message, String UID, String companyId){
        TransportMessage transportMessage = new TransportMessage(message.getCreatedAt().getTime(), UID, FirebaseChatUtils.getDriverName(ctx), "sent" , message.getText());
        chatMessagesReference.child(companyId).child(UID).push().setValue(transportMessage);
    }


    public static String getDriverFirebaseUId(Context ctx){
        return new MySharedPref(ctx).getSavedObjectFromPreference(DRIVER_FIREBASE_ID, String.class);
    }

    public static String getCompanyIDSharedPref(Context ctx){
        return new MySharedPref(ctx).getSavedObjectFromPreference(COMPANY_ID, String.class);
    }

    public static boolean getUsername( Context ctx) {
        final MySharedPref mySharedPref = new MySharedPref(ctx);
        String driverID = mySharedPref.getSavedObjectFromPreference(DRIVER_FIREBASE_ID, String.class);
        if(driverID!=null) {
            driversReference.child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Log.d("SkerdiNewFirebase", " u kap datasnapshot me id :" + dataSnapshot.getKey());
                    String username = dataSnapshot.child("name").getValue().toString();
                    String avatar = dataSnapshot.child("avatar").getValue().toString();
                    mySharedPref.saveStringInSharedPref(USERNAME, getFormatedStringFromFirebase(username));
                    mySharedPref.saveStringInSharedPref(CodesUtil.AVATAR_OF_DRIVER, avatar);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
            return true;
        }
        else return false;
    }

    public static boolean setDriverFCMinFirebase(Context ctx){
        final MySharedPref mySharedPref = new MySharedPref(ctx);
        final String driverID = mySharedPref.getSavedObjectFromPreference(DRIVER_FIREBASE_ID, String.class);
        if(driverID!=null) {
            driversReference.child(driverID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    String name;
                    Integer companyId;
                    String avatar;
                    String fcm;

                    name = dataSnapshot.child("name").getValue().toString();
                    companyId = Integer.parseInt(dataSnapshot.child("companyId").getValue().toString());
                    avatar = dataSnapshot.child("avatar").getValue().toString();
                    if (dataSnapshot.child("fcm").getValue() != null) {
                        fcm = dataSnapshot.child("fcm").getValue().toString();
                    } else fcm = null;
                    DriverFirebase driverFirebase = new DriverFirebase(name, companyId, avatar, fcm);
                    fcm = FirebaseInstanceId.getInstance().getToken();
                    driverFirebase.setFcm(fcm);
                    driversReference.child(driverID).setValue(driverFirebase);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            return true;
        }
        else return false;

    }

    public static String getDriverName(Context ctx) {
        return getFormatedStringToFirebase( new MySharedPref(ctx).getSavedObjectFromPreference(USERNAME, String.class));
    }

    public static String getFormatedStringFromFirebase(String string){
        StringBuilder result = new StringBuilder();
        String[] splitedStrings = string.split(" ");
        for(int i =0  ; i< splitedStrings.length ; i ++) {
            result.append(splitedStrings[i]);
            if(i!=splitedStrings.length-1){
                result.append("%");
            }
        }
        return result.toString() ;
    }

    public static String getFormatedStringToFirebase(String string){
        StringBuilder result = new StringBuilder();
        String[] splitedStrings = string.split("%");
        for(int i =0  ; i< splitedStrings.length ; i ++) {
            result.append(splitedStrings[i]);
            if(i!=splitedStrings.length-1){
                result.append(" ");
            }
        }
        return result.toString() ;
    }

    private static class TransportMessage {

        private long createdAt;
        private String id;
        private String name;
        private String status;
        private String text;

        public TransportMessage(long createdAt, String id, String name, String status, String text){
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

    public static class DriverFirebase {
        private String name ;
        private Integer companyId;
        private String avatar;
        private String fcm;

        public  DriverFirebase (String name, Integer companyId, String avatar, String fcm){
            this.name = name;
            this.companyId = companyId;
            this.avatar = avatar;
            this.fcm =fcm;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getCompanyId() {
            return companyId;
        }

        public void setCompanyId(Integer companyId) {
            this.companyId = companyId;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getFcm() {
            return fcm;
        }

        public void setFcm(String fcm) {
            this.fcm = fcm;
        }
    }
}
