package com.insajet.ezdine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.insajet.ezdine.model.RestInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FirstTimeUserActivity extends AppCompatActivity {


    DatabaseReference databaseReference;

    private String uid,phoneNumber;
    private SharedPreferences sharedPreferences,restInfoSharedPreferences;
    private SharedPreferences.Editor editor,restInfoSharedEditor;

    private EditText etOwnerName, etRestName, etEmail;
    private LinearLayout linearLayout;
    private TextView textView;
    private Button buttonNext;

    private ValueEventListener valueEventListener, valueEventListener1;
    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("restaurant",MODE_PRIVATE);
        restInfoSharedPreferences = getSharedPreferences("restInfo",MODE_PRIVATE);
        editor = sharedPreferences.edit();

        restInfoSharedEditor = restInfoSharedPreferences.edit();

        uid = sharedPreferences.getString("uid","0");
        phoneNumber = sharedPreferences.getString("phoneNumber","0");
        setContentView(R.layout.activity_first_time_user);






        etRestName = findViewById(R.id.etrestName);
        etEmail = findViewById(R.id.etEmail);
        etOwnerName = findViewById(R.id.etOwnerName);

        buttonNext = findViewById(R.id.btnNext);
        linearLayout = findViewById(R.id.ll);
        textView = findViewById(R.id.tvPleaseWait);
        textView.setVisibility(View.VISIBLE);
        linearLayout.setVisibility(View.INVISIBLE);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        getInfo();



        buttonNext.setOnClickListener(v -> updateRestInfo());


    }

    void updateRestInfo(){
        String restName = etRestName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String ownerName = etOwnerName.getText().toString().trim();

        if(restName.isEmpty() || email.isEmpty() || ownerName.isEmpty()){
            Toast.makeText(this, "Input cant be empty", Toast.LENGTH_SHORT).show();

        }else if (!isValidEmailId(email)){
            Toast.makeText(this, "Enter valid email ID", Toast.LENGTH_SHORT).show();
        }else {
            textView.setVisibility(View.VISIBLE);
            linearLayout.setVisibility(View.INVISIBLE);
            RestInfo restInfo = new RestInfo(uid,restName,ownerName,phoneNumber,email,"0","0");
            databaseReference.child("users").child(uid).child("restInfo").setValue(restInfo);
            databaseReference.child("users").child(uid).child("loginInfo").child("isOld").setValue("true");

            getInfo();
        }


    }

    void getInfo(){                                                         
         valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild("isOld")){
                    String isOld = snapshot.child("isOld").getValue(String.class);
                    assert isOld != null;
                    if(isOld.equals("true")){
                        valueEventListener1 = new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                RestInfo restInfo = snapshot.getValue(RestInfo.class);
                                assert restInfo != null;
                                restInfoSharedEditor.putString("restName",restInfo.getRestName());
                                restInfoSharedEditor.putString("ownerName",restInfo.getOwnerName());
                                restInfoSharedEditor.putString("id",restInfo.getId());
                                restInfoSharedEditor.putString("phoneNumber",restInfo.getPhoneNumber());
                                restInfoSharedEditor.putString("email",restInfo.getEmail());
                                restInfoSharedEditor.putString("address",restInfo.getAddress());
                                restInfoSharedEditor.putString("website",restInfo.getWebsite());
                                restInfoSharedEditor.apply();
                                editor.putString("restName",restInfo.getRestName());
                                editor.putString("hasInfo","yes");
                                editor.commit();
                                removeLi();
                                Intent intent = new Intent(FirstTimeUserActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);


                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        };
                        databaseReference.child("users").child(uid).child("restInfo").addValueEventListener(valueEventListener1);
                    }
                }else {
                    textView.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

        databaseReference.child("users").child(uid).child("loginInfo").addListenerForSingleValueEvent(valueEventListener);

    }


    public static boolean isValidEmailId(String email) {
        String emailPattern = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern p = Pattern.compile(emailPattern);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    void removeLi(){
        databaseReference.child("users").child(uid).child("restInfo").removeEventListener(valueEventListener);
        databaseReference.child("users").child(uid).child("loginInfo").removeEventListener(valueEventListener1);
    }
}