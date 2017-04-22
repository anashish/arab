package com.mishwar.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mishwar.R;
import com.mishwar.driver_ui.SendPaymentDetailsToPassengerActivity;
import com.mishwar.model.AllTrips;

import java.util.ArrayList;

public class TripMonthllyAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AllTrips> getCanceledTrip;

    public TripMonthllyAdapter(Context context, ArrayList<AllTrips> getRequests) {
        this.context = context;
        this.getCanceledTrip = getRequests;

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


        final AllTrips item = getCanceledTrip.get(position);
        //  holder.AuthrName.setMovementMethod(new ScrollingMovementMethod());

        holder.itemUserPickup.setText(" "+item.getPickupAddress());
        holder.itemUserDestination.setText(""+item.getDestinationAddress());
        holder.itemAllTripTime.setText(""+item.getRideStartTime());
        holder.itemAllTripDate.setText(""+item.getRideStartDate());
        holder.itemCanceledTripCost.setText(" "+" $ "+item.getTripTotal());
        holder.itemCanceledTripDistance.setText(" "+item.getDistance()+" km");

        final String mobileNo =item.getMobileNumber();

       /* if (fullName.equalsIgnoreCase("") || fullName.isEmpty()|| fullName.equals(null)){
            holder.itemUserName.setVisibility(View.GONE);
        }else {
            holder.itemUserName.setText("Name : "+item.getFullName());
        }

        if (mobileNo.equalsIgnoreCase("") || mobileNo.isEmpty() || fullName.equals(null)){
            holder.itemUserMobileNo.setVisibility(View.GONE);
        }else {
            holder.itemUserMobileNo.setText("Contact : "+mobileNo);
        }*/

        final String  PaymentStatus =  item.getRideStatus();

        if (PaymentStatus.equalsIgnoreCase("2")){
            holder.itemUserPaymentStatus.setText(""+"Pending");

        }else if (PaymentStatus.equalsIgnoreCase("6")){
            holder.itemUserPaymentStatus.setText(""+"Completed");

        }else if (PaymentStatus.equalsIgnoreCase("3")){
            holder.itemUserPaymentStatus.setText(" "+"Completed");
        }

        holder.itemUserBookingId.setText(""+item.getBookingId());
        holder.itemUserName.setText(""+item.getFullName());
        holder.itemUserMobileNo.setText(""+mobileNo);
        holder.itemUserTripType.setText("Trip monthly");


        return row;
    }
    class ViewHolder {

        TextView itemUserName,itemUserPickup,itemUserDestination,itemAllTripTime,itemAllTripDate,itemUserPaymentStatus,
                itemUserMobileNo,itemCanceledTripDistance,itemCanceledTripCost,itemUserBookingId,itemUserTripType;
    }
}
