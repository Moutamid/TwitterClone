package com.moutamid.twitterclone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.moutamid.twitterclone.Model.UserModel;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

public class DetailsScreen extends AppCompatActivity {

    private ImageView profileImage,postImage;
    private TextView name,username,time,message;
    private AppCompatButton deleteBtn,downloadBtn,copyBtn,translateBtn;
    private UserModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);
        profileImage = findViewById(R.id.profile);
        postImage = findViewById(R.id.image);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        message = findViewById(R.id.details);
        time = findViewById(R.id.time);
        deleteBtn = findViewById(R.id.delete);
        downloadBtn = findViewById(R.id.download);
        copyBtn = findViewById(R.id.copy);
        translateBtn = findViewById(R.id.translate);
        model = getIntent().getParcelableExtra("tweet_details");
        name.setText(model.getName());
        username.setText(model.getUsername());
        message.setText(model.getMessage());
        Picasso.with(DetailsScreen.this)
                .load(model.getProfile_image_url())
                .into(profileImage);
        time.setText(model.getCreated_at());

    }
}