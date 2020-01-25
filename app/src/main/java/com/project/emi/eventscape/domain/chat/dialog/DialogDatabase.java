package com.project.emi.eventscape.domain.chat.dialog;

import com.project.emi.eventscape.domain.chat.Message;

import java.util.List;

public class DialogDatabase {

    List<Message> messages;
    String users;

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getUsers() {
        return users;
    }

    public void setUsers(String users) {
        this.users = users;
    }
}
