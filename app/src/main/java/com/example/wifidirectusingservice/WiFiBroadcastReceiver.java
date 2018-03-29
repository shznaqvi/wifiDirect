package com.example.wifidirectusingservice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.ConnectionInfoListener;
import android.util.Log;


/**
 * A BroadcastReceiver that notifies of important wifi p2p events.
 */

public class WiFiBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager manager;
    private Channel mChannel;
    private Activity activity;


    public WiFiBroadcastReceiver(WifiP2pManager manager, Channel mChannel, Activity activity) {
        // TODO Auto-generated constructor stub
        super();
        this.manager = manager;
        this.mChannel = mChannel;
        this.activity = activity;
    }

    @Override
    public void onReceive(Context arg0, Intent intent) {
        String action = intent.getAction();
        Log.d(WiFiDirectUsingServiceActivity.TAG, action);
        if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            if (manager == null) {
                return;
            }

            NetworkInfo networkInfo = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {

                // we are connected with the other device, request connection
                // info to find group owner IP
                Log.d(WiFiDirectUsingServiceActivity.TAG, "Connected to p2p network. Requesting network details");
                manager.requestConnectionInfo(mChannel, (ConnectionInfoListener) activity);
            } else {
                // It's a disconnect
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

            WifiP2pDevice device = intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            Log.d(WiFiDirectUsingServiceActivity.TAG, "Device status -" + device.status);

        }
    }
}
