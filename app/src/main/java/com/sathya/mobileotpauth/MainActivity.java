package com.sathya.mobileotpauth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;


import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sathya.mobileotpauth.helper.Constants;
import com.sathya.mobileotpauth.helper.KeyboardFragment;
import com.sathya.mobileotpauth.helper.NotificationBuildHelper;

import java.util.concurrent.TimeUnit;

import im.crisp.client.Crisp;

public class MainActivity extends AppCompatActivity implements KeyboardFragment.ConnectorForCallback {

    KeyboardFragment keyboard = null;
    EditText editText;
    String phoneNumber ="+91";
    SharedPreferences sharedpreferences;
    Window window;

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NotificationBuildHelper.createNotificationChannel(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
        Crisp.configure(getApplicationContext(), "d5e9ef30-0df8-48de-83f1-d16d100c7fa6");
        hideSystemUI();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        window = this.getWindow();
        window.setStatusBarColor(this.getResources().getColor(R.color.splash));
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                sharedpreferences = getSharedPreferences(Constants.MyPREFERENCES, Context.MODE_PRIVATE);
                if(checkForLogin(sharedpreferences)){
                    Toast.makeText(MainActivity.this, "You have Logged in as :  "
                            +sharedpreferences.getString(Constants.MOBILE,""), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, SourceMapActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    window.setStatusBarColor(getBaseContext().getResources().getColor(R.color.gray));
                    findViewById(R.id.splash).setVisibility(View.GONE);
                }
            }
        }, 3000);

        keyboard     = new KeyboardFragment(this);

        editText = findViewById(R.id.editTextPhone);
        editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                keyboard.awakeKeyboard();
            }
        });


        phoneNumber = "+91";
        editText.setText("");


        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_layout2,keyboard)
                .commit();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:" + credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);

                Toast.makeText(MainActivity.this, "onVerificationFailed", Toast.LENGTH_SHORT).show();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request

                    Toast.makeText(MainActivity.this, "Invalid request", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Toast.makeText(MainActivity.this, "The SMS quota for the project has been exceeded", Toast.LENGTH_SHORT).show();
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);
                Toast.makeText(MainActivity.this, "OTP Sent..", Toast.LENGTH_SHORT).show();

                Intent intent =  new Intent(getBaseContext(), OtpValidation.class);
                // intent.putExtra("CELL",phoneNumber);
                intent.putExtra("verificationID",verificationId);  // s pick this from  onCodeSent(@NonNull String verificationID,

                intent.putExtra("phoneNumber",phoneNumber);
                //  intent.putExtra("MainActivityContext", (Parcelable) this); // or pass this via interface so  GenerateOTP()  can be called
                startActivity(intent);
                // Save verification ID and resending token so we can use them later
//                mVerificationId = verificationId;
//                mResendToken = token;
            }
        };
    }

    public static boolean checkForLogin(SharedPreferences sharedpreferences) {
        if(sharedpreferences.contains(Constants.IS_LOGIN) && sharedpreferences.getBoolean(Constants.IS_LOGIN,false)) {
            return true;
        }
        else{
            return false;
        }
    }




    void insertNumber(String key) {
        String phone = editText.getText().toString();
        int length = phone.length();
        if(key.equals("-1") && length>0){
            switch (length) {
                case 5: phone=phone.substring(0,3);
                    break;
                case 9: phone=phone.substring(0,7);
                    break;
                default:
                    phone = phone.substring(0,length-1);
            }
        }
        else{
            if (length < 12 && !key.equals("-1")) {
                switch (length) {
                    case 3:
                    case 7:
                        phone += " " + key;
                        break;
                    default:
                        phone += key;
                }
            }
        }
        editText.setText(phone);
        phoneNumber = "+91"+phone.replaceAll("\\s", "");
        Log.d("phone------>",phoneNumber+" : "+phoneNumber.length());
    }

    @Override
    public void update(int key) {

        if(key<10)
            insertNumber(new Integer(key).toString());
        if(key == 10 && phoneNumber.length()>12){

            // TODO Bring in generate OTP  module here..
            // under onCode Sent method bring the following line


            GenerateOTP();
        }
    }

    private void GenerateOTP() {


        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(120L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);



        // TODO Bring in generate OTP  module here..
        // under onCode Sent method bring the following line
//        PhoneAuthProvider.getInstance().verifyPhoneNumber(
//                phoneNumber,
//                120,
//                TimeUnit.SECONDS,
//                MainActivity.this,
//                new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
//
//                    @Override
//                    public void onVerificationCompleted(@NonNull  PhoneAuthCredential phoneAuthCredential) {
//                        Toast.makeText(MainActivity.this, "Verification Completed..", Toast.LENGTH_SHORT).show();
//                         Log.d("tag","Verification Completed..");
//                    }
//
//                    @Override
//                    public void onVerificationFailed(@NonNull  FirebaseException e) {
//                        Toast.makeText(MainActivity.this, "Verification FAILED..", Toast.LENGTH_SHORT).show();
//                        Log.d("tag","Verification FAILED..");
//                    }
//
//                    @Override
//                    public void onCodeSent(@NonNull  String verificationID, @NonNull  PhoneAuthProvider.ForceResendingToken forceResendingToken) {
//                        super.onCodeSent(verificationID, forceResendingToken);
//                         Log.d("tag"," VerificationID "+verificationID);
//                        Toast.makeText(MainActivity.this, "OTP Sent..", Toast.LENGTH_SHORT).show();
//
//                        Intent intent =  new Intent(getBaseContext(), OtpValidation.class);
//                        // intent.putExtra("CELL",phoneNumber);
//                        intent.putExtra("verificationID",verificationID);  // s pick this from  onCodeSent(@NonNull String verificationID,
//
//                        intent.putExtra("phoneNumber",phoneNumber);
//                        //  intent.putExtra("MainActivityContext", (Parcelable) this); // or pass this via interface so  GenerateOTP()  can be called
//                        startActivity(intent);
//                    }
//                }
//
//
//        );



    }



    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }



}