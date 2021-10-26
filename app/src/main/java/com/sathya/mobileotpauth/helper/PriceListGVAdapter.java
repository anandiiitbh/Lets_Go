package com.sathya.mobileotpauth.helper;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.icu.util.TimeZone;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sathya.mobileotpauth.BookedView;
import com.sathya.mobileotpauth.R;
import com.sathya.mobileotpauth.dbConnectivity.PastRidesDbHelper;
import com.sathya.mobileotpauth.dbConnectivity.RidesSchema;
import com.sathya.mobileotpauth.helper.KeyboardFragment;
import com.sathya.mobileotpauth.helper.RideModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

public class PriceListGVAdapter extends ArrayAdapter<RideModel> {
    callback callbackToMapsActivity;
    Context context;

    public PriceListGVAdapter(@NonNull Context context, ArrayList<RideModel> rideModelArrayList,
                              callback callbackToMapsActivity) {
        super(context, 0, rideModelArrayList);
        this.context = context;
        this.callbackToMapsActivity = callbackToMapsActivity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }

        RideModel rideModel = getItem(position);
        TextView courseTV = listitemView.findViewById(R.id.idTVPrice);
        ImageView courseIV = listitemView.findViewById(R.id.idIVprice);
        courseTV.setText(rideModel.getType()+" E.P. : ₹ "+ rideModel.getPrice());
        courseIV.setImageResource(rideModel.getImgid());
        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callbackToMapsActivity.callForPayment(rideModel);
            }
        });
        return listitemView;
    }

    public interface callback{
        void callForPayment(RideModel rideModel);
    }
}