package com.sathya.mobileotpauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.BaseColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.razorpay.Checkout;
import com.sathya.mobileotpauth.databinding.ActivitySourceMapBinding;
import com.sathya.mobileotpauth.dbConnectivity.PastRidesDbHelper;
import com.sathya.mobileotpauth.dbConnectivity.RidesSchema;
import com.sathya.mobileotpauth.helper.models.BookmarkModel;
import com.sathya.mobileotpauth.helper.adapters.HorizontalBookmarkAdapter;
import com.sathya.mobileotpauth.helper.adapters.SearchableAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SourceMapActivity extends AppCompatActivity implements OnMapReadyCallback, SearchableAdapter.UpdateAfterClick {


    private static final int LOCATION_UPDATED = 1;
    private ActivitySourceMapBinding binding;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    ListView listView;
    LatLng myLocation = null;
    StringBuilder myLocationName = null;
    static Handler handler;
    Marker marker, markfirst, markSecond,markThird;
    private SharedPreferences sharedpreferences;
    private boolean removingBookmark = false;
    View locationButton;
    HorizontalBookmarkAdapter adapter;
    RecyclerView horizontal_recycler_view;

//    menuButton Menu
    FloatingActionButton menuButton, pastRideButton, walletButton, customerSupportButton;
    Animation menuButtonOpen, menuButtonClose, rotateForward, rotateBackward;
    boolean isOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideSystemUI();
        statusCheck();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        binding = ActivitySourceMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LinearLayout llayout = findViewById(R.id.bookmarksLayout);
        llayout.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                Toast.makeText(SourceMapActivity.this,"focused",Toast.LENGTH_LONG).show();
                if(b){
                    Toast.makeText(SourceMapActivity.this,"focused",Toast.LENGTH_LONG).show();
                    findViewById(R.id.lastRide).setVisibility(View.GONE);
                }
                else {

                    findViewById(R.id.lastRide).setVisibility(View.VISIBLE);
                }
            }
        });

