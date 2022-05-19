package com.summon.finder.service;

import android.net.Uri;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.UUID;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;


public class UpdateImageTask {

    private static UpdateImageTask instance;
    private final FirebaseStorage firebaseStorage;
    private final StorageReference storageReference;
    private Disposable disposable;

    public UpdateImageTask() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public static UpdateImageTask getInstance() {
        if (instance == null) {
            instance = new UpdateImageTask();
        }
        return instance;
    }

    public Observable<IDataDto> getUriObservable(HashMap<String, String> hashMap) {
        return Observable.create(new ObservableOnSubscribe<IDataDto>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<IDataDto> emitter) throws Throwable {
                final int[] size = {0};
                int sizeHashMap = hashMap.size();
                hashMap.forEach((key, value) -> {
                    String randomKey = UUID.randomUUID().toString();
                    StorageReference riverRef = storageReference.child("images/" + randomKey);


                    Uri uri = Uri.parse(value);

                    if(value.contains("http")){
                        handleNext(uri, emitter, key, size, sizeHashMap);
                        return;
                    }

                    riverRef.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            riverRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    handleNext(uri, emitter, key, size, sizeHashMap);
                                }
                            });
                        }
                    });

                });
            }

            private void handleNext(Uri uri, ObservableEmitter<IDataDto> emitter, String key, int[] size, int sizeHashMap) {
                emitter.onNext(new IDataDto(key, uri));
                size[0] = size[0] + 1;

                if(size[0] == sizeHashMap && !emitter.isDisposed()){
                    emitter.onComplete();
                }
            }
        });
    }

    public Observer<IDataDto> getUriObserver(Observer<IDataDto> uploadTaskObserver) {
        return uploadTaskObserver;
    }

    public class IDataDto {
        public String idString;
        public Uri uri;

        public IDataDto(String idString, Uri uri) {
            this.idString = idString;
            this.uri = uri;
        }
    }
}
