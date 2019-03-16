package com.ronan.carpanion.entitites;

import java.util.Date;

public class Message
{
    private String fromUser;
    private String toUser;
    private String messageText;
    private long messageTime;

    public Message(String fromUser, String toUser, String messageText)
    {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.messageText = messageText;
        messageTime = new Date().getTime();
    }

    public Message()
    {

    }

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}
