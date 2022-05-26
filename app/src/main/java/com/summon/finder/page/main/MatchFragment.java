package com.summon.finder.page.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.summon.finder.R;
import com.summon.finder.component.match.MatchAdapter;
import com.summon.finder.model.ChatModel;
import com.summon.finder.model.UserModel;


public class MatchFragment extends Fragment {

    private View view;
    private MainActivity mainActivity;
    private MatchAdapter adapter;
    private TextView likeNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_match, container, false);
        mainActivity = (MainActivity) getActivity();
        likeNumber = view.findViewById(R.id.likeNumber);

        handleInitAdapter();
        handleAddDataAdapter();
        setNumberLike(String.valueOf(0));

        return view;
    }

    private void setNumberLike(String i) {
        likeNumber.setText(i + " lượt thích ❤");
    }

    private void handleInitAdapter() {
        adapter = new MatchAdapter();

        RecyclerView listMatch = view.findViewById(R.id.listMatch);

        listMatch.setLayoutManager(new GridLayoutManager(getActivity(), 2));

        listMatch.setAdapter(adapter);
    }

    private void handleAddDataAdapter() {
        DatabaseReference usersDb = FirebaseDatabase.getInstance().getReference("user");
        DatabaseReference userCurrentDb = FirebaseDatabase.getInstance().getReference("user").child(mainActivity.getUserModel().getUid())
                .child("match");
        userCurrentDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String idUser = snapshot.getKey();

                usersDb.child(idUser).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        UserModel user = new UserModel(dataSnapshot);
                        ChatModel userModel = new ChatModel(snapshot.child("chatId").getValue(String.class), user, "");
                        adapter.addData(userModel);
                        adapter.notifyDataSetChanged();

                        setNumberLike(String.valueOf(adapter.getItemCount()));
                    }
                });
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
}