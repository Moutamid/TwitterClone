package com.android.cts.clone.database;

import static androidx.room.OnConflictStrategy.REPLACE;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.android.cts.clone.Model.TweetModel;

import java.util.List;

@Dao
public interface MainDAO {

    @Insert(onConflict = REPLACE)
    void insert(TweetModel tweets);

    @Query("SELECT * FROM tweets ORDER BY id ASC")
    List<TweetModel> getAll();

    @Query("UPDATE tweets SET name = :name, username = :username, email= :email, message= :message, created_at= :created_at, imageUrl= :imageUrl, profile_image_url= :profile_image_url  where id = :id")
    void update(int id, String name, String username, String email, String message, String created_at, String imageUrl, String profile_image_url);

    @Delete
    void Delete(TweetModel tweet);
}
