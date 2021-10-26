package com.sathya.mobileotpauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.sathya.mobileotpauth.dbConnectivity.PastRidesDbHelper;
import com.sathya.mobileotpauth.dbConnectivity.RidesSchema;
import com.sathya.mobileotpauth.helper.Constants;
import com.sathya.mobileotpauth.helper.NotificationBuildHelper;
import com.sathya.mobileotpauth.helper.PastRideListAdapter;
import com.sathya.mobileotpauth.helper.RideModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookedView extends AppCompatActivity {

    String from;
    String to;
    String price;
    String type;
    String phone;
    String cabNumber;
    String driverDetails = null;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booked_view);

        getSupportActionBar().hide();


        sharedpreferences = getSharedPreferences(Constants.Wallet, Context.MODE_PRIVATE);
        from  = getIntent().getStringExtra(RideModel.FROM);
        to    = getIntent().getStringExtra(RideModel.TO);
        price = getIntent().getStringExtra(RideModel.PRICE);
        type  = getIntent().getStringExtra(RideModel.TYPE);
        driverDetails  = getIntent().getStringExtra("D_DETAILS");
        phone  = getIntent().getStringExtra("PHONE");
        cabNumber  = getIntent().getStringExtra("CAB");


        //Creating Notification
        RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.ride_info_notification);
        switch(type){
            case "Prime Sedan" : contentView.setImageViewResource(R.id.typeNotification, R.drawable.prime_sedan);
                break;
            case "Sedan"       : contentView.setImageViewResource(R.id.typeNotification, R.drawable.sedan);
                break;
            case "Auto"        : contentView.setImageViewResource(R.id.typeNotification, R.drawable.auto);
                break;
            default            : contentView.setImageViewResource(R.id.typeNotification, R.drawable.bike);
        }

        contentView.setTextViewText(R.id.driverNotification, "Driver : "+ driverDetails);
        contentView.setTextViewText(R.id.driverCabNotification, "Veh Number : "+cabNumber);
        contentView.setTextViewText(R.id.driverPhoneNotification, "Enjoy your ride \uD83D\uDE0A");
//        contentView.s
        NotificationBuildHelper.createNotification("Ride Booked \uD83D\uDE0A", "Enjoy your ride with: ", this,227,contentView);


        ((TextView) findViewById(R.id.driver)).setText("Driver: "+driverDetails);
        ((TextView) findViewById(R.id.driverCab)).setText("Cab Number: "+cabNumber);
        ((TextView) findViewById(R.id.driverPhone)).setText("Driver Number: "+phone);
        ((TextView) findViewById(R.id.price)).setText("Pay ₹ : "+price);


        ImageView img = findViewById(R.id.type);
        switch(type){
            case "Prime Sedan" : img.setImageResource(R.drawable.prime_sedan);
                                 break;
            case "Sedan"       : img.setImageResource(R.drawable.sedan);
                                 break;
            case "Auto"        : img.setImageResource(R.drawable.auto);
                                 break;
            default            : img.setImageResource(R.drawable.bike);
        }
        ((TextView) findViewById(R.id.src)).setText(from);
        ((TextView) findViewById(R.id.dest)).setText(to);

    }

    public void shareWithFamily(View view) {
        if(driverDetails!=null){
            Geocoder geocoder = new Geocoder(this);
            List<Address> list = null;
            try {
                list = geocoder.getFromLocationName(from,1);

            } catch (IOException e) {
                e.printStackTrace();
            }
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Hi \uD83E\uDD17,\n I m going *from* ```"+from+
                    "``` \n*to* ```"+to+
                    "``` \n& This is my Ride Details : \n\n*Driver Name* : ```"+driverDetails+
                    "``` \n*Veh Number* : ```"+cabNumber+
                    "```\n*Driver Ph No* : ```"+phone+"```"
                    +
                    "```\n*My Current loc* : ```"+"https://www.google.com/maps/@"+list.get(0).getLatitude()+","+list.get(0).getLongitude()+",16z"+"```");
            sendIntent.setType("text/plain");
            startActivity(sendIntent);
        }
    }

    public void gotoPastBookings(View view) {
        Intent intent = new Intent(this, PastRideViews.class);
        startActivity(intent);
        finish();
    }

    public void callDriver(View view) {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone.trim()));
            startActivity(intent);
    }


    public void cancelRide(View view) {
        PastRidesDbHelper dbHelper = new PastRidesDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                RidesSchema.PastRides.COLUMN_NAME_PRICE
        };


        String sortOrder =
                RidesSchema.PastRides.COLUMN_NAME_DATE + " DESC";

        try{
            Cursor cursor = db.query(
                    RidesSchema.PastRides.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );
            if(cursor.moveToNext()) {
                int idx = cursor.getInt(0);
                if(cursor.getString(1).equals(price)) {
                    String selection = RidesSchema.PastRides._ID + " LIKE ?";
                    String[] selectionArgs = {String.valueOf(idx)};
                    int deletedRows = db.delete(RidesSchema.PastRides.TABLE_NAME, selection, selectionArgs);

                    SharedPreferences.Editor editor = sharedpreferences.edit();  // GE_HEALTH
                    editor.putInt(Constants.Wallet_Money, sharedpreferences.getInt(Constants.Wallet_Money,0)+Integer.parseInt(price)-20);
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"Previous ride cancelled",Toast.LENGTH_LONG).show();
                    super.onBackPressed();
                }
                else{
                    Toast.makeText(BookedView.this,"Error: Cannot cancel ride",Toast.LENGTH_LONG).show();
                }
            }
            cursor.close();
        }
        catch (Exception e){
            Log.d("tag",""+ e);
            Toast.makeText(BookedView.this,"Error: Cannot cancel ride",Toast.LENGTH_LONG).show();
        }
    }
}