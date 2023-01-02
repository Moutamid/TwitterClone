package com.android.cts.clone.Model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "tweets")
public class TweetModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id = 0;

    @ColumnInfo(name = "Name")
    private String name = "";

    @ColumnInfo(name = "Username")
    private String username = "";

    @ColumnInfo(name = "Email")
    private String email = "";

    @ColumnInfo(name = "Message")
    private String message = "";

    @ColumnInfo(name = "Created_At")
    private String created_at = "";

    @ColumnInfo(name = "timestamps")
    private long timestamps = 0;

    @ColumnInfo(name = "ImageUrl")
    private String imageUrl = "";

    @ColumnInfo(name = "publicImageUrl")
    private String publicImageUrl = "";

    @ColumnInfo(name = "Profile_Image_URL")
    private String profile_image_url = "";

    @ColumnInfo(name = "contentType")
    private String contentType = "";

    public TweetModel() {
    }

    public TweetModel(long id, String name, String username, String email, String message, String created_at, String profile_image_url, String publicImageUrl, String contentType, long timestamps) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.email = email;
        this.message = message;
        this.created_at = created_at;
        this.publicImageUrl = publicImageUrl;
        this.profile_image_url = profile_image_url;
        this.contentType = contentType;
        this.timestamps = timestamps;
    }

    public long getTimestamps() {
        return timestamps;
    }

    public void setTimestamps(long timestamps) {
        this.timestamps = timestamps;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getPublicImageUrl() {
        return publicImageUrl;
    }

    public void setPublicImageUrl(String publicImageUrl) {
        this.publicImageUrl = publicImageUrl;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }
}
