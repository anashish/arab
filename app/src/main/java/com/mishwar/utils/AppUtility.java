package com.mishwar.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;



public class AppUtility {

	//static InterstitialAd mInterstitialAd;


	public static int LENGTH_LONG = Toast.LENGTH_LONG;
	public static int LENGTH_SHORT = Toast.LENGTH_SHORT;
	//public static AlertListener alertListener;

	public static void log_DEBUG(String tag, String msg) {

		Log.d(tag, msg);
	}

	public static void log_ERROR(String tag, String msg) {

		Log.e(tag, msg);
	}

	public static void log_VERBOSE(String tag, String msg) {

		Log.v(tag, msg);
	}

	public static void log_WARN(String tag, String msg) {

		Log.w(tag, msg);
	}

	public static void log_INFO(String tag, String msg) {

		Log.i(tag, msg);
	}

	public static String getFileExt(String fileName) {
		return fileName.substring((fileName.lastIndexOf(".") + 1), fileName.length());
	}

	public static String getFileName(String filepath){

		return new File(filepath).getName();
	}

	public static String getStringFromBundle(Bundle bundle, String key, String defaultValue){
		if (Build.VERSION.SDK_INT < 12){
			String returns = bundle.getString(key);
			if(returns==null) returns = defaultValue;

			return returns;
		} else
			return bundle.getString(key, defaultValue);
	}

	public static boolean isNetworkAvailable(Context con) {
		ConnectivityManager connectivityManager
				= (ConnectivityManager)con.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	public static void mSout(String query){
		System.out.println(query);
	}

	public static void showToast(Context con, String msg, int length){

		Toast.makeText(con, msg, length).show();
	}

	public static void downloadFile(Context context, String title, String url){
		String DownloadUrl = url;
		DownloadManager.Request request = new DownloadManager.Request(Uri.parse(DownloadUrl));
		//  request.setDescription("sample pdf file for testing");   //appears the same in Notification bar while downloading
		request.setTitle(title);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			request.allowScanningByMediaScanner();
			request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
		}
		request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title);
		//request.setDestinationInExternalFilesDir(context, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath().toString(), "");

