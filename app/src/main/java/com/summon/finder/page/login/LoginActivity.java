package com.summon.finder.page.login;

import static com.basgeekball.awesomevalidation.ValidationStyle.COLORATION;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.page.main.MainActivity;
import com.summon.finder.page.setting.SettingAccountActivity;
import com.summon.finder.service.GetUserTask;
import com.summon.finder.service.GoogleLoginTask;
import com.summon.finder.utils.changeintent.ChangeIntent;

import java.util.concurrent.TimeUnit;


public class LoginActivity extends AppCompatActivity implements
        View.OnClickListener {
    private static final int RC_SIGN_IN = 100;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";
    String regexEmpty = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=.])(?=\\S+$).{8,}$";
    String regexPassword = ".{8,}";
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;

    private DAOUser daoUser;
    private FirebaseAuth mAuth;
    private String verificationId;
    private String phone;
    private String password;
    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks
            mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationId = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                Toast.makeText(LoginActivity.this, code, Toast.LENGTH_SHORT).show();
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        daoUser = new DAOUser();
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.login_google).setOnClickListener(this);
        findViewById(R.id.login_userpass).setOnClickListener(this);

        handleInitFirebase();
    }

    private void handleInitFirebase() {
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

    private void signInUserPassword() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);

        EditText mPhone = (EditText) mView.findViewById(R.id.etPhone);
        EditText mPassword = (EditText) mView.findViewById(R.id.etPassword);

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(COLORATION);
        mAwesomeValidation.addValidation(mPhone, "^\\d{10}$", "Vui lòng nhập chính xác số điện thoại");

        mAwesomeValidation.addValidation(mPassword, regexEmpty, "Mật khẩu phải có hơn 1 chữ hoa, 1 chữ thường, 1 số và 1 kí tự đặt biệt");
        mAwesomeValidation.addValidation(mPassword, regexPassword, "Mật khẩu phải dài hơn 8 kí tự");
        mAwesomeValidation.addValidation(mPassword, ".{1,}", "Vui lòng nhập mật khẩu");


        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
        Button mSignup = (Button) mView.findViewById(R.id.btnSignup);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        dialog.show();
        dialog.getWindow().setLayout(width, dialog.getWindow().getAttributes().height);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mAwesomeValidation.validate();

                if (!isCheck) {
                    return;
                }

                String phone = "+84" + mPhone.getText().toString();
                String password = mPassword.getText().toString();


                daoUser.getUserByPhone(phone, (snapshot -> {

                    if (!snapshot.exists() || snapshot.getValue() == null) {
                        Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String typeLogin = snapshot.child("typeLogin").getValue(String.class);
                    if (!typeLogin.contains("USER_PASSWORD")) {
                        Toast.makeText(LoginActivity.this, "Tài khoản không tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (!snapshot.child("password").getValue(String.class).contains(password)) {
                        Toast.makeText(LoginActivity.this, "Sai mật khẩu", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    sendVerificationCode(phone);
                    dialog.dismiss();
                    otpUserPassword();
                }));


            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                signupUserPassword();
            }
        });
    }

    private void otpUserPassword() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_otp, null);

        EditText mOTP = (EditText) mView.findViewById(R.id.etOTP);

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(COLORATION);
        mAwesomeValidation.addValidation(mOTP, ".{6,6}", "Vui lòng nhập chính xác");


        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);
        Button mSignup = (Button) mView.findViewById(R.id.btnSignup);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        dialog.show();
        dialog.getWindow().setLayout(width, dialog.getWindow().getAttributes().height);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mAwesomeValidation.validate();

                if (!isCheck) {
                    return;
                }

                String otp = mOTP.getText().toString();
                verifyCode(otp);

            }
        });

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                signupUserPassword();
            }
        });
    }

    private void signupUserPassword() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_signup, null);

        EditText mPhone = (EditText) mView.findViewById(R.id.etPhone);
        EditText mPassword = (EditText) mView.findViewById(R.id.etPassword);
        EditText mPasswordConfirm = (EditText) mView.findViewById(R.id.etPasswordConfirm);

        AwesomeValidation mAwesomeValidation = new AwesomeValidation(COLORATION);

        mAwesomeValidation.addValidation(mPhone, "^\\d{10}$", "Vui lòng nhập chính xác số điện thoại");

        mAwesomeValidation.addValidation(mPassword, regexEmpty, "Mật khẩu phải có hơn 1 chữ hoa, 1 chữ thường, 1 số và 1 kí tự đặt biệt");
        mAwesomeValidation.addValidation(mPassword, regexPassword, "Mật khẩu phải dài hơn 8 kí tự");
        mAwesomeValidation.addValidation(mPassword, ".{1,}", "Vui lòng nhập mật khẩu");

        mAwesomeValidation.addValidation(mPasswordConfirm, mPassword, "Mật khẩu không giống nhau");

        Button mSignup = (Button) mView.findViewById(R.id.btnSignup);
        Button mLogin = (Button) mView.findViewById(R.id.btnLogin);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();

        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.90);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.90);
        dialog.show();
        dialog.getWindow().setLayout(width, dialog.getWindow().getAttributes().height);

        mSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isCheck = mAwesomeValidation.validate();

                if (!isCheck) {
                    return;
                }

                String phone = "+84" + mPhone.getText().toString();
                String password = mPassword.getText().toString();
                LoginActivity.this.password = password;
                LoginActivity.this.phone = phone;

                daoUser.getUserSnapshotById(phone, (snapshot -> {
                    if (snapshot.exists()) {
                        Toast.makeText(LoginActivity.this, "Tài khoản đã tồn tại", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    sendVerificationCode(phone);
                    dialog.dismiss();
                    otpUserPassword();
                }));

            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                signInUserPassword();
            }
        });
    }

    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithCredential(credential);
    }

    private void signInWithCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "onSuccess: Logged In");

                        if (task.isSuccessful()) {
                            AuthResult authResult = task.getResult();
                            String uid = authResult.getUser().getUid();

                            if (authResult.getAdditionalUserInfo().isNewUser()) {
                                handleAddDataToDB(authResult);

                                ChangeIntent.getInstance().authGuard(uid, LoginActivity.this, new Intent(LoginActivity.this, SettingAccountActivity.class));
                            } else {
                                GetUserTask getUserTask = new GetUserTask(userModel -> {
                                    if (!userModel.getActive()) {
                                        ChangeIntent.getInstance().authGuard(uid, LoginActivity.this, new Intent(LoginActivity.this, SettingAccountActivity.class));
                                        return;
                                    }

                                    ChangeIntent.getInstance().authGuard(uid, LoginActivity.this, new Intent(LoginActivity.this, MainActivity.class));
                                });

                                getUserTask.execute(uid);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void handleAddDataToDB(AuthResult authResult) {
        String uid = authResult.getUser().getUid();
        String phoneNumber = this.phone;
        String password = this.password;

        daoUser.createUser("USER_PASSWORD", uid, phoneNumber, password, "");
    }

    private void sendVerificationCode(String number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallBack)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "test");
        switch (v.getId()) {
            case R.id.login_google:
                signInGoogle();
                break;
            case R.id.login_userpass:
                signInUserPassword();
                break;
            case R.id.btnSignupE:
                signupUserPassword();
                break;

        }
    }
}