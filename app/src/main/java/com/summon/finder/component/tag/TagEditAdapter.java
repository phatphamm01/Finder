package com.summon.finder.component.tag;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.page.setting.SettingAccountActivity;
import com.summon.finder.page.setting.SettingAccountSixFragment;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TagEditAdapter extends RecyclerView.Adapter<TagEditAdapter.ViewHolder> {
    private final Context mContext;
    private final List<String> tagList;
    private final Fragment fragment;
    HashMap<String, ViewHolder> holderlist;


    public TagEditAdapter(Context mContext, Fragment fragment, List<String> tagList) {
        this.mContext =  mContext;
        this.fragment = fragment;
        this.tagList = tagList;
        holderlist = new HashMap<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View cardView = inflater.inflate(R.layout.view_tag, parent, false);
        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        Log.d("Position", String.valueOf(position));
        String tag = tagList.get(position);

        holder.setData(tag);
        holder.setAction(tag);

        if (!holderlist.containsKey(position)) {
            holderlist.put(tag, holder);
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
        private final TextView mBtnTag;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mBtnTag = itemView.findViewById(R.id.tag_name);
        }

        public void setData(String s) {
            mBtnTag.setText(s);
        }

        public void setAction(String s) {
            mBtnTag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    if (isCheck) {
                        setStyle();
//                        return;
//                    }
//
//                    removeStyle();
                }
            });
        }

        public void setStyle() {
            mBtnTag.setTextColor(mContext.getResources().getColor(R.color.primary));
            mBtnTag.setBackground(mContext.getResources().getDrawable(R.drawable.button_checkbox_active));
        }

        public void removeStyle() {
            mBtnTag.setTextColor(mContext.getResources().getColor(R.color.neural_60));
            mBtnTag.setBackground(mContext.getResources().getDrawable(R.drawable.button_checkbox));
        }
    }
}
