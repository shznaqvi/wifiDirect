package com.example.wifidirectusingservice;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.ActionListener;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.net.wifi.p2p.WifiP2pManager.DnsSdServiceResponseListener;
import android.net.wifi.p2p.WifiP2pManager.DnsSdTxtRecordListener;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class WiFiDirectUsingServiceActivity extends Activity {
    public static final String TAG = "wifidirectdemoService";
    // TXT RECORD properties
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_wifidemotest";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";
    static final int SERVER_PORT = 4545;
    private WifiP2pManager wifiP2pManager;
    private Channel channel;
    private IntentFilter intentFilter;
    private BroadcastReceiver receiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        wifiP2pManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);

        //Registers the application with Wi-Fi Framework
        channel = wifiP2pManager.initialize(this, getMainLooper(), null);

        startRegistrationAndDiscovery();

    }

    /**
     * Registers a local service and then initiates a service discovery
     */
    private void startRegistrationAndDiscovery() {
        // TODO Auto-generated method stub

        Map<String, String> record = new HashMap<String, String>();
        record.put("listenport", String.valueOf(SERVER_PORT));
        record.put("buddyname", "John Doe" + (int) (Math.random() * 1000));
        record.put("available", "visible");


        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_REG_TYPE, record);

        wifiP2pManager.addLocalService(channel, service, new ActionListener() {

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Added Local Service", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onFailure(int reason) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "Failed to Add Local Service", Toast.LENGTH_LONG).show();
                String errorMessage = "Wi-Fi Direct Failed:";

                switch (reason) {

                    case WifiP2pManager.BUSY:
                        errorMessage += "FrameWork busy.";
                        break;
                    case WifiP2pManager.ERROR:
                        errorMessage += "Internal Error.";
                        break;
                    case WifiP2pManager.P2P_UNSUPPORTED:
                        errorMessage += "Unsupported.";
                        break;
                    default:
                        errorMessage += "Unknown Error.";
                        break;
                }
                Log.d(TAG, errorMessage);

            }
        });
        discoverService();
    }

    private void discoverService() {
        // TODO Auto-generated method stub

        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */
		
		/*DnsSdTxtRecordListener txtListener=new DnsSdTxtRecordListener() {
			
			@Override
			public void onDnsSdTxtRecordAvailable(String fullDomain,
					Map<String, String> record, WifiP2pDevice device) {
				// TODO Auto-generated method stub
				 Log.d(TAG, "DnsSdTxtRecord available -" + record.toString());
	            // buddies.put(device.deviceAddress, record.get("buddyname"));
				 record.put(device.deviceAddress, record.get("buddyname"));
				
			}
		};*/


        wifiP2pManager.setDnsSdResponseListeners(channel,
                new DnsSdServiceResponseListener() {

                    @Override
                    public void onDnsSdServiceAvailable(String instanceName,
                                                        String registrationType, WifiP2pDevice srcDevice) {
                        // A service has been discovered. Is this our app?

                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {

                            Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
                        }


                    }
                }, new DnsSdTxtRecordListener() {
                    /**
                     * A new TXT record is available. Pick up the advertised
                     * buddy name.
                     */


                    @Override
                    public void onDnsSdTxtRecordAvailable(String fullDomain,
                                                          Map<String, String> record, WifiP2pDevice device) {
                        // TODO Auto-generated method stub
                        //Log.d(TAG,device.deviceName + " is "+ record.get(TXTRECORD_PROP_AVAILABLE));
                        Log.d(TAG, device.deviceName + " is " + record.get("BuddyName"));


                    }
                });


    }


    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        receiver = new WiFiBroadcastReceiver(wifiP2pManager, channel, null);
        registerReceiver(receiver, intentFilter);

    }


    /* (non-Javadoc)
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(receiver);
    }


}
