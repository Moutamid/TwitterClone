package com.android.cts.clone.Adapters;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import com.android.cts.clone.FragmentViewPager;
import com.android.cts.clone.LoopingPagerAdapter;
import com.android.cts.clone.Model.TweetModel;
import com.android.cts.clone.R;
import com.android.cts.clone.database.RoomDB;
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
import com.google.android.material.card.MaterialCardView;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import okhttp3.OkHttpClient;

public class SimpleViewPagerAdapter extends FragmentPagerAdapter implements LoopingPagerAdapter {

    Context ctx;
    ArrayList<TweetModel> list;
    TextView name, username, time, message;
    MaterialCardView deleteBtn, downloadBtn, copyBtn, translateBtn;
    File file;
    RoomDB database;
    String dirPath, fileName;
    String currentText;
    TweetModel model;
    int position, NUMBER_OF_PAGES;


    public SimpleViewPagerAdapter(@NonNull FragmentManager fm, Context ctx, ArrayList<TweetModel> list) {
        super(fm);
        this.ctx = ctx;
        this.list = list;
    }

   /* public SimpleViewPagerAdapter(Context ctx, ArrayList<TweetModel> modelDataArrayList) {
        this.ctx = ctx;
        this.list = modelDataArrayList;



    }
*/
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FragmentViewPager.newInstance(list, position);
    }

 /*   @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int pos) {
        LayoutInflater layoutInflater= (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.detail_screen,container,false);
        TweetModel model = list.get(pos);


        Log.d("position12", "ViewPager Adapter : " + pos);

        name = view.findViewById(R.id.name);
        username = view.findViewById(R.id.username);
        message = view.findViewById(R.id.details);
        time = view.findViewById(R.id.time);
        deleteBtn = view.findViewById(R.id.delete);
        downloadBtn = view.findViewById(R.id.download);
        copyBtn = view.findViewById(R.id.copy);
        translateBtn = view.findViewById(R.id.translate);

        loadTweets(pos);



        container.addView(view);
        return view;
    }




    }*/

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

    }

    @Override
    public int getRealCount() {
        return list.size();
    }
}
