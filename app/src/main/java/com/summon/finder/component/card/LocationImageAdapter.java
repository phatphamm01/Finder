package com.summon.finder.component.card;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.summon.finder.R;

import java.util.List;

public class LocationImageAdapter extends RecyclerView.Adapter<LocationImageAdapter.ViewHolder> {
    private final Integer width;
    private List<Boolean> booleans;

    public Boolean getBoolean(int position){
        return booleans.get(position);
    }

    public List<Boolean> getBooleans() {
        return booleans;
    }

    public void setBooleans(List<Boolean> booleans) {
        this.booleans = booleans;
    }

    public LocationImageAdapter(Integer width, Integer size) {
        this.width = width / size - 14;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_location, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setWidth(width);
        holder.setBackgroud(getBoolean(position));
    }

    @Override
    public int getItemCount() {
        return booleans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        CardView cardView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.locationImage);
            cardView= itemView.findViewById(R.id.locationImageCard);
        }

        public void setBackgroud(Boolean status) {
            int color = Color.rgb(80, 80, 80);
            float alpha = 0.6f;


            if (status) {
                color = Color.rgb(255, 255, 255);
                alpha = 1f;
            }

            imageView.setBackgroundColor(color);
            cardView.setAlpha(alpha);

            cardView.requestLayout();
            imageView.requestLayout();
        }

        public void setWidth(Integer width) {
            imageView.getLayoutParams().width = width;
            imageView.requestLayout();
        }
    }
}
