package com.teamgogoal.jpa.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.teamgogoal.jpa.entity.File;

@Dao
public interface FileDao {

    @Query(
            "SELECT * " +
            "FROM File " +
            "WHERE id = :id"
    )
    File findById(int id);

    @Insert
    void insertAll(File... files);

}
