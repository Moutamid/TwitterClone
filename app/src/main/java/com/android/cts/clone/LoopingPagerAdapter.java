package com.android.cts.clone;

import android.content.Intent;

import androidx.annotation.Nullable;

public interface LoopingPagerAdapter {

    void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

    int getRealCount();
}
