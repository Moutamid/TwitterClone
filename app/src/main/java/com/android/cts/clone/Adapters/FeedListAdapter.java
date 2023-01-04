package com.android.cts.clone.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.cts.clone.Model.TweetModel;
import com.android.cts.clone.R;
import com.android.cts.clone.ViewPagerActivity;
import com.fxn.stash.Stash;

import java.util.List;

public class FeedListAdapter extends RecyclerView.Adapter<FeedListAdapter.FeedViewHolder> {
    private static final String TAG = "BUGGY";

    private Context mContext;
    private List<TweetModel> userModelArrayList;

    public FeedListAdapter(Context mContext, List<TweetModel> modelArrayList) {
        this.mContext = mContext;
        this.userModelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_layout, parent, false);
        return new FeedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedViewHolder holder, @SuppressLint("RecyclerView") int position) {
        TweetModel model = userModelArrayList.get(holder.getAdapterPosition());
        Log.d(TAG, "onBindViewHolder: position: "+ holder.getAdapterPosition()+" "+ model.getMessage());

        /*boolean dd = Stash.getBoolean(String.valueOf(model.getId()), false);
        if (dd) {
            Log.d(TAG, "onBindViewHolder: positionTrue: "+ holder.getAdapterPosition()+" "+ model.getMessage());
            userModelArrayList.remove(holder.getAdapterPosition());
            notifyDataSetChanged();
            return;
        }*/

        holder.nameTxt.setText(model.getName());
        holder.timeTxt.setText(model.getCreated_at());
        holder.msgTxt.setText(model.getMessage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("success12", "onClick: "+model.getId());
                Intent intent = new Intent(mContext, ViewPagerActivity.class);
//                Stash.put("List", userModelArrayList);
                Stash.put("position", holder.getAdapterPosition());
                Log.d(TAG, "onClick: userModelArrayList.size(): "+userModelArrayList.size());
                Log.d(TAG, "onClick: final position: "+position);
                // 1500425778428846090
                // 1500425778428846090
                Log.d(TAG, "onClick: AdapterPosition: "+holder.getAdapterPosition());
                mContext.startActivity(intent);
                Log.d("position12", "Feed Adapter : " + holder.getAdapterPosition());
                //Stash.put("List", userModelArrayList);
                Stash.put("isStarted", true);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModelArrayList.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTxt, msgTxt, timeTxt;

        public FeedViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTxt = itemView.findViewById(R.id.title);
            msgTxt = itemView.findViewById(R.id.message);
            timeTxt = itemView.findViewById(R.id.time);
        }
    }
}
