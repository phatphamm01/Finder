package com.summon.finder.page.setting;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;

public class SettingAccountOneFragment extends Fragment {
    View view;
    SettingAccountActivity settingAccountActivity;

    Button button;
    EditText editText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_config_user_step_one, container, false);

        settingAccountActivity = (SettingAccountActivity) getActivity();

        button = view.findViewById(R.id.asa_continue);
        editText = view.findViewById(R.id.textPersonName);


        addEventToButton(button);
        addEventToEditText(button, editText);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setNameToView();

        if (settingAccountActivity.userModel.getName().equals("")) {
            offModeClick(button);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setNameModel();
    }

    private void addEventToEditText(Button button, EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (editText.getText().toString().equals("")) {
                    offModeClick(button);
                    return;
                }

                onModeClick(button);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
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

    private void setNameToView() {
        assert settingAccountActivity != null;
        editText.setText(settingAccountActivity.userModel.getName());
    }

    private void setNameModel() {
        assert settingAccountActivity != null;
        settingAccountActivity.userModel.setName(String.valueOf(editText.getText()));
        new DAOUser().updateField("name",String.valueOf(editText.getText()));
    }
}
