package com.summon.finder.page.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.summon.finder.R;
import com.summon.finder.component.match.MatchAdapter;
import com.summon.finder.model.UserModel;


public class MatchFragment extends Fragment {

    private View view;
    private MainActivity mainActivity;
    private MatchAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_match, container, false);
        mainActivity = (MainActivity) getActivity();

        handleInitAdapter();
        handleAddDataAdapter();

        return view;
    }

    private void handleInitAdapter() {
        adapter = new MatchAdapter();

        RecyclerView listMatch = view.findViewById(R.id.listMatch);

        listMatch.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        listMatch.setAdapter(adapter);
    }

    private void handleAddDataAdapter() {
        UserModel userModel = new UserModel();
        userModel.addImage("1", "https://scontent.fsgn8-1.fna.fbcdn.net/v/t39.30808-6/235702922_131907059128951_4157223209859400355_n.jpg?_nc_cat=110&ccb=1-6&_nc_sid=174925&_nc_ohc=aDXM5634oOUAX-rzJ99&_nc_ht=scontent.fsgn8-1.fna&oh=00_AT_TrP6bc6QyM3vwGweo_cfhW2jgjwxHMBYxZXZC8jxmxA&oe=6289F6D2");
        userModel.setName("Phạm Minh Phát");
        userModel.setSchool("SPKT");

        adapter.addUserModel(userModel);
        adapter.addUserModel(userModel);
        adapter.addUserModel(userModel);
        adapter.addUserModel(userModel);
        adapter.addUserModel(userModel);
        adapter.addUserModel(userModel);
        adapter.addUserModel(userModel);
        adapter.addUserModel(userModel);

        adapter.notifyDataSetChanged();
    }
}