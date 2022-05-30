package com.summon.finder.component.match;

import static com.summon.finder.component.match.MatchAdapter.ViewHolder;

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


public class MatchAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<ChatModel> dataModel = new ArrayList<>();


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
        View view = inflater.inflate(R.layout.view_match, parent, false);
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

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView name, school;
        ConstraintLayout blurView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.match_imageView);
            name = itemView.findViewById(R.id.match_textView_name);
            school = itemView.findViewById(R.id.match_textView_school);
            blurView = itemView.findViewById(R.id.blur);
        }


        public void setData(ChatModel userModel) {
            if (userModel.getIdChat() != null) {
                blurView.setVisibility(View.INVISIBLE);
            }

            if (userModel.getUserModel().getImages().size() != 0) {
                String image = userModel.getUserModel().firstImage();
                Picasso.get()
                        .load(image)
                        .into(imageView);
            }

            name.setText(userModel.getUserModel().getName());
            school.setText(userModel.getUserModel().getSchool());
        }
    }
}
