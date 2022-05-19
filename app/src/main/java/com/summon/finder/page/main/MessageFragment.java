package com.summon.finder.page.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.summon.finder.R;


public class MessageFragment extends Fragment {
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);



        handleAddDataAdapter();

        return view;
    }

    private void handleAddDataAdapter() {
    }
}