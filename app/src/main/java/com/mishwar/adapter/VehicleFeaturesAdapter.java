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
public class VehicleFeaturesAdapter extends BaseAdapter {


    private Context context;
    private ArrayList<VehichleTypeBean> vfList;

    public VehicleFeaturesAdapter(Context context, ArrayList<VehichleTypeBean> vfList) {
        this.context = context;
        this.vfList = vfList;

    }

    @Override
    public int getCount() {
        return vfList.size();
    }

    @Override
    public Object getItem(int position) {
        return vfList.get(position);
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

        final VehichleTypeBean item = vfList.get(position);
      //  holder.AuthrName.setMovementMethod(new ScrollingMovementMethod());


        holder.tv_VFname.setText(item.getVehicleFacilityName());


        return row;
    }
    class ViewHolder {

       TextView tv_VFname;
    }
}
