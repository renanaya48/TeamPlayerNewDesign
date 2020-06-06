package com.example.teamplayer;

public class message_item {
    String message;
    String sender;
    String time;
    boolean is_sender;
    public message_item(String message,String sender , String time,  boolean is_sender){
        this.message=message;
        this.sender = sender;
        this.time = time;
        this.is_sender=is_sender;

    }
}
