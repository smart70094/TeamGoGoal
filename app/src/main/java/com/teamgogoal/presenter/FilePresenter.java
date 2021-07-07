package com.teamgogoal.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.widget.ImageView;

import com.teamgogoal.jpa.entity.File;
import com.teamgogoal.model.FileModel;

import java.io.IOException;
import java.util.Objects;

import okhttp3.ResponseBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class FilePresenter {

    private FileModel fileModel;

    public FilePresenter() {

        this.fileModel = new FileModel();
    }

    public void initHeadImage(Context context, int headImageId, ImageView imageView) {
        Observable<File> localHeadImageObservable = fileModel.getFromDb(context, headImageId);
        Observable<ResponseBody> apiHeadImageObservable = fileModel.get(headImageId);
        Observable<File> apiAndCreateLocalObservable =
                apiHeadImageObservable.flatMap((responseBody) -> {
                    try {

                        byte[] image = responseBody.bytes();

                        fileModel.createToLocalDb(context, headImageId, image)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();

                        File file = File.builder().id(headImageId).image(image).build();
                        return Observable.just(file);
                    } catch (IOException e) {
                        Log.e("ProfilePresenter", e.toString());
                        throw new IllegalStateException(e.toString());
                    }
                });

        Observable<?> headImageObservable =
                localHeadImageObservable.flatMap((file) -> Objects.isNull(file) ? apiAndCreateLocalObservable : Observable.just(file));

        headImageObservable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result->{
                    File file = (File) result;
                    Bitmap bitmap = BitmapFactory.decodeByteArray(file.getImage(), 0, file.getImage().length);
                    RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), bitmap);
                    roundedBitmapDrawable.setCircular(true);
                    imageView.setImageDrawable(roundedBitmapDrawable);
                });
    }
}
