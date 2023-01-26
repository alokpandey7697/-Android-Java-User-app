package com.alok.alok.Notification.SendNotificationPack;

public class Data {
    private String Title;
    private String Message;
    private String Sender;



    public Data(String title, String message, String sender) {
        Title = title;
        Message = message;
        Sender = sender;
    }

    public Data() {
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String sender) {
        Sender = sender;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

}
