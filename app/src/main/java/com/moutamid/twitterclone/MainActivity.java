package com.moutamid.twitterclone;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.AccountService;

import retrofit2.Call;


public class MainActivity extends AppCompatActivity {

    TwitterLoginButton twitterLoginButton;
    private SharedPreferencesManager manager;
    private boolean loginStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))//enable logging when app is in debug mode
                .twitterAuthConfig(new TwitterAuthConfig(getResources()
                        .getString(R.string.com_twitter_sdk_android_CONSUMER_KEY),
                        getResources().getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)))//pass the created app Consumer KEY and Secret also called API Key and Secret
                .debug(true)//enable debug mode
                .build();

        //finally initialize twitter with created configs
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
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getApplicationContext(),exception.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loginMethod(TwitterSession twitterSession){

      /*  authClient = new TwitterAuthClient();
        authClient.requestEmail(twitterSession, new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                // Do something with the result, which provides the email address
                String userName=result.data;
                manager.storeBoolean("login",true);
                Intent intent= new Intent(MainActivity.this,FeedScreen.class);
                intent.putExtra("username",userName);
                startActivity(intent);
                finish();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });*/
        manager.storeBoolean("login",true);
        manager.storeString("username",twitterSession.getUserName());
        manager.storeLong("userId",twitterSession.getUserId());
        Intent intent= new Intent(MainActivity.this,FeedScreen.class);
        intent.putExtra("username",twitterSession.getUserName());
        intent.putExtra("userId",twitterSession.getUserId());
        startActivity(intent);
        finish();
  /*      TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result to the login button.
        twitterLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loginStatus){
            String userName = manager.retrieveString("username","");
            long userId = manager.retrieveLong("userId",0);
            Intent intent= new Intent(MainActivity.this,FeedScreen.class);
            intent.putExtra("username",userName);
            intent.putExtra("userId",userId);
            startActivity(intent);
            finish();
        }
    }
}