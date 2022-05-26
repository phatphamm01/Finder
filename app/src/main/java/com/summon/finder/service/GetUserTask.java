package com.summon.finder.service;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.model.UserModel;

import io.reactivex.rxjava3.disposables.Disposable;

public class GetUserTask {
    private static final String TAG = GetUserTask.class.getSimpleName();
    public Callback callback;
    private Disposable disposable;

    public GetUserTask(Callback callback) {
        this.callback = callback;
    }

    public void execute() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            callback.handle(null);
            return;
        }

        DAOUser daoUser = new DAOUser();

        daoUser.getUserSnapshot().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                DataSnapshot dataSnapshot = task.getResult();
                Object objectValue = dataSnapshot.getValue();

                if (objectValue == null) {
                    callback.handle(null);
                    return;
                }

                UserModel userModel = new UserModel(dataSnapshot);
                callback.handle(userModel);
            }
        });
    }

    public interface Callback {
        void handle(UserModel userModel);
    }
}
