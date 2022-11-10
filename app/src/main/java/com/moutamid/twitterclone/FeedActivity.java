package com.moutamid.twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.moutamid.twitterclone.twitter.TwitterAsyncTask;

public class FeedActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_feed);
        if (Utils.isNetworkConnected(this)){
            new TwitterAsyncTask().execute("BajwaShb_", this);
        } else {
            Toast.makeText(this, "Internet is not connected", Toast.LENGTH_SHORT).show();
        }
    }
}