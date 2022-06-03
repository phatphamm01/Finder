package com.summon.finder.DAO;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.summon.finder.helper.time.TimeHelper;
import com.summon.finder.model.MessageModel;

public class DAOMessage {
    private final DatabaseReference chatDb;

    public DAOMessage() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        chatDb = db.getReference("chats");
    }

    public void sendMessage(String idChat, MessageModel data) {
        DatabaseReference dataPush = chatDb.child(idChat).child("chat").push();
        chatDb.child(idChat).child("newMessage").setValue(data.getMessage());
        chatDb.child(idChat).child("newMessageDate").setValue(TimeHelper.getDateNowTime());

        dataPush.setValue(data);
    }
}
