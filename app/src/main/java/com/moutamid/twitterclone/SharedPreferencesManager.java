package com.moutamid.twitterclone;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.moutamid.twitterclone.Model.UserModel;

import java.io.IOException;
import java.util.ArrayList;

import twitter4j.User;

public class SharedPreferencesManager {
    /**
     * SharedPreferences to store the settings. This way, they'll be available next time the user starts the app
     */
    private final SharedPreferences sPreferences;
    private SharedPreferences.Editor mPrefEditor;
    private final Context context;

    private static final String APP_PREFS_NAME = "TrivialAPP";

    //private SharedPreferences.Editor sEditor;

    ArrayList<UserModel> currentList;

    public SharedPreferencesManager(Context context) {
        this.context = context;
        sPreferences = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
        this.mPrefEditor = sPreferences.edit();
    }

    private SharedPreferences.Editor getEditor() {
        return sPreferences.edit();
    }

    /**
     * Store a boolean value in sharedPreferences
     *
     * @param tag   identifies the value
     * @param value the value itself
     */

    public void storeBoolean(String tag, boolean value) {
        mPrefEditor.putBoolean(tag, value);
        mPrefEditor.commit();
    }

    /**
     * Store a string in sharedPreferences
     *
     * @param tag identifies the value
     * @param str the string itself
     */


    public void storeString(String tag, String str) {
        mPrefEditor.putString(tag, str);
        mPrefEditor.commit();
    }

    public void storeLong(String tag, long str) {
        mPrefEditor.putLong(tag, str);
        mPrefEditor.commit();
    }

    public void storeDouble(String tag, float str) {
        mPrefEditor.putFloat(tag, str);
        mPrefEditor.commit();
    }

    public void storeInt(String tag, int value) {
        mPrefEditor.putInt(tag, value);
        mPrefEditor.commit();
    }

    public void storeTweets(String tag, UserModel list){
        if (currentList == null){
            currentList = new ArrayList<UserModel>();
        }
        currentList.add(list);
        try{
            mPrefEditor.putString(tag, ObjectSerializer.serialize(currentList));
        } catch (Exception e){
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        mPrefEditor.commit();
    }

    /**
     * @param tag      identifies the value
     * @param defValue default value
     * @return the stored or default value
     */

    public boolean retrieveBoolean(String tag, boolean defValue) {
        return sPreferences.getBoolean(tag, defValue);

    }
    public int retrieveInt(String tag, int defValue) {
        return sPreferences.getInt(tag, defValue);

    }
    public float retrieveDouble(String tag, float defValue) {
        return sPreferences.getFloat(tag, defValue);

    }
    public long retrieveLong(String tag, long defValue) {
        return sPreferences.getLong(tag, defValue);

    }
    /**
     * @param tag    identifies the string
     * @param defStr default string
     * @return the stored or default string
     */

    public String retrieveString(String tag, String defStr) {
        return sPreferences.getString(tag, defStr);
    }

    public ArrayList<UserModel> retrieveTweets(String tag, UserModel defValue){
        if (null == currentList) {
            currentList = new ArrayList<>();
        }

        try {
            currentList = (ArrayList<UserModel>) ObjectSerializer.deserialize(sPreferences.getString("Tweets", ObjectSerializer.serialize(new ArrayList<UserModel>())));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return currentList;
    }

}
//Incorrect Bracket Closing Removal.