package com.android.cts.clone.twitter;

import android.app.ListActivity;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.os.AsyncTask;
import android.widget.ListView;


import androidx.annotation.RequiresApi;

import com.android.cts.R;

import java.util.ArrayList;

public class TwitterAsyncTask extends AsyncTask<Object, Void, ArrayList<TwitterTweet>> {
    ListActivity callerActivity;

    @RequiresApi(api = Build.VERSION_CODES.FROYO)
    @Override
    protected ArrayList<TwitterTweet> doInBackground(Object... params) {
        ArrayList<TwitterTweet> twitterTweets = null;
        callerActivity = (ListActivity) params[1];
        if (params.length > 0) {
            TwitterAPI twitterAPI = new TwitterAPI(
                    callerActivity.getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                    callerActivity.getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET));
            twitterTweets = twitterAPI.getTwitterTweets(params[0].toString());
        }
        return twitterTweets;
    }

    @Override
    protected void onPostExecute(ArrayList<TwitterTweet> twitterTweets) {
        ArrayAdapter<TwitterTweet> adapter =
                new ArrayAdapter<TwitterTweet>(callerActivity, R.layout.custom_layout,
                        R.id.message, twitterTweets);
        callerActivity.setListAdapter(adapter);
        ListView lv = callerActivity.getListView();
        lv.setDividerHeight(0);
        //lv.setDivider(this.getResources().getDrawable(android.R.color.transparent));
        lv.setBackgroundColor(callerActivity.getResources().getColor(R.color.LightBlue));
    }
}
