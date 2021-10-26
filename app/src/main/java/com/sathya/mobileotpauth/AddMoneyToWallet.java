package com.sathya.mobileotpauth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.sathya.mobileotpauth.helper.Constants;
import com.sathya.mobileotpauth.helper.RideModel;

import org.json.JSONException;
import org.json.JSONObject;

public class AddMoneyToWallet extends AppCompatActivity implements PaymentResultListener {

    SharedPreferences sharedpreferences;
    EditText moneyToAdd;
    Button addMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money_to_wallet);
        getSupportActionBar().setTitle("Add money in wallet");

        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_test_lRJDdEOLIeHgri");
        Checkout.preload(getApplicationContext());
        sharedpreferences = getSharedPreferences(Constants.Wallet, Context.MODE_PRIVATE);
        getSupportActionBar().setSubtitle("Current Wallet Balance: ₹"+sharedpreferences.getInt(Constants.Wallet_Money,0));

        moneyToAdd = findViewById(R.id.moneyField);
        addMoney = findViewById(R.id.addMoney);

        moneyToAdd.setText(""+getIntent().getIntExtra(RideModel.PRICE,0));

        moneyToAdd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length() == 0){
                    moneyToAdd.setText(""+0);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int value = Integer.parseInt(moneyToAdd.getText().toString());
                if(value < 100){
                    moneyToAdd.setText("100");
                    Toast.makeText(AddMoneyToWallet.this,"Can't Add less then 100",Toast.LENGTH_LONG).show();
                }
                else if(value > 2000){
                    moneyToAdd.setText("2000");
                    Toast.makeText(AddMoneyToWallet.this,"Can't Add more then 2000",Toast.LENGTH_LONG).show();
                }
                else{
                    onlinePayment();
                }
            }
        });
    }


    public void onlinePayment(){
        Checkout checkout = new Checkout();

        // set your id as below
        checkout.setKeyID("rzp_test_lRJDdEOLIeHgri");

        // set image
        checkout.setImage(R.drawable.logo_1);

        // initialize json object
        JSONObject object = new JSONObject();
        try {
            // to put name
            object.put("name", "Let's Go");

            // put description
            object.put("description", "Test payment");

            // to set theme color
            object.put("theme.color", "");

            // put the currency
            object.put("currency", "INR");

            // put amount
            object.put("amount", Integer.parseInt(moneyToAdd.getText().toString())*100);

            // put email
            object.put("prefill.email", "test@mail.com");

            // open razorpay to checkout activity
            checkout.open(AddMoneyToWallet.this, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        SharedPreferences.Editor editor = sharedpreferences.edit();  // GE_HEALTH
        editor.putInt(Constants.Wallet_Money, sharedpreferences.getInt(Constants.Wallet_Money,0)+Integer.parseInt(moneyToAdd.getText().toString()));
        editor.commit();
        super.onBackPressed();
    }

    @Override
    public void onPaymentError(int i, String s) {

        Toast.makeText(AddMoneyToWallet.this,"Payment Failed",Toast.LENGTH_LONG).show();

    }
}