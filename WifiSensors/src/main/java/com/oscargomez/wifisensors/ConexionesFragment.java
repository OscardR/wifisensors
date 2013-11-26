package com.oscargomez.wifisensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.oscargomez.wifisensors.ItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.oscargomez.wifisensors.ItemDetailActivity}
 * on handsets.
 */
public class ConexionesFragment extends Fragment {

    Context context;
    private boolean isWifi;
    private boolean isConnected;
    private ToggleButton btnToggleWifi;
    private TextView txtBanner;

    ConnectivityManager connectivity;
    NetworkInfo activeNetwork;
    WifiManager wifiManager;
    WifiMonitor wifiMonitor;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The content this fragment is presenting.
     */
    private MenuContent.MenuItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConexionesFragment() {
    }

    public ConexionesFragment(Context ctx) {
        context = ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = MenuContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }

        connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiMonitor = new WifiMonitor();
        getActivity().registerReceiver(wifiMonitor, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        Log.d("wifisensors", "context: " + context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_conexiones_detail, container, false);

        txtBanner = (TextView) rootView.findViewById(R.id.txtBanner);
        txtBanner.setText(isWifi ? "Wifi ON" : "Wifi Off");

        btnToggleWifi = (ToggleButton) rootView.findViewById(R.id.btnToggleWifi);
        //updateView();

        return rootView;
    }

    public void onClickToggleWifi(View view) {
        conectarWifi();

        Log.d("wifisensors", "Wifi is: " + (isWifi ? "ON" : "OFF"));
    }

    private void conectarWifi() {
        if (!wifiManager.isWifiEnabled()) {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                wifiManager.setWifiEnabled(true);
                Log.v("wifisensors", "Activando wifi...");
            }
        } else {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLING) {
                wifiManager.setWifiEnabled(false);
                Log.v("wifisensors", "Desactivando wifi...");
            }
        }
    }

    private void updateView() {
        Log.d("wifisensors", "llamada a updateView()");
        btnToggleWifi.setChecked(isWifi);
        txtBanner.setText(isWifi ? "Wifi ON" : "Wifi Off");
    }

    public class WifiMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // La lógica de negocio de las conexiones
            activeNetwork = connectivity.getActiveNetworkInfo();

            // Para saber si tenemos conexión, y si ésta es wifi
            isConnected = ((activeNetwork != null) && activeNetwork.isConnectedOrConnecting());
            if (isConnected) isWifi = (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);

            Toast.makeText(context, "Red activa: " + ((wifiManager.getConfiguredNetworks() != null) ? wifiManager.getConfiguredNetworks().get(0).toString() : "ninguna"), Toast.LENGTH_LONG).show();

            Log.d("wifisensors", "activeNetwork: " + ((activeNetwork != null) ? activeNetwork.toString() : "null"));

            updateView();
        }
    }
}
