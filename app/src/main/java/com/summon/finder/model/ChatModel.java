package com.summon.finder.model;

public class ChatModel {
    private String idChat;
    private String newMessage;
    private UserModel userModel;

    public ChatModel(String idChat, UserModel userUser, String newMessage) {
        this.idChat = idChat;
        this.userModel = userUser;
        this.newMessage = newMessage;
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

    public void loadMessage() {

    }
}
