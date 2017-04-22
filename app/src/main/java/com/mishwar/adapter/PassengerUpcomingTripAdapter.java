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
import com.mishwar.model.AllTrips;
import com.mishwar.utils.AppUtility;

import java.util.ArrayList;

/**
 * Created by GST-10018 on 2/26/2016.
 */
public class PassengerUpcomingTripAdapter extends BaseAdapter {

    private CustomAdapterButtonListener customButtonListener = null;
    private Context context;
    private ArrayList<AllTrips> getCanceledTrip;

    public PassengerUpcomingTripAdapter(Context context, ArrayList<AllTrips> getRequests) {
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
            row = inflater.inflate(R.layout.item_upcoming_tirps, parent, false);

            holder = new ViewHolder();
        /*find layout components id*/
            holder.itemPassUpcomingUserName = (TextView) row.findViewById(R.id.itemPassUpcomingUserName);
            holder.itemPassUpcomingPickup = (TextView) row.findViewById(R.id.itemPassUpcomingPickup);
            holder.itemPassUpcomingDestination = (TextView) row.findViewById(R.id.itemPassUpcomingDestination);
            holder.itemUpcomingBookingId = (TextView) row.findViewById(R.id.itemUpcomingBookingId);
            // holder.itemCanceledTripTime = (TextView) row.findViewById(R.id.itemCanceledTripTime);
            //  holder.itemCanceledTripDate = (TextView) row.findViewById(R.id.itemCanceledTripDate);
            holder.itemPassUpcomingMobileNo = (TextView) row.findViewById(R.id.itemPassUpcomingMobileNo);
            holder.itemPassUpcomingTripDistance = (TextView) row.findViewById(R.id.itemPassUpcomingTripDistance);
            holder.itemPassUpcomingTripCost = (TextView) row.findViewById(R.id.itemPassUpcomingTripCost);
            holder.itemPassUpcomingTripTime = (TextView) row.findViewById(R.id.itemPassUpcomingTripTime);
            holder.itemPassUpcomingTripDate = (TextView) row.findViewById(R.id.itemPassUpcomingTripDate);
            holder.btnCancelUpcomingBookings = (Button) row.findViewById(R.id.btnCancelUpcomingBookings);

            row.setTag(holder);

        } else {
            holder = (ViewHolder) row.getTag();
        }

        final AllTrips item = getCanceledTrip.get(position);

        holder.btnCancelUpcomingBookings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.getRideStatus().equals("2") || item.getRideStatus().equals("6"))
                {
                    if (customButtonListener != null)
                    {
                        customButtonListener.onButtonClick(position, "1");
                    }
                }else {
                    AppUtility.showToast(context,"You can not cancel this ride now.",0);
                }

            }
        });

        //  holder.AuthrName.setMovementMethod(new ScrollingMovementMethod());

        holder.itemUpcomingBookingId.setText(" "+item.getBookingId());
        holder.itemPassUpcomingUserName.setText(" "+item.getFullName());
        holder.itemPassUpcomingPickup.setText(" "+item.getPickupAddress());
        holder.itemPassUpcomingDestination.setText(" "+item.getDestinationAddress());
        holder.itemPassUpcomingTripCost.setText(" "+" $ "+item.getTripTotal());
        holder.itemPassUpcomingTripDistance.setText(" "+item.getDistance()+" km");
        holder.itemPassUpcomingMobileNo.setText(" "+item.getMobileNumber());
        holder.itemPassUpcomingTripTime.setText(" "+item.getRideStartTime());
        holder.itemPassUpcomingTripDate.setText(" "+item.getRideStartDate());

        return row;
    }
    class ViewHolder {

        TextView itemPassUpcomingUserName,itemPassUpcomingPickup,itemUpcomingBookingId,itemPassUpcomingDestination,itemPassUpcomingTripTime,itemPassUpcomingTripDate,itemPassUpcomingMobileNo,itemPassUpcomingTripDistance,itemPassUpcomingTripCost;
        Button btnCancelUpcomingBookings;
    }
}
