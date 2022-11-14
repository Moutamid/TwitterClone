package com.android.cts.clone;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.android.cts.clone.R;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;


public class MainActivity extends AppCompatActivity {

    TwitterLoginButton twitterLoginButton;
    private SharedPreferencesManager manager;
    private boolean loginStatus = false;

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        // finally initialize twitter with created configs
        // Twitter.initialize(this);

        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(new TwitterAuthConfig(getResources()
                        .getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                        getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))//pass the created app Consumer KEY and Secret also called API Key and Secret
                .debug(true)// enable debug mode
                .build();

        Twitter.initialize(config);
        setContentView(R.layout.activity_main);
        manager = new SharedPreferencesManager(MainActivity.this);
        loginStatus = manager.retrieveBoolean("login",false);
        twitterLoginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_btn);

        twitterLoginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;
                loginMethod(session);
                Log.d("TAG12", "Login Page Success");
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void loginMethod(TwitterSession twitterSession){

       /* TwitterAuthClient authClient = new TwitterAuthClient();
        authClient.requestEmail(twitterSession, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                // Do something with the result, which provides the email address
                String userName=result.data;
                manager.storeBoolean("login",true);
                Intent intent= new Intent(MainActivity.this, FeedScreen.class);
                intent.putExtra("username",userName);
                startActivity(intent);
                finish();
                Log.d("TAG123", "Running");
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });*/

        try {
            manager.storeBoolean("login",true);
            manager.storeString("username",twitterSession.getUserName());
            manager.storeLong("userId",twitterSession.getUserId());
            Intent intent= new Intent(MainActivity.this, FeedScreen.class);
            intent.putExtra("username",twitterSession.getUserName());
            intent.putExtra("userId", twitterSession.getUserId());
            // intent.putExtra("session", (Parcelable) twitterSession);
            startActivity(intent);
            finish();
        } catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

       /* TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        AccountService accountService = twitterApiClient.getAccountService();
        Call<User> call = accountService.verifyCredentials(true, true, true);
        call.enqueue(new Callback<com.twitter.sdk.android.core.models.User>() {
            @Override
            public void success(Result<com.twitter.sdk.android.core.models.User> userResult) {
                //here we go User details
                try {
                    User user = userResult.data;
                    String fullname = user.name;
                    long twitterID = user.id;
                    String userSocialProfile = user.profileImageUrl;
                    String userEmail = user.email;
                    String userFirstNmae = fullname.substring(0, fullname.lastIndexOf(" "));
                    String userLastNmae = fullname.substring(fullname.lastIndexOf(" "));
                    String userScreenName = user.screenName;

                    Intent intent= new Intent(MainActivity.this, FeedScreen.class);
                    intent.putExtra("username",fullname);
//                    intent.putExtra("userId",twitterSession.getUserId());
//                    intent.putExtra("session", (Parcelable) twitterSession);
                    startActivity(intent);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void failure(TwitterException exception) {
            }
        });*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loginStatus){
            String userName = manager.retrieveString("username","");
            long userId = manager.retrieveLong("userId",0);
            Intent intent= new Intent(MainActivity.this, FeedScreen.class);
            intent.putExtra("username",userName);
            intent.putExtra("userId",userId);
            startActivity(intent);
            finish();
        }
    }

}