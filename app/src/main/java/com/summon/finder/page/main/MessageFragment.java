package com.summon.finder.page.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.summon.finder.DAO.DAOChat;
import com.summon.finder.DAO.DAOMatch;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.component.match.MatchMiniAdapter;
import com.summon.finder.component.message.MessageAdapter;
import com.summon.finder.model.ChatModel;
import com.summon.finder.model.UserModel;


public class MessageFragment extends Fragment {
    public ChatModel userCurrent;
    private View view;
    private MatchMiniAdapter matchMiniAdapter;
    private MessageAdapter messageAdapter;
    private DAOUser daoUser;
    private MainActivity mainActivity;
    private DAOMatch daoMatch;
    private DAOChat daoChat;
    private View tinnhan;
    private View tuonghopmoi;
    private View noDataView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        mainActivity = (MainActivity) getActivity();
        daoUser = new DAOUser();
        daoMatch = new DAOMatch();
        daoChat = new DAOChat();
        tinnhan = view.findViewById(R.id.tinnhan);
        tuonghopmoi = view.findViewById(R.id.tuonghopmoi);
        noDataView = view.findViewById(R.id.noData);

        handleInitMatchMiniAdapter();
        handleAddDataMatchMiniAdapter();

        handleInitMessageAdapter();
        handleAddDataMessageAdapter();

        handleVisibleTuongHopMoi();
        handleVisibleTinNhan();


        return view;
    }

    private void handleVisibleTinNhan() {
        if (messageAdapter.getItemCount() == 0) {
            tinnhan.setVisibility(View.INVISIBLE);
        } else {
            tinnhan.setVisibility(View.VISIBLE);
        }
    }

    private void handleVisibleTuongHopMoi() {
        if (matchMiniAdapter.getItemCount() == 0) {
            tuonghopmoi.setVisibility(View.INVISIBLE);
            noDataView.setVisibility(View.VISIBLE);

        } else {
            tuonghopmoi.setVisibility(View.VISIBLE);
            noDataView.setVisibility(View.INVISIBLE);
        }
    }

    private void handleInitMatchMiniAdapter() {
        matchMiniAdapter = new MatchMiniAdapter(mainActivity);

        RecyclerView listMatch = view.findViewById(R.id.viewMatchMini);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        listMatch.setLayoutManager(layoutManager);

        listMatch.setAdapter(matchMiniAdapter);
    }

    private void handleAddDataMatchMiniAdapter() {
        String uid = mainActivity.getUserModel().getUid();
        daoMatch.getMatch(uid, snapshot -> {
            String idUser = snapshot.getKey();
            String isFound = snapshot.child("chatId").getValue(String.class);


            if (isFound == null || isFound.isEmpty()) return;

            daoUser.getUserSnapshotById(idUser, snapshotData -> {
                UserModel user = new UserModel(snapshotData);
                ChatModel chatModel = new ChatModel(snapshot.child("chatId").getValue(String.class), "", user);
                matchMiniAdapter.addData(chatModel);

                matchMiniAdapter.notifyDataSetChanged();
                handleVisibleTuongHopMoi();
            });
        });
    }

    private void handleInitMessageAdapter() {
        messageAdapter = new MessageAdapter(userModel -> {
            mainActivity.setFragmentMessage(userModel.getIdChat(), userModel);
        });

        RecyclerView listMessage = view.findViewById(R.id.viewMessageItem);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        listMessage.setLayoutManager(layoutManager);

        listMessage.setAdapter(messageAdapter);
    }

    private void handleAddDataMessageAdapter() {
        daoUser.getMatchByIdAddChildEvent(mainActivity.getUserModel().getUid(), snapshot -> {
            if (snapshot.child("chatId").getValue() == null) return;
            String idChat = snapshot.child("chatId").getValue().toString();
            String idUser = snapshot.getKey();


            daoUser.getUserSnapshotById(idUser, snapshotUser -> {
                daoChat.getNewMessageAddValue(idChat, (text, date) -> {
                    UserModel userModel = new UserModel(snapshotUser);

                    ChatModel chatModel = new ChatModel(idChat, text == null ? "Hãy gửi lời chào tới nhau" : text, userModel, date);

                    messageAdapter.addUserModel(chatModel);
                    messageAdapter.notifyDataSetChanged();
                    handleVisibleTinNhan();
                });
            });
        });
    }
}