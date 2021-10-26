package com.sathya.mobileotpauth.helper;

import android.location.Address;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import android.content.Context;

import com.sathya.mobileotpauth.R;

import java.util.List;

public class SearchableAdapter extends ArrayAdapter {

    private List<Address> dataList;
    private Context mContext;
    UpdateAfterClick callbackContext;

    public SearchableAdapter(@NonNull Context context, UpdateAfterClick callbackContext, @NonNull List<Address> objects) {
        super(context, 0, objects);
        dataList = objects;
        mContext = context;
        this.callbackContext = callbackContext;
    }

    @Override
    public int getCount() {
        if(dataList != null)
        return dataList.size();
        else
            return 0;
    }

    @Override
    public Address getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {

        if (view == null) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callbackContext.update(getItem(position));
                view.setVisibility(View.GONE);
            }
        });
        TextView resultItem = (TextView) view.findViewById(R.id.address);
        resultItem.setText(getItem(position).getAddressLine(0));
        TextView resultItem1 = (TextView) view.findViewById(R.id.address1);
        resultItem1.setText(getItem(position).getCountryName());
        return view;
    }

    public interface UpdateAfterClick{
        void update(Address address);
    }
}