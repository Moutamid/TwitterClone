package com.android.cts.clone;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.cts.clone.Model.TweetModel;
import com.android.cts.clone.database.RoomDB;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.util.ArrayList;

public class FragmentViewPager extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.detail_screen, container, false);


        return view;
    }

    public static FragmentViewPager newInstance(ArrayList<TweetModel> text, int pos) {

        FragmentViewPager f = new FragmentViewPager();
        Bundle b = new Bundle();
        b.putSerializable("list", text);
        b.putInt("pos", pos);

        f.setArguments(b);

        return f;
    }
}
