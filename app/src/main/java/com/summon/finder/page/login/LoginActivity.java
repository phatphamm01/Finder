package com.summon.finder.page.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.summon.finder.R;
import com.summon.finder.databinding.ActivityLoginBinding;
import com.summon.finder.service.GoogleLoginTask;

public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {

    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private ActivityLoginBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginGoogle.setOnClickListener(this);

        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.d(TAG, "onActivityResult: Google SignIn intent result");

            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(accountTask);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> accountTask) {
        try {
            GoogleSignInAccount account = accountTask.getResult(ApiException.class);

            new GoogleLoginTask(this, firebaseAuth, account).execute();
        } catch (Exception e) {
            Log.d(TAG, "onActivityResult: " + e.getMessage());
        }
    }

    private void signInGoogle() {
        Intent intent = googleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "test");
        switch (v.getId()) {
            case R.id.login_google:
                signInGoogle();
                break;
        }
    }


}