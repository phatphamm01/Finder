package com.summon.finder.DAO;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.summon.finder.model.UserModel;

import java.util.UUID;

public class DAOUser {
    private final DatabaseReference userDB;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        userDB = db.getReference("user");
    }

    public void createUser(String uid, String name) {
        UserModel userModel = handleSuccessGoogleLogin(uid, name);

        addUser(userModel);
    }

    public void createUser(String typeLogin, String uid, String phone, String password, String name) {
        UserModel userModel = handleSuccessGoogleLogin(uid, name);
        userModel.setPhone(phone);
        userModel.setPassword(password);
        userModel.setTypeLogin(typeLogin);
        addUser(userModel);
    }

    public void getUserByPhone(String phone, ICallback cb) {
        userDB.orderByChild("phone").equalTo(phone).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        }).addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    cb.execute(postSnapshot);
                }
            }
        });
    }

    private UserModel handleSuccessGoogleLogin(String uid, String name) {
        UserModel userModel = new UserModel();
        userModel.setUid(uid);
        userModel.setName(name);

        return userModel;
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
        if (id == null || id.isEmpty()) {
            cb.execute(null);
            return;

        }

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
                .child(uidCurrent).setValue("");

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
    public DatabaseReference getMatchById(String uidUser) {
        return userDB
                .child(uidUser)
                .child("match");
    }

    public void getMatchByIdAddChildEvent(String uidUser, ICallback cb) {
        userDB
                .child(uidUser)
                .child("match").addChildEventListener(new ChildEventListener() {
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

    public void getMatchByIdAddValueEvent(String uidUser, ICallback cb) {
        userDB
                .child(uidUser)
                .child("match").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                cb.execute(snapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @NonNull
    private DatabaseReference getDatabaseReference() {
        String firebaseUser = FirebaseAuth.getInstance().getUid();
        return userDB.child(firebaseUser);
    }

    public void addUser(UserModel newProfile) {
        userDB.child(newProfile.getUid()).setValue(newProfile);
    }

    public void update(UserModel newProfile) {
        userDB.child(newProfile.getUid()).setValue(newProfile);
    }

    public interface ICallback {
        void execute(DataSnapshot snapshot);
    }

    public interface ICallbackDefault {
        void execute();
    }
}

