package com.summon.finder.page.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.summon.finder.R;
import com.summon.finder.component.card.CardStackAdapter;
import com.summon.finder.model.UserModel;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class SwipeFragment extends Fragment {
    private static final String TAG = "MainActivity";
    View view;
    MainActivity mainActivity;

    private CardStackLayoutManager cardStackLayoutManager;

    private CardStackAdapter cardStackAdapter;

    private DatabaseReference usersDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_swipe, container, false);
        mainActivity = (MainActivity) getActivity();

        usersDb = FirebaseDatabase.getInstance().getReference().child("user");

        handleCardStack();
        getOppositeSexUser();
        return view;
    }


    private void handleCardStack() {
        CardStackView cardStackView = view.findViewById(R.id.card_stack_view);
        cardStackLayoutManager = new CardStackLayoutManager(mainActivity, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }


            @Override
            public void onCardSwiped(Direction direction) {


                //handle last item
                if (cardStackLayoutManager.getTopPosition() == cardStackLayoutManager.getItemCount()) {
                    view.findViewById(R.id.notFound).setVisibility(View.VISIBLE);
                    view.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);

                    setColorActionBtn(false);
                }
            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + cardStackLayoutManager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardRewound: " + cardStackLayoutManager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }
        });


        setEventHandleSwipe(cardStackView);

        cardStackLayoutManager.setStackFrom(StackFrom.None);
        cardStackLayoutManager.setVisibleCount(3);
        cardStackLayoutManager.setTranslationInterval(8.0f);
        cardStackLayoutManager.setScaleInterval(0.95f);
        cardStackLayoutManager.setSwipeThreshold(0.5f);
        cardStackLayoutManager.setMaxDegree(50.0f);
        cardStackLayoutManager.setDirections(Direction.FREEDOM);
        cardStackLayoutManager.setCanScrollHorizontal(true);
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.Manual);
        cardStackLayoutManager.setSwipeableMethod(SwipeableMethod.AutomaticAndManual);
        cardStackLayoutManager.setOverlayInterpolator(new LinearInterpolator());
        cardStackAdapter = new CardStackAdapter(mainActivity);
        cardStackView.setLayoutManager(cardStackLayoutManager);
        cardStackView.setAdapter(cardStackAdapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
    }


    private void setColorActionBtn(Boolean status) {
        int colorBtnUnActiv = getResources().getColor(R.color.btn_unactive);
        MaterialCardView cancelCardView = view.findViewById(R.id.cancelCardView);
        MaterialCardView heartCardView = view.findViewById(R.id.heartCardView);

        ImageView cancelImageView = view.findViewById(R.id.cancelImageView);
        ImageView heartImageView = view.findViewById(R.id.heartImageView);

        cancelCardView.setClickable(status);
        cancelCardView.setFocusable(status);
        heartCardView.setClickable(status);
        heartCardView.setFocusable(status);

        if (!status) {
            cancelCardView.setStrokeColor(colorBtnUnActiv);
            heartCardView.setStrokeColor(colorBtnUnActiv);

            cancelImageView.setColorFilter(colorBtnUnActiv);
            heartImageView.setColorFilter(colorBtnUnActiv);

            return;
        }


        cancelCardView.setStrokeColor(getResources().getColor(R.color.cancel_active));
        heartCardView.setStrokeColor(getResources().getColor(R.color.heart_active));

        cancelImageView.setColorFilter(null);
        heartImageView.setColorFilter(null);
    }

    private void setEventHandleSwipe(CardStackView cardStackView) {
        SwipeAnimationSetting settingRight = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Right)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();
        SwipeAnimationSetting settingLeft = new SwipeAnimationSetting.Builder()
                .setDirection(Direction.Left)
                .setDuration(Duration.Normal.duration)
                .setInterpolator(new AccelerateInterpolator())
                .build();


        view.findViewById(R.id.cancelCardView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStackLayoutManager.setSwipeAnimationSetting(settingLeft);
                cardStackView.swipe();
            }
        });

        view.findViewById(R.id.heartCardView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardStackLayoutManager.setSwipeAnimationSetting(settingRight);
                cardStackView.swipe();
            }
        });
    }


    public void getOppositeSexUser() {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserModel userModel = new UserModel(snapshot);

                ArrayList<UserModel> newUserModelList = new ArrayList<>(cardStackAdapter.getUserModelList());

                newUserModelList.add(userModel);

                cardStackAdapter.setUserModelList(newUserModelList);
                cardStackAdapter.notifyDataSetChanged();

                setColorActionBtn(true);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public List<Integer> mixArrayString(List<Integer> arr) {
        int length = arr.size();
        int[] indexs = IntStream.range(0, length).toArray();

        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            int int_random = rand.nextInt(length);
            int temp = indexs[i];
            indexs[i] = indexs[int_random];
            indexs[int_random] = temp;
        }

        Integer[] arrMixed = new Integer[length];
        for (int i = 0; i < length; i++) {
            arrMixed[indexs[i]] = arr.get(i);
        }

        return Arrays.asList(arrMixed);
    }
}