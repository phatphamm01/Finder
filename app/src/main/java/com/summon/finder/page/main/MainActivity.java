package com.summon.finder.page.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.summon.finder.R;
import com.summon.finder.page.login.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleSetStatusWorking();
        setFragmentDefault();
        addEventOnClickChangeFragment();


        findViewById(R.id.logo).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
    }

    private void addEventOnClickChangeFragment() {
        findViewById(R.id.homeCardView).setOnClickListener(this);
        findViewById(R.id.matchCardView).setOnClickListener(this);
        findViewById(R.id.messageCardView).setOnClickListener(this);
    }

    private void setFragmentDefault() {
        setFragment(FRAGMENT.HOME);
    }

    private void setFragment(FRAGMENT step) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;

        switch (step) {
            case HOME:
                fragment = new SwipeFragment();
                break;
            case MESSAGE:
                fragment = new MessageFragment();
                break;
            case MATCH:
                fragment = new MatchFragment();
                break;
            default:
                fragment = new SwipeFragment();
        }

        fragmentTransaction.replace(R.id.fragmentMain, fragment, fragment.getTag());
        fragmentTransaction.addToBackStack(fragment.getTag());
        fragmentTransaction.commit();
    }

    private void handleSetStatusWorking() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://server-check-active.herokuapp.com/?user=" + firebaseUser.getUid());
    }

    @Override
    public void onClick(View v) {
        String[] idList = new String[]{"home", "message", "match"};
        ArrayList<String> listView = new ArrayList<String>(Arrays.asList(idList));

        listView.forEach(value -> {
            int id = getResources().getIdentifier(value + "ImageView", "id", getPackageName());
            ((ImageView) findViewById(id)).setColorFilter(getResources().getColor(R.color.page_unactive));
        });

        int color = getResources().getColor(R.color.primary);
        switch (v.getId()) {
            case R.id.homeCardView:
                setFragment(FRAGMENT.HOME);
                ((ImageView) findViewById(R.id.homeImageView)).setColorFilter(null);
                break;
            case R.id.messageCardView:
                setFragment(FRAGMENT.MESSAGE);
                ((ImageView) findViewById(R.id.messageImageView)).setColorFilter(color);
                break;
            case R.id.matchCardView:
                setFragment(FRAGMENT.MATCH);
                ((ImageView) findViewById(R.id.matchImageView)).setColorFilter(color);
                break;
        }
    }

    enum FRAGMENT {
        HOME,
        MESSAGE,
        MATCH
    }
}