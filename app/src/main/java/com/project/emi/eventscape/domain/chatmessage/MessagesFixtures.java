package com.project.emi.eventscape.domain.chatmessage;

import android.content.Context;

import com.project.emi.eventscape.models.User;

import java.util.Date;


public final class MessagesFixtures extends FixturesData {
    private MessagesFixtures() {
        throw new AssertionError();
    }

    public static Message getTextMessage() {
       // return getTextMessage( getRandomMessage());
        return  new Message();
    }


    public static Message getTextMessage(Context ctx, String text, String name, Date date) {
        return new Message(FirebaseChatHelper.getDriverFirebaseUId(ctx),getUser(), text, name , date);
    }


    private static User getUser() {
        boolean even = rnd.nextBoolean();
//        return new User(
//                even ? "0" : "1",
//                even ? names.get(0) : names.get(1),
//                even ? avatars.get(0) : avatars.get(1),
//                true);
        return null;
    }
}
