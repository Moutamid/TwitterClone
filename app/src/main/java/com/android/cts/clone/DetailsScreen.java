package com.android.cts.clone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cts.clone.Adapters.SimpleViewPagerAdapter;
import com.androidnetworking.AndroidNetworking;
import com.downloader.Error;
import com.downloader.OnCancelListener;
import com.downloader.OnDownloadListener;
import com.downloader.OnPauseListener;
import com.downloader.OnProgressListener;
import com.downloader.OnStartOrResumeListener;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.downloader.Progress;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.android.cts.clone.Model.TweetModel;
import com.android.cts.clone.database.RoomDB;
import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;
import com.twitter.sdk.android.core.models.MediaEntity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;

public class DetailsScreen extends AppCompatActivity {

    private TweetModel model;
    private CircleImageView profileImage;
    List<MediaEntity> mediaEntities;
    ArrayList<TweetModel> list;
    int position;
    ViewPager viewPager;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);

        viewPager = findViewById(R.id.viewPager);

        list = Stash.getArrayList("List", TweetModel.class);
        position = Stash.getInt("position", 0);

        SimpleViewPagerAdapter adapter = new SimpleViewPagerAdapter(DetailsScreen.this, list);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int pos) {
//                position = pos;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }

        });

        adapter.notifyDataSetChanged();

        //loadTweets(position);

        //file Creating With Folder & Fle Name
        //file = new File(dirPath, fileName);

        /*left.setOnClickListener(v -> {
            if (position > 0){
                loadTweets(position-1);
            }
        });

        right.setOnClickListener(v -> {
            if (position < list.size()-1){
                loadTweets(position + 1);
            }
        });*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}