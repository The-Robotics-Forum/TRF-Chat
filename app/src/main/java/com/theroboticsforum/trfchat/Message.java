package com.theroboticsforum.trfchat;

import java.util.Date;

public class Message {
    private String message;
    private String senderEmail;

    public Message()
    {
        //necessary no-argument contructor
    }


    public Message(String message, String senderEmail) {
        this.message = message;
        this.senderEmail = senderEmail;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderEmail() {
        return senderEmail;
    }
}
