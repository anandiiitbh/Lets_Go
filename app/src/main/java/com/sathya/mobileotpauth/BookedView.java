package com.sathya.mobileotpauth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sathya.mobileotpauth.dbConnectivity.PastRidesDbHelper;
import com.sathya.mobileotpauth.dbConnectivity.RidesSchema;
import com.sathya.mobileotpauth.helper.Constants;
import com.sathya.mobileotpauth.helper.NotificationBuildHelper;
import com.sathya.mobileotpauth.helper.models.RideModel;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.protocol.types.Track;

import java.io.IOException;
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
    private static final String CLIENT_ID = "4aeea5d5556743f3a6219c7f9971f63a";
    private static final String REDIRECT_URI = "lets-go-app-login://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    CardView musicContainer = null;
    CardView musicContainerClosed = null;
    Animation musicButtonOpen = null;
    Animation musicButtonClose = null;
    ImageView playPauseButton = null;
    TextView musicText = null;
    boolean playPaused = false;

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



//        menuButton Menu buttons
        musicContainer = (CardView) findViewById(R.id.musicContainer);
        musicContainerClosed = (CardView) findViewById(R.id.musicContainerClosed);
        playPauseButton = findViewById(R.id.playPause);
        musicText = findViewById(R.id.musicText);

        musicButtonOpen = AnimationUtils.loadAnimation
                (this, R.anim.music_player_open);
        musicButtonClose = AnimationUtils.loadAnimation
                (this, R.anim.music_player_close);

        musicContainerClosed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMenuButton(false);
            }
        });
        findViewById(R.id.musicCloser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateMenuButton(true);
            }
        });
        findViewById(R.id.skipNext).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSpotifyAppRemote.getPlayerApi().skipNext();
            }
        });
        findViewById(R.id.playPause).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(playPaused){
                    mSpotifyAppRemote.getPlayerApi().pause();
                    playPauseButton.setImageDrawable(getDrawable(R.drawable.ic_sharp_play_arrow_24));
                    playPaused=false;
                }
                else{
                    mSpotifyAppRemote.getPlayerApi().resume();
                    playPauseButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
                    playPaused=true;
                }
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(this, connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d("MainActivity", "Connected! Yay!");

                        // Now you can start interacting with App Remote

                    }

                    public void onFailure(Throwable throwable) {
                        Log.e("MyActivity", throwable.getMessage(), throwable);

                        // Something went wrong when attempting to connect! Handle errors here
                    }
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected() {

        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

        mSpotifyAppRemote.getPlayerApi()
                .subscribeToPlayerState()
                .setEventCallback(playerState -> {
                    final Track track = playerState.track;
                    if (track != null) {
                        musicText.setText(track.name+" : "+track.artist.name);
                        Log.d("MainActivity", track.name + " by " + track.artist.name);
                    }
                });
    }

    private void animateMenuButton(boolean isOpen){
        if (isOpen){
            //SimleButtonState
            musicContainer.startAnimation(musicButtonClose);
            mSpotifyAppRemote.getPlayerApi().pause();
            musicContainerClosed.setVisibility(View.VISIBLE);
            playPaused=false;
        }else {

            //MusicState
            musicContainer.setVisibility(View.VISIBLE);
            musicContainer.startAnimation(musicButtonOpen);
            musicContainerClosed.setVisibility(View.GONE);
            playPauseButton.setImageDrawable(getDrawable(R.drawable.ic_baseline_pause_24));
            connected();
            playPaused=true;
        }
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