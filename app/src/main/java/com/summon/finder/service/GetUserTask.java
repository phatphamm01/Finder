package com.summon.finder.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

    public void execute(String uid) {
        DAOUser daoUser = new DAOUser();

        daoUser.getUserSnapshotById(uid, snapshot -> {
            if (snapshot == null) {
                callback.handle(null);
                return;
            }

            Object objectValue = snapshot.getValue();

            if (objectValue == null) {
                callback.handle(null);
                return;
            }

            UserModel userModel = new UserModel(snapshot);
            callback.handle(userModel);
        });
    }

    public interface Callback {
        void handle(UserModel userModel);
    }
}
