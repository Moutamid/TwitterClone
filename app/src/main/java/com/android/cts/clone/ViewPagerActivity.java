package com.android.cts.clone;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.cts.clone.Adapters.SimpleViewPagerAdapter;
import com.android.cts.clone.Model.TweetModel;
import com.android.cts.clone.database.RoomDB;
import com.androidnetworking.AndroidNetworking;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.fxn.stash.Stash;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;

public class ViewPagerActivity  extends AppCompatActivity {
    ViewPager viewPager;
    RoomDB database;
    private TweetModel model;
    List<TweetModel> list;
    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);

        viewPager = findViewById(R.id.viewPager);

        database = RoomDB.getInstance(this);

        list = Stash.getArrayList("List", TweetModel.class);
        position = Stash.getInt("position", 0);
        Log.d("position12", "Detail Screen : " + position);

        viewPager.setCurrentItem(position);
        SimpleViewPagerAdapter simpleViewPagerAdapter = new SimpleViewPagerAdapter(this, list, position);
        viewPager.setAdapter(simpleViewPagerAdapter);
        simpleViewPagerAdapter.notifyDataSetChanged();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

}
