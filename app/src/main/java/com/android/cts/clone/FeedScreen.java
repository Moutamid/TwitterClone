package com.android.cts.clone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.cts.clone.R;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.twitter.sdk.android.core.internal.TwitterApi;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;


public class FeedScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageButton refresh;
    private TextView emailTxt;
    private String username;
    private SharedPreferencesManager sharedPref;
    private String JSON_URL = "https://api.twitter.com/2/users/%s/tweets";
    private long id;
    RoomDB database;
    private ArrayList<TweetModel> tweetList = new ArrayList<>();
    private String bearerToken = "AAAAAAAAAAAAAAAAAAAAAHLUiQEAAAAATcIyY%2BxekJ5M7R%2FpDLSiBvr8N6E%3DgxSzwJvDlqkyL0k0fs7i1eDkwcYpytc42GhDs8MB6GNtVFBQMC";
    private TweetTimelineRecyclerViewAdapter adapter;
    private TwitterSession session;
    List<TweetModel> list;
    SimpleDateFormat formatter;
    Date sessionTime, dateE, dateS, endTime;
    String s, stashDate, enddate;

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
        } catch (Exception e){
            e.printStackTrace();
        }

        formatter  = new SimpleDateFormat("E, MMM dd yyyy, hh:mm aa");

        if (se.isEmpty() || se == null){
            sessionTime = new Date();
            s = formatter.format(sessionTime);
            Stash.put("loginSession", s);
            Log.d("List123", "Stash S : " + s);
        }

        MyApplication.getInstance().setOnVisibilityChangeListener(new MyApplication.ValueChangeListener() {
            @Override
            public void onChanged(Boolean value) {
                Log.d("isAppInBackground", String.valueOf(value));
                if(value){
                    sessionTime = new Date();
                    s = formatter.format(sessionTime);
                    Stash.put("loginSession", s);
                }
            }
        });

        sharedPref = new SharedPreferencesManager(FeedScreen.this);

        emailTxt = findViewById(R.id.email);
        username = getIntent().getStringExtra("username");
        id = getIntent().getLongExtra("userId",0);
        LinearLayoutManager manager = new LinearLayoutManager(FeedScreen.this);
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(manager);
        session = TwitterCore.getInstance().getSessionManager().getActiveSession();
        emailTxt.setText(username);
        Log.d("token",""+id);

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

        TwitterApiClient twitterApiClient =  TwitterCore.getInstance().getApiClient(session);
       /* Call<List<Tweet>> tweetCall = twitterApiClient.getStatusesService().userTimeline(
                id, username, 100, null, null, false,
                false, false, true);*/
        Log.d("List123", "inside Function");

        Call<List<Tweet>> tweetCall = twitterApiClient.getStatusesService().homeTimeline(170,
                null, null, false, true, true, true);

      /*  Call<List<Tweet>> tweetCall = twitterApiClient.getListService().statuses(id, "slug", username, id, null, null, 100, true, true);
*/

        tweetCall.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                for (int i = 0; i < result.data.size(); i++) {
                    Tweet tweet = result.data.get(i);
                    TweetModel model = new TweetModel();

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

                    if (dateE.compareTo(dateS) == 0 || (dateE.compareTo(dateS) > 0 && dateE.compareTo(endTime) < 0)) {
                        model.setId(tweet.id);
                        model.setName("@" + tweet.user.screenName);
                        model.setUsername(tweet.user.name);
                        model.setEmail(tweet.user.email);
                        model.setProfile_image_url(tweet.user.profileImageUrl);
                        model.setMessage(tweet.text);
                        model.setCreated_at(date);

                        if (tweet.extendedEntities.media.size() > 0) {
                            model.setContentType(tweet.extendedEntities.media.get(0).type);
                            model.setPublicImageUrl(tweet.extendedEntities.media.get(0).mediaUrlHttps);
                        }
                        database.mainDAO().insert(model);
                        Log.d("List123", "Working " + tweetList.size() + "  " + i);
                        tweetList.add(model);
                    }
                }
                Collections.reverse(tweetList);

                FeedListAdapter adapter = new FeedListAdapter(FeedScreen.this, tweetList);
                recyclerView.setAdapter(adapter);
                adapter.notifyItemInserted(tweetList.size()-1);
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
       });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onResume() {
        super.onResume();
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