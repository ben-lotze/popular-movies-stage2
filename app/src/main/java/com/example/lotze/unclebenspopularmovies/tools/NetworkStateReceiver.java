package com.example.lotze.unclebenspopularmovies.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.lotze.unclebenspopularmovies.tools.NetworkUtils;

/**
 * BroadcastReceiver offers Listener interface for Activities to get notified about changes in network state
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkStateReceiver";


    public NetworkStateReceiver(Context context, NetworkStateChangeListener listener) {
        this.listener = listener;

        previousStateConnectionAvailable = NetworkUtils.internetConnectionAvailable(context);

    }

    private NetworkStateChangeListener listener;
    public interface NetworkStateChangeListener {
        public void onNetworkStateChanged(boolean networkAvailable);
    }

    /** to prevent Bug when entering other Activities and returning
     * (which reloads movies because onReceive triggers too)
     */
    private boolean previousStateConnectionAvailable;


    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d(TAG, "Network state changed");

        if (intent.getExtras() != null) {
            final ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            final NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {

                if (!previousStateConnectionAvailable) {
                    listener.onNetworkStateChanged(true);
                    Log.i(TAG, "Network " + networkInfo.getTypeName() + " JUST connected");
                }
                else {
                    Log.i(TAG, "Network " + networkInfo.getTypeName() + " STILL connected");
                }
                previousStateConnectionAvailable = true;


            } else if (intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, Boolean.FALSE)) {


                if (previousStateConnectionAvailable) {
                    Log.d(TAG, "CHANGE: no network connectivity");
                    listener.onNetworkStateChanged(false);
                }
                else {
                    Log.d(TAG, "STILL no network connectivity");
                }
                previousStateConnectionAvailable = false;
            }
        }
    }
}
