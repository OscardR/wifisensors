package com.oscargomez.wifisensors;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by oscar on 29/10/13.
 */
public class WifiListAdapter extends BaseAdapter {

    private List<WifiConfiguration> listData;

    private LayoutInflater layoutInflater;

    public WifiListAdapter(Context context, List<WifiConfiguration> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.wifi_list_item, null);
            holder = new ViewHolder();
            holder.networkSSID = (TextView) convertView.findViewById(R.id.txtSSID);
            holder.networkType = (TextView) convertView.findViewById(R.id.txtType);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.networkSSID.setText(listData.get(position).SSID);
        holder.networkType.setText(listData.get(position).networkId);

        return convertView;
    }

    static class ViewHolder {
        TextView networkSSID;
        TextView networkType;
    }
}
