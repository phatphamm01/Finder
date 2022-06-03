package com.summon.finder;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.summon.finder.background.notification.NotificationService;
import com.summon.finder.page.login.LoginActivity;
import com.summon.finder.utils.changeintent.ChangeIntent;

public class Main extends Application implements Application.ActivityLifecycleCallbacks {
    @Override
    public void onCreate() {
        super.onCreate();

        String uid = FirebaseAuth.getInstance().getUid();


        ChangeIntent.getInstance().authGuard(uid, this, new Intent(this, LoginActivity.class));
        startService(new Intent(this, NotificationService.class));

    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        Log.d("Main", "out");
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {
        Log.d("Main", "out");
    }

}
