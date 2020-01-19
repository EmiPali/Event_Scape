package com.project.emi.eventscape.domain.chatmessage;

import com.google.firebase.database.Exclude;
import com.project.emi.eventscape.models.User;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;


public class Message implements IMessage,
        MessageContentType.Image, /*this is for default image messages implementation*/
        MessageContentType /*and this one is for custom content type (in this case - voice message)*/ {

    private String id;
    private String text;
    private Date createdat;
    private long createdAt;
    private User user;
    private Image image;
    private Voice voice;
    private String UserId;
    private String senderName;

    public Message(){

    }

    public Message(String id, User user, String text, String senderName, Date createdat) {
        this.user=user;
        this.UserId = user.getUser_id();
        this.senderName = senderName;
        this.createdat = createdat;
        this.createdAt = this.createdat.getTime();
        this.text = text;
    }


    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }


    @Exclude
    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public Date getCreatedAt() {
        return this.createdat;
    }

    @Override
    public String getImageUrl() {
        return image == null ? null : image.url;
    }

    public Voice getVoice() {
        return voice;
    }

    public String getStatus() {
        return "Sent";
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public static class Image {

        private String url;

        public Image(String url) {
            this.url = url;
        }
    }

    public static class Voice {

        private String url;
        private int duration;

        public Voice(String url, int duration) {
            this.url = url;
            this.duration = duration;
        }

        public String getUrl() {
            return url;
        }

        public int getDuration() {
            return duration;
        }
    }
}