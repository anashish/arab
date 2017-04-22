package com.mishwar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mishwar.R;
import com.mishwar.model.VehichleTypeBean;

import java.util.ArrayList;

/**
 * Created by GST-10018 on 2/26/2016.
 */
public class DriverVehicleTypeAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<VehichleTypeBean> vtList;

    public DriverVehicleTypeAdapter(Context context, ArrayList<VehichleTypeBean> vtList) {
        this.context = context;
        this.vtList = vtList;

    }

    @Override
    public int getCount() {
        return vtList.size();
    }

    @Override
    public Object getItem(int position) {
        return vtList.get(position);
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
            holder.tv_VFname = (TextView) row.findViewById(R.id.text1);


            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final VehichleTypeBean item = vtList.get(position);
      //  holder.AuthrName.setMovementMethod(new ScrollingMovementMethod());


        holder.tv_VFname.setText(item.getCarName());


        return row;
    }
    class ViewHolder {

       TextView tv_VFname;
    }
}
