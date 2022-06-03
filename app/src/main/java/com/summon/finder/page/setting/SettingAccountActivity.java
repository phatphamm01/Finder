package com.summon.finder.page.setting;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.databinding.ActivitySettingAccountBinding;
import com.summon.finder.model.UserModel;
import com.summon.finder.service.GetUserTask;

public class SettingAccountActivity extends AppCompatActivity implements View.OnClickListener {
    private final int START_STEP = 1;
    private final int END_STEP = 7;
    public UserModel userModel;
    public ImageView imageView;

    private DAOUser daoUser;

    private int step = 1;
    private ActivitySettingAccountBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String uid = intent.getStringExtra("uid");
        daoUser = new DAOUser();

        new GetUserTask(newUserModel -> {
            userModel = newUserModel;

            handleViewWhenChangeStep(step);
        }).execute(uid);


        binding.btnComeback.setOnClickListener(this);


    }

    private void setStep(int step) {
        if (step < START_STEP) {
            this.step = START_STEP;
            return;
        }

        if (step > END_STEP) {
            this.step = END_STEP;
            return;
        }

        this.step = step;
    }

    private void handleViewWhenChangeStep(int step) {
        setFragment(step);

        changeProgressLoading(step);

        changeViewBtnComeback(step);
    }

    private void changeViewBtnComeback(int step) {
        if (step == START_STEP) {
            binding.btnComeback.setVisibility(View.INVISIBLE);
            return;
        }

        binding.btnComeback.setVisibility(View.VISIBLE);
    }

    private void changeProgressLoading(int step) {
        int widthLayout = binding.bgLoading.getWidth();
        int widthTab = widthLayout / END_STEP;

        binding.loading.getLayoutParams().width = widthTab * step;
        binding.loading.getLayoutParams().width = widthTab * step;
        binding.loading.requestLayout();
    }

    private void setFragment(int step) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment;

        switch (step) {
            case 1:
                fragment = new SettingAccountOneFragment();
                break;
            case 2:
                fragment = new SettingAccountTwoFragment();
                break;
            case 3:
                fragment = new SettingAccountThreeFragment();
                break;
            case 4:
                fragment = new SettingAccountFourFragment();
                break;
            case 5:
                fragment = new SettingAccountFiveFragment();
                break;
            case 6:
                fragment = new SettingAccountSixFragment();
                break;
            default:
                fragment = new SettingAccountSevenFragment();
        }

        fragmentTransaction.replace(R.id.fragment_setting_account, fragment, fragment.getTag());
        fragmentTransaction.commit();
    }


    public void handleUpStep() {
        setStep(step + 1);
        handleViewWhenChangeStep(step);
    }

    public void handleDownStep() {
        setStep(step - 1);
        handleViewWhenChangeStep(step);
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.asa_continue:
                handleUpStep();
                break;
            case R.id.btn_comeback:
                handleDownStep();
                break;
        }
    }

}