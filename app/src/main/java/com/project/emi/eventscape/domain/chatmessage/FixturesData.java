package com.project.emi.eventscape.domain.chatmessage;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by W2020 Android on 11/21/2017.
 */

abstract class FixturesData {

    static SecureRandom rnd = new SecureRandom();

    static ArrayList<String> avatars = new ArrayList<String>() {
        {
            add("https://www.w3schools.com/howto/img_avatar.png");
            add("https://www.w3schools.com/howto/img_avatar.png");
            add("https://www.w3schools.com/howto/img_avatar.png");
            add("https://www.w3schools.com/howto/img_avatar.png");
        }
    };

    static final ArrayList<String> groupChatImages = new ArrayList<String>() {
        {
            add("https://www.w3schools.com/howto/img_avatar.png");
            add("https://www.w3schools.com/howto/img_avatar.png");

        }
    };

    static final ArrayList<String> groupChatTitles = new ArrayList<String>() {
        {
            add("Abetare");
            add("Barbie");
            add("Emi");
            add("Barbie");
        }
    };

    static final ArrayList<String> names = new ArrayList<String>() {
        {
            add("Abetare");
            add("Barbie");
            add("Emi");
        }
    };

    static final ArrayList<String> messages = new ArrayList<String>() {
        {
            add("Ciao");
            add("Ciao, come va oggi?");
            add("Sto bene, grazie!");
            add("Sei al lavoro oggi?");
            add("Si, dove sei?");
            add(":D");
        }
    };

    static final ArrayList<String> images = new ArrayList<String>() {
        {
            add("http://www.images.searchpointer.com/transport/9133/3.jpg");
            add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS64OuV7Lk8FH9z6Adf4_3a2BRqb7uT9QqDmyA7GtrE7yhZPQsT");
            add("http://www.multinal.com/en/wp-content/uploads/sites/3/2015/08/Transport.jpg");
        }
    };

    static String getRandomId() {
        return Long.toString(UUID.randomUUID().getLeastSignificantBits());
    }

    static String getRandomAvatar() {
        return avatars.get(rnd.nextInt(avatars.size()));
    }

    static String getRandomGroupChatImage() {
        return groupChatImages.get(rnd.nextInt(groupChatImages.size()));
    }

    static String getRandomGroupChatTitle() {
        return groupChatTitles.get(rnd.nextInt(groupChatTitles.size()));
    }

    static String getRandomName() {
        return names.get(rnd.nextInt(names.size()));
    }

    static String getRandomMessage() {
        return messages.get(rnd.nextInt(messages.size()));
    }

    static String getRandomImage() {
        return images.get(rnd.nextInt(images.size()));
    }

    static boolean getRandomBoolean() {
        return rnd.nextBoolean();
    }
}
