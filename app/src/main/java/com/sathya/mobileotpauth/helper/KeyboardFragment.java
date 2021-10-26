package com.sathya.mobileotpauth.helper;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sathya.mobileotpauth.R;


public class KeyboardFragment extends Fragment implements View.OnClickListener{

    View v;
    ConnectorForCallback numberField;

    public KeyboardFragment(ConnectorForCallback numberField) {
        this.numberField = numberField;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_keyboard, container, false);
        v.findViewById(R.id.show).setOnClickListener(this);
        v.findViewById(R.id.hide).setOnClickListener(this);
        v.findViewById(R.id.one).setOnClickListener(this);
        v.findViewById(R.id.two).setOnClickListener(this);
        v.findViewById(R.id.three).setOnClickListener(this);
        v.findViewById(R.id.four).setOnClickListener(this);
        v.findViewById(R.id.five).setOnClickListener(this);
        v.findViewById(R.id.six).setOnClickListener(this);
        v.findViewById(R.id.seven).setOnClickListener(this);
        v.findViewById(R.id.eight).setOnClickListener(this);
        v.findViewById(R.id.nine).setOnClickListener(this);
        v.findViewById(R.id.zero).setOnClickListener(this);
        v.findViewById(R.id.delete).setOnClickListener(this);
        v.findViewById(R.id.done).setOnClickListener(this);
        v.findViewById(R.id.keyboard).setVisibility(View.GONE);
        return v;
    }

    public void awakeKeyboard(){
        v.findViewById(R.id.keyboard).setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.show :    v.findViewById(R.id.keyboard).setVisibility(View.VISIBLE);
                break;
            case R.id.hide :    v.findViewById(R.id.keyboard).setVisibility(View.GONE);
                break;
            case R.id.zero:    setOnClick(0);
                break;
            case R.id.one:    setOnClick(1);
                break;
            case R.id.two:    setOnClick(2);
                break;
            case R.id.three:    setOnClick(3);
                break;
            case R.id.four:    setOnClick(4);
                break;
            case R.id.five:    setOnClick(5);
                break;
            case R.id.six:    setOnClick(6);
                break;
            case R.id.seven:    setOnClick(7);
                break;
            case R.id.eight:    setOnClick(8);
                break;
            case R.id.nine:    setOnClick(9);
                break;
            case R.id.done:    setOnClick(10);
                break;
            case R.id.delete:    setOnClick(-1);
                break;
        }
    }

    void setOnClick(int key){
        numberField.update(key);
    }

    public interface ConnectorForCallback {
        void update(int key);
    }
}