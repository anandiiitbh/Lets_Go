package com.sathya.mobileotpauth;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.sathya.mobileotpauth.dbConnectivity.PastRidesDbHelper;
import com.sathya.mobileotpauth.dbConnectivity.RidesSchema;
import com.sathya.mobileotpauth.helper.KeyboardFragment;
import com.sathya.mobileotpauth.helper.PastRideListAdapter;
import com.sathya.mobileotpauth.helper.RideModel;

import java.util.ArrayList;


public class PastRideViews extends AppCompatActivity implements KeyboardFragment.ConnectorForCallback {
    private ProgressBar spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_past_ride_views);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(this.getResources().getColor(R.color.splash));
        }
        getSupportActionBar().setTitle("Past Rides");

        PastRidesDbHelper dbHelper = new PastRidesDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                RidesSchema.PastRides.COLUMN_NAME_FROM,
                RidesSchema.PastRides.COLUMN_NAME_TO,
                RidesSchema.PastRides.COLUMN_NAME_PRICE,
                RidesSchema.PastRides.COLUMN_NAME_DATE,
                RidesSchema.PastRides.COLUMN_NAME_TYPE
        };


        String sortOrder =
                RidesSchema.PastRides.COLUMN_NAME_DATE + " DESC";



        spinner = (ProgressBar)findViewById(R.id.progressBar1);

        ListView layout = findViewById(R.id.pastViewLayout);

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
           ArrayList<RideModel> rideModelArrayList = new ArrayList<RideModel>();

           Integer countItems = 0;
           while(cursor.moveToNext()) {
               countItems++;
               Log.d("tag",cursor.toString());
               rideModelArrayList.add(new RideModel(
                       cursor.getString(3)+" On: "+cursor.getString(4),
                       cursor.getString(5),
                       cursor.getString(1),
                       cursor.getString(2),
                       R.drawable.prime_sedan));
           }
           cursor.close();

           getSupportActionBar().setSubtitle("Click on any ride to go for it again ");
           PastRideListAdapter adapter = new PastRideListAdapter(this,this, rideModelArrayList);
//           SectionedRecyclerViewAdapter sectionAdapter = new SectionedRecyclerViewAdapter();
//           sectionAdapter.addSection(adapter);
//           sectionAdapter.addSection(adapter);
           layout.setAdapter(adapter);
       }
       catch (Exception e){
           Log.d("tag",""+ e);
       }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                spinner.setVisibility(View.GONE);
            }
        }, 1000);


    }

    @Override
    public void update(int key) {
        finish();
    }

    //Uncomment below two methods for logout Feature
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.logout_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//
//        if (id == R.id.action_logout) {
//
////            Clearing Shared Preference
//            SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedpreferences.edit();  // GE_HEALTH
//            editor.putString(Constants.MOBILE, "");
//            editor.putBoolean(Constants.IS_LOGIN, false);
//            editor.commit();
//
////            Clearing Db
//            PastRidesDbHelper dbHelper = new PastRidesDbHelper(getApplicationContext());
//            SQLiteDatabase db = dbHelper.getWritableDatabase();
//            db.execSQL(SQL_DELETE_ENTRIES);
//
////            Changing Intent
//            Intent intent = new Intent(this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }


}