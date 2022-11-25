package com.insajet.ezdine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;
import java.util.Objects;

public class AboutUsActivity extends AppCompatActivity {


    private Toolbar toolbar;
    private Button btnPrivacyPolicy, btnContactUs, btnInvite;
    TextView tvReadMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setNavigationIcon(R.drawable.ic_back_white);
        toolbar.setTitle("About");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        btnContactUs = findViewById(R.id.btnContactUs);
        btnInvite = findViewById(R.id.btnInvite);
        btnPrivacyPolicy = findViewById(R.id.btnPrivacyPolicy);
        tvReadMore = findViewById(R.id.tvReadMore);

        tvReadMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        openWeb();
                    }
                }).start();
            }
        });

        btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        invite();
                    }
                }).start();
            }
        });

        btnContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(() -> contactUs()).start();
            }
        });



    }

    void openWeb(){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://ezdine.in/"));
        startActivity(Intent.createChooser(intent,"Chooose browser"));
    }


    void contactUs(){
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:support@ezdine.in"));
        intent.putExtra(Intent.EXTRA_SUBJECT,"Queriess");
        startActivity(Intent.createChooser(intent, "Send Email"));
    }

    void invite(){
        String url= "https://play.google.com/store/apps/details?id=com.insajet.ezdine";
        String invitation = "Hey! Looking for a digital Dine-in experience for your Restaurants/Cafes?\n" +
                "Download our app &  create your own Menu QR Code for Free!\n";
        String data = String.format(Locale.ENGLISH,"%s %s",invitation,url);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,data);
        startActivity(Intent.createChooser(intent,"Share Link"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}