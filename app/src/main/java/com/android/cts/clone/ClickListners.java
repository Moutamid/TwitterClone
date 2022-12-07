package com.android.cts.clone;

import com.android.cts.clone.Model.TweetModel;

public interface ClickListners {
    void copy(String text);
    void delete(TweetModel model);
    String translate(TweetModel model);
    void download(TweetModel model);
}
