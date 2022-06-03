package com.summon.finder.page.main;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.component.card.CardStackAdapter;
import com.summon.finder.model.UserModel;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.Duration;
import com.yuyakaido.android.cardstackview.RewindAnimationSetting;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeAnimationSetting;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.IntStream;


public class SwipeFragment extends Fragment {
    private static final String TAG = "MainActivity";
    private final DAOUser daoUser = new DAOUser();
    View view;
    MainActivity mainActivity;
    private CardStackLayoutManager cardStackLayoutManager;
    private CardStackAdapter cardStackAdapter;
    private DatabaseReference usersDb;
    private List<String> isNope = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_swipe, container, false);
        mainActivity = (MainActivity) getActivity();

        setColorActionBtn(false);

        usersDb = FirebaseDatabase.getInstance().getReference().child("user");

        handleCardStack();

        handleGetDataCard();

        handleTimeOutLoadData();

        return view;
    }

    private void handleTimeOutLoadData() {
        new android.os.Handler(Looper.getMainLooper()).postDelayed(
                new Runnable() {
                    public void run() {
                        lazyLoading();
                    }
                },
                3000);
    }

    private void handleGetDataCard() {
        daoUser.getUserSnapshot(snapshot -> {
            UserModel user = new UserModel(snapshot);
            String matchGender = user.getMatchGender();
            getOppositeSexUser(matchGender);
        });
    }


    private void handleCardStack() {
        CardStackView cardStackView = view.findViewById(R.id.card_stack_view);
        cardStackLayoutManager = new CardStackLayoutManager(mainActivity, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {

            }


            @Override
            public void onCardSwiped(Direction direction) {
                int index = cardStackLayoutManager.getTopPosition() - 1;

                UserModel user = cardStackAdapter.getUserModel(index);

                handleAddDataWhenSwipe(direction, user);


                //handle last item
                if (cardStackLayoutManager.getTopPosition() == cardStackLayoutManager.getItemCount()) {
                    lazyLoading();

                    setColorActionBtn(false);
                }
            }

            private void handleAddDataWhenSwipe(Direction direction, UserModel user) {
                String uidUser = user.getUid();

                if (direction == Direction.Left) {
                    isNope.add(user.getUid());
                    daoUser.unMatch(uidUser);
                }

                if (direction == Direction.Right) {
                    isNope = new ArrayList<>();
                    daoUser.match(uidUser);

                    String uidCurrent = mainActivity.getUserModel().getUid();
                    daoUser.handleLiked(user, uidUser, uidCurrent , () -> {
                        Toast.makeText(mainActivity, "Bạn có một kết nối mới", Toast.LENGTH_LONG).show();
                    });
                }
            }


            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + cardStackLayoutManager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardCanceled: " + cardStackLayoutManager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                Log.d(TAG, "onCardAppeared: " + cardStackLayoutManager.getTopPosition());

            }

            @Override
            public void onCardDisappeared(View view, int position) {
                Log.d(TAG, "onCardDisappeared: " + cardStackLayoutManager.getTopPosition());
            }
        });


        setEventHandleSwipe(cardStackView);
        setEventHandleRewind(cardStackView);

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

    private void lazyLoading() {
        view.findViewById(R.id.notFound).setVisibility(View.VISIBLE);
        view.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
    }


    private void setColorActionBtn(Boolean status) {
        int colorBtnUnActive = mainActivity.getResources().getColor(R.color.btn_unactive);
        MaterialCardView cancelCardView = view.findViewById(R.id.cancelCardView);
        MaterialCardView heartCardView = view.findViewById(R.id.heartCardView);

        ImageView cancelImageView = view.findViewById(R.id.cancelImageView);
        ImageView heartImageView = view.findViewById(R.id.heartImageView);

        cancelCardView.setClickable(status);
        cancelCardView.setFocusable(status);
        heartCardView.setClickable(status);
        heartCardView.setFocusable(status);

        if (!status) {
            cancelCardView.setStrokeColor(colorBtnUnActive);
            heartCardView.setStrokeColor(colorBtnUnActive);

            cancelImageView.setColorFilter(colorBtnUnActive);
            heartImageView.setColorFilter(colorBtnUnActive);

            return;
        }


        cancelCardView.setStrokeColor(mainActivity.getResources().getColor(R.color.cancel_active));
        heartCardView.setStrokeColor(mainActivity.getResources().getColor(R.color.heart_active));

        cancelImageView.setColorFilter(null);
        heartImageView.setColorFilter(null);
    }

    private void setEventHandleRewind(CardStackView cardStackView) {
        view.findViewById(R.id.undo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNope.size() == 0) return;

                RewindAnimationSetting setting = new RewindAnimationSetting.Builder()
                        .setDirection(Direction.Bottom)
                        .setDuration(Duration.Normal.duration)
                        .setInterpolator(new DecelerateInterpolator())
                        .build();
                cardStackLayoutManager.setRewindAnimationSetting(setting);
                cardStackView.rewind();


                String id = isNope.remove(isNope.size() - 1);
                daoUser.getConnections().child("nope").child(id).removeValue();
            }
        });
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


    public void getOppositeSexUser(String oppositeUserSex) {
        usersDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshotUser, @Nullable String previousChildName) {
                checkData(snapshotUser, oppositeUserSex, (snapshot) -> {

                    UserModel userModel = new UserModel(snapshotUser);

                    ArrayList<UserModel> newUserModelList = new ArrayList<>(cardStackAdapter.getUserModelList());


                    newUserModelList.add(userModel);

                    cardStackAdapter.setUserModelList(newUserModelList);
                    cardStackAdapter.notifyDataSetChanged();

                    setColorActionBtn(true);
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshotUser, @Nullable String previousChildName) {
                checkData(snapshotUser, oppositeUserSex, (snapshot) -> {
                    UserModel userModel = new UserModel(snapshotUser);

                    ArrayList<UserModel> newUserModelList = new ArrayList<>(cardStackAdapter.getUserModelList());
                    Optional<UserModel> userSame = newUserModelList.stream().filter((user) -> user.getUid().contains(userModel.getUid())).findFirst();

                    if (userSame.isPresent()) {
                        if (userModel.equals(userSame.get())) {
                            return;
                        }

                        newUserModelList.removeIf(user -> user.getUid().contains(userModel.getUid()));
                    }


                    newUserModelList.add(userModel);
                    cardStackAdapter.setUserModelList(newUserModelList);
                    cardStackAdapter.notifyDataSetChanged();

                    setColorActionBtn(true);
                });
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

    private void checkData(@NonNull DataSnapshot snapshotUser, String oppositeUserSex, IHandle handle) {
        if (snapshotUser.exists() && snapshotUser.child("gender").getValue() != null
                && snapshotUser.child("active").getValue(Boolean.class)) {
            String uid = snapshotUser.getKey();

            if(uid.equals(mainActivity.getUserModel().getUid())){
                return;
            }

            String gender = snapshotUser.child("gender").getValue().toString();

            if (gender.equals(oppositeUserSex)) {
                daoUser.getConnections().get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot snapshotConnectCurrent) {
                        if (!snapshotConnectCurrent.child("nope").hasChild(uid) && !snapshotConnectCurrent.child("yep").hasChild(uid)) {
                            handle.handleCb(snapshotUser);
                        }
                    }
                });
            }
        }
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


    public interface IHandle {
        void handleCb(DataSnapshot snapshotUser);
    }


}