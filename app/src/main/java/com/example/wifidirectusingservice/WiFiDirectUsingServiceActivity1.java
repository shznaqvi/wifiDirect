package com.example.wifidirectusingservice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class WiFiDirectUsingServiceActivity1 extends Activity {

    public static final String TAG = "Wi-Fi Direct Demo";
    public static final String TXTRECORD_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "Wi-Fi Demo Test";
    public static final String SERVICE_TYPE = "_presence._tcp";

    private WifiP2pManager mWifiP2pManager;
    private Channel mChannel;
    private IntentFilter mIntentFilter = new IntentFilter();
    private BroadcastReceiver receiver = null;
    private WifiP2pDnsSdServiceRequest serviceRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mWifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(this, getMainLooper(), null);
        startRegistrationAndDiscovery();
    }

    private void startRegistrationAndDiscovery() {
        // TODO Auto-generated method stub
        Log.d(TAG, "startRegistrationAndDiscovery() Called");

        Map<String, String> record = new HashMap<String, String>();
        record.put("TXTRECORD_AVAILABLE", "visible");

        WifiP2pDnsSdServiceInfo serviceInfo = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_TYPE, record);
        mWifiP2pManager.addLocalService(mChannel, serviceInfo, new ActionListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Service Added", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Service Failed", Toast.LENGTH_SHORT).show();
            }
        });

        discoverService();
    }

    private void discoverService() {
        // TODO Auto-generated method stub
        Log.d(TAG, "discoverService() Called");
		
/*        @Override
        public void onDnsSdServiceAvailable(String instanceName,
                String registrationType, WifiP2pDevice srcDevice) {

            // A service has been discovered. Is this our app?
            if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                // update the UI and add the item the discovered
            	// device.
                WiFiDirectServicesList fragment = (WiFiDirectServicesList) getFragmentManager()
                        .findFragmentByTag("services");
                if (fragment != null) {
                    WiFiDevicesAdapter adapter = ((WiFiDevicesAdapter) fragment.getListAdapter());
                    WiFiP2pService service = new WiFiP2pService();
                    service.device = srcDevice;
                    service.instanceName = instanceName;
                    service.serviceRegistrationType = registrationType;
                    adapter.add(service);
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "onBonjourServiceAvailable "+ instanceName);
               }
            }

        }
    */

        DnsSdTxtRecordListener txtListener = new DnsSdTxtRecordListener() {
            @Override
            public void onDnsSdTxtRecordAvailable(String fullDomain,
                                                  Map<String, String> record, WifiP2pDevice device) {
                // TODO Auto-generated method stub

                Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
                // buddies.put(device.deviceAddress, record.get("buddyname"));
                Log.d(TAG, device.deviceName + " is " + record.get(TXTRECORD_AVAILABLE));
                
                /*String devName=device.deviceName;
                Toast.makeText(getApplicationContext(), ""+devName, Toast.LENGTH_SHORT);*/
            }
        };
        serviceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        Log.d(TAG, "addServiceRequest() Calling");
        mWifiP2pManager.addServiceRequest(mChannel, serviceRequest, new ActionListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d(TAG, "Added service discovery request");

            }

            @Override
            public void onFailure(int reason) {
                // TODO Auto-generated method stub
                Log.d(TAG, "Failed adding service discovery request");

            }
        });

        mWifiP2pManager.discoverServices(mChannel, new ActionListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Log.d(TAG, "Service discovery initiated");
            }

            @Override
            public void onFailure(int reason) {
                // TODO Auto-generated method stub
                Log.d(TAG, "Service discovery failed");
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        receiver = new WiFiBroadcastReceiver(mWifiP2pManager, mChannel, this);
        registerReceiver(receiver, mIntentFilter);
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(receiver);
    }

}