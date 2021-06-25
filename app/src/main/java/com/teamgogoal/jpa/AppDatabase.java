package com.teamgogoal.jpa;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.teamgogoal.jpa.dao.FileDao;
import com.teamgogoal.jpa.entity.File;

@Database(entities = {File.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FileDao fileDao();
}
