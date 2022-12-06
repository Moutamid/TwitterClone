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

        //viewPager.setCurrentItem(position);
        SimpleViewPagerAdapter simpleViewPagerAdapter = new SimpleViewPagerAdapter(this, list, position);
        viewPager.setAdapter(simpleViewPagerAdapter);
        simpleViewPagerAdapter.notifyDataSetChanged();

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }
}
