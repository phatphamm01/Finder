package com.summon.finder.page.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.ritesh.ratiolayout.RatioRelativeLayout;
import com.squareup.picasso.Picasso;
import com.summon.finder.DAO.DAOUser;
import com.summon.finder.R;
import com.summon.finder.component.tag.TagEditAdapter;
import com.summon.finder.databinding.FragmentEditProfileBinding;
import com.summon.finder.model.UserModel;
import com.summon.finder.service.UpdateImageTask;
import com.summon.finder.utils.changeintent.ChangeIntent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class EditProfileFragment extends Fragment {
    private final List<String> tagList = new ArrayList<>(Arrays.asList("Trung học phổ thông", "Thạc sĩ", "Cử nhân Tiến sĩ", "Chó", "Mèo", "Bò sát", "Động vật", "Lưỡng cư", "Không nuôi thú cưng", "Tất cả các loại thủ cưng", "Cá", "Ăn thuần chay", "Ăn chay", "Chỉ ăn hải sản và rau củ", "Ăn chay bán phần", "Ăn đa dạng", "Chỉ ăn thịt", "Ma Kết", "Bảo Bình", "Song Ngư", "Bạch Dương", "Kim Ngưu", "Cự Giải", "Sư Tử", "Xử Nữ", "Thiên Bình", "Bọ Cạp", "Nhân Mã", "Song Tử", "Hút thuốc với bạn bè", "Không hút thuốc", "Hút thuốc thường xuyên"));
    List<ImageView> imageViews = new ArrayList<>();
    List<ImageView> cancelViews = new ArrayList<>();
    private View view;
    private TagEditAdapter adapter;
    private MainActivity mainActivity;
    private FragmentEditProfileBinding binding;
    private DAOUser daoUser;
    private UserModel userModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_profile, container, false);
        view = binding.getRoot();
        mainActivity = (MainActivity) getActivity();
        userModel = mainActivity.getUserModel();
        adapter = new TagEditAdapter(getActivity(), this, tagList);

        daoUser = new DAOUser();
        handleAdapter();

        setEventDone();

        setGender();
        setEventGender();

        getAllViewImages(view.findViewById(R.id.listImage));
        addEventToImageView(imageViews);
        addEventToCancelView(cancelViews);

        setSchool();


        return view;
    }

    private void setSchool() {
        String scholl = userModel.getSchool();
        binding.school.setText(scholl);
    }

    @Override
    public void onStart() {
        super.onStart();
        HashMap<String, String> images = mainActivity.getUserModel().getImages();

        if (images != null) {
            handleViewImage(images);

            handleViewCancel(images);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 404 && data != null && data.getData() != null && !data.getData().toString().isEmpty()) {
            String idName = requireContext().getResources().getResourceEntryName(mainActivity.imageView.getId());
            String id = idName.substring(idName.length() - 1);
            mainActivity.getUserModel().handleSetImage(id, data.getData().toString());

            binding.loading.setVisibility(View.VISIBLE);
            Observable<UpdateImageTask.IDataDto> uriObservable = UpdateImageTask.getInstance().getUriObservable(mainActivity.getUserModel().getImages());
            Observer<UpdateImageTask.IDataDto> uriObserver = UpdateImageTask.getInstance().getUriObserver(new Observer<UpdateImageTask.IDataDto>() {
                @Override
                public void onSubscribe(@NonNull Disposable d) {

                }

                @Override
                public void onNext(UpdateImageTask.@NonNull IDataDto iDataDto) {
                    mainActivity.getUserModel().handleSetImage(iDataDto.idString, iDataDto.uri.toString());
                }

                @Override
                public void onError(@NonNull Throwable e) {
                    new android.os.Handler(Looper.getMainLooper()).post(
                            new Runnable() {
                                public void run() {
                                    binding.loading.setVisibility(View.INVISIBLE);
                                }
                            });

                }

                @Override
                public void onComplete() {
                    new android.os.Handler(Looper.getMainLooper()).post(
                            new Runnable() {
                                public void run() {
                                    binding.loading.setVisibility(View.INVISIBLE);
                                }
                            });
                }
            });

            uriObservable
                    .observeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(uriObserver);
        }
    }


    private void addEventToImageView(List<ImageView> imageViews) {
        imageViews.forEach((imageView) -> {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mainActivity.imageView = imageView;

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

                }

                private void handleLogicClick() {
                    String idName = requireContext().getResources().getResourceEntryName(cancelView.getId());
                    String id = idName.substring(idName.length() - 1);

                    int idImageView = getResources().getIdentifier("imageView_" + id, "id", mainActivity.getPackageName());
                    ((ImageView) view.findViewById(idImageView)).setImageResource(R.drawable.ic_plus);

                    mainActivity.getUserModel().removeImage(id);
                }
            });
        });
    }


    private void handleViewImage(HashMap<String, String> images) {

        images.forEach((key, value) -> {
            int id = getResources().getIdentifier("imageView_" + key, "id", mainActivity.getPackageName());

            Picasso.get().load(Uri.parse(value)).into((ImageView) view.findViewById(id));
        });


    }

    private void handleViewCancel(HashMap<String, String> images) {
        for (int i = 1; i <= 6; i++) {
            int id = getResources().getIdentifier("cancel_" + i, "id", mainActivity.getPackageName());
            if (images.get(String.valueOf(i)) != null) {
                view.findViewById(id).setVisibility(View.VISIBLE);

                continue;
            }
            view.findViewById(id).setVisibility(View.INVISIBLE);
        }


    }


    private void setEventDone() {
        view.findViewById(R.id.done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String schoolData = binding.school.getText().toString();

                daoUser.updateField("images", userModel.getImages());
                daoUser.updateField("school", schoolData.isEmpty() ? userModel.getSchool() : schoolData);
                daoUser.updateField("tags", userModel.getTags());

                ChangeIntent.getInstance().authGuard(mainActivity.getUserModel().getUid(), mainActivity, new Intent(mainActivity, MainActivity.class));
            }
        });
    }

    private void handleAdapter() {
        RecyclerView listView = view.findViewById(R.id.list_tag);
        listView.setAdapter(adapter);

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(getActivity(), FlexDirection.ROW, FlexWrap.WRAP);
        flexboxLayoutManager.setJustifyContent(JustifyContent.CENTER);
        listView.setLayoutManager(flexboxLayoutManager);
    }

    private void setEventGender() {
        binding.boy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daoUser.updateField("gender", "Nam");
                addStyle(binding.boy);
                removeStyle(binding.girl);
            }
        });

        binding.girl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daoUser.updateField("gender", "Nữ");
                addStyle(binding.girl);
                removeStyle(binding.boy);
            }
        });
    }

    private void setGender() {
        if (userModel.getGender().equals("Nam")) {
            addStyle(binding.boy);
        } else {
            addStyle(binding.girl);
        }
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

    private void addStyle(Button button) {
        button.setTextColor(getResources().getColor(R.color.primary));
        button.setBackground(requireActivity().getDrawable(R.drawable.button_checkbox_active));
    }

    private void removeStyle(Button button) {
        button.setTextColor(getResources().getColor(R.color.neural_80));
        button.setBackground(requireActivity().getDrawable(R.drawable.button_checkbox));
    }
}