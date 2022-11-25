package com.insajet.ezdine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.insajet.ezdine.adapters.ProfileAdapter;
import com.insajet.ezdine.model.ProfileModel;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity implements Attri {


    private String restName;
    private String ownerName;
    private String id;
    private String email;
    private String phoneNumber;
    private String website;
    private String address;

    private SharedPreferences restInfoPreferences,sharedPreferences,categoryCountPref;
    private SharedPreferences.Editor restInfoEditor, restEditor, categoryCountEditor;
    private ArrayList<ProfileModel> profileModels;


    private static ProfileActivity instance;
    private Toolbar toolbar;
    private ListView listView;

    DatabaseReference databaseReference;

    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        String uid = getIntent().getStringExtra("uid");
        setContentView(R.layout.activity_profile);

        restInfoPreferences = getSharedPreferences(restInfoPrefName,MODE_PRIVATE);
        sharedPreferences = getSharedPreferences(restPrefName,MODE_PRIVATE);
        categoryCountPref = getSharedPreferences(categoryCountPrefName,MODE_PRIVATE);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid).child("restInfo");


        restEditor = sharedPreferences.edit();
        restInfoEditor = restInfoPreferences.edit();
        categoryCountEditor = categoryCountPref.edit();

        instance = this;

        toolbar = findViewById(R.id.toolbar);
        listView = findViewById(R.id.listView);
        relativeLayout = findViewById(R.id.relativateLayout);

        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setTitle("Profile");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        listUpdate();



    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }
        return false;
    }

    public static ProfileActivity getInstance(){
        return instance;
    }

    public void listUpdate(){

        ownerName = restInfoPreferences.getString("ownerName","d");
        restName = restInfoPreferences.getString("restName","d");
        id = restInfoPreferences.getString("id","d");
        email = restInfoPreferences.getString("email","d");
        phoneNumber = restInfoPreferences.getString("phoneNumber","d");
        website = restInfoPreferences.getString("website","d");
        address = restInfoPreferences.getString("address","d");


        profileModels = new ArrayList<>();
        profileModels.clear();
        profileModels.add(new ProfileModel("User Id",id,R.drawable.id,0));
        profileModels.add(new ProfileModel("Owner Name",ownerName,R.drawable.owner,0));

        profileModels.add(new ProfileModel("Rest Name",restName,R.drawable.restaurant,0));
        profileModels.add(new ProfileModel("Phone Number",phoneNumber,R.drawable.call,0));
        profileModels.add(new ProfileModel("Email",email,R.drawable.email,0));
        profileModels.add(new ProfileModel("Website",website,R.drawable.global,0));
        profileModels.add(new ProfileModel("Address",address,R.drawable.map,0));
        profileModels.add(new ProfileModel("SignOut","sssssssssss",R.drawable.global,1));
        ProfileAdapter profileAdapter = new ProfileAdapter(ProfileActivity.this,profileModels, restInfoEditor, restEditor, categoryCountEditor, restInfoPreferences, sharedPreferences, categoryCountPref,relativeLayout,databaseReference);
        listView.setAdapter(profileAdapter);
    }

}