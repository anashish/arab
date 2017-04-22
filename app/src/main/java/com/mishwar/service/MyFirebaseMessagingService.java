package com.mishwar.service;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mishwar.R;
import com.mishwar.commun_ui.RoleActivity;
import com.mishwar.driver_fregments.DriverCancelBookingsFragment;
import com.mishwar.driver_fregments.DriverCurrentMapFragment;
import com.mishwar.driver_fregments.DriverPriviousTripFragment;
import com.mishwar.driver_fregments.DriverUpcomingTripFragment;
import com.mishwar.driver_ui.DriverHomeActivity;
import com.mishwar.driver_ui.DriverMyBookingsActivity;
import com.mishwar.driver_ui.DriverProfileActivity;
import com.mishwar.driver_ui.ShowMonthllyTripsActivity;
import com.mishwar.driver_ui.SingleNotificationActivity;
import com.mishwar.commun_ui.TripMonthllyNotificationActivity;
import com.mishwar.passanger_ui.PassengerPaymentActivity;
import com.mishwar.passenger_fragments.CurrentMapFragment;
import com.mishwar.passenger_fragments.PassengerCancelBookingsFragment;
import com.mishwar.passenger_fragments.PassengerPreviousTripFragment;
import com.mishwar.passenger_fragments.PassengerUpcomingTripFragment;
import com.mishwar.model.DriverRequestNotifications;
import com.mishwar.passanger_ui.HomeActivity;
import com.mishwar.passanger_ui.PassengerMyBookingsActivity;
import com.mishwar.session.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mindiii on 6/10/16.
 */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    private SessionManager sessionManager;
    String notificationMsg="",notificationTitle="",notificationType="",userType="";
    private List<DriverRequestNotifications> driverRequestNotifications = new ArrayList<DriverRequestNotifications>();;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        sessionManager = new SessionManager(getApplicationContext());
        driverRequestNotifications.clear();

        if (remoteMessage == null)
            return;

        if(remoteMessage.getNotification()!=null){
            String eventTitle =  remoteMessage.getNotification().getTitle();
            String eventDesc =  remoteMessage.getNotification().getBody();
            handleNotification(0, remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getTitle());
        }

        if(remoteMessage.getData().containsKey("title") || remoteMessage.getData().containsKey("message")){
            Log.e("data values",remoteMessage.getData().toString());

            notificationMsg =  remoteMessage.getData().get("message").toString();
            notificationTitle =  remoteMessage.getData().get("title").toString();

            if (remoteMessage.getData().containsKey("userType") && remoteMessage.getData().containsKey("notifyType")){
                userType =  remoteMessage.getData().get("userType").toString();
                notificationType =  remoteMessage.getData().get("notifyType").toString();
            }

            System.out.println("notificationMsg--------------"+notificationMsg);
            System.out.println("notification title --------------"+notificationTitle);

            handleNotification(0,notificationTitle, notificationTitle);
        }
    }

    /*------------------------------------------------------------------------------------------------------------------------------*/
    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void handleNotification(int id,String title, String messageBody) {
        if(sessionManager.isLoggedIn()){

            if (sessionManager.getUserType().equals("driver")){

                if (notificationType.equalsIgnoreCase("approval")) {
                    try {
                        JSONObject object = new JSONObject(notificationMsg);
                        if (object.has("approval")) {
                            String  approval = object.getString("approval").trim();

                            if (notificationTitle.equalsIgnoreCase("Your identity proof is approved")){
                                sessionManager.setIdentityProofStatus("1");

                            }else if (notificationTitle.equalsIgnoreCase("Your licence number is approved")){
                                sessionManager.setLicenceNumberStatus("1");
                            }

                        }
                        Intent aprovalIntent = new Intent(this, DriverHomeActivity.class);

                        if(isRunningInForeground())
                        {
                            aprovalIntent.putExtra("notificationTitle", notificationTitle);
                            setNotificationSound();
                            aprovalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(aprovalIntent);
                        }
                        else if (!isRunningInForeground()){
                            aprovalIntent.putExtra("directCallFromFCM", "true");
                            aprovalIntent.putExtra("notificationTitle", notificationTitle);
                            setNotificationSound();
                            aprovalIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            aprovalIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            PendingIntent approvalPendingIntent = PendingIntent.getActivity(this, 0, aprovalIntent, PendingIntent.FLAG_ONE_SHOT);
                            onReceiveNotification(getApplicationContext(), title, approvalPendingIntent);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                else if (notificationType.equalsIgnoreCase("request"))
                {
                    try {
                        JSONObject object = new JSONObject(notificationMsg);
                        String destinationLatLong="",pickupAddress="",rideStatus="",rideStartTime="",pickupLatLong="",rideStartDate="",bookingId="",
                                fullName="", mobileNumber="",price="",distance="",destinationAddress="",paymentType = "";

                        if (notificationType.equalsIgnoreCase("request"))
                        {
                            if (object.has("bookingId")) {
                                destinationLatLong = object.getString("destinationLatLong").trim();
                                destinationAddress = object.getString("destinationAddress").trim();
                                pickupAddress = object.getString("pickupAddress").trim();
                                rideStatus = object.getString("rideStatus").trim();
                                rideStartTime = object.getString("rideStartTime").trim();
                                pickupLatLong = object.getString("pickupLatLong").trim();
                                rideStartDate = object.getString("rideStartDate").trim();
                                bookingId = object.getString("bookingId").trim();
                                fullName = object.getString("fullName").trim();
                                mobileNumber = object.getString("mobileNumber").trim();
                                price = object.getString("price").trim();
                                distance = object.getString("distance").trim();

                                if (object.has("paymentType")) {
                                    paymentType = object.getString("paymentType").trim();
                                    sessionManager.setPaymentType(paymentType);
                                }

                                if(isRunningInForeground())
                                {
                                    if(!pickupAddress.equals("") && !destinationAddress.equals("")){
                                        //  if(!pickupAddress.equals("")) {
                                        Intent driverIntent = new Intent(this, SingleNotificationActivity.class);
                                        driverIntent.putExtra("bookingId", bookingId);
                                        driverIntent.putExtra("pickupAddress", pickupAddress);
                                        driverIntent.putExtra("destinationAddress", destinationAddress);
                                        driverIntent.putExtra("rideStartTime", rideStartTime);
                                        driverIntent.putExtra("rideStartDate", rideStartDate);
                                        driverIntent.putExtra("mobileNumber", mobileNumber);
                                        driverIntent.putExtra("fullName", fullName);
                                        driverIntent.putExtra("price", price);
                                        driverIntent.putExtra("distance", distance);
                                        driverIntent.putExtra("paymentType", paymentType);
                                        setNotificationSound();
                                        driverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        driverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                        setNotificationSound();
                                        startActivity(driverIntent);

                                    }
                                }else if(!isRunningInForeground()){
                                    //  if(!pickupAddress.equals("")) {
                                    Intent driverIntent = new Intent(this, SingleNotificationActivity.class);
                                    driverIntent.putExtra("bookingId", bookingId);
                                    driverIntent.putExtra("pickupAddress", pickupAddress);
                                    driverIntent.putExtra("destinationAddress", destinationAddress);
                                    driverIntent.putExtra("rideStartTime", rideStartTime);
                                    driverIntent.putExtra("rideStartDate", rideStartDate);
                                    driverIntent.putExtra("mobileNumber", mobileNumber);
                                    driverIntent.putExtra("fullName", fullName);
                                    driverIntent.putExtra("price", price);
                                    driverIntent.putExtra("distance", distance);
                                    driverIntent.putExtra("paymentType", paymentType);
                                    setNotificationSound();
                                    driverIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    driverIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, driverIntent, PendingIntent.FLAG_ONE_SHOT);
                                    onReceiveNotification(getApplicationContext(), title, pendingIntent);
                                }
                            }
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }else  if (notificationType.equalsIgnoreCase("Cancel")) {

                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(notificationMsg);
                        if (obj.has("reaso")) {
                            String  reason = obj.getString("reaso").trim();
                            DriverPriviousTripFragment.createPriviousTripView = 0;
                            DriverCurrentMapFragment.createView =0;
                            DriverCancelBookingsFragment.createCancelView =0;
                            DriverUpcomingTripFragment.createUpcomingTripView =0;

                            sessionManager.setBookingId("");
                            sessionManager.setTripDate("");
                            sessionManager.setTripType("");

                            Intent cancelDriverRideIntent = new Intent(getApplicationContext(), DriverMyBookingsActivity.class);
                            if(isRunningInForeground())
                            {
                                cancelDriverRideIntent.putExtra("reason", reason);
                                cancelDriverRideIntent.putExtra("notificationMsg", notificationMsg);
                                cancelDriverRideIntent.putExtra("notificationTitle", notificationTitle);
                                cancelDriverRideIntent.setAction(Long.toString(System.currentTimeMillis()));
                                setNotificationSound();
                                cancelDriverRideIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(cancelDriverRideIntent);

                            }else if (!isRunningInForeground()){
                                cancelDriverRideIntent.putExtra("reason", reason);
                                cancelDriverRideIntent.putExtra("notificationMsg", notificationMsg);
                                cancelDriverRideIntent.putExtra("notificationTitle", notificationTitle);
                                cancelDriverRideIntent.setAction(Long.toString(System.currentTimeMillis()));
                                PendingIntent cancelRidePendingIntent = PendingIntent.getActivity(this, 0, cancelDriverRideIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                onReceiveNotification(getApplicationContext(),title,cancelRidePendingIntent);
                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }  else   if (notificationType.equalsIgnoreCase("tripMonthly")){
                    Intent tripMonthlyIntent = new Intent(this, TripMonthllyNotificationActivity.class);

                    try {
                        JSONObject object = new JSONObject(notificationMsg);
                        if (object.has("approval")) {
                            JSONObject jsonObject = object.getJSONObject("approval");
                            String destinationAddress = jsonObject.getString("destinationAddress").trim();
                            String pickupAddress = jsonObject.getString("pickupAddress").trim();
                            String tripTotal = jsonObject.getString("tripTotal").trim();
                            String mobileNumber = jsonObject.getString("mobileNumber").trim();
                            String fullName = jsonObject.getString("fullName").trim();
                            String rideStartTime = jsonObject.getString("rideStartTime").trim();
                            String rideStartDate = jsonObject.getString("rideStartDate").trim();

                            if(isRunningInForeground())
                            {
                                tripMonthlyIntent.putExtra("notificationTitle", notificationTitle);
                                tripMonthlyIntent.putExtra("notificationType", notificationType);
                                tripMonthlyIntent.putExtra("destinationAddress", destinationAddress);
                                tripMonthlyIntent.putExtra("pickupAddress", pickupAddress);
                                tripMonthlyIntent.putExtra("tripTotal", tripTotal);
                                tripMonthlyIntent.putExtra("mobileNumber", mobileNumber);
                                tripMonthlyIntent.putExtra("fullName", fullName);
                                tripMonthlyIntent.putExtra("rideStartTime", rideStartTime);
                                tripMonthlyIntent.putExtra("rideStartDate", rideStartDate);

                                setNotificationSound();
                                tripMonthlyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(tripMonthlyIntent);

                            }else if (!isRunningInForeground()){
                                tripMonthlyIntent.putExtra("notificationTitle", notificationTitle);
                                tripMonthlyIntent.putExtra("notificationType", notificationType);
                                tripMonthlyIntent.putExtra("destinationAddress", destinationAddress);
                                tripMonthlyIntent.putExtra("pickupAddress", pickupAddress);
                                tripMonthlyIntent.putExtra("tripTotal", tripTotal);
                                tripMonthlyIntent.putExtra("mobileNumber", mobileNumber);
                                tripMonthlyIntent.putExtra("fullName", fullName);
                                tripMonthlyIntent.putExtra("rideStartTime", rideStartTime);
                                tripMonthlyIntent.putExtra("rideStartDate", rideStartDate);
                                PendingIntent cancelRidePendingIntent = PendingIntent.getActivity(this, 0, tripMonthlyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                onReceiveNotification(getApplicationContext(),title,cancelRidePendingIntent);
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setNotificationSound();
                    tripMonthlyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    tripMonthlyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent onTheWayPendingIntent = PendingIntent.getActivity(this, 0, tripMonthlyIntent, PendingIntent.FLAG_ONE_SHOT);
                    onReceiveNotification(getApplicationContext(),title,onTheWayPendingIntent);

                } else   if (notificationType.equalsIgnoreCase("tripMonthlyPayment")){
                    Intent tripMonthlyPayIntent = new Intent(this, ShowMonthllyTripsActivity.class);

                    Toast.makeText(this, "your payment has been submitted successfull", Toast.LENGTH_LONG).show();
                    setNotificationSound();
                    tripMonthlyPayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    tripMonthlyPayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent onTheWayPendingIntent = PendingIntent.getActivity(this, 0, tripMonthlyPayIntent, PendingIntent.FLAG_ONE_SHOT);
                    onReceiveNotification(getApplicationContext(),title,onTheWayPendingIntent);

                }
                else {
                    Intent Intent = new Intent(this, DriverHomeActivity.class);
                    Intent.putExtra("directCallFromFCM", "true");
                    setNotificationSound();
                    Intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent otherPendingIntent = PendingIntent.getActivity(this, 0, Intent, PendingIntent.FLAG_ONE_SHOT);
                    onReceiveNotification(getApplicationContext(),title,otherPendingIntent);

                }
            }
            else  if (sessionManager.getUserType().equals("passenger")){

                if (notificationType.equalsIgnoreCase("Rejected")){
                    Intent notificationIntentRejected = new Intent(getApplicationContext(), HomeActivity.class);
                    notificationIntentRejected.putExtra("notificationMsg", notificationMsg);
                    notificationIntentRejected.putExtra("notificationTitle", notificationTitle);
                    notificationIntentRejected.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    notificationIntentRejected.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    onReceiveNotification(getApplicationContext(),title,null);
                }
                else if(notificationType.equalsIgnoreCase("Accepted"))
                {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(notificationMsg);
                        if (obj.has("paypalId")) {
                            String  paypalId="";
                            paypalId = obj.getString("paypalId").trim();
                            //if (paypalId.equals("")){
                            sessionManager.setPaypalId(paypalId);
                            //}
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    CurrentMapFragment.createView =0;
                    PassengerCancelBookingsFragment.createCancelView =0;
                    PassengerPreviousTripFragment.createPreviousTripView =0;
                    PassengerUpcomingTripFragment.createUpcomingTripView =0;

                    Intent notificationIntent = new Intent(getApplicationContext(), PassengerMyBookingsActivity.class);
                    notificationIntent.putExtra("notificationMsg", notificationMsg);
                    notificationIntent.putExtra("notificationTitle", notificationTitle);
                    notificationIntent.setAction(Long.toString(System.currentTimeMillis()));

                    PendingIntent PassengrPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    onReceiveNotification(getApplicationContext(),title,null);
                }

                else if (notificationType.equalsIgnoreCase("Cancel")) {
                    CurrentMapFragment.createView =0;
                    PassengerCancelBookingsFragment.createCancelView =0;
                    PassengerPreviousTripFragment.createPreviousTripView =0;
                    PassengerUpcomingTripFragment.createUpcomingTripView =0;

                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(notificationMsg);
                        if (obj.has("reaso")) {
                            String  reason = obj.getString("reaso").trim();
                            Intent cancelDriverRideIntent = new Intent(getApplicationContext(), PassengerMyBookingsActivity.class);

                            sessionManager.setBookingId("");
                            sessionManager.setTripDate("");
                            sessionManager.setTripType("");

                            if(isRunningInForeground())
                            {
                                cancelDriverRideIntent.putExtra("reason", reason);
                                cancelDriverRideIntent.putExtra("notificationMsg", notificationMsg);
                                cancelDriverRideIntent.putExtra("notificationTitle", notificationTitle);

                                setNotificationSound();
                                cancelDriverRideIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(cancelDriverRideIntent);

                            }else if (!isRunningInForeground()){
                                cancelDriverRideIntent.putExtra("reason", reason);
                                cancelDriverRideIntent.putExtra("notificationMsg", notificationMsg);
                                cancelDriverRideIntent.putExtra("notificationTitle", notificationTitle);

                                cancelDriverRideIntent.setAction(Long.toString(System.currentTimeMillis()));
                                PendingIntent cancelRidePendingIntent = PendingIntent.getActivity(this, 0, cancelDriverRideIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                                onReceiveNotification(getApplicationContext(),title,cancelRidePendingIntent);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else   if (notificationType.equalsIgnoreCase("onTheWay")){
                    CurrentMapFragment.createView =0;
                    PassengerCancelBookingsFragment.createCancelView =0;
                    PassengerPreviousTripFragment.createPreviousTripView =0;
                    PassengerUpcomingTripFragment.createUpcomingTripView =0;

                    Intent onTheWayIntent = new Intent(this, PassengerMyBookingsActivity.class);
                    if(isRunningInForeground())
                    {
                        onTheWayIntent.putExtra("notificationTitle", notificationTitle);
                        setNotificationSound();
                        onTheWayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(onTheWayIntent);
                    }else  if(!isRunningInForeground()){
                        onTheWayIntent.putExtra("notificationType", notificationType);
                        setNotificationSound();
                        onTheWayIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        onTheWayIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent onTheWayPendingIntent = PendingIntent.getActivity(this, 0, onTheWayIntent, PendingIntent.FLAG_ONE_SHOT);
                        onReceiveNotification(getApplicationContext(),title,onTheWayPendingIntent);
                    }

                }else   if (notificationType.equalsIgnoreCase("start")){
                    CurrentMapFragment.createView =0;
                    PassengerCancelBookingsFragment.createCancelView =0;
                    PassengerPreviousTripFragment.createPreviousTripView =0;
                    PassengerUpcomingTripFragment.createUpcomingTripView =0;
                    Intent startIntent = new Intent(this, PassengerMyBookingsActivity.class);

                    if(isRunningInForeground())
                    {
                        startIntent.putExtra("notificationTitle", notificationTitle);
                        setNotificationSound();
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startIntent);
                    }else  if(!isRunningInForeground()){
                        startIntent.putExtra("notificationType", notificationType);
                        setNotificationSound();
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent onTheWayPendingIntent = PendingIntent.getActivity(this, 0, startIntent, PendingIntent.FLAG_ONE_SHOT);
                        onReceiveNotification(getApplicationContext(),title,onTheWayPendingIntent);
                    }

                }else   if (notificationType.equalsIgnoreCase("end")){
                    CurrentMapFragment.createView =0;
                    PassengerCancelBookingsFragment.createCancelView =0;
                    PassengerPreviousTripFragment.createPreviousTripView =0;
                    PassengerUpcomingTripFragment.createUpcomingTripView =0;
                    Intent endIntent = new Intent(this, PassengerMyBookingsActivity.class);
                    endIntent.putExtra("directCallFromFCM", "true");
                    sessionManager.setBookingId("");
                    sessionManager.setTripDate("");
                    sessionManager.setTripType("");

                    if(isRunningInForeground())
                    {
                        setNotificationSound();
                        endIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(endIntent);
                    }else  if(!isRunningInForeground()){
                        setNotificationSound();
                        endIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        endIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent onTheWayPendingIntent = PendingIntent.getActivity(this, 0, endIntent, PendingIntent.FLAG_ONE_SHOT);
                        onReceiveNotification(getApplicationContext(),title,onTheWayPendingIntent);
                    }

                }
                else if (notificationType.equalsIgnoreCase("payment"))
                {
                    try {

                        JSONObject object = new JSONObject(notificationMsg);
                        if (object.has("paymentAmount")) {
                            String paymentAmount = object.getString("paymentAmount").trim();
                            String paymenType = object.getString("paymenType").trim();
                            String bookingId = object.getString("bookingId").trim();
                            String  rideTime = object.getString("rideTime").trim();
                            String  rideDistance = object.getString("rideDistance").trim();
                            String startDate = object.getString("startDate").trim();
                            String startTime = object.getString("startTime").trim();
                            String endTime = object.getString("endTime").trim();
                            String endDate = object.getString("endDate").trim();
                            String  pickupAddress = object.getString("pickupAddress").trim();
                            String destinationAddress = object.getString("destinationAddress").trim();
                            String paymentId = object.getString("paymentId").trim();
                            String driver = object.getString("driver").trim();

                            Intent paymentIntent = new Intent(getApplicationContext(), PassengerPaymentActivity.class);

                            if(isRunningInForeground())
                            {

                                paymentIntent.putExtra("paymentAmount", paymentAmount);
                                paymentIntent.putExtra("paymenType", paymenType);
                                paymentIntent.putExtra("rideTime", rideTime);
                                paymentIntent.putExtra("rideDistance", rideDistance);
                                paymentIntent.putExtra("startDate", startDate);
                                paymentIntent.putExtra("startTime", startTime);
                                paymentIntent.putExtra("endTime", endTime);
                                paymentIntent.putExtra("endDate", endDate);
                                paymentIntent.putExtra("pickupAddress", pickupAddress);
                                paymentIntent.putExtra("destinationAddress", destinationAddress);
                                paymentIntent.putExtra("bookingId", bookingId);
                                paymentIntent.putExtra("paymentId", paymentId);
                                paymentIntent.putExtra("driver", driver);

                                setNotificationSound();
                                paymentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(paymentIntent);
                            }
                            else if (!isRunningInForeground()){
                                paymentIntent.putExtra("paymentAmount", paymentAmount);
                                paymentIntent.putExtra("paymenType", paymenType);
                                paymentIntent.putExtra("rideTime", rideTime);
                                paymentIntent.putExtra("rideDistance", rideDistance);
                                paymentIntent.putExtra("startDate", startDate);
                                paymentIntent.putExtra("startTime", startTime);
                                paymentIntent.putExtra("endTime", endTime);
                                paymentIntent.putExtra("endDate", endDate);
                                paymentIntent.putExtra("pickupAddress", pickupAddress);
                                paymentIntent.putExtra("destinationAddress", destinationAddress);
                                paymentIntent.putExtra("bookingId", bookingId);
                                paymentIntent.putExtra("paymentId", paymentId);
                                paymentIntent.putExtra("driver", driver);
                                paymentIntent.setAction(Long.toString(System.currentTimeMillis()));
                                PendingIntent paymentIntentPendingIntent = PendingIntent.getActivity(this, 0, paymentIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                onReceiveNotification(getApplicationContext(),title,paymentIntentPendingIntent);
                            }
                        }

                    }catch (Exception ex){
                        ex.printStackTrace();
                    }

                }
                else   if (notificationType.equalsIgnoreCase("tripMonthly")){
                    Intent tripMonthlyIntent = new Intent(this, TripMonthllyNotificationActivity.class);

                    try {
                        JSONObject object = new JSONObject(notificationMsg);
                        if (object.has("approval")) {
                            JSONObject jsonObject = object.getJSONObject("approval");
                            String destinationAddress = jsonObject.getString("destinationAddress").trim();
                            String pickupAddress = jsonObject.getString("pickupAddress").trim();
                            String tripTotal = jsonObject.getString("tripTotal").trim();
                            String mobileNumber = jsonObject.getString("mobileNumber").trim();
                            String fullName = jsonObject.getString("fullName").trim();
                            String rideStartTime = jsonObject.getString("rideStartTime").trim();
                            String rideStartDate = jsonObject.getString("rideStartDate").trim();


                            if(isRunningInForeground())
                            {
                                tripMonthlyIntent.putExtra("notificationType", notificationType);
                                tripMonthlyIntent.putExtra("destinationAddress", destinationAddress);
                                tripMonthlyIntent.putExtra("pickupAddress", pickupAddress);
                                tripMonthlyIntent.putExtra("tripTotal", tripTotal);
                                tripMonthlyIntent.putExtra("mobileNumber", mobileNumber);
                                tripMonthlyIntent.putExtra("fullName", fullName);
                                tripMonthlyIntent.putExtra("rideStartTime", rideStartTime);
                                tripMonthlyIntent.putExtra("rideStartDate", rideStartDate);
                                setNotificationSound();
                                tripMonthlyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(tripMonthlyIntent);
                            }else  if(!isRunningInForeground()){
                                tripMonthlyIntent.putExtra("notificationType", notificationType);
                                tripMonthlyIntent.putExtra("destinationAddress", destinationAddress);
                                tripMonthlyIntent.putExtra("pickupAddress", pickupAddress);
                                tripMonthlyIntent.putExtra("tripTotal", tripTotal);
                                tripMonthlyIntent.putExtra("mobileNumber", mobileNumber);
                                tripMonthlyIntent.putExtra("fullName", fullName);
                                tripMonthlyIntent.putExtra("rideStartTime", rideStartTime);
                                tripMonthlyIntent.putExtra("rideStartDate", rideStartDate);
                                setNotificationSound();
                                tripMonthlyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                tripMonthlyIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent onTheWayPendingIntent = PendingIntent.getActivity(this, 0, tripMonthlyIntent, PendingIntent.FLAG_ONE_SHOT);
                                onReceiveNotification(getApplicationContext(),title,onTheWayPendingIntent);

                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else   if (notificationType.equalsIgnoreCase("tmAccept")){
                    Intent tmAcceptIntent = new Intent(this, TripMonthllyNotificationActivity.class);
                    try {
                        JSONObject object = new JSONObject(notificationMsg);
                        if (object.has("accept")) {
                            JSONObject jsonObject = object.getJSONObject("accept");
                            String destinationAddress = jsonObject.getString("destinationAddress").trim();
                            String pickupAddress = jsonObject.getString("pickupAddress").trim();
                            String tripTotal = jsonObject.getString("tripTotal").trim();
                            //  String mobileNumber = jsonObject.getString("mobileNumber").trim();
                            //  String fullName = jsonObject.getString("fullName").trim();
                            String rideStartTime = jsonObject.getString("rideStartTime").trim();
                            String rideStartDate = jsonObject.getString("rideStartDate").trim();
                            String bookingId = jsonObject.getString("bookingId").trim();


                            if(isRunningInForeground())
                            {

                                tmAcceptIntent.putExtra("notificationType", notificationType);
                                tmAcceptIntent.putExtra("destinationAddress", destinationAddress);
                                tmAcceptIntent.putExtra("pickupAddress", pickupAddress);
                                tmAcceptIntent.putExtra("tripTotal", tripTotal);
                                //  tripMonthlyIntent.putExtra("fullName", fullName);
                                tmAcceptIntent.putExtra("rideStartTime", rideStartTime);
                                tmAcceptIntent.putExtra("rideStartDate", rideStartDate);
                                tmAcceptIntent.putExtra("bookingId", bookingId);
                                setNotificationSound();
                                tmAcceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(tmAcceptIntent);

                            }else  if(!isRunningInForeground()){
                                tmAcceptIntent.putExtra("notificationType", notificationType);
                                tmAcceptIntent.putExtra("destinationAddress", destinationAddress);
                                tmAcceptIntent.putExtra("pickupAddress", pickupAddress);
                                tmAcceptIntent.putExtra("tripTotal", tripTotal);
                                //  tripMonthlyIntent.putExtra("fullName", fullName);
                                tmAcceptIntent.putExtra("rideStartTime", rideStartTime);
                                tmAcceptIntent.putExtra("rideStartDate", rideStartDate);
                                tmAcceptIntent.putExtra("bookingId", bookingId);
                                setNotificationSound();
                                tmAcceptIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                tmAcceptIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                PendingIntent onTheWayPendingIntent = PendingIntent.getActivity(this, 0, tmAcceptIntent, PendingIntent.FLAG_ONE_SHOT);
                                onReceiveNotification(getApplicationContext(),title,onTheWayPendingIntent);

                            }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                else   if (notificationType.equalsIgnoreCase("tmReject")){
                    Intent tmRejectIntent = new Intent(this, HomeActivity.class);

                    setNotificationSound();
                    tmRejectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    tmRejectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent onTheWayPendingIntent = PendingIntent.getActivity(this, 0, tmRejectIntent, PendingIntent.FLAG_ONE_SHOT);
                    onReceiveNotification(getApplicationContext(),title,onTheWayPendingIntent);

                }
            }

        }else{
            Intent intent = new Intent(this, RoleActivity.class);
            setNotificationSound();
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
            onReceiveNotification(getApplicationContext(),title,pendingIntent);
            startActivity(intent);
        }

    }


/*-------------------------------------------------------------------------------------------------------------------------------*/

    public void onReceiveNotification(final Context context,String title, PendingIntent intent) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.app_icon)
                .setContentTitle("Mishwar")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(title))
                .setContentText(title)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(intent);
        notificationBuilder.getNotification().flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, notificationBuilder.build());
    }

/*-------------------------------------------------------------------------------------------------------------------------------*/

           /* if(isRunningInForeground())
            {
                Intent driverNotificationIntent = new Intent(getApplicationContext(), SingleNotificationActivity.class);
                driverNotificationIntent.putExtra("notificationType", "messege");
                driverNotificationIntent.putExtra("directCallFromFCM", "true");
                driverNotificationIntent.putExtra("bookingId", bookingId);
                driverNotificationIntent.putExtra("pickupAddress", pickupAddress);
                driverNotificationIntent.putExtra("destinationAddress", destinationAddress);
                driverNotificationIntent.putExtra("rideStartTime", rideStartTime);
                driverNotificationIntent.putExtra("rideStartDate", rideStartDate);
                driverNotificationIntent.putExtra("mobileNumber", mobileNumber);
                driverNotificationIntent.putExtra("fullName", fullName);
                setNotificationSound();
                driverNotificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(driverNotificationIntent);
            }*/
/*-------------------------------------------------------------------------------------------------------------------------------*/

    protected boolean isRunningInForeground()
    {
        Log.d("inService", "check 6");
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = manager.getRunningTasks(1);
        if (tasks.isEmpty()) {
            return false;
        }
        String topActivityName = tasks.get(0).topActivity.getPackageName();
        return topActivityName.equalsIgnoreCase(getPackageName());
    }
    /*-------------------------------------------------------------------------------------------------------------------------------*/
    public void setNotificationSound()
    {
        try
        {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}