package com.summon.finder.component.match;

import static com.summon.finder.component.match.MatchAdapter.ViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.summon.finder.R;
import com.summon.finder.databinding.ViewMatchBinding;
import com.summon.finder.model.UserModel;

import java.util.ArrayList;
import java.util.List;

public class MatchAdapter extends RecyclerView.Adapter<ViewHolder> {

    private List<UserModel> userModelList = new ArrayList<>();

    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    public void addUserModel(UserModel userModel) {
        this.userModelList.add(userModel);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_match, parent, false);
        return new ViewHolder(view, ViewMatchBinding.inflate(inflater));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(userModelList.get(position));
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ViewMatchBinding binding;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView, ViewMatchBinding binding) {
            super(itemView);

            imageView = itemView.findViewById(R.id.match_imageView);
            this.binding = binding;
        }

        public void setData(UserModel userModel) {
            if (userModel.getImages().size() != 0) {
                String image = userModel.getImages().values().stream().findFirst().get();
                Picasso.get()
                        .load("https://giaitri.vn/wp-content/uploads/2019/11/hot-girl-Nam-%C4%90%E1%BB%8Bnh.jpg")
                        .into(imageView);
            }


            binding.matchTextViewName.setText(userModel.getName());
            binding.matchTextViewSchool.setText(userModel.getSchool());
        }
    }
}
