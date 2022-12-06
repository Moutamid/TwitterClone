package com.android.cts.clone;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.cts.clone.Adapters.SimpleViewPagerAdapter;
import com.android.cts.clone.Model.TweetModel;
import com.fxn.stash.Stash;

import java.util.ArrayList;

public class ViewPagerActivity  extends AppCompatActivity {
    ViewPager viewPager;
    private TweetModel model;
    ArrayList<TweetModel> list;
    int position;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);

        viewPager = findViewById(R.id.viewPager);

        list = Stash.getArrayList("List", TweetModel.class);
        position = Stash.getInt("position", 0);
        Log.d("position12", "Detail Screen : " + position);

        viewPager.setAdapter(new SimpleViewPagerAdapter(this, list));

    }
}