//      Razorpay preload
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_lRJDdEOLIeHgri");
        Checkout.preload(getApplicationContext());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.ghost_view);
        mapFragment.getMapAsync(this);


        EditText source = binding.editTextTextPlaceName;
        findViewById(R.id.imageViewclear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                source.setText("");
            }
        });

        source.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    findViewById(R.id.mapContainer).setAlpha(1);
                }
                else {

                    findViewById(R.id.mapContainer).setAlpha(0.7f);
                }
            }
        });
        source.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    findViewById(R.id.imageViewclear).setVisibility(View.VISIBLE);
                }
                if(charSequence.length() == 0){
                    findViewById(R.id.imageViewclear).setVisibility(View.GONE);
                }
            }



            @Override
            public void afterTextChanged(Editable editable) {
                Geocoder geocoder = new Geocoder(getBaseContext());
                List<Address> list = null;
                try {
                    list = geocoder.getFromLocationName(editable.toString(), 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(list != null && list.size()>0 && myLocation.latitude != list.get(0).getLatitude()){
                    listView = findViewById(R.id.listView);
                    listView.setVisibility(View.VISIBLE);
                    listView.bringToFront();
                    listView.setTranslationZ(1);
                    ArrayAdapter adapter = new SearchableAdapter(getBaseContext(), SourceMapActivity.this, list);
                    listView.setAdapter(adapter);
                }

            }
        });
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        ImageView bookmark = findViewById(R.id.imageView5);
        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveBookmark(view);
            }
        });


        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == LOCATION_UPDATED) {
                    goToLocationZoom(myLocation.latitude, myLocation.longitude, 15);
//                    Toast.makeText(SourceMapActivity.this, "Location: " + getAreaName(), Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };


//        menuButton Menu buttons
        menuButton = (FloatingActionButton) findViewById(R.id.fabMenu);
        pastRideButton = (FloatingActionButton) findViewById(R.id.gotoPastRides);
        walletButton = (FloatingActionButton) findViewById(R.id.fabWallet);
        customerSupportButton = (FloatingActionButton) findViewById(R.id.fabCustomerSupport);

        menuButtonOpen = AnimationUtils.loadAnimation
                (this,R.anim.fab_open);
        menuButtonClose = AnimationUtils.loadAnimation
                (this,R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation
                (this,R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation
                (this,R.anim.rotate_backward);

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMenuButton();
            }
        });
        pastRideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoPastBookings();
            }
        });
        walletButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoWalletRecharge();
            }
        });
        customerSupportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoCustomerSupport();
            }
        });


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                animateMenuButton();
            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                animateMenuButton();
            }
        }, 3000);

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(isOpen)
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    animateMenuButton();
                }
            }, 1000);
    }

    private void animateMenuButton(){
        if (isOpen){
            menuButton.startAnimation(rotateForward);
            menuButton.setImageDrawable(getDrawable(R.drawable.ic_round_menu_24));
            pastRideButton.startAnimation(menuButtonClose);
            walletButton.startAnimation(menuButtonClose);
            customerSupportButton.startAnimation(menuButtonClose);
            pastRideButton.setClickable(false);
            walletButton.setClickable(false);
            customerSupportButton.setClickable(false);
            isOpen=false;
        }else {
            menuButton.startAnimation(rotateBackward);
            menuButton.setImageDrawable(getDrawable(R.drawable.ic_round_close_24));
            pastRideButton.startAnimation(menuButtonOpen);
            walletButton.startAnimation(menuButtonOpen);
            customerSupportButton.startAnimation(menuButtonOpen);
            pastRideButton.setClickable(true);
            walletButton.setClickable(true);
            customerSupportButton.setClickable(true);
            isOpen=true;
        }
    }




    @SuppressLint("ResourceType")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mMap = googleMap;
        enableMyLocation();
        mMap.getUiSettings().setZoomControlsEnabled(false);
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.ghost_view);
        locationButton = mapFragment.getView().findViewById(0x2);
        // Change the visibility of my location button
        if(locationButton != null)
            locationButton.setVisibility(View.GONE);

        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.mapTheme)));

        Runnable refreshCurrentLocation = new Runnable() {
            public void run() {
                while (myLocation == null) ;
                Message msg = handler.obtainMessage();
                msg.what = LOCATION_UPDATED;
                handler.sendMessage(msg);
            }
        };


        mMap.setOnCameraMoveListener((GoogleMap.OnCameraMoveListener) () -> {
            myLocation = googleMap.getCameraPosition().target;
            setMarker();
        });

        while(ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED);
        gotoMyCurrentLocation();

        userLocationmenuButton();
        recentRideEnable();
        showBookMarks();


    }


    private void showBookMarks(){

        PastRidesDbHelper dbHelper = new PastRidesDbHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String[] projection = {
                BaseColumns._ID,
                RidesSchema.BookMark.COLUMN_NAME,
                RidesSchema.BookMark.COLUMN_LOCATION
        };

        String sortOrder =
                RidesSchema.BookMark._ID + " DESC";

        try {
            Cursor cursor = db.query(
                    RidesSchema.BookMark.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );



            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<BookmarkModel> list = new ArrayList<>();
                    Integer countItems = 0;
                    while (cursor.moveToNext()) {
                        countItems++;
                        list.add(new BookmarkModel(
                                cursor.getInt(0),
                                cursor.getString(1),
                                cursor.getString(2)
                        ));
                    }
                    cursor.close();
                    horizontal_recycler_view = findViewById(R.id.bookmarkList);
                    adapter=new HorizontalBookmarkAdapter(SourceMapActivity.this, list,SourceMapActivity.this);
                    LinearLayoutManager horizontalLayoutManagaer
                            = new LinearLayoutManager(SourceMapActivity.this, LinearLayoutManager.HORIZONTAL, false);
                    horizontal_recycler_view.setLayoutManager(horizontalLayoutManagaer);
                    horizontal_recycler_view.setAdapter(adapter);
                }
            });

        }
        catch (Exception e){
            Log.d("tag",""+ e);
        }

    }


    private void recentRideEnable() {
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

        try {
            Cursor cursor = db.query(
                    RidesSchema.PastRides.TABLE_NAME,   // The table to query
                    projection,             // The array of columns to return (pass null to get all)
                    null,              // The columns for the WHERE clause
                    null,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );


            Integer countItems = 0;
            while (cursor.moveToNext() && countItems == 0) {
                countItems++;
                findViewById(R.id.lastRide).setVisibility(View.VISIBLE);
                String from = cursor.getString(1);
                String to = cursor.getString(2);
                ((TextView)findViewById(R.id.lastFromPlace)).setText(from);
                ((TextView)findViewById(R.id.latToPlace)).setText(to);
                findViewById(R.id.sameRecent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SourceMapActivity.this, MapsActivity.class);
                        LatLng src = getLatLng(from);
                        LatLng dest = getLatLng(to);
                        //Source Coordinate
                        intent.putExtra("S_LATITUDE", src.latitude);
                        intent.putExtra("S_LONGITUDE", src.longitude);
                        intent.putExtra("SRC", from);
                        //Destination Coordinate
                        intent.putExtra("D_LATITUDE", dest.latitude);
                        intent.putExtra("D_LONGITUDE",dest.longitude);
                        intent.putExtra("DEST", to);
                        startActivity(intent);
                    }
                });
                findViewById(R.id.reverseRecent).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SourceMapActivity.this, MapsActivity.class);
                        LatLng src = getLatLng(from);
                        LatLng dest = getLatLng(to);
                        //Source Coordinate
                        intent.putExtra("S_LATITUDE", dest.latitude);
                        intent.putExtra("S_LONGITUDE", dest.longitude);
                        intent.putExtra("SRC", to);
                        //Destination Coordinate
                        intent.putExtra("D_LATITUDE", src.latitude);
                        intent.putExtra("D_LONGITUDE",src.longitude);
                        intent.putExtra("DEST", from);
                        startActivity(intent);
                    }
                });
            }
            cursor.close();

        }
       catch (Exception e){
        Log.d("tag",""+ e);
        }
    }

    public void gotoMyCurrentLocation(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null) {
                            myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            getAreaName();
                            goToLocationZoom(myLocation.latitude,myLocation.longitude,17);
                        }
                    }
                });
    }



    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void goToLocationZoom(double lat, double lng, double zoom) {
        myLocation = new LatLng(lat,lng);
        LatLng latLng = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, (float) zoom);
        this.mMap.moveCamera(cameraUpdate);
        setMarker();

    }

    private void setMarker() {

        if (marker != null) {
            marker.remove();
            marker = null;
        }

        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(myLocation.latitude,
                        myLocation.longitude))
                .draggable(false)
                .flat(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        marker = mMap.addMarker(options);
        showRandomVehicles();

    }

    String getAreaName(){
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        try {

            Log.d("tag","Location: "+myLocation.toString());
            list = geocoder.getFromLocation(myLocation.latitude,myLocation.longitude,5);

            Log.d("tag","LocationName: "+list.get(0).toString());

        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = list.get(0);
        myLocationName = new StringBuilder(address.getAddressLine(0));

        return address.getAddressLine(0);
    }

    public void gotoDestination(View view) {
        if(myLocation!=null){
            Intent intent = new Intent(SourceMapActivity.this, DestMapActivity.class);
            intent.putExtra("LATITUDE", myLocation.latitude);
            intent.putExtra("LONGITUDE", myLocation.longitude);
            intent.putExtra("SRC", myLocationName.toString());
            startActivity(intent);
        }
    }

    public void gotoPastBookings() {
        Intent intent = new Intent(this, PastRideViews.class);
        startActivity(intent);
    }

    public void gotoWalletRecharge() {
        Intent intent = new Intent(this, AddMoneyToWallet.class);
        startActivity(intent);
    }

    public void gotoCustomerSupport() {
        Intent intent = new Intent(this, CustomerSupport.class);
        startActivity(intent);
    }



    public void saveBookmark(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);


        alert.setTitle("Bookmark Location");
        alert.setMessage("Name this bookmark");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        input.setPadding(60,20,0,30);
        alert.setView(input);

        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                int idx = insertBookMartIntoTable(input.getText().toString());
                adapter.update(new BookmarkModel(idx, input.getText().toString(),getAreaName()));
                // Do something with value!
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }

    private int insertBookMartIntoTable(String bookmarkName) {
                //Inserting rides into database
                PastRidesDbHelper dbHelper = new PastRidesDbHelper(getApplicationContext());
                // Gets the data repository in write mode
                SQLiteDatabase db = dbHelper.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues values = new ContentValues();
                values.put(RidesSchema.BookMark.COLUMN_NAME, bookmarkName);
                values.put(RidesSchema.BookMark.COLUMN_LOCATION, getAreaName());
                // Insert the new row, returning the primary key value of the new row
                long newRowId = db.insert(RidesSchema.BookMark.TABLE_NAME, null, values);

                Log.d("bookmark",bookmarkName+" "+getAreaName()+" /->"+newRowId);
                return (int) newRowId;
    }

    public void showRandomVehicles(){
        getNearByLocation(myLocation.latitude,myLocation.longitude,50);
    }

    void getNearByLocation(double x0, double y0, int radius) {
        Random random = new Random();

        if (markfirst != null) {
            markfirst.remove();
            markfirst = null;
        }
        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        double u = random.nextDouble();
        double v = random.nextDouble();
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(Math.toRadians(y0));

        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;
        Log.d("tag", "my loc : "+myLocation.latitude+"..."+myLocation.longitude+"###"+foundLatitude+"  "+foundLongitude);
        //setting nearby vehicles
        MarkerOptions optionSrc = new MarkerOptions();
        optionSrc.position(new LatLng(foundLongitude,
                foundLatitude));
        optionSrc.icon(BitmapDescriptorFactory.fromResource(R.drawable.sedan1));
        markfirst = mMap.addMarker(optionSrc);

        if (markSecond != null) {
            markSecond.remove();
            markSecond = null;
        }

        u = random.nextDouble();
        v = random.nextDouble();
        w = radiusInDegrees * Math.sqrt(u);
        t = 2 * Math.PI * v;
        x = w * Math.cos(t);
        y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        new_x = x / Math.cos(Math.toRadians(y0));

        foundLongitude = new_x + x0;
        foundLatitude = y + y0;
        Log.d("tag", "my loc : "+myLocation.latitude+"..."+myLocation.longitude+"###"+foundLatitude+"  "+foundLongitude);
        //setting nearby vehicles
        optionSrc = new MarkerOptions();
        optionSrc.position(new LatLng(foundLongitude,
                foundLatitude));
        optionSrc.icon(BitmapDescriptorFactory.fromResource(R.drawable.sedan2));
        markSecond = mMap.addMarker(optionSrc);


        if (markThird != null) {
            markThird.remove();
            markThird = null;
        }

        u = random.nextDouble();
        v = random.nextDouble();
        w = radiusInDegrees * Math.sqrt(u);
        t = 2 * Math.PI * v;
        x = w * Math.cos(t);
        y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        new_x = x / Math.cos(Math.toRadians(y0));

        foundLongitude = new_x + x0;
        foundLatitude = y + y0;
        Log.d("tag", "my loc : "+myLocation.latitude+"..."+myLocation.longitude+"###"+foundLatitude+"  "+foundLongitude);
        //setting nearby vehicles
        optionSrc = new MarkerOptions();
        optionSrc.position(new LatLng(foundLongitude,
                foundLatitude));
        optionSrc.icon(BitmapDescriptorFactory.fromResource(R.drawable.auto1));
        markThird = mMap.addMarker(optionSrc);

    }

    @Override
    public void update(Address address) {
        myLocation = new LatLng(address.getLatitude(),address.getLongitude());
        listView = findViewById(R.id.listView);
        listView.setVisibility(View.GONE);
        goToLocationZoom(address.getLatitude(),address.getLongitude(), 17);
        EditText editText = binding.editTextTextPlaceName;
        editText.clearFocus();
        editText.setText(getAreaName());
    }

    private void userLocationmenuButton(){
        FloatingActionButton menuButton = (FloatingActionButton) findViewById(R.id.myLocationButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationButton.callOnClick();
//                Location location = mMap.getMyLocation();
//                if(location != null) { // Check to ensure coordinates aren't null, probably a better way of doing this...
//                    goToLocationZoom(location.getLatitude(),location.getLongitude(),17);
//                }
            }
        });
    }

    LatLng getLatLng(String placeName){
        Geocoder geocoder = new Geocoder(this);  // To whom the STUB has to be provided...
        List<Address> list = null;


        try {
            list = geocoder.getFromLocationName(placeName, 5);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = list.get(0);

        return new LatLng(address.getLatitude(),address.getLongitude());
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

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}