package com.sathya.mobileotpauth;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class CustomerSupport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);

        getSupportActionBar().setTitle("Customer Support");

        findViewById(R.id.askForCallback).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Wait for the callback within next 24 hrs", Toast.LENGTH_LONG).show();
                CustomerSupport.this.onBackPressed();
            }
        });

        findViewById(R.id.submitQuery).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"Your query is under review", Toast.LENGTH_LONG).show();
                    CustomerSupport.this.onBackPressed();
            }
        });
    }
}