package com.summon.finder.page.introductory;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.summon.finder.R;
import com.summon.finder.page.login.LoginActivity;
import com.summon.finder.page.main.MainActivity;
import com.summon.finder.utils.changeintent.ChangeIntent;

public class IntroductoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);

        String uid = FirebaseAuth.getInstance().getUid();
        ChangeIntent.getInstance().authGuard(uid,this, new Intent(this, MainActivity.class));
    }
}