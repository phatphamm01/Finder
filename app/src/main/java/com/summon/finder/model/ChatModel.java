package com.summon.finder.model;

import java.util.Date;

public class ChatModel {
    private String idChat;
    private String newMessage;
    private UserModel userModel;
    private Date newMessageDate = new Date(2323223232L);

    public ChatModel(String idChat, String newMessage, UserModel userModel) {
        this.idChat = idChat;
        this.newMessage = newMessage;
        this.userModel = userModel;
    }

    public ChatModel(String idChat, String newMessage, UserModel userModel, Date dateNewMessage) {
        this.idChat = idChat;
        this.newMessage = newMessage;
        this.userModel = userModel;
        this.newMessageDate = dateNewMessage != null ? dateNewMessage : this.newMessageDate;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getNewMessage() {
        return newMessage;
    }

    public void setNewMessage(String newMessage) {
        this.newMessage = newMessage;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public Date getNewMessageDate() {
        return newMessageDate;
    }

    public void setNewMessageDate(Date newMessageDate) {
        this.newMessageDate = newMessageDate;
    }
}
