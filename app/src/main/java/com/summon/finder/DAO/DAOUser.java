package com.summon.finder.DAO;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.summon.finder.model.UserModel;

import java.util.HashMap;

public class   DAOUser {
    private final DatabaseReference databaseReference;
    private FirebaseUser firebaseUser;

    public DAOUser() {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = db.getReference("user").child(firebaseUser.getUid());
    }

    public Task<DataSnapshot> getUserSnapshot() {
        return databaseReference.get();
    }

    public DatabaseReference getUser() {
       return databaseReference;
    }

    public Task<Void> addUser(UserModel userModel) {
        return databaseReference.setValue(userModel);
    }

    public Task<Void> update(UserModel userModel) {
        return databaseReference.setValue(userModel);
    }

    public Task<Void> updateField(String field,Object data) {
        return databaseReference.child(field).setValue(data);
    }

    public DatabaseReference getConnections() {
        return databaseReference.child("connections");
    }
}

