package com.android.cts.clone;

import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterSession;

public class MyTwitterApiClient extends TwitterApiClient {
    public MyTwitterApiClient(TwitterSession session) {
        super(session);
    }
    public Api verifyCredentials() {
        return getService(Api.class);
    }

    /**
     * Provide CustomService with defined endpoints
     */
    public Api getCustomService() {
        return getService(Api.class);
    }
}