package com.summon.finder.component.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;
import com.summon.finder.R;
import com.summon.finder.helper.time.TimeHelper;
import com.summon.finder.model.UserModel;
import com.summon.finder.page.main.MainActivity;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder> {
    private final MainActivity context;
    private List<UserModel> userModelList = new ArrayList<>();

    public CardStackAdapter(Context context) {
        this.context = (MainActivity) context;
    }

    public List<UserModel> getUserModelList() {
        return userModelList;
    }

    public void setUserModelList(List<UserModel> userModelList) {
        this.userModelList = userModelList;
    }

    public UserModel getUserModel(int position) {
        return userModelList.get(position);
    }

    public void addUserModel(UserModel userModel) {
        this.userModelList.add(userModel);
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.view_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(getUserModel(position));
        holder.image.post(new Runnable() {
            @Override
            public void run() {
                int width = holder.image.getWidth();
                holder.setLocationImageAdapter(width);
            }
        });
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recyclerLocationImageView;
        private final RecyclerView recyclerTagView;
        private final MaterialCardView statusWorkingIcon;
        private final ImageView image;
        private final TextView name, age, school, distanceView, statusWorking;

        private final List<String> images = new ArrayList<>();
        private int index;
        private int lengthImage = 0;

        private LocationImageAdapter locationImageAdapter;

        private TagAdapter tagAdapter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            index = 0;


            image = itemView.findViewById(R.id.item_image);
            name = itemView.findViewById(R.id.item_name);
            age = itemView.findViewById(R.id.item_age);
            school = itemView.findViewById(R.id.item_school);
            distanceView = itemView.findViewById(R.id.item_distance);
            statusWorking = itemView.findViewById(R.id.statusWorking);
            statusWorkingIcon = itemView.findViewById(R.id.statusWorkingIcon);

            recyclerLocationImageView = itemView.findViewById(R.id.locationImageList);
            recyclerTagView = itemView.findViewById(R.id.tagList);
        }

        private void setImage(UserModel data) {
            data.getImages().forEach((key, value) -> {
                images.add(value);
            });
        }

        @SuppressLint("ClickableViewAccessibility")
        private void addEventChangeImage(UserModel data) {
            image.post(new Runnable() {
                @Override
                public void run() {
                    int width = image.getWidth();
                    image.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_MOVE) {
                                return true;
                            }

                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                int x = (int) event.getX();
                                if (x > 0 && x < width / 4 && index > 0) {
                                    index--;
                                    Picasso.get().load(images.get(index)).centerCrop().fit().into(image);
                                    handleSetDataLocationImageAdapter(index, true);
                                    Log.d("CARD", String.valueOf(index));

                                }

                                if (x > width / 4 * 3 && x < width && index < lengthImage - 1) {
                                    index++;
                                    Picasso.get().load(images.get(index)).centerCrop().fit().into(image);
                                    handleSetDataLocationImageAdapter(index, true);
                                    Log.d("CARD", String.valueOf(index));
                                }
                            }
                            return true;
                        }
                    });
                }
            });
        }

        void setLocationImageAdapter(int width) {
            handleLocationImageAdapter(width);
            handleSetDataLocationImageAdapter(index, true);
        }


        private void handleLocationImageAdapter(int width) {
            locationImageAdapter = new LocationImageAdapter(width, lengthImage);
            recyclerLocationImageView.setAdapter(locationImageAdapter);

            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.NOWRAP);
            flexboxLayoutManager.setJustifyContent(JustifyContent.SPACE_EVENLY);

            recyclerLocationImageView.setLayoutManager(flexboxLayoutManager);
        }


        private void handleSetDataLocationImageAdapter(Integer index, Boolean status) {
            List<Boolean> booleansList = LocationImageModel.getBooleanList(lengthImage);
            booleansList.set(index, status);

            locationImageAdapter.setBooleans(booleansList);
            locationImageAdapter.notifyDataSetChanged();
        }

        void setData(UserModel data) {
            setImage(data);
            lengthImage = data.getImages().size();

            Picasso.get().load(images.get(index)).centerCrop().fit().into(image);
            addEventChangeImage(data);

            name.setText(data.getName());
            try {
                age.setText(data.handleGetAge());
            } catch (Exception ignored) {

            }
            school.setText(data.getSchool());
            long distance = context.getUserModel().handleGetDistance(data);

            if (distance == -1) {
                distanceView.setText("Cách xa " + "???" + " km");
            } else {
                distanceView.setText("Cách xa " + Math.toIntExact(distance) + " km");

                ColorStateList csl = AppCompatResources.getColorStateList(context, R.color.gray_400);

                statusWorkingIcon.setBackgroundTintList(csl);
            }

            try {
                TimeHelper.TimeDifference mili = TimeHelper.getTimeDifference(data.getLastOperatingTime(), TimeHelper.getStringNowTime());
                if (mili.getMiliSeconds() < new TimeHelper.TimeDifference(0, 5, 0, 0, 0).getMiliSeconds()) {
                    statusWorking.setText("Có hoạt động gần đây");
                } else {
                    statusWorking.setText("Hoạt động cách đây " + TimeHelper.findDifference(data.getLastOperatingTime(), TimeHelper.getStringNowTime()));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


            handleInitTagAdapter();
            handleDataTagAdapter(data);
        }

        private void handleInitTagAdapter() {
            tagAdapter = new TagAdapter();
            recyclerTagView.setAdapter(tagAdapter);

            FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(context, FlexDirection.ROW, FlexWrap.WRAP);
            recyclerTagView.setLayoutManager(flexboxLayoutManager);
        }

        private void handleDataTagAdapter(UserModel data) {
            data.getTags().forEach((value) -> {
                tagAdapter.addTag(value);
                tagAdapter.notifyDataSetChanged();
            });
        }


    }
}
