package com.teamgogoal.model;

import android.content.Context;

import androidx.room.Room;

import com.teamgogoal.jpa.AppDatabase;
import com.teamgogoal.jpa.entity.File;
import com.teamgogoal.service.FileApiService;
import com.teamgogoal.utils.TggRetrofitUtils;
import com.teamgogoal.utils.UUIDUtils;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import rx.Observable;

public class FileModel {

    private FileApiService fileApiService;

    public FileModel() {
        fileApiService = TggRetrofitUtils.getTggService(FileApiService.class);
    }

    public Observable<ResponseBody> get(int id) {
        return fileApiService.get(id);
    }

    public Observable<File> getFromDb(Context context, int id) {
        return Observable.just(id)
                .flatMap((imageHeadId) ->{
                    AppDatabase appDatabase = Room.databaseBuilder(context, AppDatabase.class, "teamgogoal").build();
                    return Observable.just(appDatabase.fileDao().findById(imageHeadId));
                });

    }

    public Observable<File> create(String filename, byte[] image) {
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), image);

        MultipartBody.Part part =
                MultipartBody.Part.createFormData("file", filename, requestBody);

        return fileApiService.create(part);
    }

    public Observable<File> createToLocalDb(Context context, int id, byte[] image) {
        return Observable.create((subscriber) -> {
                AppDatabase appDatabase = Room.databaseBuilder(context, AppDatabase.class, "teamgogoal").build();

            File file = File.builder()
                    .id(id)
                    .image(image)
                    .build();

            appDatabase.fileDao().insertAll(file);
                subscriber.onNext(file);
                subscriber.onCompleted();
        });
    }

}
