package com.summon.finder.page.setting;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.ritesh.ratiolayout.RatioRelativeLayout;
import com.squareup.picasso.Picasso;
import com.summon.finder.R;
import com.summon.finder.page.main.MainActivity;
import com.summon.finder.service.UpdateImageTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingAccountSevenFragment extends Fragment {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    View view;
    SettingAccountActivity settingAccountActivity;
    Button button;
    List<ImageView> imageViews = new ArrayList<>();
    List<ImageView> cancelViews = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_config_user_step_seven, container, false);

        settingAccountActivity = (SettingAccountActivity) getActivity();

        button = view.findViewById(R.id.asa_continue);
        addEventToButton(button);

        getAllViewImages(view.findViewById(R.id.listImage));
        addEventToImageView(imageViews);
        addEventToCancelView(cancelViews);
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        HashMap<String, String> images = settingAccountActivity.userModel.getImages();

        if (images != null) {
            handleViewImage(images);

            handleViewCancel(images);
        }

        if (images == null || images.size() < 2) {
            handleContinue();
        }
    }

    private void handleViewImage(HashMap<String, String> images) {

        images.forEach((key, value) -> {
            int id = getResources().getIdentifier("imageView_" + key, "id", settingAccountActivity.getPackageName());

            Picasso.get().load(Uri.parse(value)).into((ImageView) view.findViewById(id));
        });


    }

    private void handleViewCancel(HashMap<String, String> images) {
        for (int i = 1; i <= 6; i++) {
            int id = getResources().getIdentifier("cancel_" + i, "id", settingAccountActivity.getPackageName());
            if (images.get(String.valueOf(i)) != null) {
                view.findViewById(id).setVisibility(View.VISIBLE);
                continue;
            }
            view.findViewById(id).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 404) {
            String idName = requireContext().getResources().getResourceEntryName(settingAccountActivity.imageView.getId());
            String id = idName.substring(idName.length() - 1);

            settingAccountActivity.userModel.addImage(id, data.getData().toString());
            handleContinue();
        }
    }


    private void addEventToImageView(List<ImageView> imageViews) {
        imageViews.forEach((imageView) -> {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    settingAccountActivity.imageView = imageView;

                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 404);
                }
            });
        });
    }

    private void addEventToCancelView(List<ImageView> cancelViews) {
        cancelViews.forEach((cancelView) -> {
            cancelView.setClickable(true);
            cancelView.setFocusable(true);
            cancelView.setVisibility(View.INVISIBLE);

            cancelView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cancelView.setVisibility(View.INVISIBLE);

                    handleLogicClick();
                    handleContinue();
                }

                private void handleLogicClick() {
                    String idName = requireContext().getResources().getResourceEntryName(cancelView.getId());
                    String id = idName.substring(idName.length() - 1);

                    int idImageView = getResources().getIdentifier("imageView_" + id, "id", settingAccountActivity.getPackageName());
                    ((ImageView) view.findViewById(idImageView)).setImageResource(R.drawable.ic_plus);

                    settingAccountActivity.userModel.removeImage(id);
                }
            });
        });
    }

    private void addEventToButton(Button button) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Observable<UpdateImageTask.IDataDto> uriObservable = UpdateImageTask.getInstance().getUriObservable(settingAccountActivity.userModel.getImages());
                Observer<UpdateImageTask.IDataDto> uriObserver = UpdateImageTask.getInstance().getUriObserver(new Observer<UpdateImageTask.IDataDto>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(UpdateImageTask.@NonNull IDataDto iDataDto) {
                        settingAccountActivity.userModel.addImage(iDataDto.idString, iDataDto.uri.toString());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        settingAccountActivity.userModel.setActive(true);

                        Intent intent = new Intent(settingAccountActivity, MainActivity.class);
                        startActivity(intent);
                    }
                });

                uriObservable
                        .observeOn(Schedulers.io())
                        .subscribeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(uriObserver);
            }
        });
    }

    public void handleContinue() {
        final int size = settingAccountActivity.userModel.getImages().size();

        if (size >= 2) {
            onModeClick(button);
            return;
        }

        offModeClick(button);
    }

    private void onModeClick(Button button) {
        button.setEnabled(true);
        button.setClickable(true);
        button.setFocusable(true);
        button.setBackgroundColor(getResources().getColor(R.color.primary));
    }

    private void offModeClick(Button button) {
        button.setEnabled(false);
        button.setClickable(false);
        button.setFocusable(false);
        button.setBackgroundColor(getResources().getColor(R.color.neural_80));
    }


    public void getAllViewImages(ViewGroup layout) {
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if (v instanceof RatioRelativeLayout) {
                getAllViewImages((ViewGroup) v);
            }

            if (v instanceof CardView) {
                getAllViewImages((ViewGroup) v);
            }

            if (v instanceof ImageView) {
                String name = getResources().getResourceEntryName(v.getId());
                if (name.contains("cancel")) {
                    cancelViews.add((ImageView) v);
                    continue;
                }
                imageViews.add((ImageView) v);
            }
        }
    }
}
