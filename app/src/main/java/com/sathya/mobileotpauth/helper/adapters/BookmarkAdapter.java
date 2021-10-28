package com.sathya.mobileotpauth.helper.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sathya.mobileotpauth.R;
import com.sathya.mobileotpauth.dbConnectivity.PastRidesDbHelper;
import com.sathya.mobileotpauth.dbConnectivity.RidesSchema;
import com.sathya.mobileotpauth.helper.models.BookmarkModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookmarkAdapter extends ArrayAdapter<BookmarkModel> {
    Context context;
    SearchableAdapter.UpdateAfterClick callback;
    ArrayList<BookmarkModel> items = null;

    public BookmarkAdapter(@NonNull Context context, SearchableAdapter.UpdateAfterClick callback, ArrayList<BookmarkModel> rideModelArrayList) {
        super(context, 0, rideModelArrayList);
        this.context = context;
        this.callback = callback;
        items = rideModelArrayList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listitemView = convertView;
        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.bookmark_item, parent, false);
        }

        listitemView.setPadding(350,60,60,60);
        ((TextView) listitemView.findViewById(R.id.bookmarkName)).setText(items.get(position).getName());

        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address address = getAddress(items.get(position).getLocation());
                callback.update(address);
            }
        });
        listitemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PastRidesDbHelper dbHelper = new PastRidesDbHelper(context.getApplicationContext());
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String selection = RidesSchema.BookMark._ID + " LIKE ?";
                String[] selectionArgs = { String.valueOf(items.get(position).getId()) };
                int deletedRows = db.delete(RidesSchema.BookMark.TABLE_NAME, selection, selectionArgs);
                items.remove(position);
                Toast.makeText(context,"Bookmark Removed",Toast.LENGTH_LONG).show();
                notifyDataSetChanged();
                return false;
            }
        });
        return listitemView;
    }

    public void update(BookmarkModel bookmark){
        items.add(0,bookmark);
        notifyDataSetChanged();
    }

    Address getAddress(String placeName){
        Geocoder geocoder = new Geocoder(context);  // To whom the STUB has to be provided...
        List<Address> list = null;


        try {
            list = geocoder.getFromLocationName(placeName, 5);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list.get(0);
    }
}