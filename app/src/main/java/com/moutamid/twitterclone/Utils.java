package com.moutamid.twitterclone;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;


public class Utils {

	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager mConnectManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (mConnectManager != null) {
			NetworkInfo[] mNetworkInfo = mConnectManager.getAllNetworkInfo();
			for (int i = 0; i < mNetworkInfo.length; i++) {
				if (mNetworkInfo[i].getState() == NetworkInfo.State.CONNECTED)
					return true;
			}
		}

		// Toast.makeText(context, "Internet is not connected", Toast.LENGTH_SHORT).show();

		return false;
	}

}
