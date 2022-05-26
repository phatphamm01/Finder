package com.summon.finder.page.introductory;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.summon.finder.R;
import com.summon.finder.page.main.MainActivity;
import com.summon.finder.utils.ChangeIntent;

public class IntroductoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introductory);

        ChangeIntent.getInstance().authGuard(this, MainActivity.class);
    }
}