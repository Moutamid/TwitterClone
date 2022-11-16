package com.android.cts.clone;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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


    private TextView name, username, time, message;
    private MaterialCardView deleteBtn, downloadBtn, copyBtn, translateBtn, left, right;
    private TweetModel model;
    private CircleImageView profileImage;
    File file;
    RoomDB database;
    String dirPath, fileName;
    String currentText;
    List<MediaEntity> mediaEntities;
    ArrayList<TweetModel> list;
    int position;
    ProgressDialog progressDialog;
    String permission[] = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.MANAGE_EXTERNAL_STORAGE};

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);
        name = findViewById(R.id.name);
        username = findViewById(R.id.username);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Downloading");
        progressDialog.setCancelable(false);

        message = findViewById(R.id.details);
        time = findViewById(R.id.time);
        deleteBtn = findViewById(R.id.delete);
        downloadBtn = findViewById(R.id.download);
        copyBtn = findViewById(R.id.copy);
        translateBtn = findViewById(R.id.translate);
        left = findViewById(R.id.arrowLeft);
        right = findViewById(R.id.arrowRight);

        list = Stash.getArrayList("List", TweetModel.class);
        position = getIntent().getIntExtra("position", 0);

        loadTweets(position);

        database = RoomDB.getInstance(this);

        PRDownloader.initialize(this);

        PRDownloaderConfig config = PRDownloaderConfig.newBuilder()
                .setReadTimeout(30_000)
                .setConnectTimeout(30_000)
                .build();
        PRDownloader.initialize(getApplicationContext(), config);

        AndroidNetworking.initialize(getApplicationContext());

        // Adding an Network Interceptor for Debugging purpose :
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();
        AndroidNetworking.initialize(getApplicationContext(), okHttpClient);

        //Folder Creating Into Phone Storage
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        SimpleDateFormat timeStampFormat = new SimpleDateFormat("yyyyMMddHHmmssSS");
        Date myDate = new Date();
        fileName = timeStampFormat.format(myDate) + "i";

        //file Creating With Folder & Fle Name
       // file = new File(dirPath, fileName);

        deleteBtn.setOnClickListener(v -> {
            database.mainDAO().Delete(model);
            //Toast.makeText(getApplicationContext(), "Tweet Deleted Successfully", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(DetailsScreen.this, FeedScreen.class));
            finish();
        });

        left.setOnClickListener(v -> {
            loadTweets(position-1);
        });

        right.setOnClickListener(v -> {
            loadTweets(position+1);
        });

        downloadBtn.setOnClickListener(v -> {
            if (model.getImageUrl().isEmpty()) {
                Toast.makeText(this, "No Image/Video Found", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(this, permission, 1);
                download();
                //    AltexImageDownloader.writeToDisk(DetailsScreen.this, mediaEntities.get(0).mediaUrl, dirPath);
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

    private void download() {
        PRDownloader.download(model.getPublicImageUrl(), file.getPath(), "fileName")
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

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void loadTweets(int i){
        model = list.get(i);
        position = i;
        name.setText(model.getName());
        username.setText(model.getUsername());
        message.setText(model.getMessage());

        time.setText(model.getCreated_at());

        if (!model.getImageUrl().isEmpty()){
            downloadBtn.setVisibility(View.VISIBLE);
        } else {
            downloadBtn.setVisibility(View.GONE);
        }
    }

    private void showDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.language_popup);

        Button english = dialog.findViewById(R.id.eng);
        Button germany = dialog.findViewById(R.id.grm);
        Button french = dialog.findViewById(R.id.frc);
        Button spanish = dialog.findViewById(R.id.span);
        Button orig = dialog.findViewById(R.id.orig);
        ImageButton cancel = dialog.findViewById(R.id.close);

        currentText = message.getText().toString();

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

    private void translate(String code){
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

        /*translate.setOnTranslationCompleteListener(new TranslateAPI.OnTranslationCompleteListener() {
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
        translate.execute(currentText, "en", code);*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}