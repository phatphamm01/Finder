package com.summon.finder.component.card;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.summon.finder.R;

import java.util.ArrayList;
import java.util.List;

public class TagAdapter extends RecyclerView.Adapter<TagAdapter.ViewHolder> {
    private List<String> tagList = new ArrayList<>();
    private Context mContext;

    public List<String> getTagList() {
        return tagList;
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    public void addTag(String tagList) {
        this.tagList.add(tagList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        mContext = parent.getContext();

        View view = inflater.inflate(R.layout.view_tag_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String tag = tagList.get(position);
        holder.setData(tag);

        if (tag.contains("Lii")) {
            holder.setStyle();
        } else {
            holder.removeStyle();
        }
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        private final MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.tagNameText);
            cardView = itemView.findViewById(R.id.tagNameCard);
        }

        public void setData(String s) {
            textView.setText(s);
        }

        public void setStyle() {
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
            cardView.setStrokeColor(mContext.getResources().getColor(R.color.primary));
        }

        public void removeStyle() {
            textView.setTextColor(mContext.getResources().getColor(R.color.white));
            cardView.setStrokeColor(mContext.getResources().getColor(R.color.white));
        }
    }
}
