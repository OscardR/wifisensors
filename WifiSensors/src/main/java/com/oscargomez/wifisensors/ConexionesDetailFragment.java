package com.oscargomez.wifisensors;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link com.oscargomez.wifisensors.ItemListActivity}
 * in two-pane mode (on tablets) or a {@link com.oscargomez.wifisensors.ItemDetailActivity}
 * on handsets.
 */
public class ConexionesDetailFragment extends Fragment {

    Context context;
    private boolean isWifi;

    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private MenuContent.MenuItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConexionesDetailFragment() {
    }

    public ConexionesDetailFragment(Context ctx) {
        context = ctx;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = MenuContent.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }

        Log.d("wifisensors", "context: " + context);

        if (context != null) {
            // La lógica de negocio del wifi
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo activeNetwork = connectivity.getActiveNetworkInfo();
            boolean isConnected = ((activeNetwork != null) &&
                    activeNetwork.isConnectedOrConnecting());
            isWifi = (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI);
        } else {
            Log.d("wifisensors", "No hay contexto!!!!");
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("wifisensors", isWifi ? "Wifi ON" : "WifiOff");
        View rootView = inflater.inflate(R.layout.fragment_conexiones_detail, container, false);

        TextView txtBanner = (TextView) rootView.findViewById(R.id.txtBanner);
        txtBanner.setText(isWifi ? "Wifi ON" : "WifiOff");
        Log.d("wifisensors", "Wifi is: " + isWifi);

        return rootView;
    }
}
