package com.android.cts.clone;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cts.clone.Adapters.FeedListAdapter;
import com.android.cts.clone.Model.TweetModel;
import com.android.cts.clone.database.RoomDB;
import com.fxn.stash.Stash;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;


public class FeedScreen extends AppCompatActivity {

    RoomDB database;
    List<TweetModel> list;
    SimpleDateFormat formatter;
    Date sessionTime, dateE, dateS, endTime;
    String s, stashDate, enddate;
    private RecyclerView recyclerView;
    private ImageButton refresh;
    private TextView emailTxt;
    private String username;
    private SharedPreferencesManager sharedPref;
    private final String JSON_URL = "https://api.twitter.com/2/users/%s/tweets";
    private long id;
    private final ArrayList<TweetModel> tweetList = new ArrayList<>();
    private final String bearerToken = "AAAAAAAAAAAAAAAAAAAAAHLUiQEAAAAATcIyY%2BxekJ5M7R%2FpDLSiBvr8N6E%3DgxSzwJvDlqkyL0k0fs7i1eDkwcYpytc42GhDs8MB6GNtVFBQMC";
    private TweetTimelineRecyclerViewAdapter adapter;
    private TwitterSession session;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Twitter.initialize(this);
        setContentView(R.layout.activity_feed_screen);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(new TwitterAuthConfig(getResources()
                        .getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                        getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))//pass the created app Consumer KEY and Secret also called API Key and Secret
                .debug(true) //enable debug mode
                .build();

        //finally initialize twitter with created configs
        Twitter.initialize(config);

        recyclerView = findViewById(R.id.recyclerView);
        refresh = findViewById(R.id.refresh);

        String se = null;

        try {
            se = Stash.getString("loginSession");
            Log.d("List123", "Stash : " + se);
        } catch (Exception e) {
            e.printStackTrace();
        }

        formatter = new SimpleDateFormat("E, MMM dd yyyy, hh:mm aa");

        if (se.isEmpty() || se == null) {
            sessionTime = new Date();
            s = formatter.format(sessionTime);
            Stash.put("loginSession", s);
            Log.d("List123", "Stash S : " + s);
        }

        MyApplication.getInstance().setOnVisibilityChangeListener(new MyApplication.ValueChangeListener() {
            @Override
            public void onChanged(Boolean value) {
                Log.d("isAppInBackground", String.valueOf(value));
                if (value) {
                    sessionTime = new Date();
                    s = formatter.format(sessionTime);
                    Stash.put("loginSession", s);
                }
            }
        });

        sharedPref = new SharedPreferencesManager(FeedScreen.this);

        emailTxt = findViewById(R.id.email);
        username = getIntent().getStringExtra("username");
        id = getIntent().getLongExtra("userId", 0);
        LinearLayoutManager manager = new LinearLayoutManager(FeedScreen.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        emailTxt.setText(username);
        Log.d("token", "" + id);

        database = RoomDB.getInstance(this);

        list = database.mainDAO().getAll();

        refreshTweets();

        /*if (list.size() >= 1 && list != null){
            Log.d("List123", "List offline "+list.size());
            FeedListAdapter adapter = new FeedListAdapter(FeedScreen.this, list);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Log.d("List123", "List zero "+list.size());
            refreshTweets();
        }*/

        refresh.setOnClickListener(v -> {
            tweetList.clear();
            refreshTweets();
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void refreshTweets() {
        if (Utils.isNetworkConnected(FeedScreen.this)) {
            getUserTweets();
        } else {
            Toast.makeText(this, "Internet is not connected", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void getUserTweets() {
        Date d = new Date();
        stashDate = Stash.getString("loginSession", formatter.format(d));
        Log.d("List123", "Stash F : " + stashDate);

        endTime = new Date();
        enddate = formatter.format(endTime);

        try {
            dateS = formatter.parse(stashDate);
            endTime = formatter.parse(enddate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Toast.makeText(this, "stash : " + stashDate, Toast.LENGTH_SHORT).show();

        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
        /* Call<List<Tweet>> tweetCall = twitterApiClient.getStatusesService().userTimeline(
                id, username, 100, null, null, false,
                false, false, true); */
        Log.d("List123", "inside Function");

        Call<List<Tweet>> tweetCall = twitterApiClient.getStatusesService().homeTimeline(170,
                null, null, false, true, true, true);

        tweetCall.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                for (int i = 0; i < result.data.size(); i++) {
                    Tweet tweet = result.data.get(i);

                    String date = null;

                    try {
                        date = new SimpleDateFormat("E, MMM dd yyyy, hh:mm aa", Locale.getDefault())
                                .format(new SimpleDateFormat("E MMM dd hh:mm:ss Z yyyy").parse(tweet.createdAt));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    try {
                        dateE = new SimpleDateFormat("E, MMM dd yyyy, hh:mm aa", Locale.getDefault()).parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    Log.d("List123", "dateE : " + dateE);
                    Log.d("List123", "dateS : " + dateS);
                    Log.d("List123", "enddate : " + endTime);

                    TweetModel model = new TweetModel(
                            tweet.id,
                            "@"+ tweet.user.screenName,
                            tweet.user.name,
                            tweet.user.email,
                            tweet.retweeted ? tweet.retweetedStatus.text : tweet.text,
                            date,
                            tweet.user.profileImageUrl,
                            tweet.extendedEntities.media.size() > 0 ? tweet.extendedEntities.media.get(0).mediaUrlHttps : "",
                            tweet.extendedEntities.media.size() > 0 ? tweet.extendedEntities.media.get(0).type : ""
                    );

                    database.mainDAO().insert(model);
//                        tweetList.clear();
                    Log.d("List123", "Working " + tweetList.size() + "  " + i);
                    tweetList.add(model);

                    if (dateE.compareTo(dateS) == 0 || (dateE.compareTo(dateS) > 0 && dateE.compareTo(endTime) < 0)) {
                        /*TweetModel model = new TweetModel(
                                tweet.id,
                                "@"+ tweet.user.screenName,
                                tweet.user.name,
                                tweet.user.email,
                                tweet.retweeted ? tweet.retweetedStatus.text : tweet.text,
                                date,
                                tweet.user.profileImageUrl,
                                tweet.extendedEntities.media.size() > 0 ? tweet.extendedEntities.media.get(0).mediaUrlHttps : "",
                                tweet.extendedEntities.media.size() > 0 ? tweet.extendedEntities.media.get(0).type : ""
                        );

                        database.mainDAO().insert(model);
//                        tweetList.clear();
                        Log.d("List123", "Working " + tweetList.size() + "  " + i);
                        tweetList.add(model);*/
                    }
                }

                Collections.reverse(tweetList);

                ArrayList<TweetModel> newList = new ArrayList<>(new HashSet<>(tweetList));

                FeedListAdapter adapter = new FeedListAdapter(FeedScreen.this, newList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                tweetList.clear();

            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onResume() {
        super.onResume();
        // tweetList.clear();
        refreshTweets();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}