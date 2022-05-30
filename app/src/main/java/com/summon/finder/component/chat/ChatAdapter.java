package com.summon.finder.component.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.annotations.SerializedName;
import com.summon.finder.R;
import com.summon.finder.model.ChatModel;
import com.summon.finder.model.MessageModel;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    static public String currentId = "";
    private final ChatModel userCurrent;
    private final ChatModel user;
    private List<MessageModel> chatList = new ArrayList<>();

    public ChatAdapter(ChatModel userCurrent, ChatModel user) {
        this.userCurrent = userCurrent;
        this.user = user;
    }

    public List<MessageModel> getChatList() {
        return chatList;
    }

    public void setChatList(List<MessageModel> chatList) {
        this.chatList = chatList;
    }

    public void addChat(MessageModel chatList) {
        this.chatList.add(chatList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MessageModel chat = chatList.get(viewType);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        Status status;

        status = Status.LEFT;
        view = inflater.inflate(R.layout.view_chat_left, parent, false);

        if (chat.getCreatedByUser().equals(userCurrent.getUserModel().getUid())) {
            status = Status.RIGHT;
            view = inflater.inflate(R.layout.view_chat_right, parent, false);
        }

        return new ViewHolder(view, status);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder myHolder = (ViewHolder) holder;

        myHolder.setData(chatList.get(position), userCurrent, user);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    enum Status {
        RIGHT,
        LEFT
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        Status status;

        public ViewHolder(View itemView, Status status) {
            super(itemView);

            text = itemView.findViewById(R.id.chat_text);

            this.status = status;
        }

        public void setData(MessageModel chatModel, ChatModel userCurrent, ChatModel user) {
            text.setText(chatModel.getMessage());
        }
    }
}
