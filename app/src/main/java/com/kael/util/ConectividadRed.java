package com.kael.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by kael on 26/08/2016.
 */
public class ConectividadRed {
    private Activity activity;

    public ConectividadRed(Activity activity){
        this.activity = activity;
    }


    public Boolean isConnectedToWifi() {
        return isConnected(ConnectivityManager.TYPE_WIFI);
    }

    public Boolean isConnectedToRedMobile() {
        return isConnected(ConnectivityManager.TYPE_MOBILE);
    }


    private Boolean isConnected(int typeConnection){
        ConnectivityManager connectivity = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(typeConnection);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
