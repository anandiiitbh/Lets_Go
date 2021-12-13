package com.sathya.mobileotpauth;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.BaseColumns;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.sathya.mobileotpauth.databinding.ActivityDestMapBinding;
import com.sathya.mobileotpauth.dbConnectivity.PastRidesDbHelper;
import com.sathya.mobileotpauth.dbConnectivity.RidesSchema;
import com.sathya.mobileotpauth.helper.adapters.BookmarkAdapter;
import com.sathya.mobileotpauth.helper.models.BookmarkModel;
import com.sathya.mobileotpauth.helper.adapters.SearchableAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class DestMapActivity extends AppCompatActivity implements OnMapReadyCallback, SearchableAdapter.UpdateAfterClick {
    private ActivityDestMapBinding binding;
    private GoogleMap mMap;
    LatLng destLocation = null;
    StringBuilder destLocationName = null;
    Marker marker;
    ListView listView;
    View locationButton;

    ArrayList<BookmarkModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDestMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        hideSystemUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.dest_view);
        mapFragment.getMapAsync(this);

        EditText destination = binding.editTextDestPlaceName;
        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    findViewById(R.id.DestInput).setAlpha(1);
                }
                else {

                    findViewById(R.id.DestInput).setAlpha(0.7f);
                }
            }
        });
        findViewById(R.id.imageViewcleardest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                destination.setText("");
            }
        });

        destination.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()>0){
                    findViewById(R.id.imageViewcleardest).setVisibility(View.VISIBLE);
                }
                if(charSequence.length() == 0){
                    findViewById(R.id.imageViewcleardest).setVisibility(View.GONE);
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
                if(list != null && list.size()>0 && destLocation.latitude != list.get(0).getLatitude()){
                    listView = findViewById(R.id.destListView);
                    listView.setVisibility(View.VISIBLE);
                    listView.bringToFront();
                    listView.setTranslationZ(1);
                    ArrayAdapter adapter = new SearchableAdapter(getBaseContext(), DestMapActivity.this, list);
                    listView.setAdapter(adapter);
                }

            }
        });

        initArray();
    }

    @SuppressLint("ResourceType")
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(false);
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.mapTheme)));

        enableLocation();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.dest_view);
        locationButton = mapFragment.getView().findViewById(0x2);
        // Change the visibility of my location button
        if(locationButton != null)
            locationButton.setVisibility(View.GONE);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(getIntent().getDoubleExtra("LATITUDE",12),getIntent().getDoubleExtra("LONGITUDE",12)))
                .draggable(false)
                .flat(false));
        goToLocationZoom(getIntent().getDoubleExtra("LATITUDE",12),getIntent().getDoubleExtra("LONGITUDE",12),17);

        mMap.setOnCameraMoveListener((GoogleMap.OnCameraMoveListener) () -> {
            destLocation = mMap.getCameraPosition().target;
            setMarker();
        });

        userLocationFAB();

    }

    private void enableLocation() {

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
        mMap.setMyLocationEnabled(true);
    }

    private void setMarker() {

        if (marker != null) {
            marker.remove();
            marker = null;
        }

        MarkerOptions options = new MarkerOptions()
                .position(new LatLng(destLocation.latitude,
                        destLocation.longitude))
                .draggable(false)
                .flat(false)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        marker = mMap.addMarker(options);
    }


    private void goToLocationZoom(double lat, double lng, double zoom) {

        destLocation = new LatLng(lat, lng);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(destLocation, (float) zoom);
        this.mMap.moveCamera(cameraUpdate);
    }

    public void gotoBilling(View view) {
        if(destLocation!=null){
            Intent intent = new Intent(DestMapActivity.this, MapsActivity.class);
            //Source Coordinate
            intent.putExtra("S_LATITUDE", getIntent().getDoubleExtra("LATITUDE",12));
            intent.putExtra("S_LONGITUDE", getIntent().getDoubleExtra("LONGITUDE",12));
            intent.putExtra("SRC", getIntent().getStringExtra("SRC"));
            //Destination Coordinate
            intent.putExtra("D_LATITUDE", destLocation.latitude);
            intent.putExtra("D_LONGITUDE",destLocation.longitude);
            intent.putExtra("DEST", getAreaName());
            Log.d("Location",getIntent().getStringExtra("SRC")+"\n"+ getAreaName());
            startActivity(intent);
            finish();
        }
    }

    String getAreaName(){
        Geocoder geocoder = new Geocoder(this);
        List<Address> list = null;
        try {
            list = geocoder.getFromLocation(destLocation.latitude,destLocation.longitude,5);


        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = list.get(0);

        destLocationName = new StringBuilder(address.getAddressLine(0));

        setMarker();
        return address.getAddressLine(0);
    }

    private void userLocationFAB(){
        ImageView FAB = (ImageView) findViewById(R.id.enableLocation);
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationButton.callOnClick();
            }
        });
    }

    @Override
    public void update(Address address) {
        destLocation = new LatLng(address.getLatitude(),address.getLongitude());
        listView = findViewById(R.id.destListView);
        listView.setVisibility(View.GONE);
        goToLocationZoom(address.getLatitude(),address.getLongitude(), 17);
        EditText editText = binding.editTextDestPlaceName;
        editText.clearFocus();
        editText.setText(getAreaName());
    }

    public void showBookMarks(View view) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Select Location");

        BookmarkAdapter adapter = new BookmarkAdapter(DestMapActivity.this, DestMapActivity.this, list);
        alert.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setPositiveButton("Close", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alert.show();
    }
    private void initArray(){

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
                }
            });

        }
        catch (Exception e){
            Log.d("tag",""+ e);
        }

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

    public void voiceSearchDest(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 11);
        } else {
            Toast.makeText(this, "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 11:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    EditText source = binding.editTextDestPlaceName;
                    source.setText(result.get(0));
                }
                break;
        }
    }
}