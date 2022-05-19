package com.summon.finder.service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.model.UserModel;
import com.summon.finder.page.main.MainActivity;
import com.summon.finder.page.setting.SettingAccountActivity;
import com.summon.finder.utils.ChangeIntent;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class GoogleLoginTask extends AsyncTask<String, Void, Void> {
    public static final String USER_BIRTHDAY_READ = "https://www.googleapis.com/auth/user.birthday.read";
    public static final String USER_PHONENUMBERS_READ = "https://www.googleapis.com/auth/user.phonenumbers.read";
    public static final String USERINFO_EMAIL = "https://www.googleapis.com/auth/userinfo.email";
    public static final String USERINFO_PROFILE = "https://www.googleapis.com/auth/userinfo.profile";
    private static final String TAG = "GET_DATA_USER_TASK";
    private final Activity context;
    private final GoogleSignInAccount acct;
    private final FirebaseAuth firebaseAuth;

    public GoogleLoginTask(@NotNull Activity context, @NotNull FirebaseAuth firebaseAuth, @NotNull GoogleSignInAccount acct) {
        this.context = context;
        this.acct = acct;
        this.firebaseAuth = firebaseAuth;
    }

    public static String readStream(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data = new byte[2048];
        int len = 0;
        while ((len = is.read(data, 0, data.length)) >= 0) {
            bos.write(data, 0, len);
        }
        is.close();
        return bos.toString(String.valueOf(StandardCharsets.UTF_8));
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            JSONObject profile = getJsonObject();
            firebaseAuthWithGoogleAccount(acct, profile);
        } catch (UserRecoverableAuthException recoverableException) {
            context.startActivityForResult(recoverableException.getIntent(), 1234);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account, JSONObject profile) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Log.d(TAG, "onSuccess: Logged In");

                        try {
                            if (authResult.getAdditionalUserInfo().isNewUser()) {
                                handleAddDataToDB(authResult);

                                ChangeIntent.getInstance().authGuard(context, SettingAccountActivity.class);
                            } else {
                                GetUserTask getUserTask = new GetUserTask(userModel -> {
                                    if (!userModel.getActive()) {
                                        ChangeIntent.getInstance().authGuard(context, SettingAccountActivity.class);
                                        return;
                                    }

                                    ChangeIntent.getInstance().authGuard(context, MainActivity.class);
                                });

                                getUserTask.execute();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    private void handleAddDataToDB(AuthResult authResult) throws JSONException {
                        UserModel newProfile = handleSuccessGoogleLogin(authResult);
                        DAOUser daoUser = new DAOUser();
                        daoUser.addUser(newProfile);
                    }

                    private UserModel handleSuccessGoogleLogin(AuthResult authResult) throws JSONException {
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String uid = firebaseUser.getUid();
                        String name = firebaseUser.getDisplayName();

                        UserModel userModel = new UserModel();
                        userModel.setUid(uid);
                        userModel.setName(name);

                        if (profile == null) {
                            return userModel;
                        }

                        JSONArray birthdays = (JSONArray) profile.opt("birthdays");
                        assert birthdays != null;
                        JSONObject data = (JSONObject) birthdays.getJSONObject(1).get("date");
                        userModel.setBirthday(data.get("day") + "/" + data.get("month") + "/" + data.get("year"));

                        return userModel;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: Login failed" + e.getMessage());

                        handleFailGoogleLogin();
                    }

                    private void handleFailGoogleLogin() {

                    }
                });
    }

    @Nullable
    private JSONObject getJsonObject() throws IOException, GoogleAuthException, JSONException {
        String token = GoogleAuthUtil.getToken(context, acct.getAccount(), "oauth2: " + USERINFO_PROFILE + " " + USER_PHONENUMBERS_READ + " " + USERINFO_EMAIL + " " + USER_BIRTHDAY_READ);

        URL url = new URL("https://people.googleapis.com/v1/people/me?"
                + "personFields=genders,birthdays,phoneNumbers,emailAddresses"
                + "&access_token=" + token);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        int sc = con.getResponseCode();
        if (sc == 200) {
            InputStream is = con.getInputStream();
            JSONObject profile = new JSONObject(readStream(is));
            Log.d(TAG, "Got:" + profile.toString(2));
            Log.d(TAG, "genders: " + profile.opt("genders"));
            Log.d(TAG, "birthdays: " + profile.opt("birthdays"));
            Log.d(TAG, "phoneNumbers: " + profile.opt("phoneNumbers"));
            return profile;
        }
        return null;
    }

}
