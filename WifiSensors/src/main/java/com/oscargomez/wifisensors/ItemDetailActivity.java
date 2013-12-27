package com.oscargomez.wifisensors;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;

/**
 * An activity representing a single Item detail screen. This
 * activity is only used on handset devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link ItemListActivity}.
 * <p/>
 * This activity is mostly just a 'shell' activity containing nothing
 * more than a {@link ItemDetailFragment}.
 */
public class ItemDetailActivity extends FragmentActivity {

    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        // Show the Up button in the action bar.
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.d("wifisensors", "No hay ActionBar");
        }

        // When there is fragment state saved from previous configurations
        // of this activity (e.g. when rotating the screen from portrait
        // to landscape) the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(
                    ItemDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID)
            );

            Log.d("wifisensors", "item_id: " + getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            MenuContent.MenuItem mItem = MenuContent.ITEM_MAP.get(getIntent().getStringExtra(ItemDetailFragment.ARG_ITEM_ID));
            if (mItem != null) {
                // Inicializar el Fragment adecuado;
                Context ctx = getApplicationContext();

                if (mItem.clss.equals("SensoresFragment")) {
                    fragment = new SensoresFragment(ctx);
                } else if (mItem.clss.equals("ConexionesFragment")) {
                    fragment = new ConexionesFragment(ctx);
                } else { // ItemDetailFragment: default dummy fragment
                    fragment = new ItemDetailFragment();
                }

                // Pasarle los argumentos y cargarlo
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.item_detail_container, fragment)
                        .commit();
            } else {
                Log.d("wifisensor", "No hay mItem!!!");
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button
                NavUtils.navigateUpTo(this, new Intent(this, ItemListActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Callback para el fragment
     * @param view
     */
//    public void onClickToggleWifi(View view) {
//        Log.d("wifisensors", "ItemDetailActivity.onClickToggleWifi");
//        ((ConexionesFragment) fragment).onClickToggleWifi(view);
//    }
}
