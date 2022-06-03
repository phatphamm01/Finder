package com.summon.finder.page.main;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.helper.time.TimeHelper;
import com.summon.finder.http.HTTPLocation;
import com.summon.finder.model.ChatModel;
import com.summon.finder.model.UserModel;
import com.summon.finder.utils.location.GpsService;
import com.summon.finder.utils.location.IBaseGpsListener;
import com.summon.finder.utils.timeobserver.TimeManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, IBaseGpsListener {
    public ImageView imageView;
    int delayMillis = 1000;
    boolean doubleBackToExitPressedOnce = false;
    private GpsService gpsService;
    private Location currentLocation;
    private DAOUser userDao;
    private UserModel userModel;
    private Handler handlerLocation, handlerTime;
    private ImageView userImage;
    private Toast toastExit;
    private int timeSave;


    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userImage = (ImageView) findViewById(R.id.userImage);
        toastExit = Toast.makeText(this, "Vui lòng double click để để thoát", Toast.LENGTH_SHORT);

        userDao = new DAOUser();
        getUserCurrent();

        handleLocation(this);
        setLocationInterval();
        setTimeInterval();

        handleSetStatusWorking();
        setFragmentDefault();
        addEventOnClickChangeFragment();


        userImage.setOnClickListener(v -> {
            setFragmentUser(FragmentUser.PROFILE);
        });
    }

    @Override
    protected void onStop() {
        handlerLocation.removeCallbacksAndMessages(null);
        handlerTime.removeCallbacksAndMessages(null);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        toastExit.cancel();
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        toastExit.show();


        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 200);
    }

    private void getUserCurrent() {
        userDao.getUser().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userModel = new UserModel(snapshot);

                Picasso.get().load(userModel.firstImage()).into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setTimeInterval() {
        timeSave = TimeHelper.getDateNowTime().getMinutes();
        handlerTime = new Handler(Looper.getMainLooper());


        handlerTime.postDelayed(() -> {
            if (TimeHelper.getDateNowTime().getMinutes() != timeSave) {
                timeSave = TimeHelper.getDateNowTime().getMinutes();
                TimeManager.getInstance().change(TimeHelper.getStringNowTime());
                setTimeInterval();
            }
        }, 1000);
    }

    private void setLocationInterval() {
        handlerLocation = new Handler(Looper.getMainLooper());

        handlerLocation.postDelayed(() -> {
            delayMillis = 60 * 1000;
            setLocationToDB();
        }, delayMillis);
    }

    private void setLocationToDB() {
        userDao = new DAOUser();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://location-api-mu.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        HTTPLocation httpLocation = retrofit.create(HTTPLocation.class);

        setLocationInterval();
        if (currentLocation == null) {
            return;
        }

        retrofit2.Call<HTTPLocation.LocationModel> call = httpLocation.getData(currentLocation.getLatitude(), currentLocation.getLongitude());
        call.enqueue(new Callback<HTTPLocation.LocationModel>() {
            @Override
            public void onResponse(@NonNull retrofit2.Call<HTTPLocation.LocationModel> call, @NonNull Response<HTTPLocation.LocationModel> response) {
                if (response.code() != 200) {
                    Log.d("Location", "fail");
                    return;
                }
                HTTPLocation.LocationModel data = response.body();


                Map<String, Object> payload = new HashMap<>();

                payload.put("lat", currentLocation.getLatitude());
                payload.put("lon", currentLocation.getLongitude());
                assert data != null;
                payload.put("name", data.location);
                payload.put("locationDetail", data.locationDetail);

                userDao.updateField("location", payload);
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<HTTPLocation.LocationModel> call, @NonNull Throwable t) {
                Log.d("Location", "fail");
            }
        });
    }

    private void handleLocation(MainActivity mainActivity) {
        gpsService = new GpsService(mainActivity);
        gpsService.execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        gpsService.handleResquestPermissionResult(requestCode, permissions, grantResults);
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
        fragmentTransaction.commit();
    }

    public void visibleFragmentMessage() {
        findViewById(R.id.fragmentMain).setVisibility(View.INVISIBLE);
        findViewById(R.id.fragmentMessage).setVisibility(View.VISIBLE);
        findViewById(R.id.fragmentUser).setVisibility(View.INVISIBLE);
    }

    public void visibleFragmentMain() {
        findViewById(R.id.fragmentMain).setVisibility(View.VISIBLE);
        findViewById(R.id.fragmentMessage).setVisibility(View.INVISIBLE);
        findViewById(R.id.fragmentUser).setVisibility(View.INVISIBLE);
    }

    public void visibleFragmentUser() {
        findViewById(R.id.fragmentMain).setVisibility(View.INVISIBLE);
        findViewById(R.id.fragmentMessage).setVisibility(View.INVISIBLE);
        findViewById(R.id.fragmentUser).setVisibility(View.VISIBLE);
    }

    public void setFragmentMessage(String idChat, ChatModel user) {
        visibleFragmentMessage();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ChatModel userCurrent = new ChatModel(user.getIdChat(), user.getNewMessage(), userModel);
        Fragment fragment = new ChatFragment(idChat, userCurrent, user);

        fragmentTransaction.replace(R.id.fragmentMessage, fragment, fragment.getTag());
        fragmentTransaction.commit();
    }

    public void setFragmentUser(FragmentUser fragmentUser) {
        visibleFragmentUser();

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = new ProfileFragment();

        switch (fragmentUser) {
            case PROFILE:
                fragment = new ProfileFragment();
                break;
            case EDIT_PROFILE:
                fragment = new EditProfileFragment();
                break;
            case SETTING:
                fragment = new SettingFragment();
                break;
        }

        fragmentTransaction.replace(R.id.fragmentUser, fragment, fragment.getTag());
        fragmentTransaction.commit();
    }

    private void handleSetStatusWorking() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        assert firebaseUser != null;
        webView.loadUrl("https://server-check-active.herokuapp.com/?user=" + firebaseUser.getUid());
    }

    @Override
    public void onClick(View v) {
        String[] idList = new String[]{"home", "message", "match"};
        ArrayList<String> listView = new ArrayList<>(Arrays.asList(idList));

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

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onGpsStatusChanged(int event) {

    }

    public enum FragmentUser {
        PROFILE,
        EDIT_PROFILE,
        SETTING,
    }

    enum FRAGMENT {
        HOME,
        MESSAGE,
        MATCH
    }
}