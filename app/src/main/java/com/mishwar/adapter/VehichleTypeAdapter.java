package com.mishwar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mishwar.R;
import com.mishwar.model.VehichleTypeBean;

import java.util.ArrayList;

/**
 * Created by gst-10096 on 30/5/16.
 */
public class VehichleTypeAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<VehichleTypeBean> list;

    public VehichleTypeAdapter(Context context, ArrayList<VehichleTypeBean> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
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
            row = inflater.inflate(R.layout.item_vehichletype, parent, false);

            holder = new ViewHolder();
        /*find layout components id*/
            holder.iv_vehichletype_items = (ImageView) row.findViewById(R.id.iv_vehichletype_items);
            holder.vehichleName = (TextView) row.findViewById(R.id.vehichleName);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        VehichleTypeBean item = list.get(position);


        holder.vehichleName.setText(item.getCarName());
        Glide.with(context).load(item.getVehichleType_Url()).into(holder.iv_vehichletype_items);

      /*  if(pickup!=null && destination!=null){
            lat1 = pickup.latitude;
            lng1 = pickup.longitude;
            lat2 = destination.latitude;
            lng2 = destination.longitude;

            calDistance(lat1,lng1,lat2,lng2,"K");
        }*/

        // holder.iv_oldpostActivity_postitems.setImageResource(R.drawable.my_post_image_box);


        /*holder.iv_oldpostActivity_postitems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Home1Activity.class));
            }
        });*/


        return row;
    }
    class ViewHolder {
        ImageView iv_vehichletype_items;
        TextView vehichleName;
    }
}
