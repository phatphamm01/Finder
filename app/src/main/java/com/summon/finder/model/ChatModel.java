package com.summon.finder.model;

public class ChatModel {
    public String idChat;
    public String newMessage;
    public UserModel userModel;

    public ChatModel(String idChat, UserModel userUser, String newMessage) {
        this.idChat = idChat;
        this.userModel = userUser;
        this.newMessage = newMessage;
    }
}
