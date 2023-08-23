package com.aapthitech.android.developers.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.aapthitech.android.developers.Utils.Template;

public class ConnectivityReceiver extends BroadcastReceiver {
    public static ConnectivityReceiverListener connectivityReceiverListener;

    public ConnectivityReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try{

            ConnectivityManager cm = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null
                    && activeNetwork.isConnectedOrConnecting();

            Log.d("checkconnection","ok");

            if (connectivityReceiverListener != null) {
                connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
            }

        }
        catch (Exception e){
            System.out.println("CheckConnectivity Exception: " + e.getMessage());
        }

    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) Template.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

}