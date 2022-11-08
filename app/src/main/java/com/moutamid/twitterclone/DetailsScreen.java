package com.moutamid.twitterclone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.artjimlop.altex.AltexImageDownloader;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.moutamid.twitterclone.Model.TweetModel;
import com.moutamid.twitterclone.Model.UserModel;
import com.moutamid.twitterclone.database.RoomDB;
import com.squareup.picasso.Picasso;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class DetailsScreen extends AppCompatActivity {

    private ImageView postImage;
    private TextView name, username, time, message;
    private AppCompatButton deleteBtn, downloadBtn, copyBtn, translateBtn;
    private TweetModel model;
    private CircleImageView profileImage;
    File file;
    RoomDB database;
    String dirPath, fileName;
    String currentText;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
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
        model = (TweetModel) getIntent().getSerializableExtra("tweet_details");
        name.setText(model.getName());
        username.setText(model.getUsername());
        message.setText(model.getMessage());
        currentText = model.getMessage();
        Picasso.with(DetailsScreen.this).load(model.getProfile_image_url()).into(profileImage);

        if (!model.getImageUrl().isEmpty()) {
            Picasso.with(DetailsScreen.this).load(model.getImageUrl()).into(postImage);
        }
        time.setText(model.getCreated_at());

        database = RoomDB.getInstance(this);

        AndroidNetworking.initialize(getApplicationContext());

        // Adding an Network Interceptor for Debugging purpose :
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        //Folder Creating Into Phone Storage
        dirPath = Environment.getExternalStorageDirectory() + "/Image" + "/Tweetee Tweets" + "/";
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date myDate = new Date();
        fileName = timeStampFormat.format(myDate) + "i.jpeg";

        //file Creating With Folder & Fle Name
        file = new File(dirPath, fileName);

        deleteBtn.setOnClickListener(v -> {
            database.mainDAO().Delete(model);
            Toast.makeText(getApplicationContext(), "Tweet Deleted Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DetailsScreen.this, FeedScreen.class));
            finish();
        });

        downloadBtn.setOnClickListener(v -> {
            if (model.getImageUrl().isEmpty()) {
                Toast.makeText(this, "No Image/Video Found", Toast.LENGTH_SHORT).show();
            } else {
                AltexImageDownloader.writeToDisk(DetailsScreen.this, model.getImageUrl(), dirPath);
            }
        });

        copyBtn.setOnClickListener(v -> {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);
                clipboard.setText(message.getText().toString());
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(this.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Tweet", message.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        translateBtn.setOnClickListener(v -> {
            showDialog();
        });
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_popup);

        Button english = dialog.findViewById(R.id.eng);
        Button germany = dialog.findViewById(R.id.grm);
        Button french = dialog.findViewById(R.id.frc);
        Button spanish = dialog.findViewById(R.id.span);
        ImageButton cancel = dialog.findViewById(R.id.close);

        cancel.setOnClickListener(v -> {
            dialog.cancel();
        });

        english.setOnClickListener(v -> {
            translateEnglish();
            dialog.cancel();
        });

        germany.setOnClickListener(v -> {
            translateGerman();
            dialog.cancel();
        });

        french.setOnClickListener(v -> {
            translateFrench();
            dialog.cancel();
        });

        spanish.setOnClickListener(v -> {
            translateSpanish();
            dialog.cancel();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void translateSpanish() {
        TranslateAPI translate = new TranslateAPI();
        translate.setOnTranslationCompleteListener(new TranslateAPI.OnTranslationCompleteListener() {
            @Override
            public void onStartTranslation() {

            }

            @Override
            public void onCompleted(String text) {
                message.setText(text);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        translate.execute(currentText, "en", "es");
    }

    private void translateGerman() {
        TranslateAPI translate = new TranslateAPI();
        translate.setOnTranslationCompleteListener(new TranslateAPI.OnTranslationCompleteListener() {
            @Override
            public void onStartTranslation() {

            }

            @Override
            public void onCompleted(String text) {
                message.setText(text);
            }

            @Override
            public void onError(Exception e) {

            }
        });
        translate.execute(currentText, "en", "de");
    }

    private void translateFrench() {
        try {
            TranslateAPI translate = new TranslateAPI();
            translate.setOnTranslationCompleteListener(new TranslateAPI.OnTranslationCompleteListener() {
                @Override
                public void onStartTranslation() {

                }

                @Override
                public void onCompleted(String text) {
                    message.setText(text);
                }

                @Override
                public void onError(Exception e) {

                }
            });
            translate.execute(currentText, "en", "fr");
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void translateEnglish() {
        message.setText(currentText);
    }

}