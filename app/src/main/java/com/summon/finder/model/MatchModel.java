package com.summon.finder.model;

public class MatchModel {
    private String chatID;

    public String getChatID() {
        return chatID;
    }

    public void setChatID(String chatID) {
        this.chatID = chatID;
    }

    public MatchModel(String chatID) {
        this.chatID = chatID;
    }
}
