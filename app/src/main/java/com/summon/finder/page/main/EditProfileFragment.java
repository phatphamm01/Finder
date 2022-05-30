package com.summon.finder.page.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.summon.finder.R;
import com.summon.finder.component.tag.TagAdapter;
import com.summon.finder.component.tag.TagEditAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class EditProfileFragment extends Fragment {
    private final List<String> tagList = new ArrayList<>(Arrays.asList("Trung học phổ thông", "Thạc sĩ", "Cử nhân Tiến sĩ", "Chó", "Mèo", "Bò sát", "Động vật", "Lưỡng cư", "Không nuôi thú cưng", "Tất cả các loại thủ cưng", "Cá", "Ăn thuần chay", "Ăn chay", "Chỉ ăn hải sản và rau củ", "Ăn chay bán phần", "Ăn đa dạng", "Chỉ ăn thịt", "Ma Kết", "Bảo Bình", "Song Ngư", "Bạch Dương", "Kim Ngưu", "Cự Giải", "Sư Tử", "Xử Nữ", "Thiên Bình", "Bọ Cạp", "Nhân Mã", "Song Tử", "Hút thuốc với bạn bè", "Không hút thuốc", "Hút thuốc thường xuyên"));
    private View view;
    private TagEditAdapter adapter;
    private MainActivity mainActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        mainActivity = (MainActivity) getActivity();

        adapter = new TagEditAdapter(getActivity(), this, tagList);
        handleAdapter();

        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.setFragmentUser(MainActivity.FragmentUser.PROFILE);
            }
        });

        return view;
    }

    private void handleAdapter() {
        RecyclerView listView = view.findViewById(R.id.list_tag);
        listView.setAdapter(adapter);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW, FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        listView.setLayoutManager(flexboxLayoutManager);
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