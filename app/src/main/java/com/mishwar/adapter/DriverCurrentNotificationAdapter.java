package com.mishwar.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.mishwar.R;
import com.mishwar.listner.CustomAdapterButtonListener;
import com.mishwar.model.GetDriverRequests;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by GST-10018 on 2/26/2016.
 */
public class DriverCurrentNotificationAdapter extends BaseAdapter {

    private CustomAdapterButtonListener customButtonListener = null;
    private Context context;
    private ArrayList<GetDriverRequests> getRequests;

    public DriverCurrentNotificationAdapter(Context context, ArrayList<GetDriverRequests> getRequests) {
        this.context = context;
        this.getRequests = getRequests;

    }

    public void setCustomListener(CustomAdapterButtonListener customButtonListener){
        this.customButtonListener = customButtonListener;
    }

    @Override
    public int getCount() {
        return getRequests.size();
    }

    @Override
    public Object getItem(int position) {
        return getRequests.get(position);
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
            row = inflater.inflate(R.layout.item_driver_allrequest, parent, false);

            holder = new ViewHolder();
        /*find layout components id*/
            holder.itemPassengerPickup = (TextView) row.findViewById(R.id.itemPassengerPickup);
            holder.itemPassengerDestination = (TextView) row.findViewById(R.id.itemPassengerDestination);
            holder.itemBookedTripTime = (TextView) row.findViewById(R.id.itemBookedTripTime);
            holder.itemBookedTripDate = (TextView) row.findViewById(R.id.itemBookedTripDate);
            holder.itemPassengerCount = (TextView) row.findViewById(R.id.itemPassengerCount);
            holder.itemPassengerName = (TextView) row.findViewById(R.id.itemPassengerName);
            holder.itemPassMobileNo = (TextView) row.findViewById(R.id.itemPassMobileNo);
            holder.itemRideType = (TextView) row.findViewById(R.id.itemRideType);
            holder.itemTripTotal = (TextView) row.findViewById(R.id.itemTripTotal);

            holder.itemAcceptBtn = (Button) row.findViewById(R.id.itemAcceptBtn);
            holder.itemCancelBtn = (Button) row.findViewById(R.id.itemCancelBtn);


            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        holder.itemAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customButtonListener != null)
                    customButtonListener.onButtonClick(position, "1");
            }
        });
        holder.itemCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (customButtonListener != null)
                    customButtonListener.onButtonClick(position, "2");
            }
        });

        final GetDriverRequests item = getRequests.get(position);
        //  holder.AuthrName.setMovementMethod(new ScrollingMovementMethod());
        String createdAt = item.getRideStartDate().toString();
        String rideStartTime = item.getRideStartTime().toString();
        String StartDate = createdAt;

        // String[] time = StartTime.split(":");
        String[] time = rideStartTime.split(":");
        String hours = time[0];
        String min = time[1];
        String finalTime = hours+":"+min;

        String tripDate = "";
        SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat writeFormat = new SimpleDateFormat("dd-MMM-yyyy");
        java.util.Date date;
        try {
            date = readFormat.parse(createdAt);
            StartDate = writeFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String Details = "Trip time - "+finalTime+", "+" Date - "+StartDate;

        holder.itemPassengerPickup.setText("Pickup Address : "+item.getPickupAddress());
        holder.itemPassengerDestination.setText("Desination Address : "+item.getDestinationAddress());
        holder.itemBookedTripTime.setText("Ride start time : "+finalTime);
        holder.itemBookedTripDate.setText("Ride start Date : "+StartDate);
        holder.itemPassengerCount.setText("No. of passenger : "+item.getPassengerCount());
        holder.itemPassengerName.setText("Passenger Name : "+item.getFullName());
        holder.itemPassMobileNo.setText("Contact : "+item.getMobileNumber());
        holder.itemTripTotal.setText("Trip total : "+item.getTripTotal());

        String rideType = item.getRideType();

        if (rideType.equals("TN")){
            holder.itemRideType.setText("Trip type  :  Trip now");
        }else  if (rideType.equals("TL")){
            holder.itemRideType.setText("Trip type  :  Trip later");
        }


        return row;
    }
    class ViewHolder {

        TextView itemPassengerPickup,itemPassengerDestination,itemTripTotal,itemRideType,itemBookedTripTime,itemBookedTripDate,itemPassengerCount,itemPassengerName,itemPassMobileNo;
        Button itemAcceptBtn,itemCancelBtn;
    }
}
