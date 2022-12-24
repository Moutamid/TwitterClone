package com.android.cts.clone;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.cts.clone.Adapters.SimpleViewPagerAdapter;
import com.android.cts.clone.Model.TweetModel;
import com.android.cts.clone.database.RoomDB;
import com.fxn.stash.Stash;

import java.util.List;

public class ViewPagerActivity extends AppCompatActivity {
    private static final String TAG = "BUGGY";
    ViewPager viewPager;
    RoomDB database;
    private TweetModel model;
    List<TweetModel> list;
    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_screen);

        viewPager = findViewById(R.id.viewPager);

        database = RoomDB.getInstance(this);

        list = Stash.getArrayList("List", TweetModel.class);
        position = Stash.getInt("position", 0);

        Log.d(TAG, "onCreate: listSize: "+list.size());
        Log.d(TAG, "onCreate: position: "+position);
        Log.d("position12", "Detail Screen : " + position);

        SimpleViewPagerAdapter simpleViewPagerAdapter = new SimpleViewPagerAdapter(this, list, position, database);
        viewPager.setAdapter(simpleViewPagerAdapter);
        simpleViewPagerAdapter.notifyDataSetChanged();
        Log.d(TAG, "onCreateAfterViewpagerAdapterSet: listSize: "+list.size());
        viewPager.setCurrentItem(position);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected: position: "+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Stash.put("isStarted", false);
    }
}
