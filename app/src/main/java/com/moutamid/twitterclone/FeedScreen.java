package com.moutamid.twitterclone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.moutamid.twitterclone.Adapters.FeedListAdapter;
import com.moutamid.twitterclone.Model.TweetModel;
import com.moutamid.twitterclone.Model.UserModel;
import com.moutamid.twitterclone.database.RoomDB;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.internal.TwitterSessionVerifier;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.TweetTimelineRecyclerViewAdapter;
import com.twitter.sdk.android.tweetui.UserTimeline;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;


public class FeedScreen extends AppCompatActivity {

    private RecyclerView recyclerView;
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

        /*if (Utils.isNetworkConnected(FeedScreen.this)) {
            getUserTweets();
        } else {
            Toast.makeText(this, "Internet is not connected", Toast.LENGTH_SHORT).show();
        }*/

        database = RoomDB.getInstance(this);

        List<TweetModel> list = database.mainDAO().getAll();

        if (Utils.isNetworkConnected(FeedScreen.this)) {
            getUserTweets();
        } else {
            Toast.makeText(this, "Internet is not connected", Toast.LENGTH_SHORT).show();
        }

        if (list.size() >= 1 && list != null){
            FeedListAdapter adapter = new FeedListAdapter(FeedScreen.this, list);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            if (Utils.isNetworkConnected(FeedScreen.this)) {
                getUserTweets();
            } else {
                Toast.makeText(this, "Internet is not connected", Toast.LENGTH_SHORT).show();
            }
        }

        /*UserTimeline userTimeline = new UserTimeline.Builder()
                .userId(id)//User ID of the user to show tweets for
                .screenName(username)//screen name of the user to show tweets for
                .includeReplies(false)//Whether to include replies. Defaults to false.
                .includeRetweets(false)//Whether to include re-tweets. Defaults to true.
                .maxItemsPerRequest(2)//Max number of items to return per request
                .build();

        adapter = new TweetTimelineRecyclerViewAdapter.Builder(FeedScreen.this)
                .setTimeline(userTimeline)//set the created timeline

                //action callback to listen when user like/unlike the tweet
                .setOnActionCallback(new Callback<Tweet>() {
                    @Override
                    public void success(Result<Tweet> result) {
                        //do something on success response
                        Toast.makeText(FeedScreen.this,""+ result.data.text,Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("msg",exception.getMessage());
                        //do something on failure response
                        Toast.makeText(FeedScreen.this,"dfd "+exception.getMessage(),Toast.LENGTH_LONG).show();
                    }
                })
                //set tweet view style
                .setViewStyle(com.twitter.sdk.android.R.style.tw__TweetLightStyle)
                .build();

        //finally set the created adapter to recycler view
        recyclerView.setAdapter(adapter); */
    }

    private void getUserTweets() {
        TwitterApiClient twitterApiClient =  TwitterCore.getInstance().getApiClient(session);
        Call<List<Tweet>> tweetCall = twitterApiClient.getStatusesService().userTimeline(
                id, username, 100, null, null, false,
                false, false, true);
        tweetCall.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void success(Result<List<Tweet>> result) {
                for (int i = 0; i < result.data.size(); i++) {
                    Tweet tweet = result.data.get(i);
                    TweetModel model = new TweetModel();
                    model.setId(tweet.id);
                    model.setName(tweet.user.screenName);
                    model.setUsername(tweet.user.name);
                    model.setEmail(tweet.user.email);
                    model.setProfile_image_url(tweet.user.profileImageUrl);
                    model.setMessage(tweet.text);
                    model.setCreated_at(tweet.createdAt);
                    if (tweet.entities.media.size() >= 1){
                        model.setImageUrl(tweet.entities.media.get(0).mediaUrl);
                    } else {
                        model.setImageUrl("");
                    }
                    database.mainDAO().insert(model);
                    tweetList.clear();
                    tweetList.addAll(database.mainDAO().getAll());
                }

                FeedListAdapter adapter = new FeedListAdapter(FeedScreen.this, tweetList);
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(getApplicationContext(), exception.getMessage().toString(), Toast.LENGTH_SHORT).show();
                exception.printStackTrace();
            }
        });
    }


    private void getTweets(long id, String token) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,"https://api.twitter.com/2/users/:id/tweets?since_id="+id ,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //hiding the progressbar after completion
                        try {
                            //getting the whole json object from the response
                            JSONObject obj = new JSONObject(response);
                            //   String email = obj.getString("to");
                             Log.d("msg",obj.getString("created_at"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //    Log.d("error",error.getMessage());
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", String.format("Bearer %s", token));
                return params;
            }
        };

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

}