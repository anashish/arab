package com.mishwar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mishwar.R;
import com.mishwar.model.VehichleTypeBean;
import com.mishwar.model.tripMonthlyInfo;

import java.util.ArrayList;

/**
 * Created by GST-10018 on 2/26/2016.
 */
public class TripMonthllyPlacesAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<tripMonthlyInfo> placesList;

    public TripMonthllyPlacesAdapter(Context context, ArrayList<tripMonthlyInfo> vtList) {
        this.context = context;
        this.placesList = vtList;

    }

    @Override
    public int getCount() {
        return placesList.size();
    }

    @Override
    public Object getItem(int position) {
        return placesList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = null;


        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.item_country_spinner, parent, false);

            holder = new ViewHolder();
        /*find layout components id*/
            holder.tvPlaces = (TextView) row.findViewById(R.id.text1);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final tripMonthlyInfo item = placesList.get(position);
        //if (position==0){
         //   holder.tvPlaces.setText("select place");
        //}else {
            holder.tvPlaces.setText(item.getPlaceName());
        //}

        return row;
    }
    class ViewHolder {

        TextView tvPlaces;
    }
}
