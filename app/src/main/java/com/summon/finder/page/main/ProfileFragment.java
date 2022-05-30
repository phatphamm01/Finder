package com.summon.finder.page.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.summon.finder.R;


public class ProfileFragment extends Fragment {
    private View view;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_profile, container, false);
        mainActivity = (MainActivity) getActivity();

        setEventBack();
        setEventToSetting();
        setEventToEditProfile();


        return view;
    }




    private void setEventToEditProfile() {
        view.findViewById(R.id.btnEditProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setFragmentUser(MainActivity.FragmentUser.EDIT_PROFILE);
            }
        });
    }

    private void setEventToSetting() {
        view.findViewById(R.id.btnSetting).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setFragmentUser(MainActivity.FragmentUser.SETTING);
            }
        });
    }

    private void setEventBack() {
        view.findViewById(R.id.iconBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.visibleFragmentMain();
            }
        });
    }
}