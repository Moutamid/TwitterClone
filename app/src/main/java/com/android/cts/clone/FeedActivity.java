package com.android.cts.clone;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.android.cts.clone.twitter.TwitterAsyncTask;

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

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        startActivity(new Intent(FeedActivity.this, FeedScreen.class));
    }
}