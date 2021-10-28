package com.sathya.mobileotpauth.helper.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.sathya.mobileotpauth.MapsActivity;
import com.sathya.mobileotpauth.R;
import com.sathya.mobileotpauth.helper.KeyboardFragment;
import com.sathya.mobileotpauth.helper.models.RideModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PastRideListAdapter extends ArrayAdapter<RideModel> {
    Context context;
    KeyboardFragment.ConnectorForCallback callbackToPastRideViewsActivity;

    public PastRideListAdapter(@NonNull Context context,KeyboardFragment.ConnectorForCallback callbackToPastRideViewsActivity, ArrayList<RideModel> rideModelArrayList) {
        super(context, 0, rideModelArrayList);
        this.context = context;
        this.callbackToPastRideViewsActivity = callbackToPastRideViewsActivity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.past_rides_item, parent, false);
        }

        RideModel rideModel = getItem(position);
        TextView title = listitemView.findViewById(R.id.title);
        TextView from = listitemView.findViewById(R.id.from_place);
        TextView to = listitemView.findViewById(R.id.to_place);
        TextView price = listitemView.findViewById(R.id.price_of_ride);
        ImageView img = listitemView.findViewById(R.id.veh_type);
        ImageView swap = listitemView.findViewById(R.id.reverse);
        LinearLayout same = listitemView.findViewById(R.id.same);

        String date = rideModel.getPrice().substring(rideModel.getPrice().length()-19,rideModel.getPrice().length()-9);
        if(position==0){
            title.setText(date);
            title.setVisibility(View.VISIBLE);
        }
        else{
            String prevDate =getItem(position-1).getPrice().substring(getItem(position-1).getPrice().length()-19,getItem(position-1).getPrice().length()-9);
            if(!date.equals(prevDate)){
                title.setText(date);
                title.setVisibility(View.VISIBLE);
            }
        }
        from.setText(rideModel.getFrom());
        to.setText(rideModel.getTo());
        price.setText("Paid: ₹ "+ rideModel.getPrice());
        switch(rideModel.getType()){
            case "Prime Sedan" : img.setImageResource(R.drawable.prime_sedan);
                break;
            case "Sedan"       : img.setImageResource(R.drawable.sedan);
                break;
            case "Auto"        : img.setImageResource(R.drawable.auto);
                break;
            default            : img.setImageResource(R.drawable.bike);
        }
        same.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                LatLng src = getLatLng(rideModel.getFrom());
                LatLng dest = getLatLng(rideModel.getTo());
//                Source Coordinate
                intent.putExtra("S_LATITUDE", src.latitude);
                intent.putExtra("S_LONGITUDE", src.longitude);
                intent.putExtra("SRC", rideModel.getFrom());
                //Destination Coordinate
                intent.putExtra("D_LATITUDE", dest.latitude);
                intent.putExtra("D_LONGITUDE",dest.longitude);
                intent.putExtra("DEST", rideModel.getTo());
                context.startActivity(intent);
                callbackToPastRideViewsActivity.update(10);
            }
        });

        swap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MapsActivity.class);
                LatLng src = getLatLng(rideModel.getFrom());
                LatLng dest = getLatLng(rideModel.getTo());
//                Source Coordinate
                intent.putExtra("S_LATITUDE", dest.latitude);
                intent.putExtra("S_LONGITUDE", dest.longitude);
                intent.putExtra("SRC", rideModel.getTo());
                //Destination Coordinate
                intent.putExtra("D_LATITUDE", src.latitude);
                intent.putExtra("D_LONGITUDE",src.longitude);
                intent.putExtra("DEST", rideModel.getFrom());
                context.startActivity(intent);
                callbackToPastRideViewsActivity.update(10);
            }
        });
        return listitemView;
    }

    LatLng getLatLng(String placeName){
        Geocoder geocoder = new Geocoder(context);  // To whom the STUB has to be provided...
        List<Address> list = null;


        try {
            list = geocoder.getFromLocationName(placeName, 5);

        } catch (IOException e) {
            e.printStackTrace();
        }

        Address address = list.get(0);

        return new LatLng(address.getLatitude(),address.getLongitude());
    }
}