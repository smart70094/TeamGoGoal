package com.teamgogoal.jpa.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class File {

    @PrimaryKey int id;

    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    private byte[] image;
}
