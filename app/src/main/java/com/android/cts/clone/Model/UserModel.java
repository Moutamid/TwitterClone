package com.android.cts.clone.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class UserModel implements Parcelable {

    private long id;
    private String name;
    private String username;
    private String email;
    private String message;
    private String created_at;
    private String imageUrl;
    private String profile_image_url;

    public UserModel(){

    }

    protected UserModel(Parcel in) {
        id = in.readLong();
        name = in.readString();
        username = in.readString();
        email = in.readString();
        message = in.readString();
        created_at = in.readString();
        imageUrl = in.readString();
        profile_image_url = in.readString();
    }

    public static final Creator<UserModel> CREATOR = new Creator<UserModel>() {
        @Override
        public UserModel createFromParcel(Parcel in) {
            return new UserModel(in);
        }

        @Override
        public UserModel[] newArray(int size) {
            return new UserModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeString(username);
        parcel.writeString(email);
        parcel.writeString(message);
        parcel.writeString(created_at);
        parcel.writeString(imageUrl);
        parcel.writeString(profile_image_url);
    }
}
