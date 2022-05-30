package com.summon.finder.DAO;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.summon.finder.model.UserModel;

import java.util.UUID;

public class DAOUser {
    private final DatabaseReference userDB;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        userDB = db.getReference("user");
    }

    public void getUserSnapshot(ICallback cb) {
        DatabaseReference user = getDatabaseReference();
        user.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                cb.execute(dataSnapshot);
            }
        });
    }

    public DatabaseReference getUser() {
        DatabaseReference user = getDatabaseReference();
        return user;
    }

    public void getUserSnapshotById(String id, ICallback cb) {
        DatabaseReference user = userDB.child(id);
        user.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                cb.execute(dataSnapshot);
            }
        });
    }

    public Task<Void> updateField(String field, Object data) {
        DatabaseReference user = getDatabaseReference();
        return user.child(field).setValue(data);
    }

    public DatabaseReference getConnections() {
        DatabaseReference user = getDatabaseReference();
        return user.child("connections");
    }

    public void unMatch(String id) {
        this.getConnections().child("nope").child(id).setValue("");
    }

    public void match(String id) {
        this.getConnections().child("yep").child(id).setValue("");
    }

    public void handleLiked(UserModel user, String uidUser, String uidCurrent, ICallbackDefault cb) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference matchByIdDB = getMatchById(uidUser);
        matchByIdDB
                .child(uidUser).setValue("");

        getMatchById(uidCurrent)
                .get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(user.getUid()).getValue() != null) {
                    cb.execute();


                    String uuid = UUID.randomUUID().toString();
                    db.getReference("chats").child(uuid).setValue("");

                    setIdChatToMatch(uuid, uidUser, uidCurrent);
                    setIdChatToMatch(uuid, uidCurrent, uidUser);
                }
            }
        });
    }

    private void setIdChatToMatch(String uuid, String uidUser, String uidCurrent) {
        getMatchById(uidUser).child(uidCurrent).child("chatId").setValue(uuid);
    }

    @NonNull
    private DatabaseReference getMatchById(String uidUser) {
        return userDB
                .child(uidUser)
                .child("match");
    }

    @NonNull
    private DatabaseReference getDatabaseReference() {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        return userDB.child(firebaseUser.getUid());
    }

    public void addUser(UserModel newProfile) {
        userDB.child(newProfile.getUid()).setValue(newProfile);
    }

    public interface ICallback {
        void execute(DataSnapshot snapshot);
    }

    public interface ICallbackDefault {
        void execute();
    }
}

