package com.summon.finder.page.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.summon.finder.DAO.DAOMatch;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.component.match.MatchAdapter;
import com.summon.finder.model.ChatModel;
import com.summon.finder.model.UserModel;


public class MatchFragment extends Fragment {
    private View view;
    private MainActivity mainActivity;
    private MatchAdapter adapter;
    private TextView likeNumber;
    private final DAOMatch daoMatch = new DAOMatch();
    private final DAOUser daoUser = new DAOUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_match, container, false);
        mainActivity = (MainActivity) getActivity();
        likeNumber = view.findViewById(R.id.likeNumber);

        handleInitAdapter();
        handleAddDataAdapter();
        setNumberLike(String.valueOf(0));

        return view;
    }

    private void setNumberLike(String i) {
        likeNumber.setText(i + " lượt thích ❤");
    }

    private void handleInitAdapter() {
        adapter = new MatchAdapter();

        RecyclerView listMatch = view.findViewById(R.id.listMatch);

        listMatch.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        listMatch.setAdapter(adapter);
    }

    private void handleAddDataAdapter() {
        String uid = mainActivity.getUserModel().getUid();
        daoMatch.getMatch(uid, snapshot -> {
            String idUser = snapshot.getKey();

            daoUser.getUserSnapshotById(idUser, snapshotData -> {
                UserModel user = new UserModel(snapshotData);
                ChatModel userModel = new ChatModel(snapshot.child("chatId").getValue(String.class), user, "");
                adapter.addData(userModel);

                adapter.notifyDataSetChanged();

                setNumberLike(String.valueOf(adapter.getItemCount()));
            });
        });
    }
}