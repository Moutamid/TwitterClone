package com.android.cts.clone.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cts.clone.R;
import com.android.cts.clone.DetailsScreen;
import com.android.cts.clone.Model.TweetModel;
import com.android.cts.clone.ViewPagerActivity;
import com.fxn.stash.Stash;

import java.util.ArrayList;
import java.util.List;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedViewHolder>{

    private Context mContext;
    private List<TweetModel> userModelArrayList;

    public FeedListAdapter(Context mContext,List<TweetModel> modelArrayList){
        this.mContext = mContext;
        this.userModelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_layout,parent,false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TweetModel model = userModelArrayList.get(holder.getAdapterPosition());

        holder.nameTxt.setText(model.getName());
        holder.timeTxt.setText(model.getCreated_at());
        holder.msgTxt.setText(model.getMessage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ViewPagerActivity.class);
                Stash.put("List", userModelArrayList);
                Stash.put("position", holder.getAdapterPosition());
                mContext.startActivity(intent);
                Log.d("position12", "Feed Adapter : " + holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModelArrayList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder{

        private TextView nameTxt,msgTxt,timeTxt;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.title);
            msgTxt = itemView.findViewById(R.id.message);
            timeTxt = itemView.findViewById(R.id.time);
        }
    }
}
