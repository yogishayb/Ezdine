package com.insajet.ezdine;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class SendOTPActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        setContentView(R.layout.activity_send_o_t_p);
        sharedPreferences = getSharedPreferences("restaurant",MODE_PRIVATE);


        final EditText etPhoneNumber = findViewById(R.id.etMobileNumber);
        final Button btnSendOtp = findViewById(R.id.btnSendOtp);
        final ProgressBar progressBar = findViewById(R.id.progressBar);


        btnSendOtp.setOnClickListener(v -> {
            String phoneNumber = etPhoneNumber.getText().toString().trim();
            if(phoneNumber.length() != 10){
                Toast.makeText(SendOTPActivity.this, "Enter mobile number correctly", Toast.LENGTH_SHORT).show();
            }else {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                progressBar.setVisibility(View.VISIBLE);
                btnSendOtp.setVisibility(View.INVISIBLE);
                PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                        .setPhoneNumber(String.format("+91-%s",phoneNumber))
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .setActivity(SendOTPActivity.this)
                        .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {

                                Toast.makeText(SendOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                btnSendOtp.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    Intent intent = new Intent(SendOTPActivity.this,VerifyOTPActivity.class);
                                    intent.putExtra("phoneNumber",phoneNumber);
                                    intent.putExtra("verificationId",s);
                                    intent.putExtra("token",forceResendingToken);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.animate_fade_enter, R.anim.animate_fade_exit);
                                    progressBar.setVisibility(View.GONE);
                                    btnSendOtp.setVisibility(View.VISIBLE);
                            }
                        })
                        .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser !=null){
            if(sharedPreferences.contains("hasInfo")){
                if(sharedPreferences.getString("hasInfo","0").equals("yes")){
                    startActivity(new Intent(SendOTPActivity.this,MainActivity.class));
                    finish();
                }
            }else{
                startActivity(new Intent(SendOTPActivity.this, FirstTimeUserActivity.class));
                finish();
            }

        }
    }
}