package com.sathya.mobileotpauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sathya.mobileotpauth.helper.Constants;
import com.sathya.mobileotpauth.helper.KeyboardFragment;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OtpValidation extends AppCompatActivity implements KeyboardFragment.ConnectorForCallback, View.OnClickListener{
    String verificationId = null;
    String phoneNumber;
//    String otp;
    String userOtp="";
    KeyboardFragment keyboard = null;
    EditText otp0,otp1,otp2,otp3,otp4,otp5;
    SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_validation);
        getSupportActionBar().hide();
        // MainActivity.GenerateOTP();
        keyboard     = new KeyboardFragment(this);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        TextView otpSentMsg = findViewById(R.id.otpSentMsg);
        otpSentMsg.setText(" \""+phoneNumber+"\"");
        Log.d("tag"," Phone Number OTP VALIDATED "+phoneNumber);

        verificationId = getIntent().getStringExtra("verificationID");

       // sentOtp(phoneNumber);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.keyboardLayout1,keyboard)
                .commit();



        findViewById(R.id.changeNumber).setOnClickListener(this);
        findViewById(R.id.resendOtp).setOnClickListener(this);

        otp0 = findViewById(R.id.otp0);
        otp1 = findViewById(R.id.otp1);
        otp2 = findViewById(R.id.otp2);
        otp3 = findViewById(R.id.otp3);
        otp4 = findViewById(R.id.otp4);
        otp5 = findViewById(R.id.otp5);
        otp0.setOnClickListener(this);
        otp1.setOnClickListener(this);
        otp2.setOnClickListener(this);
        otp3.setOnClickListener(this);
        otp4.setOnClickListener(this);
        otp5.setOnClickListener(this);

    }

    void insertNumber(String key) {
        int length = userOtp.length();
        if(key.equals("-1") && length <=6 && length >=1){
            switch (length) {
                case 1: otp0.setText("");
                break;
                case 2: otp1.setText("");
                    break;
                case 3: otp2.setText("");
                    break;
                case 4: otp3.setText("");
                    break;
                case 5: otp4.setText("");
                    break;
                case 6: otp5.setText("");
            }
            userOtp = userOtp.substring(0,length-1);
        }
        if (length < 6 && !key.equals("-1")) {
            switch (length) {
                case 0 :
                    otp0.setText(key);
                    break;
                case 1 :
                    otp1.setText(key);
                    break;
                case 2 :
                    otp2.setText(key);
                    break;
                case 3 :
                    otp3.setText(key);
                    break;
                case 4 :
                    otp4.setText(key);
                    break;
                case 5 :
                    otp5.setText(key);
                    break;
            }
            userOtp+=key;
            Log.d("tag"," userOTP "+userOtp);
        }

    }

    public static boolean isValid(String s)
    {
        Pattern p = Pattern.compile("^\\d{10}$");
        Matcher m = p.matcher(s);
        return (m.matches());
    }

    @Override
    public void update(int key) {

        if(key<10){
            insertNumber(new Integer(key).toString());
        }
        if(key==10){
//            sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editor = sharedpreferences.edit();  // GE_HEALTH
//            editor.putString(Constants.MOBILE, phoneNumber);
//            editor.putBoolean(Constants.IS_LOGIN, true);
//            editor.commit();
//
//            //Initial Wallet amount
//            SharedPreferences sharedpreferencess = getSharedPreferences(Constants.Wallet, Context.MODE_PRIVATE);
//            SharedPreferences.Editor editorr = sharedpreferencess.edit();  // GE_HEALTH
//            editorr.putInt(Constants.Wallet_Money, 100);
//            editorr.commit();
//            Intent intent = new Intent(getBaseContext(), SourceMapActivity.class);
//            intent.putExtra("STATUS", "VERIFICATION SUCCESS");
//            startActivity(intent);
//            finish();
            PhoneAuthCredentialMethod(verificationId, userOtp);
        }

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.changeNumber:

                findViewById(R.id.changeNumber).setBackgroundColor(Color.WHITE);
                finish();

                break;
            case R.id.resendOtp:
                sendOTP();
                break;
            default: keyboard.awakeKeyboard();
        }
    }

    private void sendOTP() {
        clearOtpPanel();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                120,
                TimeUnit.SECONDS,
                this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                    @Override
                    public void onVerificationCompleted(@NonNull  PhoneAuthCredential phoneAuthCredential) {
                        Toast.makeText(getBaseContext(), "Verification Completed..", Toast.LENGTH_SHORT).show();
                        Log.d("tag","Verification Completed..");
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(getBaseContext(), "Verification FAILED..", Toast.LENGTH_SHORT).show();
                        Log.d("tag","Verification FAILED..");
                    }

                    @Override
                    public void onCodeSent(@NonNull  String verificationID, @NonNull  PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(verificationID, forceResendingToken);
                        Log.d("tag"," VerificationID "+verificationID);
//
//                        Intent intent =  new Intent(getBaseContext(), OtpValidation.class);
//                        // intent.putExtra("CELL",phoneNumber);
//                        intent.putExtra("verificationID",verificationID);  // s pick this from  onCodeSent(@NonNull String verificationID,
//
//                        intent.putExtra("phoneNumber",phoneNumber);
//                        //  intent.putExtra("MainActivityContext", (Parcelable) this); // or pass this via interface so  GenerateOTP()  can be called
//                        startActivity(intent);
                    }
                }


        );
    }

    void clearOtpPanel(){
        otp0.setText("");
        otp1.setText("");
        otp2.setText("");
        otp3.setText("");
        otp4.setText("");
        otp5.setText("");
        userOtp="";
    }

    void PhoneAuthCredentialMethod( String verificationId , String userOtp){
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                verificationId, userOtp

        );
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull  Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("tag", "OTP Verified its Success");
                            Toast.makeText(OtpValidation.this, "OTP Verified..", Toast.LENGTH_SHORT).show();

                            Log.d("tag", "VERIFICATION SUCCESS");
                            sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();  // GE_HEALTH
                            editor.putString(Constants.MOBILE, phoneNumber);
                            editor.putBoolean(Constants.IS_LOGIN, true);
                            editor.commit();

                            //Initial Wallet amount
                            SharedPreferences sharedpreferencess = getSharedPreferences(Constants.Wallet, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editorr = sharedpreferencess.edit();  // GE_HEALTH
                            editorr.putInt(Constants.Wallet_Money, 100);
                            editorr.commit();
                            Intent intent = new Intent(getBaseContext(), SourceMapActivity.class);
                            intent.putExtra("STATUS", "VERIFICATION SUCCESS");
                            startActivity(intent);
                            finish();

                        } else {
                            Toast.makeText(OtpValidation.this, "Verification Failed", Toast.LENGTH_SHORT).show();
                            Log.d("tag", "VERIFICATION Failed");
                        }
                    }

                });


}



}