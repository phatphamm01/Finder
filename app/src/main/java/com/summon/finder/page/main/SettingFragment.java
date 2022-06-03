package com.summon.finder.page.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.component.slide.ContinuousSlider;
import com.summon.finder.component.slide.RangeSlide;
import com.summon.finder.databinding.FragmentSettingBinding;
import com.summon.finder.model.UserModel;
import com.summon.finder.page.login.LoginActivity;
import com.summon.finder.utils.changeintent.ChangeIntent;

import java.util.Arrays;
import java.util.List;

public class SettingFragment extends Fragment {
    FirebaseAuth mGoogleSignInClient;
    private MainActivity mainActivity;
    private View view;
    private UserModel userModel;
    private FragmentSettingBinding binding;
    private DAOUser daoUser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false);
        view = binding.getRoot();
        mainActivity = (MainActivity) getActivity();
        userModel = mainActivity.getUserModel();
        daoUser = new DAOUser();
        mGoogleSignInClient = FirebaseAuth.getInstance();

        addEventDone();

        addEventLogout();

        setGender();
        setEventGender();

        setDistance();
        setAge();
        setStatusDistance();
        setStatusAge();

        return view;
    }

    private void setStatusAge() {
        Boolean matchStatusDistance = userModel.getStatusMatchDistance();
        ((Switch) binding.statusRangeDistance).setChecked(matchStatusDistance);
    }

    private void setStatusDistance() {
        Boolean matchStatusAge = userModel.getStatusMatchAge();
        ((Switch) binding.statusRangeAge).setChecked(matchStatusAge);
    }


    private void setDistance() {
        float matchDistance = userModel.getMatchDistance();
        matchDistance = matchDistance != 0 ? matchDistance : 2;
        ((ContinuousSlider) binding.rangeDistance).setValueSlide(matchDistance);
    }

    private void setAge() {
        List<Float> matchAge = userModel.getMatchAge();
        matchAge = matchAge != null ? matchAge : Arrays.asList(18f, 24f);
        ((RangeSlide) binding.rangeAge).setValueSlide(matchAge);
    }


    private void setEventGender() {
        binding.boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daoUser.updateField("matchGender", "Nam");
                addStyle(binding.boy);
                removeStyle(binding.girl);
            }
        });

        binding.girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daoUser.updateField("matchGender", "Nữ");
                addStyle(binding.girl);
                removeStyle(binding.boy);
            }
        });
    }

    private void setGender() {
        if (userModel.getMatchGender().equals("Nam")) {
            addStyle(binding.boy);
        } else {
            addStyle(binding.girl);
        }
    }


    private void addEventDone() {
        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float valueDistance = ((ContinuousSlider) binding.rangeDistance).getValueSlide();
                List<Float> valueAge = ((RangeSlide) binding.rangeAge).getValueSlide();
                boolean statusMatchDistance = ((Switch) binding.statusRangeDistance).isChecked();
                boolean statusMatchAge = ((Switch) binding.statusRangeAge).isChecked();

                daoUser.updateField("matchDistance", valueDistance);
                daoUser.updateField("matchAge", valueAge);
                daoUser.updateField("statusMatchDistance", statusMatchDistance);
                daoUser.updateField("statusMatchAge", statusMatchAge);

                ChangeIntent.getInstance().authGuard(mainActivity.getUserModel().getUid(), mainActivity, new Intent(mainActivity, MainActivity.class));
            }
        });
    }

    private void addEventLogout() {
        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AuthUI.getInstance()
                        .signOut(mainActivity)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                ChangeIntent.getInstance().authGuard("", mainActivity, new Intent(mainActivity, LoginActivity.class));
                            }
                        });
            }
        });
    }


    private void addStyle(Button button) {
        button.setTextColor(getResources().getColor(R.color.primary));
        button.setBackground(requireActivity().getDrawable(R.drawable.button_checkbox_active));
    }

    private void removeStyle(Button button) {
        button.setTextColor(getResources().getColor(R.color.neural_80));
        button.setBackground(requireActivity().getDrawable(R.drawable.button_checkbox));
    }
}