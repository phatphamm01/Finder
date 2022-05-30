package com.summon.finder.model;

public class MessageModel {
    private String message;
    private String createdByUser;

    public MessageModel(String message, String createdByUser) {
        this.message = message;
        this.createdByUser = createdByUser;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(String createdByUser) {
        this.createdByUser = createdByUser;
    }
}
