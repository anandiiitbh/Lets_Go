package com.sathya.mobileotpauth.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.sathya.mobileotpauth.R;
import com.sathya.mobileotpauth.dbConnectivity.PastRidesDbHelper;
import com.sathya.mobileotpauth.dbConnectivity.RidesSchema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HorizontalBookmarkAdapter extends RecyclerView.Adapter<HorizontalBookmarkAdapter.ViewHolder> {


    Context context;
    SearchableAdapter.UpdateAfterClick callback;
    ArrayList<BookmarkModel> items = null;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;
        View view;
        LinearLayout layout;
        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            this.view = view;
            textView = (TextView) view.findViewById(R.id.bookmarkName);
            layout = view.findViewById(R.id.bookmarkContainer);
        }

        public LinearLayout getContainer(){return layout;}
        public TextView getTextView() {
            return textView;
        }
        public View getView(){
            return view;
        }
    }

    public HorizontalBookmarkAdapter(Context context, ArrayList<BookmarkModel> items, SearchableAdapter.UpdateAfterClick callback) {
        this.items = items;
        this.callback = callback;
        this.context = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.bookmark_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(items.get(position).getName());
        Random rnd = new Random();
        int currentColor = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

//        viewHolder.getContainer().setBackgroundColor(currentColor);
        viewHolder.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Address address = getAddress(items.get(position).getLocation());
                callback.update(address);
            }
        });
        viewHolder.getView().setOnLongClickListener(new View.OnLongClickListener() {
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
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