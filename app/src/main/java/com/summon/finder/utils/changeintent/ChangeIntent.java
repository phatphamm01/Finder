package com.summon.finder.utils.changeintent;

import android.content.Context;
import android.content.Intent;

import com.summon.finder.page.login.LoginActivity;
import com.summon.finder.page.setting.SettingAccountActivity;
import com.summon.finder.service.GetUserTask;

public class ChangeIntent {
    public static ChangeIntent instance;

    public static ChangeIntent getInstance() {
        if (instance == null) {
            instance = new ChangeIntent();
            return instance;
        }

        return instance;
    }

    public void authGuard(String uid, Context activity, Intent intent) {
        new GetUserTask(userModel -> {
            if (userModel == null) {
                Intent intentLogin = new Intent(activity, LoginActivity.class);
                intentLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                activity.startActivity(intentLogin);
                return;
            }

            if (!userModel.getActive()) {
                Intent intentSetting = new Intent(activity, SettingAccountActivity.class);
                intentSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intentSetting.putExtra("uid", userModel.getUid());
                activity.startActivity(intentSetting);
                return;
            }


            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("uid", userModel.getUid());
            activity.startActivity(intent);
        }).execute(uid);

    }
}
