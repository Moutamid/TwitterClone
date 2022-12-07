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

import com.android.cts.clone.DetailsScreen;
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
import com.fxn.stash.Stash;
import com.google.android.material.card.MaterialCardView;
import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import okhttp3.OkHttpClient;

public class SimpleViewPagerAdapter extends PagerAdapter implements LoopingPagerAdapter {

    Context ctx;
    ArrayList<TweetModel> list;
    TextView name, username, time, message;
    MaterialCardView deleteBtn, downloadBtn, copyBtn, translateBtn;
    File file;
    RoomDB database;
    String dirPath, fileName;
    String currentText;
    TweetModel model;
    ProgressDialog progressDialog;
    int position, NUMBER_OF_PAGES;
    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE};


     public SimpleViewPagerAdapter(Context ctx, ArrayList<TweetModel> modelDataArrayList, int position) {
        this.ctx = ctx;
        this.list = modelDataArrayList;
        this.position = position;
         Log.d("position12", "Detail Screen : " + position);

         database = RoomDB.getInstance(ctx);

         progressDialog = new ProgressDialog(ctx);
         progressDialog.setMessage("Downloading");
         progressDialog.setCancelable(false);

         PRDownloader.initialize(ctx);

         PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                 .setReadTimeout(30_000)
                 .setConnectTimeout(30_000)
                 .build();
         PRDownloader.initialize(ctx, config);

         AndroidNetworking.initialize(ctx);

         // Adding an Network Interceptor for Debugging purpose :
         OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                 .addNetworkInterceptor(new StethoInterceptor())
                 .build();
         AndroidNetworking.initialize(ctx, okHttpClient);

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

   @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int pos) {
        LayoutInflater layoutInflater= (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(R.layout.detail_screen,container,false);
        model = list.get(pos);

        Log.d("position12", "ViewPager Adapter : " + pos);

        // position = pos;

        name = view.findViewById(R.id.name);
        username = view.findViewById(R.id.username);
        message = view.findViewById(R.id.details);
        time = view.findViewById(R.id.time);
        deleteBtn = view.findViewById(R.id.delete);
        downloadBtn = view.findViewById(R.id.download);
        copyBtn = view.findViewById(R.id.copy);
        translateBtn = view.findViewById(R.id.translate);

        loadTweets(pos);

       deleteBtn.setOnClickListener(v -> {
           database.mainDAO().Delete(model);
           //Toast.makeText(getApplicationContext(), "Tweet Deleted Successfully", Toast.LENGTH_SHORT).show();
            /*startActivity(new Intent(DetailsScreen.this, FeedScreen.class));
            finish();*/
           int p = position;
           if (position < list.size()-1){
               loadTweets(position + 1);
               list.remove(p);
           }
       });

       downloadBtn.setOnClickListener(v -> {
           if (model.getPublicImageUrl().isEmpty()) {
               Toast.makeText(ctx, "No Image/Video Found", Toast.LENGTH_SHORT).show();
           } else {
               // Toast.makeText(this, model.getContentType(), Toast.LENGTH_SHORT).show();
               ActivityCompat.requestPermissions((Activity) ctx, permission, 1);
               download();
               // AltexImageDownloader.writeToDisk(DetailsScreen.this, mediaEntities.get(0).mediaUrl, dirPath);
           }
       });

       copyBtn.setOnClickListener(v -> {
           int sdk = android.os.Build.VERSION.SDK_INT;
           if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
               android.text.ClipboardManager clipboard = (android.text.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
               clipboard.setText(message.getText().toString());
               Toast.makeText(ctx, "Copied to clipboard", Toast.LENGTH_SHORT).show();
           } else {
               android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
               android.content.ClipData clip = android.content.ClipData.newPlainText("Tweet", message.getText().toString());
               clipboard.setPrimaryClip(clip);
               Toast.makeText(ctx, "Copied to clipboard", Toast.LENGTH_SHORT).show();
           }
       });

       translateBtn.setOnClickListener(v -> {
           showDialog();
       });

       Objects.requireNonNull(container).addView(view);
        return view;
    }

    private void download() {
        PRDownloader.download(model.getPublicImageUrl(), file.getPath(), fileName)
                .build()
                .setOnStartOrResumeListener(new OnStartOrResumeListener() {
                    @Override
                    public void onStartOrResume() {
                        progressDialog.show();
                    }
                })
                .setOnPauseListener(new OnPauseListener() {
                    @Override
                    public void onPause() {

                    }
                })
                .setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel() {

                    }
                })
                .setOnProgressListener(new OnProgressListener() {
                    @Override
                    public void onProgress(Progress progress) {
                        long n = progress.currentBytes*100/progress.totalBytes;
                        progressDialog.setMessage("Downloading " + n + "%");
                    }
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        progressDialog.dismiss();
                        Log.d("download", "Download Complete");
                    }

                    @Override
                    public void onError(Error error) {
                        progressDialog.dismiss();
                        Log.d("download", error.toString());
                    }
                });
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(ctx);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_popup);

        Button english = dialog.findViewById(R.id.eng);
        Button germany = dialog.findViewById(R.id.grm);
        Button french = dialog.findViewById(R.id.frc);
        Button spanish = dialog.findViewById(R.id.span);
        Button orig = dialog.findViewById(R.id.orig);
        ImageButton cancel = dialog.findViewById(R.id.close);

        currentText = model.getMessage();

        orig.setOnClickListener(v -> {
            message.setText(model.getMessage());
            dialog.cancel();
        });

        cancel.setOnClickListener(v -> {
            dialog.cancel();
        });

        english.setOnClickListener(v -> {
            translate("en");
            dialog.cancel();
        });

        germany.setOnClickListener(v -> {
            translate("de");
            dialog.cancel();
        });

        french.setOnClickListener(v -> {
            translate("fr");
            dialog.cancel();
        });

        spanish.setOnClickListener(v -> {
            translate("es");
            dialog.cancel();
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void translate(String code) {
        //TranslateAPI translate = new TranslateAPI();

        TranslateAPI translate = new TranslateAPI(
                Language.AUTO_DETECT,
                code,
                currentText
        );

        translate.setTranslateListener(new TranslateAPI.TranslateListener() {
            @Override
            public void onSuccess(String translatedText) {
                message.setText(translatedText);
            }

            @Override
            public void onFailure(String ErrorText) {
                Log.d("tt12", ErrorText);
            }
        });
    }

    private void loadTweets(int i){
        model = list.get(i);
        Log.d("position12", "ViewPager load : " + i);

        //Folder Creating Into Phone Storage
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        }

        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date myDate = new Date();

        if (model.getContentType().equals("video")){
            fileName = timeStampFormat.format(myDate) + "i.mp4";
        } else {
            fileName = timeStampFormat.format(myDate) + "i.jpg";
        }

        position = i;

        name.setText(model.getName());
        username.setText(model.getUsername());
        message.setText(model.getMessage());

        time.setText(model.getCreated_at());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            if (!model.getPublicImageUrl().isEmpty()){
                downloadBtn.setVisibility(View.VISIBLE);
            } else {
                downloadBtn.setVisibility(View.GONE);
            }
        }
    }


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
