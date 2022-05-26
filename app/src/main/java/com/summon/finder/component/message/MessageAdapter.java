package com.summon.finder.component.message;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.summon.finder.R;
import com.summon.finder.model.ChatModel;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<ChatModel> userModelList = new ArrayList<>();

    public List<ChatModel> getUserModelList() {
        return userModelList;
    }

    public void setUserModelList(List<ChatModel> userModelList) {
        this.userModelList = userModelList;
    }

    public void addUserModel(ChatModel userModel) {
        userModelList.removeIf(chatModel -> chatModel.userModel.getUid().equals(userModel.userModel.getUid()));

        this.userModelList.add(userModel);
    }

    public interface Callback {
        void event(ChatModel userModel);
    }

    Callback callback;

    public MessageAdapter(Callback callback) {
        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_message_item, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(userModelList.get(position), callback);
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, newMessage;
        ConstraintLayout messageItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            newMessage = itemView.findViewById(R.id.newMessage);
            messageItem = itemView.findViewById(R.id.messageItem);
        }

        public void setData(ChatModel userModel, Callback callback) {
            String image = userModel.userModel.getImages().values().stream().findFirst().get();

            Picasso.get().load(image).into(imageView);

            name.setText(userModel.userModel.getName());
            newMessage.setText(userModel.newMessage);

            messageItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.event(userModel);
                }
            });
        }
    }
}
