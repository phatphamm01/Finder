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
import com.summon.finder.helper.time.TimeHelper;
import com.summon.finder.model.ChatModel;
import com.summon.finder.utils.timeobserver.TimeManager;
import com.summon.finder.utils.timeobserver.WorkingListener;
import com.summon.finder.utils.timeobserver.WorkingMessListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    Callback callback;
    private List<ChatModel> userModelList = new ArrayList<>();

    public MessageAdapter(Callback callback) {
        this.callback = callback;
    }

    public List<ChatModel> getUserModelList() {
        return userModelList;
    }

    public void setUserModelList(List<ChatModel> userModelList) {
        this.userModelList = userModelList;
    }

    public void addUserModel(ChatModel userModel) {
        userModelList.removeIf(chatModel -> chatModel.getUserModel().getUid().equals(userModel.getUserModel().getUid()));

        userModelList.add(userModel);
        List<ChatModel> result = userModelList.stream()
                .sorted(Comparator.comparing(ChatModel::getNewMessageDate).reversed())
                .collect(Collectors.toList());

        setUserModelList(result);
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

    public interface Callback {
        void event(ChatModel userModel);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, newMessage, activeStatus;
        ConstraintLayout messageItem;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            name = itemView.findViewById(R.id.name);
            newMessage = itemView.findViewById(R.id.newMessage);
            messageItem = itemView.findViewById(R.id.messageItem);
            activeStatus = itemView.findViewById(R.id.activeStatus);
        }

        public void setData(ChatModel chatModel, Callback callback) {
            String id = chatModel.getUserModel().getUid();
            if (chatModel.getUserModel().getWorking()) {
                activeStatus.setText("Đang hoạt động");
                TimeManager.getInstance().getService().unsubscribe("message" + id);
            } else {
                WorkingMessListener timeView = new WorkingMessListener("message" + id, activeStatus, chatModel.getUserModel().getLastOperatingTime());
                timeView.handleSetStatus(TimeHelper.getStringNowTime());

                TimeManager.getInstance().getService().subscribe(timeView);
            }


            String image = chatModel.getUserModel().firstImage();

            Picasso.get().load(image).into(imageView);

            name.setText(chatModel.getUserModel().getName());
            newMessage.setText(chatModel.getNewMessage());

            messageItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.event(chatModel);
                }
            });
        }
    }
}
