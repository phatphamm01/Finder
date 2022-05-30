package com.summon.finder.page.main;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.summon.finder.DAO.DAOChat;
import com.summon.finder.DAO.DAOMessage;
import com.summon.finder.R;
import com.summon.finder.component.chat.ChatAdapter;
import com.summon.finder.model.ChatModel;
import com.summon.finder.model.MessageModel;


public class ChatFragment extends Fragment {
    private final String idChat;
    private final ChatModel userCurrent;
    private final ChatModel user;
    private final DAOChat daoChat = new DAOChat();
    private final DAOMessage daoMessage = new DAOMessage();
    private View view;
    private MainActivity mainActivity;
    private DatabaseReference chatDb;
    private ChatAdapter chatAdapter;
    private EditText editText;
    private Button btnSend;

    public ChatFragment(String idChat, ChatModel userCurrent, ChatModel user) {
        this.idChat = idChat;
        this.userCurrent = userCurrent;
        this.user = user;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_chat, container, false);
        mainActivity = (MainActivity) getActivity();
        chatDb = FirebaseDatabase.getInstance().getReference("chats");
        editText = view.findViewById(R.id.messageText);
        btnSend = view.findViewById(R.id.btnSend);


        handleInitAdapter();
        setDataChat();

        setEventReturn();
        setDataUserChat();
        setEventSendMessage();

        return view;
    }

    private void setEventSendMessage() {

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    btnSend.performClick();
                }
                return false;
            }
        });


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                if (text.isEmpty()) return;

                MessageModel newMessage = new MessageModel(text, userCurrent.getUserModel().getUid());
                daoMessage.sendMessage(idChat, newMessage);

                editText.setText(null);
            }
        });
    }

    private void setDataUserChat() {
        Picasso.get().load(user.getUserModel().firstImage()).into((ImageView) view.findViewById(R.id.imageUser));
        ((TextView) view.findViewById(R.id.nameUser)).setText(user.getUserModel().getName());
    }

    private void setEventReturn() {
        view.findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.visibleFragmentMain();
            }
        });
    }

    private void handleInitAdapter() {
        chatAdapter = new ChatAdapter(userCurrent, user);

        RecyclerView listMessage = view.findViewById(R.id.listMessage);

        listMessage.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        listMessage.setLayoutManager(layoutManager);
        listMessage.setAdapter(chatAdapter);
    }

    private void setDataChat() {
        daoChat.loadMessage(idChat, (snapshot) -> {
            String message = snapshot.child("message").getValue(String.class);
            String createdByUser = snapshot.child("createdByUser").getValue(String.class);

            MessageModel chat = new MessageModel(message, createdByUser);

            chatAdapter.addChat(chat);
            chatAdapter.notifyDataSetChanged();
        });
    }
}