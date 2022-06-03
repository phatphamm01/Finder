package com.summon.finder.page.setting;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.summon.finder.R;
import com.summon.finder.component.tag.TagAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class SettingAccountSixFragment extends Fragment {
    private final List<String> tagList = new ArrayList<>(Arrays.asList("Trung học phổ thông", "Thạc sĩ", "Cử nhân Tiến sĩ", "Chó", "Mèo", "Bò sát", "Động vật", "Lưỡng cư", "Không nuôi thú cưng", "Tất cả các loại thủ cưng", "Cá", "Ăn thuần chay", "Ăn chay", "Chỉ ăn hải sản và rau củ", "Ăn chay bán phần", "Ăn đa dạng", "Chỉ ăn thịt", "Ma Kết", "Bảo Bình", "Song Ngư", "Bạch Dương", "Kim Ngưu", "Cự Giải", "Sư Tử", "Xử Nữ", "Thiên Bình", "Bọ Cạp", "Nhân Mã", "Song Tử", "Hút thuốc với bạn bè", "Không hút thuốc", "Hút thuốc thường xuyên"));
    private final List<String> tagListSelected = new ArrayList<>();
    private View view;
    private SettingAccountActivity settingAccountActivity;
    private Button button;
    private TagAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_config_user_step_six, container, false);

        settingAccountActivity = (SettingAccountActivity) getActivity();

        button = view.findViewById(R.id.asa_continue);
        addEventToButton(button);

        adapter = new TagAdapter(getActivity(), this, tagList);
        handleAdapter();

        return view;
    }

    private void handleAdapter() {
        RecyclerView listView = view.findViewById(R.id.list_tag);
        listView.setAdapter(adapter);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW, FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        listView.setLayoutManager(flexboxLayoutManager);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (settingAccountActivity.userModel.getTags().size() < 5) {
            handleContinue();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    private void addEventToButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                settingAccountActivity.handleUpStep();
            }
        });
    }

    public void handleContinue() {
        final int size = settingAccountActivity.userModel.getTags().size();
        final String TEXT_DEFAULT = "Tiếp tục";
        if (size == 0) {
            button.setText(TEXT_DEFAULT);
        } else {
            button.setText(TEXT_DEFAULT + " (" + String.valueOf(size) + "/5)");
        }


        if (size == 5) {
            onModeClick(button);
            return;
        }

        offModeClick(button);
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


}

//    public List<String> mixArrayString(List<String> arr) {
//        int length = arr.size();
//        int[] indexs = IntStream.range(0, length).toArray();
//
//        Random rand = new Random();
//        for (int i = 0; i < length; i++) {
//            int int_random = rand.nextInt(length);
//            int temp = indexs[i];
//            indexs[i] = indexs[int_random];
//            indexs[int_random] = temp;
//        }
//
//        String[] arrMixed = new String[length];
//        for (int i = 0; i < length; i++) {
//            arrMixed[indexs[i]] = arr.get(i);
//        }
//
//        return Arrays.asList(arrMixed);
//    }
