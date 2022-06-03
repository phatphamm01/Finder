package com.summon.finder.component.tag;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.page.main.EditProfileFragment;
import com.summon.finder.page.main.MainActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class TagEditAdapter extends RecyclerView.Adapter<TagEditAdapter.ViewHolder> {
    private final MainActivity mContext;
    private final List<String> tagList;
    private final EditProfileFragment fragment;
    HashMap<String, ViewHolder> holderlist;


    public TagEditAdapter(Context mContext, EditProfileFragment fragment, List<String> tagList) {
        this.mContext = (MainActivity) mContext;
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

        //Trick
        List<String> tags = mContext.getUserModel().getTags();

        Optional<String> isCheck = tags.stream().filter(value -> value.equals(tag)).findFirst();

        if (isCheck.isPresent()) {
            holder.setStyle();
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
                    Boolean isCheck = mContext.getUserModel().addTagListSelected(s);

                    if (isCheck) {
                        setStyle();

                        return;
                    }

                    removeStyle();

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
