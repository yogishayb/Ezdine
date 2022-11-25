package com.insajet.ezdine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyOTPActivity extends AppCompatActivity {
    private String verificatiionId;
    private String mToken;
    private String phoneNumber;
    private FirebaseAuth firebaseAuth;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    private TextView tvResendOtp, tvDisplay, tvChangeNumber;

    int seconds;
    private boolean iscounting = true;
    private ProgressBar progressBar;
    private Button button;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.WHITE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        setContentView(R.layout.activity_verify_o_t_p);

        Toolbar toolbar = findViewById(R.id.toolbar);
        tvResendOtp = findViewById(R.id.tvResendOtp);
        tvDisplay = findViewById(R.id.tvDisplay);
        tvChangeNumber = findViewById(R.id.tvChangeNumber);
        button =  findViewById(R.id.btnVerifyOtp);
        final EditText etOtp = findViewById(R.id.etotp);
        progressBar = findViewById(R.id.progressBar);

        toolbar.setTitle("Verify");
        toolbar.setTitleTextColor(Color.BLACK);
        toolbar.setBackgroundColor(Color.WHITE);

        toolbar.setNavigationIcon(R.drawable.ic_baseline_chevron_left_24);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        verificatiionId = getIntent().getStringExtra("verificationId");
        mToken = getIntent().getStringExtra("token");
        phoneNumber = getIntent().getStringExtra("phoneNumber");
        sharedPreferences =getSharedPreferences("restaurant", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        firebaseAuth = FirebaseAuth.getInstance();


        String displayText = String.format(Locale.ENGLISH,"One time password sent to +91-%s number please enter same here to login",phoneNumber);
        tvDisplay.setText(displayText);


        tvChangeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvResendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(iscounting){
                    Toast.makeText(VerifyOTPActivity.this, "Wait", Toast.LENGTH_SHORT).show();
                }else {
                    tvResendOtp.setText("Wait.....");
                    resendOtp();

                    Toast.makeText(VerifyOTPActivity.this, "Resending", Toast.LENGTH_SHORT).show();
                }
            }
        });
        timeCounter();

        button.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();
            if(otp.length() != 6){
                Toast.makeText(VerifyOTPActivity.this, "Enter OTP", Toast.LENGTH_SHORT).show();
            }else {
                InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                progressBar.setVisibility(View.VISIBLE);
                button.setVisibility(View.INVISIBLE);
                new Thread(() -> {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificatiionId,otp);
                    firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
                                String uid = firebaseAuth.getCurrentUser().getUid();
                                HashMap<String, String > loginInfo = new HashMap<>();
                                loginInfo.put("phoneNumber",phoneNumber);
                                loginInfo.put("id",uid);
                                reference.child("users").child(uid).child("loginInfo").child("id").setValue(uid);
                                reference.child("users").child(uid).child("loginInfo").child("phoneNumber").setValue(phoneNumber);
                                editor.putString("uid",uid);
                                editor.putString("phoneNumber",phoneNumber);
                                editor.apply();
                                Intent intent = new Intent(VerifyOTPActivity.this,FirstTimeUserActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                runOnUiThread(() -> {
                                    progressBar.setVisibility(View.GONE);
                                    button.setVisibility(View.VISIBLE);
                                });
                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(VerifyOTPActivity.this, "Wrong OTP", Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                        button.setVisibility(View.VISIBLE);

                                    }
                                });
                            }
                        }
                    });
                }).start();

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return false;
    }

    void timeCounter(){
        CountDownTimer countDownTimer = new CountDownTimer(30000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                iscounting = true;
                int seconds =(int) millisUntilFinished/1000;
                int minutes = seconds/60;
                int sec = seconds%60;
                tvResendOtp.setTextColor(Color.BLACK);
                tvResendOtp.setText(String.format(Locale.ENGLISH,"%02d:%02d",minutes, sec));

            }

            @Override
            public void onFinish() {
                iscounting = false;

                tvResendOtp.setTextColor(getResources().getColor(R.color.pink));
                tvResendOtp.setTextColor(getResources().getColor(R.color.black));
                tvResendOtp.setText(String.format(Locale.ENGLISH,"%s","Resend now"));
            }
        };
        countDownTimer.start();
    }


    void resendOtp(){
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder()
                .setPhoneNumber(String.format("+91-%s",phoneNumber))
                .setTimeout(30L, TimeUnit.SECONDS)
                .setActivity(VerifyOTPActivity.this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifyOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        verificatiionId = s;
                        timeCounter();
                        Toast.makeText(VerifyOTPActivity.this, "Code resent", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.animate_fade_enter, R.anim.animate_fade_exit);

    }
}