		// get download service and enqueue file
		DownloadManager manager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
		manager.enqueue(request);
	}

	public static boolean isDownloadManagerAvailable(Context context) {
		try {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
				return false;
			}
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_LAUNCHER);
			intent.setClassName("com.android.providers.downloads.commun_ui","com.android.providers.downloads.commun_ui.DownloadList");
			List <ResolveInfo> list = context.getPackageManager().queryIntentActivities(intent,
					PackageManager.MATCH_DEFAULT_ONLY);
			return list.size() > 0;
		} catch (Exception e) {
			return false;
		}
	}

	public static void showAlertDialog_SingleButton(final Activity  con, String msg, String title, String ok){

		AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
		builder1.setTitle(title);
		builder1.setMessage(msg);
		builder1.setCancelable(true);
		builder1.setPositiveButton(ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						//con.finish();
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	public static void showAlertDialog_SingleButton_finishActivity(final Activity  con, String msg, String title, String ok){

		AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
		builder1.setTitle(title);
		builder1.setMessage(msg);
		builder1.setCancelable(true);
		builder1.setPositiveButton(ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						con.finish();
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}


	public static void showAlertDialogSingleButtonWithTextView(final Activity  con, String msg, String title, String ok, final TextView textView){

		AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
		builder1.setTitle(title);
		builder1.setMessage(msg);
		builder1.setCancelable(true);
		builder1.setPositiveButton(ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						textView.setText("");
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}


	public static void showAlertDialog(final Activity con, String msg, String title, String ok){

		AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
		builder1.setTitle(title);
		builder1.setMessage(msg);
		builder1.setCancelable(true);
		builder1.setPositiveButton(ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						con.finish();
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}
	public static void showAlertDialog_EmptyList( Context  con, String msg, String title, String ok){

		AlertDialog.Builder builder1 = new AlertDialog.Builder(con);
		builder1.setTitle(title);
		builder1.setMessage(msg);
		builder1.setCancelable(true);
		builder1.setPositiveButton(ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}
	public static void showAlertDialog_NetworkError(Context con, String msg, String title, String positive, String negative, final int type){

	}

	public static String getToday(String format){
		Date date = new Date();
		return new SimpleDateFormat(format).format(date);
	}



	public static Bitmap uriToBitmap(Context context, Uri selectedFileUri) {
		Bitmap bitmap = null;
		try {
			ParcelFileDescriptor parcelFileDescriptor =
					context.getContentResolver().openFileDescriptor(selectedFileUri, "r");
			FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
			bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);

			parcelFileDescriptor.close();
			AppUtility.mSout(" uriTobitmap   Success");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bitmap;
	}


	// get path of selected image
	public static String getImagePath(FragmentActivity context, Uri uri)
	{
		String[] projection = { MediaStore.MediaColumns.DATA };
		Cursor cursor = context.managedQuery(uri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}

	public static String getDuration(String begin_date, String end_date/*, String begin_time, String end_time*/){
		/*String toyBornTime = "2014-06-18 12:56:50";*/
		String toyBornTime = begin_date /*+ " " + begin_time*/;
		String toyEndTime = end_date /*+ " " + end_time*/;
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"dd/MM/yyyy");

		try {

			Date oldDate = dateFormat.parse(toyBornTime);
			System.out.println(oldDate);

			Date currentDate = dateFormat.parse(toyEndTime);//new Date();

			long diff = currentDate.getTime() - oldDate.getTime();
			long seconds = diff / 1000 ;
			long minutes = seconds / 60;
			long hours = minutes / 60;
			long days = hours / 24;

			if (oldDate.before(currentDate)) {

				Log.e("oldDate", "is previous date");
				Log.e("Difference: ", " seconds: " + seconds + " minutes: " + minutes
						+ " hours: " + hours + " days: " + days);

			}
			String FinalOPT = days + " days " +
					" " + hours % 24 + " hours ";
			//String FinalOPT = days + " days, " + hours + " hours " + minutes + " minutes";
			return FinalOPT;
		} catch (ParseException e) {

			e.printStackTrace();
		}
		return"";
	}

	public static String getCurrentTime()
	{
		DateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
		dateFormatter.setLenient(false);
		Date today = new Date();
		String s = dateFormatter.format(today);

		System.out.println("Date getCurrentTime"+s);

		return s;
	}

	public static String getDD_MM_YYYY(String input) {
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		//Date/time pattern of desired output date
		DateFormat outputformat = new SimpleDateFormat("dd/MM/yyyy");
		Date date = null;
		String output = null;
		try {
			//Conversion of input String to date
			date = df.parse(input);
			//old date format to new date format
			output = outputformat.format(date);
			System.out.println(output);
		} catch (ParseException pe) {
			pe.printStackTrace();
		}

		return output;
	}


	public static int getAge (int _year, int _month, int _day) {

		GregorianCalendar cal = new GregorianCalendar();
		int y, m, d, a;

		y = cal.get(Calendar.YEAR);
		m = cal.get(Calendar.MONTH);
		d = cal.get(Calendar.DAY_OF_MONTH);
		cal.set(_year, _month, _day);
		a = y - cal.get(Calendar.YEAR);
		if ((m < cal.get(Calendar.MONTH))
				|| ((m == cal.get(Calendar.MONTH)) && (d < cal
				.get(Calendar.DAY_OF_MONTH)))) {
			--a;
		}
		if(a < 0)
			a=0;
		return a;
	}


	public static String getCountryName(Context context, double latitude, double longitude) {
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		List<Address> addresses = null;
		try {
			addresses = geocoder.getFromLocation(latitude, longitude, 1);
			Address result;

			if (addresses != null && !addresses.isEmpty()) {
				return addresses.get(0).getCountryName();
			}

		} catch (IOException ignored) {
			//do something
		}
		return "";
	}

}