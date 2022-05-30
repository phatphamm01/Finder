package com.summon.finder.page.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_message, container, false);
        mainActivity = (MainActivity) getActivity();
        daoUser = new DAOUser();

        handleInitMatchMiniAdapter();
        handleAddDataMatchMiniAdapter();

        handleInitMessageAdapter();
        handleAddDataMessageAdapter();

        return view;
    }

    private void handleInitMatchMiniAdapter() {
        matchMiniAdapter = new MatchMiniAdapter();

        RecyclerView listMatch = view.findViewById(R.id.viewMatchMini);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(RecyclerView.HORIZONTAL);
        listMatch.setLayoutManager(layoutManager);

        listMatch.setAdapter(matchMiniAdapter);
    }

    private void handleAddDataMatchMiniAdapter() {
        UserModel userModel = new UserModel();
        userModel.addImage("1", "https://scontent.fsgn8-1.fna.fbcdn.net/v/t39.30808-6/235702922_131907059128951_4157223209859400355_n.jpg?_nc_cat=110&ccb=1-6&_nc_sid=174925&_nc_ohc=aDXM5634oOUAX-rzJ99&_nc_ht=scontent.fsgn8-1.fna&oh=00_AT_TrP6bc6QyM3vwGweo_cfhW2jgjwxHMBYxZXZC8jxmxA&oe=6289F6D2");
        userModel.setName("Phạm Minh Phát");
        userModel.setSchool("SPKT");

        matchMiniAdapter.addUserModel(userModel);
        matchMiniAdapter.addUserModel(userModel);
        matchMiniAdapter.addUserModel(userModel);
        matchMiniAdapter.addUserModel(userModel);
        matchMiniAdapter.addUserModel(userModel);
        matchMiniAdapter.addUserModel(userModel);
        matchMiniAdapter.addUserModel(userModel);
        matchMiniAdapter.addUserModel(userModel);

        matchMiniAdapter.notifyDataSetChanged();
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
        daoUser.getUser().child("match").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if (snapshot.child("chatId").getValue() == null) return;
                String idUser = snapshot.getKey();
                String idChat = snapshot.child("chatId").getValue().toString();


                final FirebaseDatabase instance = FirebaseDatabase.getInstance();


                instance.getReference("user").child(idUser).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        instance.getReference("chats").child(idChat).child("newMessage")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String newMessage = snapshot.getValue(String.class);
                                        UserModel userModel = new UserModel(dataSnapshot);

                                        ChatModel chatModel = new ChatModel(idChat, userModel, newMessage == null ? "Hãy gửi lời chào tới nhau" : newMessage);

                                        messageAdapter.addUserModel(chatModel);
                                        messageAdapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}