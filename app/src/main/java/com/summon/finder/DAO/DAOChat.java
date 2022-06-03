package com.summon.finder.DAO;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class DAOChat {
    private final DatabaseReference chatDb;

    public DAOChat() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        chatDb = db.getReference("chats");
    }

    public void loadMessage(String idChat, ICallback cb) {
        chatDb.child(idChat).child("chat")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        cb.execute(snapshot);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    public void getNewMessageAddValue(String idChat, ICallbackNewMessage cb) {
        DatabaseReference newMessage = chatDb.child(idChat).child("newMessage");
        DatabaseReference newMessageDate = chatDb.child(idChat).child("newMessageDate");

        newMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshotNewMessage) {
                newMessageDate.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot snapshotNewMessageDate) {
                        String text = snapshotNewMessage.getValue(String.class);
                        Date date = snapshotNewMessageDate.getValue(Date.class);
                        cb.execute(text, date);
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public interface ICallback {
        void execute(DataSnapshot snapshot);
    }

    public interface ICallbackNewMessage {
        void execute(String text, Date date);
    }
}
