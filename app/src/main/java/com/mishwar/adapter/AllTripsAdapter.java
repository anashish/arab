package com.mishwar.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mishwar.R;
import com.mishwar.listner.CustomAdapterButtonListener;
import com.mishwar.model.AllTrips;

import java.util.ArrayList;

public class AllTripsAdapter extends BaseAdapter {

    private CustomAdapterButtonListener customButtonListener = null;
    private Context context;
    private ArrayList<AllTrips> getCanceledTrip;

    public AllTripsAdapter(Context context, ArrayList<AllTrips> getRequests) {
        this.context = context;
        this.getCanceledTrip = getRequests;

    }

    public void setCustomListener(CustomAdapterButtonListener customButtonListener){
        this.customButtonListener = customButtonListener;
    }

    @Override
    public int getCount() {
        return getCanceledTrip.size();
    }

    @Override
    public Object getItem(int position) {
        return getCanceledTrip.get(position);
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
            row = inflater.inflate(R.layout.item_all_tirps, parent, false);

            holder = new ViewHolder();
        /*find layout components id*/
            holder.itemUserName = (TextView) row.findViewById(R.id.itemUserName);
            holder.itemUserPickup = (TextView) row.findViewById(R.id.itemUserPickup);
            holder.itemUserDestination = (TextView) row.findViewById(R.id.itemUserDestination);
            // holder.itemCanceledTripTime = (TextView) row.findViewById(R.id.itemCanceledTripTime);
            //  holder.itemCanceledTripDate = (TextView) row.findViewById(R.id.itemCanceledTripDate);
            holder.itemUserMobileNo = (TextView) row.findViewById(R.id.itemUserMobileNo);
            holder.itemCanceledTripDistance = (TextView) row.findViewById(R.id.itemCanceledTripDistance);
            holder.itemCanceledTripCost = (TextView) row.findViewById(R.id.itemCanceledTripCost);
            holder.itemAllTripTime = (TextView) row.findViewById(R.id.itemAllTripTime);
            holder.itemAllTripDate = (TextView) row.findViewById(R.id.itemAllTripDate);
            holder.itemUserPaymentStatus = (TextView) row.findViewById(R.id.itemUserPaymentStatus);
            holder.itemUserBookingId = (TextView) row.findViewById(R.id.itemUserBookingId);
            holder.itemUserTripType = (TextView) row.findViewById(R.id.itemUserTripType);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

       /* holder.itemAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customButtonListener != null)
                    customButtonListener.onButtonClick(position, "1");
            }
        });*/

        final AllTrips item = getCanceledTrip.get(position);
        //  holder.AuthrName.setMovementMethod(new ScrollingMovementMethod());

        holder.itemUserName.setText(" "+item.getFullName());
        holder.itemUserPickup.setText(" "+item.getPickupAddress());
        holder.itemUserDestination.setText(" "+item.getDestinationAddress());
        holder.itemAllTripTime.setText(" "+item.getRideStartTime());
        holder.itemAllTripDate.setText(" "+item.getRideStartDate());
        holder.itemCanceledTripCost.setText(""+" $ "+item.getTripTotal());
        holder.itemCanceledTripDistance.setText(" "+item.getDistance()+" km");
        holder.itemUserMobileNo.setText(" "+item.getMobileNumber());
        holder.itemUserPaymentStatus.setText(" "+item.getPaymentStatus());
        holder.itemUserBookingId.setText(" "+item.getBookingId());
        String rideType = "";
        rideType =  item.getRideType();

        if (rideType.equals("TN")){
            holder.itemUserTripType.setText(" Trip now");
        }else  if (rideType.equals("TL")){
            holder.itemUserTripType.setText("  Trip later");
        }
        return row;
    }
    class ViewHolder {

        TextView itemUserName,itemUserPickup,itemUserDestination,itemAllTripTime,itemAllTripDate,itemUserPaymentStatus,itemCanceledTripTime,itemCanceledTripDate,
                itemUserMobileNo,itemCanceledTripDistance,itemCanceledTripCost,itemUserBookingId,itemUserTripType;
    }
}
