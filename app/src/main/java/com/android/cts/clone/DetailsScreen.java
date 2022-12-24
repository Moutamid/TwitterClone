package com.android.cts.clone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.core.view.MotionEventCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
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

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class DetailsScreen extends AppCompatActivity {

    private TweetModel model;
    ArrayList<TweetModel> list;
    int position;
    TextView name, username, time, message;
    MaterialCardView deleteBtn, downloadBtn, copyBtn, translateBtn;
    File file;
    RoomDB database;
    String dirPath, fileName;
    ProgressDialog progressDialog;
    String currentText;
    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE};
    View view;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_screen);

        list = Stash.getArrayList("List", TweetModel.class);
        position = Stash.getInt("position", 0);
        Log.d("position12", "Detail Screen : " + position);

        database = RoomDB.getInstance(this);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading");
        progressDialog.setCancelable(false);

        PRDownloader.initialize(this);

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(this, config);

        AndroidNetworking.initialize(this);

        // Adding an Network Interceptor for Debugging purpose :
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(this, okHttpClient);

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        message = findViewById(R.id.details);
        time = findViewById(R.id.time);
        deleteBtn = findViewById(R.id.delete);
        downloadBtn = findViewById(R.id.download);
        copyBtn = findViewById(R.id.copy);
        translateBtn = findViewById(R.id.translate);

        // loadTweets(position);

       /* view.setOnTouchListener(new OnSwipeTouchListener(DetailsScreen.this){
            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (position > 0){
                    loadTweets(position-1);
                }
            }

            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (position < list.size()-1){
                    loadTweets(position + 1);
                }
            }
        }); */

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

        deleteBtn.setOnClickListener(v -> {
            database.mainDAO().Delete(model);
            //Toast.makeText(getApplicationContext(), "Tweet Deleted Successfully", Toast.LENGTH_SHORT).show();
            /*startActivity(new Intent(DetailsScreen.this, FeedScreen.class));
            finish();*/
            int p = position;
            if (position < list.size()-1){
                //loadTweets(position + 1);
                list.remove(p);
            }
        });

        downloadBtn.setOnClickListener(v -> {
            if (model.getPublicImageUrl().isEmpty()) {
                Toast.makeText(this, "No Image/Video Found", Toast.LENGTH_SHORT).show();
            } else {
                // Toast.makeText(this, model.getContentType(), Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(DetailsScreen.this, permission, 1);
                download();
                // AltexImageDownloader.writeToDisk(DetailsScreen.this, mediaEntities.get(0).mediaUrl, dirPath);
            }
        });

        copyBtn.setOnClickListener(v -> {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(message.getText().toString());
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Tweet", message.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });

        translateBtn.setOnClickListener(v -> {
            showDialog();
        });
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
/*
    private void loadTweets(int i){
        model = list.get(i);
        Log.d("position12", "ViewPager load : " + i);

        //Folder Creating Into Phone Storage
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

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

        if (!model.getPublicImageUrl().isEmpty()){
            downloadBtn.setVisibility(View.VISIBLE);
        } else {
            downloadBtn.setVisibility(View.GONE);
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*startActivity(new Intent(DetailsScreen.this, FeedScreen.class));
        finish();*/
        onBackPressed();
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(DetailsScreen.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_popup);

        Button english = dialog.findViewById(R.id.eng);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final String DEBUG_TAG = "Gestures12";
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        @Override
        public boolean onDown(MotionEvent event) {
            Log.d(DEBUG_TAG, "onDown: " + event.toString());
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }

        public void onSwipeRight() {
        }

        public void onSwipeLeft() {
        }

        public void onSwipeTop() {
        }

        public void onSwipeBottom() {
        }
    }
}