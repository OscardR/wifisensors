package com.oscargomez.wifisensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.oscargomez.wifisensors.ItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.oscargomez.wifisensors.ItemDetailActivity}
 * on handsets.
 */
public class ConexionesFragment extends ItemDetailFragment implements OnClickListener, OnCheckedChangeListener {

    private boolean isWifi;
    private boolean isConnected;

    /**
     * UI
     */
    private ToggleButton btnToggleWifi;
    private TextView txtBanner;
    private RelativeLayout layLista;

    /**
     * Conectividad
     */
    ConnectivityManager connectivity;
    NetworkInfo activeNetwork;
    WifiManager wifiManager;
    WifiMonitor wifiMonitor;

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

        // Managers varios
        connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiMonitor = new WifiMonitor();

        getActivity().registerReceiver(wifiMonitor, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = super.onCreateView(inflater, container,
                savedInstanceState);

        layLista = (RelativeLayout) rootView.findViewById(R.id.layLista);
        btnToggleWifi = (ToggleButton) rootView.findViewById(R.id.btnToggleWifi);
        txtBanner = (TextView) rootView.findViewById(R.id.txtBanner);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Setear este fragment como listener para el botón
        btnToggleWifi.setOnCheckedChangeListener(this);
        btnToggleWifi.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d("wifisensors", "onClick handler");
        switch (v.getId()) {
            case R.id.btnToggleWifi:
                onClickToggleWifi();
                break;
            default:
                Log.d("wifisensors", "sin handler");
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        makeToast("Botón " + (isChecked ? "ON" : "OFF"));
    }

    public void onClickToggleWifi() {
        toggleWifi();
    }

    private void toggleWifi() {
        if (!wifiManager.isWifiEnabled()) {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                wifiManager.setWifiEnabled(true);
                makeToast("Activando wifi");
            }
        } else {
            if (wifiManager.getWifiState() != WifiManager.WIFI_STATE_DISABLING) {
                wifiManager.setWifiEnabled(false);
                makeToast("Desactivando wifi...");
            }
        }
    }

    public class WifiMonitor extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                if (state == WifiManager.WIFI_STATE_ENABLED) {

                    // La lógica de negocio de las conexiones
                    activeNetwork = connectivity.getActiveNetworkInfo();

                    // Para saber si tenemos conexión, y si ésta es wifi
                    isConnected = ((activeNetwork != null) && activeNetwork.isConnectedOrConnecting());
                    if (isConnected)
                        isWifi = (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);

                    Log.d("wifisensors", "activeNetwork: " + ((activeNetwork != null) ? activeNetwork.toString() : "null"));
                }
                makeToast("El wifi está " + (isWifi && isConnected ? "ON" : "OFF"));
            }

            makeToast("Red activa: " + ((wifiManager.getConfiguredNetworks() != null) ?
                    wifiManager.getConfiguredNetworks().get(0).BSSID : "ninguna"), true);
        }
    }
}
