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
import com.summon.finder.helper.time.TimeHelper;

import java.util.Calendar;
import java.util.Date;

public class SettingAccountTwoFragment extends Fragment {
    private final String ddmmyyyy = "DDMMYYYY";
    private final Calendar cal = Calendar.getInstance();
    View view;
    SettingAccountActivity settingAccountActivity;
    Button button;
    EditText date;
    private String current = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_config_user_step_two, container, false);

        settingAccountActivity = (SettingAccountActivity) getActivity();

        button = view.findViewById(R.id.asa_continue);
        date = view.findViewById(R.id.textPersonDate);


        addEventToButton(button);
        addEventToEditText(button, date);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setSchoolToView();
        if (settingAccountActivity.userModel.getBirthday().equals("")) {
            offModeClick(button);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        setSchoolModel();
    }

    private void addEventToEditText(Button button, EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]", "");
                    String cleanC = current.replaceAll("[^\\d.]", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));

                        if (mon > 12) mon = 12;
                        cal.set(Calendar.MONTH, mon - 1);

                        int currentYear = Calendar.getInstance().get(Calendar.YEAR);

                        int maxYear = currentYear - 18;
                        int minYear = currentYear - 50;
                        year = (year < minYear) ? minYear : (year > maxYear) ? maxYear : year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%02d", day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    date.setText(current);
                    date.setSelection(sel < current.length() ? sel : current.length());
                    if (editText.getText().toString().equals("")) {
                        offModeClick(button);
                        return;
                    }

                    onModeClick(button);
                }
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

    private void setSchoolToView() {
        assert settingAccountActivity != null;
        date.setText(settingAccountActivity.userModel.getBirthday());
    }

    private void setSchoolModel() {
        assert settingAccountActivity != null;
        settingAccountActivity.userModel.setBirthday(String.valueOf(date.getText()));
        new DAOUser().updateField("school", String.valueOf(date.getText()));
    }
}
