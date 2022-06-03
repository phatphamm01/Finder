package com.summon.finder.component.match;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.summon.finder.R;
import com.summon.finder.model.ChatModel;
import com.summon.finder.page.main.MainActivity;
import com.summon.finder.page.setting.SettingAccountActivity;

import java.util.ArrayList;
import java.util.List;


public class MatchMiniAdapter extends RecyclerView.Adapter<MatchMiniAdapter.ViewHolder> {
    private final MainActivity mContext;
    private List<ChatModel> dataModel = new ArrayList<>();

    public MatchMiniAdapter(Context context) {
        this.mContext = (MainActivity) context;
    }

    public List<ChatModel> getDataModel() {
        return dataModel;
    }

    public void setDataModel(List<ChatModel> dataModel) {
        this.dataModel = dataModel;
    }

    public void addData(ChatModel dataModel) {
        this.dataModel.add(dataModel);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_match_mini, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(dataModel.get(position));
    }

    @Override
    public int getItemCount() {
        return dataModel.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        ImageView imageView;
        CardView onlineView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.match_textView_name);
            imageView = itemView.findViewById(R.id.match_imageView);
            onlineView = itemView.findViewById(R.id.online);
        }

        public void setData(ChatModel chatModel) {
            if (chatModel.getUserModel().getImages().size() != 0) {
                String image = chatModel.getUserModel().firstImage();
                Picasso.get()
                        .load(image)
                        .into(imageView);
            }

            if (chatModel.getUserModel().getWorking() != null && chatModel.getUserModel().getWorking()) {
                onlineView.setBackgroundTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.online)));
            }

            textView.setText(chatModel.getUserModel().getName());
        }
    }
}
