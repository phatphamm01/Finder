package com.summon.finder.background.notification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.summon.finder.DAO.DAOChat;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.model.UserModel;

public class NotificationService extends Service {
    private FirebaseUser firebaseUser;
    private DAOUser daoUser;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) return;

        daoUser = new DAOUser();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (daoUser == null) return START_STICKY;

        DAOChat daoChat = new DAOChat();
        daoUser.getMatchByIdAddValueEvent(firebaseUser.getUid(), snapshot -> {
            if (snapshot.child("chatId").getValue() == null) return;
            String idChat = snapshot.child("chatId").getValue().toString();
            String idUser = snapshot.getKey();

            daoUser.getUserSnapshotById(idUser, snapshotUser -> {

                daoChat.getNewMessageAddValue(idChat, (text, date) -> {
                    UserModel userModel = new UserModel(snapshotUser);

                    NotificationGenarate notification = new NotificationGenarate(NotificationService.this);
                    notification.customBigNotification();

                });
            });
        });

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
