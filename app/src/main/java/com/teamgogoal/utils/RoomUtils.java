package com.teamgogoal.utils;

import android.content.Context;

import androidx.room.Room;

import com.teamgogoal.jpa.AppDatabase;

public class RoomUtils {

    public AppDatabase getAppDatabase(Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "teamgogoal").build();
    }
}
