package com.sathya.mobileotpauth;

import androidx.fragment.app.FragmentActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.razorpay.*;
import org.json.JSONObject;

import com.sathya.mobileotpauth.dbConnectivity.PastRidesDbHelper;
import com.sathya.mobileotpauth.dbConnectivity.RidesSchema;
import com.sathya.mobileotpauth.helper.Constants;
import com.sathya.mobileotpauth.helper.adapters.PriceListGVAdapter;
import com.sathya.mobileotpauth.helper.models.RideModel;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, PriceListGVAdapter.callback, PaymentResultListener {

    private GoogleMap mMap;
    private LatLng mOrigin;
    private LatLng mDestination;
    GridView coursesGV;
    RideModel rideModel = null ;
    SharedPreferences sharedpreferences;
    int walletMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);

        hideSystemUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_lRJDdEOLIeHgri");
        Checkout.preload(getApplicationContext());
        mapFragment.getMapAsync(this);
        mOrigin = new LatLng(getIntent().getDoubleExtra("S_LATITUDE",12),getIntent().getDoubleExtra("S_LONGITUDE",12));
        mDestination = new LatLng(getIntent().getDoubleExtra("D_LATITUDE",12),getIntent().getDoubleExtra("D_LONGITUDE",12));
        Log.d("tag",mOrigin.toString());
        Log.d("tag",mDestination.toString());


        sharedpreferences = getSharedPreferences(Constants.Wallet, Context.MODE_PRIVATE);

        walletMoney = sharedpreferences.getInt(Constants.Wallet_Money,0);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        MarkerOptions optionSrc = new MarkerOptions();
        optionSrc.position(mOrigin);
        optionSrc.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        Marker markSrc = mMap.addMarker(optionSrc);
        MarkerOptions optionDest = new MarkerOptions();
        optionDest.position(mDestination);
        optionDest.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        Marker markDest = mMap.addMarker(optionDest);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(mOrigin);
        builder.include(mDestination);
        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels/3;
        int padding = (int) (width * 0.12); // offset from edges of the map 12% of screen


        CameraUpdate cameraUpdate;
        cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds,width, height, padding);
        mMap.animateCamera(cameraUpdate);

        //Distance between Src and Dest
        Location locationA = new Location("Source ");
        locationA.setLatitude(mOrigin.latitude);
        locationA.setLongitude(mOrigin.longitude);

        Location locationB = new Location("Dest ");
        locationB.setLatitude(mDestination.latitude);
        locationB.setLongitude(mDestination.longitude);
        float distance = locationA.distanceTo(locationB)/1000;
        distance += (distance*0.3);

        String from = getIntent().getStringExtra("SRC");
        String to = getIntent().getStringExtra("DEST");
        ((TextView) findViewById(R.id.from)).setText(from);
        ((TextView) findViewById(R.id.to)).setText(to);
        ((TextView) findViewById(R.id.distance)).setText("Estimated Distance: " + String.format("%.02f", distance) + " Kms");

        Integer primeSedan = fareCalculater((int) distance,55,7,13);
        Integer sedan      = fareCalculater((int) distance,40,6,12);
        Integer auto       = fareCalculater((int) distance,50,6,12);
        Integer bike       = fareCalculater((int) distance,19,3,3);

        //Booking Options
        coursesGV = findViewById(R.id.idGVcourses);

        String errorMsg = "";
        if(from.substring(from.length()-5).equals(to.substring(to.length()-5))){
            boolean count = false;
            ArrayList<RideModel> rideModelArrayList = new ArrayList<RideModel>();
//        String price, String type, String from, String to, int imgid
            if (distance < 1000) {
                rideModelArrayList.add(new RideModel(
                        primeSedan.toString(),
                        "Prime Sedan",
                        from,
                        to,
                        R.drawable.prime_sedan));
                count = true;
            }
            if (distance < 800) {
                rideModelArrayList.add(new RideModel(
                        sedan.toString(),
                        "Sedan",
                        from,
                        to,
                        R.drawable.sedan));
            }
            if (distance < 100) {
                rideModelArrayList.add(new RideModel(
                        auto.toString(),
                        "Auto",
                        from,
                        to,
                        R.drawable.auto));
            }
            if (distance < 50) {
                rideModelArrayList.add(new RideModel(
                        bike.toString(),
                        "Bike",
                        from,
                        to,
                        R.drawable.bike));
            }


            PriceListGVAdapter adapter = new PriceListGVAdapter(this, rideModelArrayList, this);
            coursesGV.setAdapter(adapter);
            if (!count) {
                errorMsg = "Sorry. We don't serve this much distance " + String.format("%.02f", distance) + " Kms";
            }
        }
        else{
            errorMsg = "Sorry. We don't serve cross countries!!";
        }


        ((TextView) findViewById(R.id.msg)).setText(errorMsg);

    }


    public static Integer fareCalculater(Integer distance, Integer baseFare, Integer travelChargersBefore20, Integer travelChargersAfter20){
        Integer fare = baseFare + (travelChargersBefore20 * (int)distance);

        if(distance > 20) {
            // Base fare + travel chargers before 20 km + travel chargers after 20 km
            fare = baseFare + (travelChargersBefore20 * 20) + (((int) distance - 20) * travelChargersAfter20);
        }
        return fare;
    }



    @Override
    protected void onResume() {
        super.onResume();
        findViewById(R.id.paymentFooter).setVisibility(View.GONE);
        walletMoney = sharedpreferences.getInt(Constants.Wallet_Money,0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    @Override
    public void callForPayment(RideModel rideModel) {

        this.rideModel = rideModel;

        findViewById(R.id.paymentFooter).setVisibility(View.VISIBLE);


        ((TextView)findViewById(R.id.selectMethod)).setText(" (₹"+rideModel.getPrice()+") Select Payment Method");
        FloatingActionButton cashButton = findViewById(R.id.cashButton);
        FloatingActionButton walletButton = findViewById(R.id.walletButton);
        FloatingActionButton onlineButton = findViewById(R.id.onlineButton);

        cashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeTransaction();
            }
        });

        if(walletMoney >= Integer.parseInt(rideModel.getPrice())) {

            walletButton.setBackgroundTintList(getColorStateList(R.color.splash));
            ((TextView)findViewById(R.id.walletText)).setText("Wallet");
            walletButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    walletMoney -= Integer.parseInt(rideModel.getPrice());
                    SharedPreferences.Editor editor = sharedpreferences.edit();  // GE_HEALTH
                    editor.putInt(Constants.Wallet_Money, walletMoney);
                    editor.commit();
                    makeTransaction();
                }
            });
        }

        else{
            walletButton.setBackgroundTintList(getColorStateList(R.color.red));
            ((TextView)findViewById(R.id.walletText)).setText("Add ₹"+(Integer.parseInt(rideModel.getPrice())-walletMoney)+" more to go");
            walletButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(MapsActivity.this, AddMoneyToWallet.class);
                    intent.putExtra(RideModel.PRICE, (Integer.parseInt(rideModel.getPrice())-walletMoney));
                    startActivity(intent);
                }
            });
        }


        onlineButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlinePayment();
            }
        });
    }

    public void onlinePayment(){
        SharedPreferences sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
        Checkout checkout = new Checkout();

        // set your id as below
        checkout.setKeyID("rzp_test_lRJDdEOLIeHgri");

        // set image
        checkout.setImage(R.drawable.logo_1);

        // initialize json object
        JSONObject object = new JSONObject();
        try {
            // to put name
            object.put("name", "Let's Go");

            // put description
            object.put("description", "Test payment");

            // to set theme color
            object.put("theme.color", "");

            // put the currency
            object.put("currency", "INR");

            // put amount
            object.put("amount", Integer.parseInt(rideModel.getPrice())*100);

            // put mobile number
            object.put("prefill.contact", sharedpreferences.getString(Constants.MOBILE,""));

            // put email
            object.put("prefill.email", "test@mail.com");

            // open razorpay to checkout activity
            checkout.open(MapsActivity.this, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        makeTransaction();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this,"Payment unsuccessful | Give it another shot",Toast.LENGTH_LONG).show();
    }

    private void makeTransaction() {
        //Current Date & Time
        Date presentTime_Date = Calendar.getInstance().getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
        String dateTime = dateFormat.format(presentTime_Date);
        Log.d("tag","DateTime :: "+dateTime);


        //Driver Details Generation
        Random rand = new Random();

        String phone = "+919"+(rand.nextInt(9)+1)+""+
                (rand.nextInt(9)+1)+""+
                (rand.nextInt(9)+1)+
                "123456";
        String cabNumber = "XX-"+(rand.nextInt(9)+1)+""+(rand.nextInt(9)+1)
                +"X-"+(rand.nextInt(9)+1)+""+(rand.nextInt(9)+1)+"67";

        String driverDetails = "TestDriver"+rand.nextInt(9)+1;
        try{
            //Inserting rides into database
            PastRidesDbHelper dbHelper = new PastRidesDbHelper(getApplicationContext());
            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(RidesSchema.PastRides.COLUMN_NAME_FROM, rideModel.getFrom());
            values.put(RidesSchema.PastRides.COLUMN_NAME_TO, rideModel.getTo());
            values.put(RidesSchema.PastRides.COLUMN_NAME_PRICE, rideModel.getPrice());
            values.put(RidesSchema.PastRides.COLUMN_NAME_TYPE, rideModel.getType());
            values.put(RidesSchema.PastRides.COLUMN_NAME_DATE, dateTime);
            values.put(RidesSchema.PastRides.COLUMN_NAME_DRIVER, driverDetails+
                    " Cab Number: "+cabNumber+
                    " Ph No: "+phone);

            // Insert the new row, returning the primary key value of the new row

            long newRowId = db.insert(RidesSchema.PastRides.TABLE_NAME, null, values);

            //Invoke Billing
            Intent intent = new Intent(this, BookedView.class);
            intent.putExtra(RideModel.PRICE, ""+ rideModel.getPrice());
            intent.putExtra(RideModel.TYPE, rideModel.getType());
            intent.putExtra(RideModel.FROM, rideModel.getFrom());
            intent.putExtra(RideModel.TO, rideModel.getTo());
            intent.putExtra("D_DETAILS", driverDetails);
            intent.putExtra("PHONE", phone);
            intent.putExtra("CAB", cabNumber);
            startActivity(intent);
            finish();
        }catch(Exception e){
            Log.e("tag",e.toString());
        }
    }

}