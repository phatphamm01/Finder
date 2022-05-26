package com.summon.finder.page.setting;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;

import java.util.ArrayList;
import java.util.List;

public class SettingAccountFourFragment extends Fragment implements View.OnClickListener {
    SettingAccountActivity settingAccountActivity;
    private View view;
    private List<Button> allButton;
    private Button button;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_config_user_step_four, container, false);

        settingAccountActivity = (SettingAccountActivity) getActivity();
        allButton = getAllButtons((ViewGroup) view);

        handleAddEventOnClickBtn();

        button = view.findViewById(R.id.asa_continue);
        addEventToButton(button);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        addStyleInit();
        if (settingAccountActivity.userModel.getMatchGender().equals("")) {
            offModeClick(button);
        }
    }

    private void addEventToButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingAccountActivity.handleUpStep();
            }
        });
    }

    private void onModeClick(Button button) {
        button.setEnabled(true);
        button.setClickable(true);
        button.setFocusable(true);
        button.setBackgroundColor(getResources().getColor(R.color.primary));
    }

    private void offModeClick(Button button) {
        button.setEnabled(false);
        button.setClickable(false);
        button.setFocusable(false);
        button.setBackgroundColor(getResources().getColor(R.color.neural_80));
    }

    private void addStyleInit() {
        for (int i = 0; i < allButton.size(); i++) {
            Button button = allButton.get(i);
            String s = button.getText().toString();

            if (settingAccountActivity.userModel.getMatchGender().contains(s)) {
                addStyle(button);
            }
        }
    }

    private void handleAddEventOnClickBtn() {
        for (int i = 0; i < allButton.size(); i++) {
            Button button = allButton.get(i);
            button.setOnClickListener(this);
        }
    }

    private void addStyle(Button button) {
        button.setTextColor(getResources().getColor(R.color.primary));
        button.setBackground(requireActivity().getDrawable(R.drawable.button_checkbox_active));
    }

    private void removeStyle(Button button) {
        button.setTextColor(getResources().getColor(R.color.neural_80));
        button.setBackground(requireActivity().getDrawable(R.drawable.button_checkbox));
    }

    public List<Button> getAllButtons(ViewGroup layout) {
        List<Button> btn = new ArrayList<>();
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof Button) {
                if (v.getId() != R.id.asa_continue) {
                    btn.add((Button) v);
                }
            }
        }
        return btn;
    }

    private void removeStyleAllButton(Button button) {
        for (int index = 0; index < allButton.size(); index++) {
            removeStyle(allButton.get(index));
        }
    }

    private void handleClick(Button button) {
        settingAccountActivity.userModel.setMatchGender(button.getText().toString());
        new DAOUser().updateField("matchGender",button.getText().toString());
        addStyle(button);
    }


    @Override
    public void onClick(View v) {
        Button button = (Button) v;
        removeStyleAllButton(button);
        handleClick(button);

        onModeClick(this.button);
    }
}
