package com.sathya.mobileotpauth;

import androidx.activity.ComponentActivity;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import im.crisp.client.ChatActivity;
import im.crisp.client.Crisp;

public class CustomerSupport extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_support);

        getSupportActionBar().setTitle("Customer Support");

        Crisp.configure(getApplicationContext(), "d5e9ef30-0df8-48de-83f1-d16d100c7fa6");

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

        findViewById(R.id.liveChat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent crispIntent = new Intent(CustomerSupport.this, ChatActivity.class);
                startActivity(crispIntent);
            }
        });
    }
}