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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;
import com.summon.finder.R;
import com.summon.finder.component.chat.ChatAdapter;
import com.summon.finder.model.ChatModel;

import java.util.HashMap;
import java.util.Map;


public class ChatFragment extends Fragment {
    private final String idChat;
    private final ChatModel userCurrent;
    private final ChatModel user;
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

                DatabaseReference dataPush = chatDb.child(idChat).child("chat").push();
                chatDb.child(idChat).child("newMessage").setValue(text);

                Map newMessage = new HashMap();
                newMessage.put("createdByUser", userCurrent.userModel.getUid());
                newMessage.put("text", text);
                dataPush.setValue(newMessage);

                editText.setText(null);
            }
        });
    }

    private void setDataUserChat() {
        Picasso.get().load(user.userModel.firstImage()).into((ImageView) view.findViewById(R.id.imageUser));
        ((TextView) view.findViewById(R.id.nameUser)).setText(user.userModel.getName());
    }

    private void setEventReturn() {
        view.findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.invisibleFragmentMessage();
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
        chatDb.child(idChat).child("chat")
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        JsonObject chatModel = new JsonObject();
                        chatModel.addProperty("text", snapshot.child("text").getValue(String.class));
                        chatModel.addProperty("createdByUser", snapshot.child("createdByUser").getValue(String.class));
                        ChatAdapter.MessageModel chat = new Gson().fromJson(chatModel.getAsJsonObject(), ChatAdapter.MessageModel.class);

                        chatAdapter.addChat(chat);

                        chatAdapter.notifyDataSetChanged();
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